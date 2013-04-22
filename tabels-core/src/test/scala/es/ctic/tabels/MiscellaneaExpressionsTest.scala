package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class MiscellaneaExpressionsTest extends JUnitSuite {
  
  implicit val evaluationContext = EvaluationContext()
  	 
  //FIXME:DBPediaDisambiguation3 and DBPediaDisambiguation11 is missing
	
  	 @Test def setLangTag{
    
     assertEquals(Literal("caracola", XSD_STRING, "es") ,MiscellaneaFunctions.setLangTag("caracola","es"))
     assertEquals(Literal("seashell", XSD_STRING, "en") ,MiscellaneaFunctions.setLangTag("seashell","en"))
     assertEquals(Literal("Tritonshorn", XSD_STRING, "gr") ,MiscellaneaFunctions.setLangTag("Tritonshorn","gr"))
   
     }
    @Test def isResource{

    /*White List urls tests*/
    assertEquals(NamedResource("http://localhost:8080/tabels/project/test") ,MiscellaneaFunctions.isResource("http://localhost:8080/tabels/project/test"))
    assertEquals(NamedResource("http://idi.fundacionctic.org/scovoxl/scovoxl") ,MiscellaneaFunctions.isResource("http://idi.fundacionctic.org/scovoxl/scovoxl"))
    assertEquals(NamedResource("http://idi.fundacionctic.org/tabels/project") ,MiscellaneaFunctions.isResource("http://idi.fundacionctic.org/tabels/project"))
    assertEquals(NamedResource("http://idi.fundacionctic.org/map-styles/symbols/") ,MiscellaneaFunctions.isResource("http://idi.fundacionctic.org/map-styles/symbols/"))
    assertEquals(NamedResource("http://idi.fundacionctic.org/scovoxl/scovoxl") ,MiscellaneaFunctions.isResource("http://idi.fundacionctic.org/scovoxl/scovoxl"))

    /*Valid url test*/
    assertEquals(NamedResource("http://example.org") ,MiscellaneaFunctions.isResource("http://example.org"))

    //assertEquals(NamedResource("http://localho$¿st:8080/tabÑels/project/test"),MiscellaneaFunctions.isResource("http://localho$¿st:8080/tabÑels/project/test"))

    /*Black List tests*/
    //assertEquals(NamedResource("http://192.168.1.0/tabels/project/test"),MiscellaneaFunctions.isResource("http://192.168.1.0/tabels/project/test"))
    //assertEquals(NamedResource("http://10.168.1.0/tabels/project/test"),MiscellaneaFunctions.isResource("http://10.168.1.0/tabels/project/test"))
    //assertEquals(NamedResource("http://localhost:8080/url/de/prueba"),MiscellaneaFunctions.isResource("http://localhost:8080/url/de/prueba"))
    //assertEquals(NamedResource("http://127.0.0.1:8080/url/de/prueba"),MiscellaneaFunctions.isResource("http://127.0.0.1:8080/url/de/prueba"))
    //assertEquals(NamedResource("http://blackListExample.fundacionctic/url/de/prueba"),MiscellaneaFunctions.isResource("http://blackListExample.fundacionctic/url/de/prueba"))
  }

  @Test def canBeResource{

    assertTrue(MiscellaneaFunctions.canBeResource("http://localhost:8080/tabels/project/test"))
    assertTrue(MiscellaneaFunctions.canBeResource("xxtp:/localhost:8080/tabels/project/test"))
    assertTrue(MiscellaneaFunctions.canBeResource("http://localho$¿st:8080/tabÑels/project/test"))
    assertTrue(MiscellaneaFunctions.canBeResource(""))

    /*Black List tests*/
    assertFalse(MiscellaneaFunctions.canBeResource("http://192.168.1.0/tabels/project/test"))
    assertFalse(MiscellaneaFunctions.canBeResource("http://10.168.1.0/tabels/project/test"))
    assertFalse(MiscellaneaFunctions.canBeResource("http://localhost:8080/url/de/prueba"))
    assertFalse(MiscellaneaFunctions.canBeResource("http://127.0.0.1:8080/url/de/prueba"))
    assertFalse(MiscellaneaFunctions.canBeResource("http://blackListExample.fundacionctic/url/de/prueba"))

  }

  @Test def boolean{
    
     assertTrue(MiscellaneaFunctions.boolean(true))
     assertFalse(MiscellaneaFunctions.boolean(false))
     
   
     }

}