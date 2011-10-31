package es.ctic.tabels

class ProjectService {

    static transactional = true
    static scope = "singleton"

    static String defaultProgramFilename = "default.tabels"
    
    File workDir = new File(System.getProperty("java.io.tmpdir") + "tabels/upload/")
    File programFile = new File(workDir, defaultProgramFilename)

    def File[] getFiles() {
        log.debug "Listing files in temporary dir: ${workDir}"
        if (workDir.exists() == false) {
            log.info "Creating temporary dir: ${workDir}"
            workDir.mkdirs()
        }
        workDir.listFiles()
    }

    def getModel() {
        log.info "And Tabular Cells!"
        def dataSource = ExcelDataSource.loadAllExcelFilesFromDirectory(workDir)
        log.debug "Datasource includes these files: ${dataSource.filenames}"
        log.debug "Using Tabels program: ${programFile.canonicalPath} (available? ${programFile.exists()})" 
		def parser = new TabelsParser()
		def autogenerator = new BasicAutogenerator()
        def program = programFile.exists() ? parser.parseProgram(programFile) : autogenerator.autogenerateProgram(dataSource)
		def interpreter = new Interpreter()
		def dataOutput = new JenaDataOutput(program.prefixesAsMap())
		interpreter.interpret(program, dataSource, dataOutput)

		if (programFile.exists() == false) {
		    def tos = new PrintStream(new FileOutputStream(programFile))
		    int indent = 0
		    def prettyPrinter = new PrettyPrint(indent)
		    program.accept(prettyPrinter)
		    log.info "The writing the following program to the file ${programFile.canonicalPath}:\n${prettyPrinter.toString()}"
		    tos.print(prettyPrinter.toString())
		    tos.close()
	    }
	    
	    return dataOutput.model
    }
    
    String getProgram() {
        return programFile.exists() ? programFile.getText() : ""
    }
    
    def saveProgram(String newProgram) throws ParseException {
        def parser = new TabelsParser()
        def program = parser.parseProgram(newProgram) // validates the program
        programFile.setText(newProgram)
    }
    
}
