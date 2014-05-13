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

import meander.Base.Event

import meander.Uno.*

/**
 * routine for drawing the set of shapes representing a Flow Event in Impress
 */
class UnoImpressEvent extends Event {

    def pagesSupplier

    UnoImpressEvent ( named) {
        super( named)
        adj = (int) (adj * convertUnits)
    }

    def insertBorder( drawPane, point, size, style ) {
        def rect
        rect = UnoUtils.insertBorder( pagesSupplier, drawPane, point, size, style)

        return rect
    }

    def drawGraphic( event, drawPane, style, nodeInfo, gPosInfo, currY, compPosList) {
        def graphic

        graphic = UnoGraphic.draw( event, drawPane, pagesSupplier, style, nodeInfo, gPosInfo, currY, compPosList)
        return graphic
    }

    def drawText( str, drawPane, style, nodeInfo, tPosInfo, currY) {
        def text
        text = UnoText.draw( str,
                             drawPane,
                             pagesSupplier,
                             style,
                             nodeInfo,
                             tPosInfo,
                             currY
                           )
        return text
    }

    def drawPositionedText( str, drawPane, style, ip, wid) {
        def text
        text = UnoText.drawPositioned( str,
                                       drawPane,
                                       pagesSupplier,
                                       style,
                                       ip,
                                       wid)
        return text
    }

    def drawLabel( ip, drawPane) {
        def label
        def rect
        def group

        // store the generated label for future use
        event.label = genLabel()

        rect = insertBorder( drawPane, ip, null, style)
        label = drawPositionedText( event.label,
                                    drawPane,
                                    style,
                                    ip,
                                    null)
//                                        LEFT)
        // reposition and resize the border to be around the text
        rect.Size = label.Size
        rect.Position = label.Position

        // note grouping in OO is order dependent ... the last item will be on top in Z-order
        group = buildGroup( drawPane, [ rect, label] )

        return group
    }

    def buildGroup( drawPane, shapeList) {
        def group

        group = UnoUtils.buildGroup( pagesSupplier, drawPane, shapeList )
        return group
    }

    def createPoint( x, y) {
        UnoUtils.createPoint( (int) x, (int) y)
    }

    def createSize( w, h) {
        UnoUtils.createSize( (int) 2, (int) y)
    }
}
