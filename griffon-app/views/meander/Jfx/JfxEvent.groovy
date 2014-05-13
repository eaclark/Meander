package meander.Jfx

import javafx.geometry.Point2D
import meander.Base.Event
import meander.Style

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/5/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
class JfxEvent extends Event {
    def builder

    JfxEvent( named) {
        super( named)
        adj = adj * convertUnits
    }


    def insertBorder( drawPane, point, size, Style style ) {
        def rect
        Style styleHolder = new Style()

        style.copyTo( styleHolder)

        rect = JfxUtils.createBorder( builder,
                                      createPoint( point.x, point.y),
                                      size,
                                      styleHolder)
        return rect
    }

    def drawGraphic( event, drawPane, style, nodeInfo, gPosInfo, currY, compPosList) {
        def graphic
        graphic = JfxGraphic.draw( event, drawPane, builder, style, nodeInfo, gPosInfo, currY, compPosList)
        return graphic
    }

    def drawText( str, drawPane, style, nodeInfo, tPosInfo, currY) {
        def text
        text = JfxText.draw( str, builder, style, nodeInfo, tPosInfo, currY)
        return text
    }

    def drawPositionedText( str, drawPane, style, ip, wid) {
        def text
        text = JfxText.drawPositioned( str,
                                       drawPane,
                                       builder,
                                       style,
                                       createPoint( ip.x, ip.Y),
                                       wid)
        return text
    }

    def drawLabel( ip, drawPane) {
        def newIP
        def label, pLabel
        def rect
        def group

        // convert from com.star.Point (app level) to Point2D (javafx)
//        newIP = createPoint( ip.x, ip.y)

        // store the generated label for future use
        event.label = genLabel()
        rect = JfxUtils.createBorder( builder,
                                      ip,
                                      null,
                                      style)

        label = JfxText.drawPositioned( event.label,
                                        drawPane,
                                        builder,
                                        style,
                                        ip,
                                        null)

        // reposition and resize the border to be around the text
        rect.Width = label.Width
        rect.Height = label.Height
        rect.Position = label.Position

        // group the two shapes
        group = buildGroup( drawPane, [ rect, label])

        return group
    }

    def buildGroup( drawPane, shapeList) {
        def group

        group = JfxUtils.buildGroup( builder, drawPane, null, shapeList )
        return group
    }

    def createPoint( x, y) {
        JfxUtils.createPoint( (double) x, (double) y)
    }

    def createSize( w, h) {
        JfxUtils.createSize( (double) w, (double) h)
    }
}
