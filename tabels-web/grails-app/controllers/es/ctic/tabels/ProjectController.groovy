package es.ctic.tabels

import javax.servlet.http.HttpServletResponse
import org.fundacionctic.su4j.endpoint.EndpointFactory
import org.fundacionctic.su4j.endpoint.SparqlEndpoint
import org.fundacionctic.su4j.endpoint.http.MimeTypes
import org.fundacionctic.su4j.endpoint.utils.ResultSetHelper
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ProjectController {

    def projectService
    
    def index = {
        [path: projectService.workDir]
    }
    
    def rdf = {
        def model = projectService.getModel()

        if (request.method == "HEAD") {
            render HttpServletResponse.SC_OK // otherwise Grails will return 404, see http://adhockery.blogspot.com/2011/08/grails-gotcha-beware-head-requests-when.html
        } else {
            log.debug "Serializing RDF response, ${model.size()} triples"
		    response.contentType = "application/rdf+xml"
            model.write(response.outputStream, "RDF/XML")
        }
    }
    
	def query = {
		SparqlEndpoint endpoint = EndpointFactory.createDefaultSparqlEndpoint()
		endpoint.addNamedGraph(getGraph(), projectService.getModel())
		endpoint.setRequest(request)
		endpoint.setResponse(response)
		if (endpoint.isQuery()) {
			try {
				log.info("Executing SPARQL query")
				if (endpoint.isSelectQuery() && MimeTypes.HTML.equals(endpoint.getFormat())) {
					def results = endpoint.getResults().getResult()
					def vars = ResultSetHelper.extractVariables(results)
					def tuples = ResultSetHelper.extractTuples(results)
					def size = tuples.size()					
					log.debug "Serializing to HTML ${size} results"
					return [vars: vars, tuples: tuples, size: size ]
				} else {
					endpoint.query()
				}
			} catch (Exception e) {
				log.error(e.getMessage())
				render(status: 500, text: e.getMessage())
			}
		} else {
			redirect(action:form)
		}
	}
	
	def form = {
		log.debug("Showing SPARQL form")
		[query:"SELECT * \nFROM <" + getGraph() + "> \nWHERE { ?s ?p ?o }"]
	}
	
	private String getGraph() {
	    // FIXME: not really portable
	    return ConfigurationHolder.config.grails.serverURL
	}
	
}
