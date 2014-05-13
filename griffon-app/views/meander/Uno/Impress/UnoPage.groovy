/*
 * Copyright 2011 Ed Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package meander.Uno.Impress

import com.sun.star.beans.XPropertySet
import com.sun.star.drawing.XShape
import com.sun.star.awt.Point
import com.sun.star.awt.Size

import meander.Base.Page

import meander.Uno.UnoCategory
import meander.Uno.UnoUtils
import com.sun.star.uno.UnoRuntime

/**
 * Proxy object for an Impress slide
 */
class UnoPage extends Page {

    def pagesSupplier
    def context
    def doc

    UnoPage( named = [:], index = -1) {

        super( named, index)

        vbuffer = 100i
        vbuffer = (254i * 7i)/72i
        convertUnits = 2540i / 72i  // (2.54 cm/in) * (1000 LO units/cm) / (72 pt/in)

        def xDrawPages

        xDrawPages = pagesSupplier.getDrawPages()

        use (UnoCategory) {
            if ( index < 0) {
                // add a new page
                drawPane = xDrawPages.insertNewByIndex( -1)
//                println 'page layout = ' + drawPane.uno(XPropertySet)['Layout']
            } else {
                drawPane = xDrawPages.getByIndex( index)
                drawPane.uno(XPropertySet)['Layout'] = 20  // should be the 'blank' layout
            }

            borderTop = drawPane.uno(XPropertySet)['BorderTop']
            borderRight = drawPane.uno(XPropertySet)['BorderRight']
            borderBottom = drawPane.uno(XPropertySet)['BorderBottom']
            borderLeft = drawPane.uno(XPropertySet)['BorderLeft']

            pageWidth = drawPane.uno(XPropertySet)["Width"]
//            println 'pageWidth = ' + pageWidth + ' 0.1 mm'
//            println 'pageWidth = ' + (int) (pageWidth * 2.834646f / 100) + ' pts'     // convert to 'points'
//            println 'pageWidth = ' + (pageWidth / 2540d) + ' in'     // convert to 'inches'
            pageHeight = drawPane.uno(XPropertySet)["Height"]
//            println 'pageHeight = ' + pageHeight + ' 0.1 mm'
//            println 'pageHeight = ' + (int) (pageHeight * 2.834646f / 100) + ' pts'   // convert to 'points'
//            println 'pageHeight = ' + (pageHeight / 2540d) + ' in'     // convert to 'inches'
//
//            println 'top border = ' + borderTop
//            println 'bottom border = ' + borderBottom
//            println 'left border = ' + borderLeft
//            println 'right border = ' + borderRight

            setupPage()
        }
    }

    public void repositionShapes( shapes, buffer) {
        /* if there is more than one shape in there, we will group them together,
         * move them en masse to the currYpos, and then ungroup them
         *
         * if there's only one, then we can just move that shape
         */
        if ( shapes.getCount() > 1) {
            XShape group
            def shapeList = []

            ( 0 .. shapes.getCount() - 1).each { i ->
                shape = (XShape) UnoRuntime.queryInterface( XShape.class, shapes.getByIndex( i) )
                shapeList.add shape
            }
            group = UnoUtils.buildGroup( pagesSupplier, drawPane, shapeList )
            group.setPosition( new Point( group.getPosition().X, currYpos))
            currYpos += group.getSize().Height + buffer
            drawPane.ungroup( group)
        } else {
            shape
            shape = (XShape) UnoRuntime.queryInterface( XShape.class, shapes.getByIndex( 0) )

            shape.setPosition( new Point( shape.getPosition().X, currYpos))
            currYpos += shape.getSize().Height + buffer
        }
    }

    public addNote( noteObj) {
        def graphic
        graphic = noteObj.draw( drawPane, pagesSupplier, currYpos)
        currYpos += graphic.getSize().Height
        return graphic
    }

    public delete( s) {
        // have to grab the shape out of the wrapper
        UnoUtils.deleteShape( drawPane, s)
    }

    public void copyShapes( s) {
        // copies onto a clipboard
        UnoUtils.copyShapes( doc, context, s)
    }

    public paste() {
        // pastes from a clipboard
        def shape
        shape = UnoUtils.paste( doc, context)
        return shape
    }

    public drawEventObjs( obj) {
        def i = 0
        def graphics
        graphics = obj.draw( drawPane, pagesSupplier, nodes, nodeXPos, currYpos)
        for (gr in graphics) {
            currYpos += gr.Size.Height + vbuffer
            // if the graphic fits on the page remove it from the event list
            if ( currYpos > maxYpos) break // done with graphics on this page
            i++
        }
        return [ graphics, i]
    }

    private enhanceShapes( shape) {
        shape.metaClass.getProperty =  { name ->
            def metaProperty = shape.metaClass.getMetaProperty( name )
            if ( metaProperty ) metaProperty.getProperty( delegate )
            else {
                switch( name) {
                case 'Width':
                    shape.getSize().Width
                    break
                case 'Height':
                    shape.getSize().Height
                    break
                default:
                    break
                }
            }
        }
    }


    public drawTopLeft( str, style) {
        def text
        def size
        text = UnoText.drawCentered( handleString( str),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( xpos, ypos)
                                   )

        size = text.getSize()
        return size
    }

    public drawTopCenter( str, style) {
        def text
        def size
        text = UnoText.drawCentered( handleString( strings."${str}Str"),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( xpos, ypos)
                                   )


        size = text.getSize()
        text.setPosition( new Point( (int)(xpos - size.Width/2), ypos))
        return size
    }

    public drawTopRight( str, style) {
        def text
        def size
        text = UnoText.drawCentered( handleString( strings."${str}Str"),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( xpos, ypos)
                                   )

        size = text.getSize()
        text.setPosition( new Point( xpos - size.Width, ypos))
        return size
    }

    public drawBottomLeft( str, style) {
        def text
        def size
        text = UnoText.drawCentered( handleString( strings."${str}Str"),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( xpos, ypos)
                                   )
        size = text.getSize()
        text.setPosition( new Point( xpos, ypos - size.Height))
        return size
    }

    public drawBottomCenter( str, style) {
        def text
        def size
        text = UnoText.drawCentered( handleString( strings."${str}Str"),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( xpos, ypos)
                                   )
        size = text.getSize()
        text.setPosition( new Point( (int)(xpos - size.Width/2), ypos - size.Height))
        return size
    }

    public drawBottomRight( str, style) {
        def text
        def size
        text = UnoText.drawCentered( handleString( strings."${str}Str"),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( xpos, ypos)
                                   )
        size = text.getSize()
        text.setPosition( new Point( xpos - size.Width, ypos - size.Height))
        return size
    }

    public drawNodes( nodes, style) {
        def nodeList = []
        def rect
        def text
        def hgt = 0

        nodes.eachWithIndex { node, i ->
            xpos = nodeXPos[i]

            rect = UnoUtils.insertBorder( pagesSupplier, drawPane, new Point( xpos, ypos), null, style)

            text = UnoText.drawCentered( node,
                                         drawPane,
                                         pagesSupplier,
                                         style,
                                         new Point( xpos, ypos)
                                       )

            // reposition and resize the border to be around the text
            rect.setSize( text.getSize())
            rect.setPosition( text.getPosition())

            // group the text and the rectangle together
            def group = UnoUtils.buildGroup( pagesSupplier, drawPane, [ rect, text] )
            nodeList << group
        }
        // finally align the bottoms of the nodes
        nodeList.each { n ->
            if( n.getSize().Height > hgt) hgt = n.getSize().Height
        }
        nodeList.each { n ->
            int oldH = n.getSize().Height
            if( oldH < hgt) {
                Point oldP = n.getPosition()
                n.setPosition( new Point( oldP.X, oldP.Y + (hgt - oldH)))
            }
        }

        return hgt
    }

    public drawNodeLines( bottom ) {
        def style
        Size sz = new Size( 0, bottom - minYpos)

        nodeXPos.each { x ->
            def nodeline
            style = styles[ 'node']
            nodeline = UnoUtils.insertLine( pagesSupplier,
                                            drawPane,
                                            new Point( x, minYpos),
                                            sz,
                                            style.border.color.RGB,
                                            (int)(style.border.thickness * 25.4),
                                            style.graphic.line
                                          )
            // push it to the bottom in Z-order
            UnoUtils.setZOrder( nodeline, 0)
        }
    }

    public drawWaterMark() {
        def style
        style = styles[ 'watermark']
        def wm = UnoUtils.insertWatermark( strings."watermarkStr",
                                           pagesSupplier,
                                           drawPane,
                                           new Point( 2540i, 2540i),
                                           new Size( pageWidth - 5080, pageHeight - 5080),
                                           [ color: style.text.color, face: style.text.face]
                                         )
        UnoUtils.setZOrder( wm, 0)
    }

//    public getSize( x) {
//        x.getSize()
//    }

    public drawText(String str, x, y, style) {
        def text
        def size

        text = UnoText.drawCentered( handleString( str),
                                     drawPane,
                                     pagesSupplier,
                                     style,
                                     new Point( x, y)
        )

        size = text.getSize()
        if (maxX < size.Width + leftM) maxX = size.Width + leftM
        ypos += size.Height
        if ( ypos > maxY) maxY = ypos

        return text
    }

}
