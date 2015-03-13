import scala.language.postfixOps
//import org.apache.commons.FileUtils


object CSS{
 val allSelectors: collection.mutable.Set[ RawSelector ] = collection.mutable.Set()
  def render{
    val stylesheet = allSelectors.map( selector =>
      {
        if( selector.declarations.trim == "" )
          ""
        else 
        {
          var style =  s"${selector.s}{${selector.declarations}}"

          var styleInMedia = if( selector.media.trim != "" )
              s"${selector.media}{${style}}"
            else
              style

          styleInMedia
        }
      }
    ).mkString + "\n"
    //scss so that this can be included in other sass files if needed
    org.apache.commons.io.FileUtils.writeStringToFile( new java.io.File( "_autogenscala.scss" ), stylesheet, "UTF-8" )
  }

  //Selector is first class, going by BEM, don't use HTML tags, ids
  implicit class Selector( c: String ) extends RawSelector( s".$c" ){
  }

  //Media can be typesafe too 
  case class Media()

  case class RawSelector( val s: String ){
    var media: String = ""
    var declarations: String = ""

    CSS.allSelectors += this

    def pseudo( psel: String ) = RawSelector( s"$s:$psel" )

    def equals( that: RawSelector ) = s == that.s && media == that.media

    def child( sel: RawSelector ) = 
      RawSelector( s"$s ${sel.s}" )

    //BEM!!
    def __ = child _

    def immediateChild( sel: Selector ) = 
      RawSelector( s"$s>${sel.s}" )

    def > = immediateChild _

    def chain( sel: Selector ) = RawSelector( s"$s${sel.s}" )
    
    def sibling( sel: Selector ) = 
      RawSelector( s"$s~${sel.s}" )

    def ~  = sibling _
    
    def immediateSibling( sel: Selector ) = 
      RawSelector( s"$s+${sel.s}" )

    def +  = immediateSibling _

    def style( styles: String* ) = {
      declarations += styles.mkString
      this
    }
  }
  
  trait CssProperty{
    var name: String
    def assign( value: String ) = s"$name:$value;"
    def custom = assign _
  }

  implicit class CssCustomProperty( p: Symbol ) extends CssProperty{
    var name = ""
    def := ( v: String ) = {
      val r = "[A-Z]".r
      name = r.replaceAllIn( p.name, u => "-" + ( u.toString.toCharArray.head.toChar+32 ).toChar )
      assign( v )
    }
  }

  //Haven't thought much but it looks like shorthand properties would be hard to be made typesafe
  

  

  trait NumValuedProperty {
    this: CssProperty =>
    def apply( n: Double ) = assign( n.toString )
  }
  
  trait DimensionValuedProperty {
    this: CssProperty =>
    def apply( n: Dimension ) = assign( n.s )
  }

  trait PercentValuedProperty {
    this: CssProperty =>
    def apply( n: Percent ) = assign( n.s )
  }
  
  trait ColorValuedProperty {
    this: CssProperty =>
    def apply( n: Color ) = assign( n.s )
  }

  trait StringValuedProperty {
    this: CssProperty =>
    def apply( s: String ) = assign( s )
  }
  
  trait AutoValuedProperty {
    this: CssProperty =>
    def auto = assign( "auto" )
  }

  trait InheritableProperty {
    this: CssProperty =>
    def inherit = assign( "inherit" )
  }

  trait InitialValuedProperty {
    this: CssProperty =>
    def initial = assign( "initial" )
  }

  object width extends CssProperty with DimensionValuedProperty with PercentValuedProperty{
    var name = "width"
  }
  
  object position extends CssProperty{
    var name = "position"
    def static   = assign( "static" )
    def relative = assign( "relative" )
    def fixed    = assign( "fixed" )
  }

  object borderLeft extends CssProperty{
    var name = "border-left"

    //if you merge dimension and percent into size, this would look better, although
    // thick, think allowed values can still not be specified under this particular
    // definition
    def apply( s: Dimension, t: String, c: Color ) = assign( "" )
  }

  object borderTopVerticalBottom extends CssProperty{
    var name = "border"
    def apply( t: Dimension, v: Dimension, b: Dimension )= assign( "" )
  }
  
  object left extends CssProperty with DimensionValuedProperty with PercentValuedProperty{
    var name = "left"
  }
  
  object top extends CssProperty with DimensionValuedProperty with PercentValuedProperty{
    var name = "top"
  }
  
  object height extends CssProperty with DimensionValuedProperty with PercentValuedProperty{
    var name = "height"
  }

  object backgroundColor extends CssProperty with InheritableProperty with ColorValuedProperty{
    var name = "background-color"
    def transparent = assign( "transparent" )
  }


  trait DimensionUnit{ val dim: String }
  object Pixel extends DimensionUnit{ val dim = "px" }
  object RootEm extends DimensionUnit{ val dim = "rem" }
  object Em     extends DimensionUnit{ val dim = "em" }

  //Maybe merge Dimension and Percent into Size and make it much
  //more usable
  implicit class Dimension( val i: Int ){
    var unitType:DimensionUnit = Pixel
    def s = s"$i${unitType.dim}"

    def px = { unitType = Pixel; this }
    def rem = { unitType = RootEm; this }
    def em = { unitType = Em; this }
  }
  
  //Stuff needs to be added here, so only dummy for now
  //
  case class Color( s: String )
  
  implicit class Percent( val i: Int ){
    def s = s"$i%"

    def pc = this
  }
}

object SharedCssJsHtml{
  val firstClass  = "first"
  val secondClass = "second"
  val thirdClass  = "third"
}

object S extends App{
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
}
