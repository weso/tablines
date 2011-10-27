package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.util.matching.Regex
import es.ctic.tabels.RelativePos._
import es.ctic.tabels.TupleType._

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

import scala.collection._

import java.io.File

class TabelsParser extends JavaTokenParsers {

	val prefixes = mutable.HashMap.empty[String, Resource]
	
	// keywords

	def CELL = "cell".ignoreCase
	def FOR  = "for".ignoreCase
	def IN   = "in".ignoreCase
	def ROWS = "rows".ignoreCase
	def COLS = "cols".ignoreCase
	def SHEETS = "sheets".ignoreCase // aka: "tabs"
	def FILES = "files".ignoreCase
	def IGNORE = "ignore".ignoreCase
	def BLANKS = "blanks".ignoreCase
	def FILTER = "filter".ignoreCase
	def BY = "by".ignoreCase
	def AS = "as".ignoreCase
	def AT = "at".ignoreCase
	def OF = "of".ignoreCase
	def HORIZONTAL = "horizontal".ignoreCase
	def VERTICAL = "vertical".ignoreCase
	def LET = "let".ignoreCase
	def A = "a".ignoreCase
	def PREFIX = "prefix".ignoreCase
//	def PLACED = "placed".ignoreCase
//	def WITH = "with".ignoreCase
//	def IS = "is".ignoreCase
//	def LOCATED = "located".ignoreCase
	def LEFT = "left".ignoreCase
	def RIGHT = "right".ignoreCase
	def TOP = "top".ignoreCase
	def BOTTOM = "bottom".ignoreCase
	def MATCHES = "matches".ignoreCase
	def WHILE = "while".ignoreCase
	def UNTIL = "until".ignoreCase
	def MATCH = "match".ignoreCase
    def SET = "set".ignoreCase
    
    // functions
	def RESOURCE = "resource".ignoreCase
    def ADD = "add".ignoreCase
    def TRUE = "true".ignoreCase
    def FALSE = "false".ignoreCase
    def CONCAT = "concat".ignoreCase
    def INT = "int".ignoreCase
    def DECIMAL = "decimal".ignoreCase
    def FLOAT = "float".ignoreCase
    def BOOLEAN = "boolean".ignoreCase
    def JOIN = "join".ignoreCase
    def SUBSTRING = "substring".ignoreCase
    def STRINGLENGTH = "length". ignoreCase
    def CONTAINS = "contains".ignoreCase
    def STARTSWITH = "startswith".ignoreCase
    def ENDSWITH = "endswith".ignoreCase
    def SUBSTRINGBEFORE = "substringbefore".ignoreCase
    def SUBSTRINGAFTER = "substringafter".ignoreCase
    def REPLACE = "replace".ignoreCase
    def CASE = "case".ignoreCase
    def UPPER = "upper".ignoreCase
    def LOWER = "lower".ignoreCase
    def TRANSLATE = "translate".ignoreCase
    
    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable
	
    def tupleType: Parser[TupleType] = (HORIZONTAL|VERTICAL) ^^ {t => TupleType.withName(t.toLowerCase)}
    
	def displacement : Parser[Int] = opt("""[0-9]+""".r ^^ (_ toInt)) ^^ (_ getOrElse(1))
	
	def dimension : Parser[Dimension] = (ROWS|COLS|SHEETS|FILES) ^^ { d => Dimension.withName(d.toLowerCase) }
	
    def position : Parser[Position] =
        ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => FixedPosition(row = r.toInt - 1, col = columnConverter.alphaToInt(c)) }|
		variable ^^
		{ case v => WithVariablePosition(v) }|
		displacement ~ ((LEFT|RIGHT|BOTTOM|TOP) <~ OF) ~ position ^^
		{ case d~r~p => RelativePosition(RelativePos.withName(r.toLowerCase),p,d) }
		
		
	// RDF

    def quotedString : Parser[String] =
         stringLiteral ^^ { s => s.slice(1,s.length-1) }
         
    def langTag : Parser[String] = """[a-zA-Z][a-zA-Z\-]*""".r

	def rdfLiteral : Parser[Literal] =
       quotedString ~ ("^^" ~> iriRef) ^^ { case value~rdfType => Literal(value, rdfType = rdfType) } |
       quotedString ~ ("@" ~> langTag) ^^ { case value~langTag => Literal(value, langTag = langTag) } |
       quotedString ^^ { value => Literal(value) } |
	   decimalNumber ^^ { asString => Literal(asString, rdfType = if (asString contains ".") XSD_DECIMAL else XSD_INT) } |
	   TRUE ^^ { x => LITERAL_TRUE } |
	   FALSE ^^ { x => LITERAL_FALSE }

	def iriRef : Parser[Resource] = "<" ~>  """([^<>"{}|^`\\\x00-\x20])*""".r <~ ">" ^^ Resource
	
	def curieRef : Parser[Resource] = (ident <~ ":") ~ ident ^^ { case prefix~local => prefixes(prefix) + local }
		
	def rdfNode : Parser[RDFNode] = iriRef | curieRef | rdfLiteral
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = rep(prefixDecl) ~ rep(tabelsStatement) ~ rep(template) ^^
	   { case prefixes~ps~ts => S(prefixes,ps,ts) }
	
	def prefixDecl : Parser[(String,Resource)] = (PREFIX ~> ident) ~ (":" ~> iriRef) ^^
	    { case prefix~ns => prefixes += (prefix -> ns)
	                        (prefix -> ns)
	    }
	
	def tabelsStatement : Parser[TabelsStatement] = blockStatement | iteratorStatement | setInDimensionStatement | letStatement | matchStatement 
	
	def blockStatement : Parser[BlockStatement] = "{" ~> rep1sep(tabelsStatement, ";") <~ "}" ^^ { BlockStatement(_) }
	
	def iteratorStatement : Parser[IteratorStatement] = (FOR ~> opt(variable <~ IN)) ~ dimension ~filterCondition~ stopCondition ~ opt(tabelsStatement) ^^
        { case v~d~f~s~p => IteratorStatement(variable = v, dimension = d, filter = f, stopCond = s, nestedStatement = p) }
    
	def setInDimensionStatement : Parser[SetInDimensionStatement] = opt(SET ~> variable) ~ (IN ~> dimension) ~ quotedString ~ opt(tabelsStatement) ^^
        { case v~d~s~p => SetInDimensionStatement(variable = v, dimension = d, fixedDimension = s, nestedStatement = p) }
      
	
	def letStatement : Parser[LetStatement] = 
		(LET ~> variable) ~ ("=" ~>expression) ~ opt(tabelsStatement) ^^
        {case v1~exp~pat => LetStatement(variable = v1, expression = exp, nestedStatement = pat) }
        
    def matchStatement : Parser[MatchStatement] = 
		(MATCH ~> variableOrTuple) ~ opt(AT ~> position) ~ filterCondition ~ opt(tabelsStatement) ^^
        { case t~p~fc~pat => MatchStatement(tuple = t, position = p, filter = fc, nestedStatement = pat) }
        
    def variableOrTuple : Parser[Tuple] = variable ^^ { v => Tuple(Seq(v)) } | tuple
       
	
    def regex : Parser[Regex] =
      ("""\"[^"]*\"""".r) ^^ {case r => new Regex( (r.drop(1)).dropRight(1) )}
    
    def stopCondition : Parser[Option[Expression]] =
      opt((WHILE ~> expression)|(UNTIL ~> expression)^^{case e =>NotExpression(expression = e) })
     
    def filterCondition : Parser[Option[Expression]] = 
      opt(FILTER ~> expression)
      
      
    def expression : Parser[Expression] = 
        variable ^^ VariableReference |
        rdfLiteral ^^ LiteralExpression |
        functionExpression
      
    def functionExpression : Parser[Expression] =
        ((RESOURCE <~"(") ~> expression )~ (","~> iriRef <~")") ^^ 
    		{case v~u => ResourceExpression(expression = v, uri = u)} |		
        ((MATCHES<~"(") ~>expression ~ (","~> regex <~")") ) ^^ 
    		{case e~r => RegexExpression(expression = e, re = r)} |
        ((ADD <~"(") ~>variable ~ (","~> expression <~")") ) ^^ 
    		{case v~e => AddVariableExpression(v, e)}|
    	(CONCAT~> "("~>repsep(expression, ",")<~")")^^
    		{e => ConcatExpression(e)}|
    	(JOIN~> "("~>repsep(expression, ";")~("," ~>expression)<~")")^^
    		{case re~qs => StringJoinExpression(re, qs)}|
    	(SUBSTRING~> "("~>expression~("," ~>wholeNumber)<~")")^^
    		{case re~i => SubStringExpression(re, i.toInt)}|
    	(STRINGLENGTH~> "("~>expression<~")")^^
    		{case re => StringLengthExpression(re)}|
    	(CONTAINS~> "("~>(expression<~",") ~ expression<~")")^^
    		{case container ~content => ContainsExpression(container, content)}|
    	(STARTSWITH~> "("~>(expression<~",") ~ expression<~")")^^
    		{case container ~start => StartsWithExpression(container, start)}|
    	(ENDSWITH~> "("~>(expression<~",") ~ expression<~")")^^
    		{case container ~end => StartsWithExpression(container, end)}|
    	(SUBSTRINGBEFORE~> "("~>(expression<~",") ~ expression<~")")^^
    		{case container ~suffix => SubstringBeforeExpression(container, suffix)}|
    	(SUBSTRINGAFTER~> "("~>(expression<~",") ~ expression<~")")^^
    		{case container ~prefix => SubstringAfterExpression(container, prefix)}|
    	(REPLACE~> "("~>(expression<~",")~(regex<~"," )~ (expression<~")"))^^
    		{case input ~ re ~ replacement => ReplaceExpression(input, re, replacement)}|
    	(UPPER~> (CASE~>"("~>(expression<~")")))^^
    		{case expression => UpperCaseExpression(expression)}|
    	(LOWER~> (CASE~>"("~>(expression<~")")))^^
    		{case expression => LowerCaseExpression(expression)}|
    	(TRANSLATE~>"("~>(expression<~",")~(expression<~"," )~ (expression<~")"))^^
    		{case input ~ pattern ~ replacement => TranslateExpression(input, pattern, replacement)}|
    	(INT ~> "("~>expression<~")")^^
    		{IntExpression}|
    	(FLOAT ~> "("~>expression<~")")^^
    		{FloatExpression}|
    	(DECIMAL ~> "("~>expression<~")")^^
    		{DecimalExpression}|
    	(BOOLEAN ~> "("~>expression<~")")^^
    		{BooleanExpression}

    
    def tuple : Parser[Tuple] = ("[" ~> rep1sep(variable,",") <~ "]") ~ opt(IN ~> tupleType) ^^
    	{ case vars~direction => Tuple(vars,direction getOrElse TupleType.horizontal) }
    	
    	
    // templates
    
    def verbTemplate : Parser[Either[RDFNode, Variable]] =
		iriRef ^^ { Left(_) } |
		curieRef ^^ { Left(_) } |
		A ^^ { _ => Left(RDF_TYPE) } |
		variable ^^ { Right (_) }
       
	def objectsTemplate : Parser[Seq[Either[RDFNode, Variable]]] = rep1sep(eitherRDFNodeOrVariable, ",")

	def predicateObjectsTemplate : Parser[Seq[Tuple2[ Either[RDFNode, Variable], Either[RDFNode, Variable] ]]] =
		rep1sep((verbTemplate ~ objectsTemplate) ^^ { case pred~objs => for (obj <- objs) yield (pred,obj) }, ";") ^^ { _ flatten }

	def triplesSameSubjectTemplate : Parser[Seq[TripleTemplate]] =
	  eitherRDFNodeOrVariable~predicateObjectsTemplate ^^
	  { case subj~predObjs => for ((pred,obj) <- predObjs) yield TripleTemplate(subj,pred,obj) }
	
	def template : Parser[Template] = "{" ~> rep1sep(triplesSameSubjectTemplate, ".") <~ "}" ^^
	  { triples => Template((triples flatten)) }


	// parsing methods
	
	protected def parse[T](p : Parser[T], input: String) : T = {
        val phraseParser = phrase(p)
        phraseParser(new CharSequenceReader(input)) match {
            case Success(t,_)     => t
            case NoSuccess(msg,next) => throw new ParseException(input, msg, next.pos.line, next.pos.column)
        }		
	}
	
	def parseProgram(input : String) : S = parse(start, input)
	
	def parseProgram(file : File) : S = {
	    val source = scala.io.Source.fromFile(file)
        val lines = source.mkString
        source.close()
        return parseProgram(lines)
	}

	// trick from http://stackoverflow.com/questions/6080437/case-insensitive-scala-parser-combinator
	
	class CaseInsensitiveKeyword(str: String) {
	  def ignoreCase: Parser[String] = ("""(?i)\Q""" + str + """\E""").r
	}

	implicit def pimpString(str: String): CaseInsensitiveKeyword = new CaseInsensitiveKeyword(str)	
	
	
}
