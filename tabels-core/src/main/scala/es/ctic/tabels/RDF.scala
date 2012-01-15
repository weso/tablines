package es.ctic.tabels

sealed abstract class RDFNode {
    
}

case class Literal(value : Any, rdfType: Resource = XSD_STRING, langTag : String = "") extends RDFNode {
    
    override def toString() = "\"" + value.toString + "\"" + (if (langTag != "") ("@" + langTag) else "") + (if (rdfType != XSD_STRING) ("^^" + rdfType) else "")
	
	def truthValue : Boolean = Set("true", "1") contains this.asBoolean.value.toString

    /**
     * This method calculates the effective boolean value of the
     * literal by applying the rules of fn:boolean, see
     * http://www.w3.org/TR/rdf-sparql-query/#ebv
     */
	def asBoolean : Literal = rdfType match {
	    case XSD_BOOLEAN => this
	    case XSD_STRING =>  if (value.toString.length > 0) LITERAL_TRUE else LITERAL_FALSE
	    case XSD_INT | XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT => if (value.toString.toDouble == 0.0) LITERAL_FALSE else LITERAL_TRUE
	}
	
	def asString : Literal = Literal(value)
	
	def asInt : Literal = rdfType match {
	    case XSD_INT => this
	    case XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT => Literal(value.toString.toInt, XSD_INT)
	    case XSD_STRING => Literal(value.toString.toInt, XSD_INT)
	    case _ => throw new TypeConversionException(this, XSD_INT)
	}
	
	def asFloat : Literal = rdfType match {
	    case XSD_FLOAT => this
	    case XSD_INT | XSD_DOUBLE | XSD_DECIMAL => Literal(value.toString.toFloat, XSD_FLOAT)
	    case XSD_STRING => Literal(value.toString.toFloat, XSD_FLOAT)
	    case _ => throw new TypeConversionException(this, XSD_FLOAT)
	}
	
}

object Literal {
    
    implicit def int2literal(i : Int) : Literal = Literal(i, XSD_INT)
    implicit def float2literal(f : Float) : Literal = Literal(f, XSD_FLOAT)
    implicit def double2literal(d : Double) : Literal = Literal(d, XSD_DOUBLE)
    implicit def boolean2literal(b : Boolean) : Literal = Literal(b, XSD_BOOLEAN)
    implicit def string2literal(s : String) : Literal = Literal(s)
    
    implicit def literal2int(l : Literal) : Int = l.asInt.value.asInstanceOf[Int]
    implicit def literal2string(l : Literal) : String = l.asString.value.asInstanceOf[String]

}

abstract sealed class Resource() extends RDFNode {
    
}

case class NamedResource(uri : String) extends Resource {
    
    override def toString() = "<" + uri + ">"
    
    def toAbbrString(prefixes : Seq[(String,NamedResource)]) : String = toCurie(prefixes) getOrElse toString()
    
    def toCurie(prefixes : Seq[(String,NamedResource)]) : Option[String] =
        if (this == RDF_TYPE) Some("a")
        else prefixes find (uri startsWith _._2.uri) map { case (prefix, ns) => uri.replace(ns.uri, prefix + ":") }
    
	def +(suffix : String) : NamedResource = NamedResource(this.uri + suffix)

}

object RDF_TYPE extends NamedResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
object XSD_STRING extends NamedResource("http://www.w3.org/2001/XMLSchema#string")
object XSD_BOOLEAN extends NamedResource("http://www.w3.org/2001/XMLSchema#boolean")
object XSD_INT extends NamedResource("http://www.w3.org/2001/XMLSchema#int")
object XSD_DOUBLE extends NamedResource("http://www.w3.org/2001/XMLSchema#double")
object XSD_FLOAT extends NamedResource("http://www.w3.org/2001/XMLSchema#float")
object XSD_DECIMAL extends NamedResource("http://www.w3.org/2001/XMLSchema#decimal")
object XSD_DATE extends NamedResource("http://www.w3.org/2001/XMLSchema#date")
object LITERAL_TRUE extends Literal("true", XSD_BOOLEAN)
object LITERAL_FALSE extends Literal("false", XSD_BOOLEAN)


case class BlankNode() extends Resource {
    
    override def toString() = "[]"
    
}

// FIXME: literals can not be properties
case class Statement(subject: RDFNode, property: RDFNode, obj:RDFNode){

}

case class Namespace(ns : String) {
    
    def apply(localName : String = "") = NamedResource(ns + localName)
    override def toString() : String = ns
    
}

object CommonNamespaces {

    object EX   extends Namespace("http://example.org/ex#")
    object SCV  extends Namespace("http://purl.org/NET/scovo#")
    object RDF  extends Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    object RDFS extends Namespace("http://www.w3.org/2000/01/rdf-schema#")
    object SKOS extends Namespace("http://www.w3.org/2004/02/skos/core#")
    object XSD  extends Namespace("http://www.w3.org/2001/XMLSchema#")
    
}

// type classes

// the class of the types that can be transformed to an RDF node
trait CanToRDFNode[a] {
    def toRDFNode(x : a) : RDFNode
}

object CanToRDFNode {
    import es.ctic.tabels.Literal._
    implicit def intToRDFNode = new CanToRDFNode[Int] {
        def toRDFNode(x : Int) : RDFNode = x
    }
    implicit def stringToRDFNode = new CanToRDFNode[String] {
        def toRDFNode(x : String) : RDFNode = x
    }
    implicit def booleanToRDFNode = new CanToRDFNode[Boolean] {
        def toRDFNode(x : Boolean) : RDFNode = x
    }
    implicit def namedResourceToRDFNode = new CanToRDFNode[NamedResource] {
        def toRDFNode(x : NamedResource) : RDFNode = x
    }
    implicit def blankNodeToRDFNode = new CanToRDFNode[BlankNode] {
        def toRDFNode(x : BlankNode) : RDFNode = x
    }
    implicit def seqToRDFNode = new CanToRDFNode[Seq[Resource]] {
        def toRDFNode(x : Seq[Resource]) : RDFNode = x.head
    }
    
}

// the class of the types that can be obtained from an RDF node
trait CanFromRDFNode[a] {
    def fromRDFNode(rdfNode : RDFNode) : a
}

object CanFromRDFNode {
    import es.ctic.tabels.Literal._
    implicit def intFromRDFNode = new CanFromRDFNode[Int] {
        def fromRDFNode(rdfNode : RDFNode) : Int = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def stringFromRDFNode = new CanFromRDFNode[String] {
        def fromRDFNode(rdfNode : RDFNode) : String = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def workAreaFromRDFNode = new CanFromRDFNode[WorkArea] {
        def fromRDFNode(rdfNode : RDFNode) : WorkArea = rdfNode match {
            case l : Literal => new WorkArea
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
}
