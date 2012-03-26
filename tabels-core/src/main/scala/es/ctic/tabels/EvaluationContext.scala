package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.HashMap

case class EvaluationContext (dimensionMap : Map[Dimension, String] = new HashMap(), bindings: Bindings = Bindings()) {
	
	//val workingArea = wa
	
    def dimensions : Set[Dimension] = dimensionMap.keySet
	
	def getValue(dimension : Dimension) : String = {
	  if(dimensionMap.contains(dimension))
	    dimensionMap.get(dimension).get // throws exception if unbound
	  else "0"
	}
	
	// lazy because some of the dimensions may not exist yet
	//lazy val cursor : Point = Point(getValue(Dimension.files),getValue(Dimension.sheets),getValue(Dimension.cols).toInt, getValue(Dimension.rows).toInt)
	lazy val cursor : Point = Point(getValue(Dimension.files),getValue(Dimension.sheets),EvaluationContext.workingArea.originalColumn + getValue(Dimension.cols).toInt, EvaluationContext.workingArea.originalRow + getValue(Dimension.rows).toInt)
    
	def addBinding(variable : Variable, value : RDFNode, point: Point) : EvaluationContext =
		EvaluationContext(dimensionMap, bindings.addBinding(variable,value, point))
	
	def addDimension(dimension : Dimension, value : String) : EvaluationContext =
		EvaluationContext(dimensionMap + (dimension -> value), bindings)
		
	def removeDimension(dimension : Dimension) : EvaluationContext =
		EvaluationContext(dimensionMap - (dimension), bindings)

    def getDimensionRange(dimension : Dimension, dataSource : DataSource) : Seq[Any] = {
	  
        lazy val filename = dimensionMap(Dimension.files)
      	lazy val sheet = dimensionMap(Dimension.sheets)
      	println(" - Rango de " + cursor.row+" a "+EvaluationContext.workingArea.actualRow)
      	dimension match {
            case Dimension.files => dataSource.filenames
            case Dimension.sheets => return dataSource.getTabs(filename)
		    case Dimension.rows => return List.range(0, dataSource.getRows(filename,sheet)-EvaluationContext.workingArea.originalRow)
		    case Dimension.cols =>return List.range(0, dataSource.getCols(filename,sheet)- EvaluationContext.workingArea.originalColumn)
		    }
	    }
	}
	

object EvaluationContext{
  
  val workingArea = new WorkArea
  
}
	