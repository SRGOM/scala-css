import scala.language.postfixOps

object Main extends App{
  import CSS._

  val firstClass  = SharedCssJsHtml.firstClass 
  val secondClass = SharedCssJsHtml.secondClass
  val thirdClass  = SharedCssJsHtml.thirdClass 


  def minLimitMixin = Seq(
    'minHeight := "40px",
    'minWidth := "30px"
  )

  Selector( firstClass ) > secondClass __ thirdClass style( 
    width( 3 px ), 
    height( 7 pc ),
    'borderLeft := "3px solid red",
    backgroundColor( Color( "#333" ) )
  ) style( minLimitMixin: _* )

  Selector( firstClass ) + secondClass style(
    position.static,
    left( 3 px ),
    top( 4 rem )
  ) style( minLimitMixin: _* )

  Selector( firstClass ) chain secondClass style(
    Symbol( "-webkit-border-radius" ) := "3px 2px" 
  )

  val x = Selector( secondClass ) ~ thirdClass 
  val y = x pseudo "hover"
  y style(
    'padding := "3px 4px"
  )


  CSS.render 
  //look for _autogenscala.scss
  //.first+.second{position:static;left:3px;top:4rem;min-height:40px;min-width:30px;}.first.second{-webkit-border-radius:3px 2px;}.second~.third:hover{padding:3px 4px;}.first>.second .third{width:3px;height:7%;border-left:3px solid red;background-color:#333;min-height:40px;min-width:30px;}
  
}

