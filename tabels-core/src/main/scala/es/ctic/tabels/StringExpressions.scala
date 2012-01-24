package es.ctic.tabels

import scala.util.matching.Regex
/*
 * STRING FUNCTIONS
 * 
 */

object StringFunctions extends FunctionCollection {

    val startsWith = "starts-with" isDefinedBy { (x : String, y : String) => x.startsWith(y) }
    val upperCase = "upper-case" isDefinedBy { (x:String) => x.toUpperCase}
    val compare = "compare" isDefinedBy { (x:String, y:String) => x.compareToIgnoreCase(y) match{
    																  case num if (num < 0) => -1
    																  case num if (num > 0) => 1
																      case _ => 0
    																}
										}
     val levenshteinDistance = "levenshtein-distance" isDefinedBy { (x:String, y:String) => Levenshtein.stringDistance(x,y)}
     val translate = "translate" isDefinedBy { (x:String, y:String, z:String) =>{ 
    	 																		var newX:String = x
    	 																		for(char <- y.toCharArray().take(z.length)) {         
																					newX = newX.replaceAll(char.toString,z.charAt(y.indexOf(char)).toString )
																	     		}
																	     		for(char <- y.toCharArray().drop(z.length)) {
																	     		    newX = newX.replaceAll(char.toString, "")
																	     		}
																				newX.toString}
     										}
     val lowerCase = "lower-case" isDefinedBy { (x:String) => x.toLowerCase()}
     val replace = "replace" isDefinedBy { (x:String, y :Regex, z:String) => x.replaceAll(y.toString,z)}
     val contains = "contains" isDefinedBy { (x:String, y:String) => x.contains(y)}
     val endsWith = "ends-with" isDefinedBy { (x:String, y:String) => x.endsWith(y)}
     val substringAfter = "substring-after" isDefinedBy { (x:String, y:String) => if (x.contains(y))
    	 																				x.drop(x.indexOf(y)+y.length)
    	 																		  else ""
    	 												}
     val substringBefore = "substring-before" isDefinedBy { (x:String, y:String) =>x.dropRight(x.length-x.indexOf(y))}
     val stringLength = "string-Length" isDefinedBy { (x:String) => x.length()}	 												
	 val substring3 = "substring" isDefinedBy { (x:String, y:Int, z:Int) => if (x.length >0) 
			           															x.substring(y, y+z)
			           														else ""
           									 }
	 val substring2 = "substring" isDefinedBy { (x:String, y:Int) => if (x.length >0) 
			           															x.substring(y)
			           														else ""
           									 }
     
}

case class ConcatExpression(expressions: Seq[Expression]) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	var result : String = ""
    expressions.foreach( exp => result += exp.evaluateAsStringValue(evaluationContext))
    return Literal(result, XSD_STRING). asInstanceOf[RDFNode]
  }
   override def prettyPrint = "concat(" + expressions.map(_ toString).mkString(",") + ")"
}

case class StringJoinExpression(expressions: Seq[Expression], separator : Expression) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	val sep = separator.evaluateAsStringValue(evaluationContext)
    Literal(expressions.map(_.evaluateAsStringValue(evaluationContext)).mkString(sep), XSD_STRING). asInstanceOf[RDFNode]
   }
   override def prettyPrint = "string join(" + expressions.map(_ toString).mkString(",")  + " , "+ separator + ")"
}

/*case class SubStringExpression(expression: Expression, startingLoc : Expression, length : Option[Expression]) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
       val evaluatedExpression = expression.evaluateAsStringValue(evaluationContext)
       val evaluatedStartingLoc = startingLoc.evaluateAsIntValue(evaluationContext)
       val evaluatedLength : Option[Int] = length map (e => e.evaluateAsIntValue(evaluationContext))
       if (evaluatedExpression.length >0) {
           val newValue = evaluatedLength match {
               case None => evaluatedExpression.substring(evaluatedStartingLoc)
               case Some(l) => evaluatedExpression.substring(evaluatedStartingLoc, evaluatedStartingLoc+l)
           }
           Literal(newValue, XSD_STRING)
       } else Literal("", XSD_STRING)
   }
   override def prettyPrint = "substring(" + expression.toString  + " , "+ startingLoc + ")"
}*/

/*case class StringLengthExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal (expression.evaluateAsStringValue(evaluationContext).length, XSD_INT)
	
	override def prettyPrint = "length(" + expression.toString  + ")"
}*/

/*case class StartsWithExpression(container: Expression, start: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluateAsStringValue(evaluationContext).startsWith(start.evaluateAsStringValue(evaluationContext)))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "startswith(" + container.toString  +", "+ start.toString  + ")"
}
*/
/*case class SubstringBeforeExpression(container: Expression, subString: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode = {
	  val evaluatedContainer = container.evaluateAsStringValue(evaluationContext)
	  Literal(evaluatedContainer.dropRight(
	      evaluatedContainer.length()-evaluatedContainer.indexOf(subString.evaluateAsStringValue(evaluationContext))))
    }
	 
	override def prettyPrint = "substringbefore(" + container.toString  +", "+ subString.toString  + ")"
}*/

/*case class SubstringAfterExpression(container: Expression, subString: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode = {
	    val evaluatedContainer = container.evaluateAsStringValue(evaluationContext)
	    val evaluatedSubstring = subString.evaluateAsStringValue(evaluationContext)
	  if(evaluatedContainer.contains(evaluatedSubstring))
		  Literal(evaluatedContainer.drop(
				  evaluatedContainer.indexOf(evaluatedSubstring)+evaluatedSubstring.length()))
	  else Literal("")
	}
	override def prettyPrint = "substringafter(" + container.toString  +", "+ subString.toString  + ")"
}
*/
/*case class EndsWithExpression(container: Expression, end: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluateAsStringValue(evaluationContext).endsWith(end.evaluateAsStringValue(evaluationContext)))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "endswith(" + container.toString  +", "+ end.toString  + ")"
}*/

/*case class ContainsExpression(container: Expression, content: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluateAsStringValue(evaluationContext).contains(content.evaluateAsStringValue(evaluationContext)))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "contains(" + container.toString  +", "+ content.toString  + ")"
}
*/
/*case class ReplaceExpression(input: Expression,pattern : Regex, replacement: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	   Literal(input.evaluateAsStringValue(evaluationContext).replaceAll(pattern.toString, replacement.evaluateAsStringValue(evaluationContext)),XSD_STRING)
			
	override def prettyPrint = "replace(" + input.toString  +", "+ pattern.toString +", "+replacement.toString  + ")"
}*/

/*case class UpperCaseExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(expression.evaluateAsStringValue(evaluationContext).toUpperCase)
		  
	override def prettyPrint = "upper case(" + expression   + ")"
}*/

/*case class LowerCaseExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(expression.evaluateAsStringValue(evaluationContext).toLowerCase)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}*/

/*case class TranslateExpression(input: Expression, pattern : Expression,replacement: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode ={
	 var textChain :String =input.evaluateAsStringValue(evaluationContext)
	 val patternToMatch = pattern.evaluateAsStringValue(evaluationContext)
	 for(char <- patternToMatch.toCharArray())
	 {
	   //FIX ME: not checked if the index in pattern is out of bounds in replacement
	   textChain = textChain.replaceAll(char.toString,replacement.evaluateAsStringValue(evaluationContext).charAt(patternToMatch.indexOf(char)).toString )
	 }
	 
	 Literal(textChain,XSD_STRING)
}
	override def prettyPrint = "translate(" +  input.toString  +", "+ pattern.toString +", "+replacement.toString + ")"
}*/
/*FIX ME: To be implemented function 
 * escape-html-uri(expression: Expression)
 * */

/*case class LevenshteinDistanceExpression(expression1 :Expression, expression2: Expression) extends Expression{
  
  override def evaluate (evaluationContext : EvaluationContext) = 
    Literal(Levenshtein.stringDistance(expression1.evaluateAsStringValue(evaluationContext),expression2.evaluateAsStringValue(evaluationContext)), XSD_INT)
   override def prettyPrint = "levenshtein-distance(" + expression1.toString + expression2.toString +")"

}*/


/* *Type change expressions  * */
case class StringExpression(expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = Literal(expression.evaluateAsStringValue(evaluationContext), XSD_STRING)
  override def prettyPrint = "string(" + expression.toString + ")"

}