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
package meander.Base

//import org.codehaus.groovy.scriptom.tlb.office.powerpoint.PpSlideLayout
//import meander.PageType
////import meander.Com.PowerPoint.ComPPTUtils
//import com.sun.star.awt.Size
//import org.codehaus.groovy.scriptom.SafeArray
//
//import com.sun.star.awt.Point

import static meander.PagePlacement.*
//import org.codehaus.groovy.scriptom.tlb.office.powerpoint.PpSlideSizeType
//import org.codehaus.groovy.scriptom.tlb.office.MsoZOrderCmd
//import org.codehaus.groovy.scriptom.tlb.office.MsoTriState
//import org.codehaus.groovy.scriptom.tlb.office.MsoPresetTextEffect
//import org.fusesource.jansi.Ansi.Color
//import org.codehaus.groovy.scriptom.tlb.office.MsoPresetTextEffectShape
//import org.codehaus.groovy.scriptom.Scriptom

/**
 * Base page object
 */
class Page {
    def events
    def nodes
    def styles
    def strings
    def options
    def pageModel

    def doc
    def drawPane
    def convertUnits  // convert pageModel units (pts) to base units of output

    def shape
    int pageWidth, pageHeight, nShape
    int topM, rightM, bottomM, leftM

    int maxX, maxY

    int borderTop, borderRight, borderBottom, borderLeft
    int nodeWidth
    def nodeXPos = []
    int vbuffer

    int xpos = 0, ypos = 0, currYpos = 0, maxYpos, minYpos

    Page( named = [:], index = -1) {
        // pull in the named parameters
        named.each { key, value -> this[ key] = value }

        borderTop = 0i               // page.BorderTop???
        borderRight = 0i             // page.BorderRight???
        borderBottom = 0i            // page.BorderBottom???
        borderLeft = 0i              // page.BorderLeft???

        convertUnits = 1i            // working in pts

        pageWidth = pageModel.xpts
        pageHeight = pageModel.ypts
    }


    public setupPage() {
        def rect, text
        def size, maxX, maxY
        int headerH      // height of the header

        // get the user margins and add in the page borders
        topM = (int)(pageModel.top * convertUnits) + borderTop
        rightM = (int)(pageModel.right * convertUnits) + borderRight
        bottomM = (int)(pageModel.bottom * convertUnits) + borderBottom
        leftM = (int)(pageModel.left * convertUnits) + borderLeft

        // look for items to put at the top of the page
        // Note, more than one item could be placed at the left, right or center.  If so, they need to be 'stacked'

        // top left
        ypos = topM
        xpos = leftM

        maxX = leftM
        maxY = topM
        strings.strings.each { str ->
            if ( (strings."$str") &&
                 (strings."${str}Str" != '') &&
                 (strings."${str}Pos" == TOPLEFT)) {
//                def ts
//                if ( str == 'date') ts = handleDateStr( strings."${str}Str")
//                else ts =
                threadIt {
                    text = drawTopLeft( str, styles[ strings."${str}Style"])

                    if (maxX < text.Width + leftM) maxX = text.Width + leftM
                    ypos += text.Height
                    if ( ypos > maxY) maxY = ypos
                }
            }
        }

        // top right
        ypos = topM                               // gotta reset ypos because of the stacking handling
        xpos = pageWidth - rightM
        strings.strings.each { str ->
            if ( (strings."$str") &&
                 (strings."${str}Str" != '') &&
                 (strings."${str}Pos" == TOPRIGHT)) {
                threadIt {
                    text = drawTopRight( str, styles[ strings."${str}Style"])

                    ypos += text.Height
                    if ( ypos > maxY) maxY = ypos
                }
            }
        }

        // top center
        ypos = topM                               // gotta reset ypos because of the stacking handling
        xpos = (int)( (pageWidth - leftM - rightM)/2 + leftM)
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == TOPCENTER) &&
                 (strings."${str}Str" != '') &&
                 (strings."$str")) {
                threadIt {
                    text = drawTopCenter( str, styles[ strings."${str}Style"])

                    if ( text.Width + rightM > maxX) maxX = text.Width + rightM
                    ypos += text.Height
                    if ( ypos > maxY) maxY = ypos
                }
            }
        }
        currYpos = maxY

        // look for items to put at the bottom of the page
        // Note, more than one item could be placed at the left, right or center.  If so, they need to be 'stacked'

        // bottom left
        ypos = pageHeight - bottomM
        maxYpos = ypos
        xpos = leftM
        maxX = 0
        maxY = ypos
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == BOTTOMLEFT) &&
                 (strings."${str}Str" != '') &&
                 (strings."$str")) {
                threadIt {
                    text = drawBottomLeft( str, styles[ strings."${str}Style"])

                    if ( maxX < text.Width + leftM ) maxX = text.Width + leftM
                    ypos = ypos - text.Height
                    if ( ypos < maxY) maxY = ypos
                }
            }
        }

        // bottom right
        ypos = pageHeight - bottomM           // because stacking will have shifted ypos, got to reset it
        xpos = pageWidth - rightM
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == BOTTOMRIGHT) &&
                 (strings."${str}Str" != '') &&
                 (strings."$str")) {
                threadIt {
                    text = drawBottomRight( str, styles[ strings."${str}Style"])

                    if ( text.Width + rightM > maxX) maxX = text.Width + rightM
                    ypos = ypos - text.Height
                    if ( ypos < maxY) maxY = ypos
                }
            }
        }

        // bottom center
        ypos = pageHeight - bottomM
        xpos = (int)( (pageWidth - leftM - rightM)/2 + leftM)
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == BOTTOMCENTER) &&
               (strings."${str}Str" != '') &&
               (strings."$str")) {
                threadIt {
                    text = drawBottomCenter( str, styles[ strings."${str}Style"])

                    ypos = ypos - text.Height
                    if ( ypos < maxY) maxY = ypos
                }
            }
        }
        maxYpos = (maxY < ypos) ? maxY : ypos

//        if ( maxY < ypos) maxYpos = maxY
//        else maxYpos = ypos


        // start in on the nodes - if the node list is not null/empty
        int hgt = 0
        if (nodes?.size()) {
            def style
            ypos = currYpos
            nodeWidth = (pageWidth - leftM - rightM)  / nodes.size()
            style = styles[ 'node']

            // calculate the x position for each node
            nodes.eachWithIndex { node, i ->
                nodeXPos[i] = (int)((nodeWidth * i) + (nodeWidth / 2) + leftM)
            }

            // draw the nodes
            hgt = drawNodes( nodes, style)
        }

        // position the insertion point for the rest of the flow and save this point for
        // use when finalizing the page
        minYpos = currYpos + hgt
        currYpos += hgt + vbuffer
    }

    public finalizePage( boolean lastFlowPage) {
        def style
        /*
         * if the node list is null, then this page doesn't have any events
         * drawn on it - it is a post flow page
         *
         * If the node list has content, then use it to fill nodal lines
         *
         * Also, if the lastFlowPage parameter is true, then the lines only go to the bottom of
         * the last event instead of to the bottom of the page
         */


        // put in the nodal lines using the minYpos value saved during page setup
        int bottom = lastFlowPage ? currYpos : maxYpos
        drawNodeLines( bottom)

        // add any required watermark
        if ( (strings."watermark") &&
             (strings."watermarkStr" != '')) {
            drawWaterMark()
        }
    }

    public addEvent( eventObj) {
        def graphics
        def i

        (graphics, i) =  drawEventObjs( eventObj)

        // return the graphics list starting after the last successfully place graphic
        // it will be null if everything was placed
        return (i >= graphics.size()) ? [] : graphics[ i .. -1]
    }

    public void repositionShapes( shapes, buffer) {
        // just move the ShapeRange to the current position
        shapes.Top = currYpos
        currYpos += shapes.Height + buffer
    }

    public addNote( noteObj) {
        def graphic
        threadIt {
            graphic = drawNoteObj( noteObj)
            currYpos += graphic.Height
        }
        return ( currYpos > maxYpos) ? graphic : null
    }

    public void copyShape( shape) { }

    public paste() { }

    public deleteShapes( shapes) {
//        def nameList = []
//        def nameArray
//        // group the shapes
//        threadIt {
//            shapes.each { nameList << it.name }
//            nameArray = new SafeArray( (String[]) nameList)
//            drawPane.Shapes.Range( nameArray).Delete()
//        }
    }

    public strText = { full,f, str, l ->
        switch (str) {
        case 'PC':
        case 'FPC':
        case 'today':
        case 'now':
            return '' + f + strings.counts[ str] + l
            break
        }
        return '' + f + strings.counts[ 'raw'].format( str) + l
    }

    private extractRE = /(?m)(.*)<(.*)>(.*)/           // multi-line pattern looking for something in "< >"

    public handleString (String str) {
        while ( str =~ extractRE) {
            str = str.replaceAll( extractRE, strText)
        }
        return str
    }

    public threadIt( Closure c) { c() }

    public drawEventObjs( obj) { }

    public drawNoteObj( obj) { }

    public drawTopLeft( str, style) { }

    public drawTopCenter( str, style) { }

    public drawTopRight( str, style) { }

    public drawBottomLeft( str, style) { }

    public drawBottomCenter( str, style) { }

    public drawBottomRight( str, style) { }

    public drawNodes( nodes, style) { }

    public drawNodeLines( bottom) {}

    public drawWaterMark() {}

//    public getSize( x) { }
//
//    public drawText(String str, x, y, style) { }
//
//    public drawBorder( x, y, width, height, style) { }
//
//    public drawLine( x, y, width, height, style) { }
//
//    public groupItems( itemList) { }

    public display() { }

}
