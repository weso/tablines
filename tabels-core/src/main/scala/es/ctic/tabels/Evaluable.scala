package es.ctic.tabels

trait Evaluable{
	
  def grammarEvaluation( evaluationContext : EvaluationContext, dataSource: DataSource): Event = this match{
	  
     case _  => return new Event(new BindingList(List(new Binding("$prueba", dataSource.getValue(new Point("hola.xls","Hoja1",0,0))))))
	}
}