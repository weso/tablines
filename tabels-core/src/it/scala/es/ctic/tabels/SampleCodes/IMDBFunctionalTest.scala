package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class IMDBFunctionalTest extends AbstractFunctionalTest {

	val program = """
//@fetch("debepedia")
PREFIX project: <http://idi.fundacionctic.org/tabels/project/imdb/>
PREFIX my: <http://idi.fundacionctic.org/tabels/project/imdb/resource/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dcat: <http://www.w3.org/ns/dcat#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/#>

SET sheets "0"
    FOR ?rowId IN rows FILTER get-row(?rowId)
        MATCH [?rank,?rating,?title,?votes] IN horizontal
		LET ?titleTrimmed = trim(substring-before(?title,"("))
//		LET ?resFilm= dbpedia-disambiguation(?titleTrimmed)
		LET ?resource = resource(?titleTrimmed,<http://idi.fundacionctic.org/tabels/project/imdb/resource/>)
		LET ?year = int(substring(?title,int-add(last-index-of(?title,"("),1),4))
//		LET ?resyear= dbpedia-disambiguation(?year)
        LET ?rankInt = int(substring-before(?rank,"."))
        LET ?votesInt = int(translate(?votes,",",""))

CONSTRUCT {
    my:DataSet a dcat:Dataset .
    my:DataSet dct:title "Default title for autogenerated tabels data sets" .
    my:DataSet rdfs:keyword "tabels" .
    my:DataSet dcat:distribution my:DatasetRDF .
    my:DataSet dcat:distribution my:DatasetTurtle .
    my:DataSet dcat:distribution my:DatasetN3
}

CONSTRUCT {
    my:DatasetRDF a dcat:Distribution .
    my:DatasetRDF dcat:accessURL project:data .
    my:DatasetRDF dct:format _:B0 .
    _:B0 a dct:IMT .
    _:B0 rdf:value "application/rdf+xml" .
    _:B0 rdfs:label "RDF+XML"
}

CONSTRUCT {
    my:DatasetTurtle a dcat:Distribution .
    my:DatasetTurtle dcat:accessURL project:data?format=ttl .
    my:DatasetTurtle dct:format _:B1 .
    _:B1 a dct:IMT .
    _:B1 rdf:value "text/turtle" .
    _:B1 rdfs:label "TURTLE"
}

CONSTRUCT {
    my:DatasetN3 a dcat:Distribution .
    my:DatasetN3 dcat:accessURL project:data?format=text .
    my:DatasetN3 dct:format _:B2 .
    _:B2 a dct:IMT .
    _:B2 rdf:value "text/n3" .
    _:B2 rdfs:label "N3"
}

CONSTRUCT {
    ?resource a my:SomeResource .
	?resource rdfs:label ?titleTrimmed.
    ?resource my:rank ?rankInt .
    ?resource my:rating ?rating .
//    ?resource my:Film ?resFilm .
//    ?resource my:year ?resyear.
    ?resource my:votes ?votesInt
}

CONSTRUCT {
    my:SomeResource a rdfs:Class
}

CONSTRUCT {
    my:rank a rdf:Property .
    my:rating a rdf:Property .
    my:title a rdf:Property .
    my:votes a rdf:Property
}

        """
    val spreadsheets = Seq("Sample-imdb-top-250.html")

    @Test def testIMDBSample {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """
		   PREFIX my: <http://idi.fundacionctic.org/tabels/project/imdb/resource/>
		   ASK { 
				my:Platoon a my:SomeResource ;
						   rdfs:label "Platoon" ;
						   my:rank "147"^^xsd:int ;
						   my:rating 8.1 ;
						   my:votes "143220"^^xsd:int .
				}""")
    }
    
}

