package meander.Jfx

import javafx.scene.shape.StrokeLineCap
import meander.Base.Page


/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/3/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
class JfxPage extends Page {
    def builder
    def drawPane
    def displayPane
    def rulers
    def buffer

    JfxPage( named = [:], index = -1) {

        super( named, index)

        // create the pane to draw on in the background
        drawPane = builder.with { group() }

        vbuffer = 7.0d/2.54d  // 7 pt buffer

        // set the page border - this corresponds to the gap that the
        // output program leaves for the printer
        switch( buffer) {
        case 'Impress':
            borderTop = 29i
            borderRight = 29i
            borderBottom = 29i
            borderLeft = 29i
            break
        case 'PowerPoint':
            borderTop = 36i
            borderRight = 36i
            borderBottom = 36i
            borderLeft = 36i
            break
        default:        // covers the 'No Buffers' case
            borderTop = 0i
            borderRight = 0i
            borderBottom = 0i
            borderLeft = 0i
        }

        setupPage()
    }

    public drawEventObjs( obj) {
        def i = 0
        def graphics
        graphics = obj.draw( drawPane, builder, nodes, nodeXPos, currYpos)
        for (gr in graphics) {
            JfxUtils.insertShape( gr, builder, drawPane)
            currYpos += gr.Height + vbuffer
            // if the graphic fits on the page remove it from the event list
            if ( currYpos > maxYpos) break // done with graphics on this page
            i++
        }
        return [ graphics, i]
    }

    public drawNoteObj( obj) {
        def graphic

        graphic = obj.draw( drawPane, builder, currYpos)
        JfxUtils.insertShape( graphic, builder, drawPane)
        return graphic
    }

    public void repositionShapes( shapes, buffer) {
        // move each shape upwards
        shapes.each { s ->
            s.Y = currYpos
            currYpos += s.Height + buffer
        }
    }

    public drawNodes( nodes, style) {
        def nodeList = []
        def rect, pRect
        def text, pText
        def group, pGroup
        def back = null
        def hgt = 0
        def borderPad = ( 0.06d * 72.0d)/ 2.54d

        nodes.eachWithIndex { node, i ->
            def npos = nodeXPos[i]

            rect = JfxUtils.createBorder( builder,
                                          JfxUtils.createPoint( npos, ypos),
                                          null,
                                          style)

            text = JfxUtils.createShape( builder,
                                         'Text',
                                         [ text: node,
                                           style: style.text,
                                           alignment: style.alignment,
                                         ],
                                         JfxUtils.createPoint( npos + borderPad, ypos + borderPad)
                                       )
            // slide the text left by the half the width of the text
            text.X -= text.Width/2


            // reposition and resize the border to be around the text
            rect.width = text.Width + 2*borderPad
            rect.height = text.Height + 2*borderPad
            rect.X = text.X - borderPad
//            rect.Y = text.Y - borderPad
 //           rect.y += borderPad //not sure why this is needed

            group = JfxUtils.buildGroup( builder, drawPane, null, [ rect, text] )
            nodeList << group
        }

        // next align the bottoms of the nodes
        // which means going into each group and tweaking the children
        nodeList.each { n ->
            if( n.Height > hgt) hgt = n.Height
        }

        nodeList.each { n ->
            int oldH = n.Height
            if( oldH < hgt) n.Y += hgt - oldH
        }

        // finally insert the nodes
        nodeList.each { n ->
            JfxUtils.insertShape( n, builder, drawPane)
        }

        return hgt
    }

    public drawNodeLines( bottom ) {

        nodeXPos.each { x ->
            def nodeline

            nodeline = JfxUtils.createShape( builder,
                                             'Line',
                                             [ startX: x,
                                               startY: minYpos,
                                               endX: x,
                                               endY: bottom,
                                               style: styles[ 'node'].border,
                                               strokeLineCap: StrokeLineCap.BUTT,
                                               strokeWidth: styles[ 'node'].border.thickness,
                                             ],
                                             null)

            JfxUtils.insertShape( nodeline, builder, drawPane)

            // push it to the bottom in Z-order
            JfxUtils.setZOrder( nodeline, builder, 0)

        }
    }

//    public finalizePage( boolean lastFlowPage) {
//        super.finalizePage( lastFlowPage)
//    }

    public paste( shapes) {
        // paste the
        shapes.each { s ->
            JfxUtils.insertShape( s.shape, builder, drawPane) }
    }

    public delete( s) {
        JfxUtils.deleteShape( drawPane, builder, s)
    }

    public display() {
        builder.defer {
            displayPane.children.clear()
            displayPane.children.add drawPane
        }
    }

    public drawTopLeft( str, style) {
        def text
        def size

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: handleString( str),
                                       style: style.text,
                                       alignment: style.alignment,
                                     ],
                                     JfxUtils.createPoint( xpos, ypos)
                                   )

        // this does the JavaFX Platform defer
        JfxUtils.insertShape( text, builder, drawPane)

        return text
    }

    public drawTopCenter( str, style) {
        def text
        def size

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: handleString( strings."${str}Str"),
                                       style: style.text,
                                       alignment: style.alignment,
                                     ],
                                     JfxUtils.createPoint( xpos, ypos)
                                   )

        text.x -= text.Width/2

        // this does the JavaFX Platform defer
        JfxUtils.insertShape( text, builder, drawPane)

        return text
    }

    public drawTopRight( str, style) {
        def text
        def size

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: handleString( strings."${str}Str"),
                                       style: style.text,
                                       alignment: style.alignment,
                                     ],
                                     JfxUtils.createPoint( xpos, ypos)
                                   )

        text.x -= text.Width

        // this does the JavaFX Platform defer
        JfxUtils.insertShape( text, builder, drawPane)

        return text
    }

    public drawBottomLeft( str, style) {
        def text
        def size

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: handleString( strings."${str}Str"),
                                       style: style.text,
                                       alignment: style.alignment,
                                     ],
                                     JfxUtils.createPoint( xpos, ypos)
                                   )

        text.Y = ypos - text.Height

        // this does the JavaFX Platform defer
        JfxUtils.insertShape( text, builder, drawPane)

        return text
    }

    public drawBottomCenter( str, style) {
        def text
        def size

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: handleString( strings."${str}Str"),
                                       style: style.text,
                                       alignment: style.alignment,
                                     ],
                                     JfxUtils.createPoint( xpos, ypos)
                                   )

        text.X -= text.Width/2
        text.Y = ypos - text.Height
        // this does the JavaFX Platform defer
        JfxUtils.insertShape( text, builder, drawPane)

        return text
    }

    public drawBottomRight( str, style) {
        def text

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: handleString( strings."${str}Str"),
                                       style: style.text,
                                       alignment: style.alignment,
                                     ],
                                     JfxUtils.createPoint( xpos, ypos)
                                   )

        text.X -= text.Width
        text.Y = ypos - text.Height

        // this does the JavaFX Platform defer
        JfxUtils.insertShape( text, builder, drawPane)

        return text
    }
}
