package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File

class PXDataAdapterTest extends JUnitSuite {
   
	val filename1 : String = this.getClass.getResource("/es/ctic/tabels/Test1.px").getFile.replace("%20"," ")
	val filename2 : String = this.getClass.getResource("/es/ctic/tabels/simple.px").getFile.replace("%20"," ")
    private val dataAdapter =new PXDataAdapter(new File(filename1))
	private val dataAdapter2 =new PXDataAdapter(new File(filename2))
    val sheet1 = ""
      
    @Test def getTabs {
    assertEquals(Seq(""), dataAdapter.getTabs())
  }

  @Test def getCols {
    assertEquals(43, dataAdapter.getCols(sheet1))
  }

  @Test def getRows {
    assertEquals(130, dataAdapter.getRows(sheet1))
  }

  @Test def getValue {
    // header
    assertEquals(Literal("1998@Año"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    // Stubs
    assertEquals(Literal("EUROPA@País de nacionalidad"), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    
    //Content
    assertEquals(Literal("Total@Sexo"), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal("40854", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 2)).getContent)
    assertEquals(Literal("3465", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 3)).getContent)


    // dimensionLimits
    assertEquals(Literal(""), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 0)).getContent)
    assertEquals(Literal("APATRIDAS@País de nacionalidad"), dataAdapter.getValue(Point(filename1, sheet1, row = 129, col = 0)).getContent)
    assertEquals(Literal("2011@Año"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 42)).getContent)
    assertEquals(Literal("17", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 129, col = 42)).getContent)
    
    assertEquals(Literal("602", XSD_INT), dataAdapter2.getValue(Point(filename2, sheet1, row = 2, col = 7)).getContent)
    
    
  }
  
}