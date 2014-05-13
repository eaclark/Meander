package meander.Jfx

import meander.Base.PostNote

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/19/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
class JfxPostNote extends PostNote {
    def builder

    JfxPostNote( named=[:]) {
        super( named)
    }

    def insertBorder( drawPane, point, size, style ) {
        def rect

        rect = JfxUtils.createBorder( builder,
                createPoint( point.x, point.y),
                size,
                style)
        return rect
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

    def buildGroup( drawPane, shapeList) {
        def group

        group = JfxUtils.buildGroup( builder, drawPane, null, shapeList )
        return group
    }

    Object createPoint( x, y) {
        JfxUtils.createPoint( (double) x, (double) y)
    }

    Object createSize( w, h) {
        JfxUtils.createSize( (double) w, (double) h)
    }
}
