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
package meander.Com.PowerPoint

import meander.PageType
import com.sun.star.awt.Point
import com.sun.star.awt.Size

import static meander.PagePlacement.*

import org.codehaus.groovy.scriptom.tlb.office2007.powerpoint.PpSlideLayout
import org.codehaus.groovy.scriptom.tlb.office2007.MsoZOrderCmd
import org.codehaus.groovy.scriptom.tlb.office2007.MsoTriState
import org.codehaus.groovy.scriptom.tlb.office2007.MsoPresetTextEffect
import org.codehaus.groovy.scriptom.tlb.office2007.MsoPresetTextEffectShape
import org.codehaus.groovy.scriptom.Scriptom
import org.codehaus.groovy.scriptom.SafeArray

/**
 * PowerPoint slide proxy object
 */
class ComPPTPage {
    def events
    def nodes
    def styles
    def strings
    def options
    def pageModel

    def doc
    def page

    def shape
    int pageWidth, pageHeight, nShape
    int topM, rightM, bottomM, leftM

    private int borderTop, borderRight, borderBottom, borderLeft
    int nodeWidth
    def nodeXPos = []
    int vbuffer = 7i

    int xpos = 0, ypos = 0, currYpos = 0, maxYpos, minYpos

    ComPPTPage( named = [:], index = -1) {

        named.each { key, value -> this[ key] = value }

        // add a page at the current PC count - assumes that the caller will have incremented the counter
        // prior to this call
        page = doc.Slides.Add( strings.counts[ 'PC'], PpSlideLayout.ppLayoutBlank)
  //      if ( index < 0) {
  //          // add a new page
  //          page = xDrawPages.insertNewByIndex( -1)
  //      } else {
  //          page = xDrawPages.getByIndex( index)
  //      }

  //      doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeLetterPaper

        pageWidth = doc.PageSetup.SlideWidth
        pageHeight = doc.PageSetup.SlideHeight
        PageType.setDefaults( (Float) pageWidth/72, (Float) pageHeight/72, 'in', 72i)

        borderTop = 0i               // page.BorderTop???
        borderRight = 0i             // page.BorderRight???
        borderBottom = 0i            // page.BorderBottom???
        borderLeft = 0i              // page.BorderLeft???
    }


    public setupPage() {
        def rect, text
        def style
        def size, maxX, maxY
        def nodeList = []
        int headerH      // height of the header

        // get the user margins and add in the page borders
        topM = (int)(pageModel.top * 72i) + borderTop
        rightM = (int)(pageModel.right * 72i) + borderRight
        bottomM = (int)(pageModel.bottom * 72i) + borderBottom
        leftM = (int)(pageModel.left * 72i) + borderLeft

        // look for items to put at the top of the page
        // Note, more than one item could be placed at the left, right or center.  If so, they need to be 'stacked'

        // top left
        ypos = topM
        xpos = leftM
        maxX = 0
        maxY = 0
        strings.strings.each { str ->
            if ( (strings."$str") &&
                 (strings."${str}Str" != '') &&
                 (strings."${str}Pos" == TOPLEFT)) {
//                def ts
//                if ( str == 'date') ts = handleDateStr( strings."${str}Str")
//                else ts =
                style = styles[ strings."${str}Style"]
                Scriptom.inApartment {
                    text = ComText.drawCentered( handleString( strings."${str}Str"),
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos)
                                               )
                    if ( maxX < text.Width + leftM) maxX = text.Width + leftM
                    ypos += text.Height               // shifting ypos causes the stacking - if needed
                }
                if (maxY < ypos) maxY = ypos
            }
        }

        // top right
        ypos = topM                               // gotta reset ypos because of the stacking handling
        xpos = pageWidth - rightM
        strings.strings.each { str ->
            if ( (strings."$str") &&
                 (strings."${str}Str" != '') &&
                 (strings."${str}Pos" == TOPRIGHT)) {
                style = styles[ strings."${str}Style"]
                Scriptom.inApartment {
                    text = ComText.drawCentered( handleString( strings."${str}Str"),
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos)
                                               )
                    text.Left = xpos - text.Width
                    if ( text.Width + rightM > maxX) maxX = text.Width + rightM
                    if ( text.Height > maxY) maxY = text.Height
                    ypos += text.Height               // shifting ypos causes the stacking - if needed
                }
                if (maxY < ypos) maxY = ypos
            }
        }

        // top center
        ypos = topM                               // gotta reset ypos because of the stacking handling
        xpos = (int)( (pageWidth - leftM - rightM)/2 + leftM)
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == TOPCENTER) &&
                 (strings."${str}Str" != '') &&
                 (strings."$str")) {
                style = styles[ strings."${str}Style"]
                Scriptom.inApartment {
                    text = ComText.drawCentered( handleString( strings."${str}Str"),
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos)
                                               )
                    text.Left = xpos - text.Width/2
                    if ( text.Height > maxY) maxY = text.Height
                    ypos += text.Height               // shifting ypos causes the stacking - if needed
                }
                if (maxY < ypos) maxY = ypos
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
                style = styles[ strings."${str}Style"]
                Scriptom.inApartment {
                    text = ComText.drawCentered( handleString( strings."${str}Str"),
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos)
                                               )
                    maxX = text.Width + leftM
                    text.Top = ypos - text.Height
                    ypos = ypos - text.Height     // shifting ypos causes the stacking - if needed
                }
                if ( ypos < maxY) maxY = ypos
            }
        }

        // bottom right
        ypos = pageHeight - bottomM           // because stacking will have shifted ypos, got to reset it
        xpos = pageWidth - rightM
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == BOTTOMRIGHT) &&
                 (strings."${str}Str" != '') &&
                 (strings."$str")) {
                style = styles[ strings."${str}Style"]
                Scriptom.inApartment {
                    text = ComText.drawCentered( handleString( strings."${str}Str"),
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos)
                                               )
                    if ( text.Width + rightM > maxX) maxX = text.Width + rightM
                    text.Left = xpos - text.Width
                    text.Top = ypos - text.Height
                    ypos = ypos - text.Height
                }
                if ( ypos < maxY) maxY = ypos
            }
        }

        // bottom center
        ypos = pageHeight - bottomM
        xpos = (int)( (pageWidth - leftM - rightM)/2 + leftM)
        strings.strings.each { str ->
            if ( (strings."${str}Pos" == BOTTOMCENTER) &&
               (strings."${str}Str" != '') &&
               (strings."$str")) {
                style = styles[ strings."${str}Style"]
                Scriptom.inApartment {
                    text = ComText.drawCentered( handleString( strings."${str}Str"),
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos)
                                               )
                    text.Left = xpos - text.Width/2
                    text.Top = ypos - text.Height
                    ypos = ypos - text.Height
                }
                if ( ypos < maxY) maxY = ypos
            }
        }
        maxYpos = (maxY < ypos) ? maxY : ypos

//        if ( maxY < ypos) maxYpos = maxY
//        else maxYpos = ypos

        // start in on the nodes - if the node list is not null/empty
        int hgt = 0
        if (nodes?.size()) {
            ypos = currYpos
            nodeWidth = (pageWidth - leftM - rightM)  / nodes.size()
            style = styles[ 'node']

            // calculate the x position for each node
            nodes.eachWithIndex { node, i ->
                nodeXPos[i] = (int)((nodeWidth * i) + (nodeWidth / 2) + leftM)
            }

            // draw the nodes
            Scriptom.inApartment {
                nodes.eachWithIndex { node, i ->
                    xpos = nodeXPos[i]

                    rect = ComPPTUtils.insertBorder( page, new Point( xpos, ypos), new Size( 10i, 10i), style)
                    rect.name = 'slide' + doc.Slides.Count + '-' + 'nodeRect' + i

                    text = ComText.drawCentered( node,
                                                 page,
                                                 style,
                                                 new Point( xpos, ypos + 1i)
                                               )
                    text.Left = xpos - text.Width/2
                    text.name = 'slide' + doc.Slides.Count + '-' + 'nodeText' + i

                    // reposition and resize the border to be around the text
                    rect.Width = text.Width + 2i
                    rect.Left = text.Left - 1i
                    rect.Height = text.Height + 2i

                    // group the text and the rectangle together
                    def nameList = new SafeArray( (String[])[ rect.name, text.name])
                    def group = page.Shapes.Range( nameList).Group()
                    nodeList << group
                }
                // finally align the bottoms of the nodes
                nodeList.each { n ->
                    if( n.Height > hgt) hgt = n.Height
                }
                nodeList.each { n ->
                    int nH = n.Height
                    if( nH < hgt) {
                        n.Top = n.Top + (hgt - nH)
                    }
                }
            }
        }

        // position the insertion point for the rest of the flow and save this point for
        // use when finalizing the page
        minYpos = currYpos + hgt
        currYpos += hgt + vbuffer
    }

    public finalizePage( int effect) {
        def style

        style = styles[ 'watermark']
//        def wm = ComText.drawCentered( 'DRaFT',
//                                             page,
//                                             style,
//                                             new Point( 144, 144)
//                                           )
        Scriptom.inApartment {
            def wm = page.Shapes.AddTextEffect( effect,
                                                "DRaFT",
                                                style.text.face,
                                                style.text.size,
                                                MsoTriState.msoFalse,
                                                MsoTriState.msoFalse,
                                                75, 75)

            wm.ZOrder( MsoZOrderCmd.msoSendToBack)
            wm.Line.Visible = MsoTriState.msoFalse
            wm.fill.Visible = MsoTriState.msoFalse

    //        wm.fill.Visible = MsoTriState.msoTrue
    //        wm.fill.ForeColor.RGB = style.text.color.RGB
    //        wm.fill.Transparency = 0.5
    //        wm.fill.Solid()

            wm.TextEffect.PresetShape = MsoPresetTextEffectShape.msoTextEffectShapeSlantUp
            wm.TextFrame2.TextRange.Font.Fill.Transparency = 0.5
            wm.TextFrame2.TextRange.Font.Fill.ForeColor.RGB = style.text.color.getBlue()*256*256 +
                                                              style.text.color.getGreen()*256 +
                                                              style.text.color.getRed()
            wm.TextFrame2.TextRange.Font.Fill.Solid()
            wm.TextFrame2.TextRange.Font.Line.Visible = MsoTriState.msoFalse


            wm.Width = pageWidth - 144
            wm.Height = pageHeight - 144
        }
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
        nodeXPos.each { x ->
            def nodeline
            style = styles[ 'node']

            Scriptom.inApartment {
                nodeline = page.Shapes.AddLine( x, minYpos, x, bottom)
                nodeline.Line.ForeColor.RGB = style.border.color.getBlue()*256*256 +
                                              style.border.color.getGreen()*256 +
                                              style.border.color.getRed()
                nodeline.Line.Weight = style.border.thickness
                nodeline.ZOrder( MsoZOrderCmd.msoSendToBack)
            }
        }

        // add any required watermark
        if ( (strings."watermark") &&
             (strings."watermarkStr" != '')) {
            style = styles[ 'watermark']
            Scriptom.inApartment {
                def wm = page.Shapes.AddTextEffect( MsoPresetTextEffect.msoTextEffect1,
                                                    strings."watermarkStr",
                                                    style.text.face,
                                                    style.text.size,
                                                    MsoTriState.msoFalse,
                                                    MsoTriState.msoFalse,
                                                    75, 75)
                wm.ZOrder( MsoZOrderCmd.msoSendToBack)
                wm.Line.Visible = MsoTriState.msoFalse
                wm.fill.Visible = MsoTriState.msoFalse
    //            wm.fill.Solid()
    //            wm.fill.ForeColor.RGB = style.text.color.RGB
    //            wm.fill.Transparency = 0.5

                wm.TextEffect.PresetShape = MsoPresetTextEffectShape.msoTextEffectShapeSlantUp
                wm.TextFrame2.TextRange.Font.Fill.Transparency = 0.5
                wm.TextFrame2.TextRange.Font.Fill.ForeColor.RGB = style.text.color.getBlue()*256*256 +
                                                                  style.text.color.getGreen()*256 +
                                                                  style.text.color.getRed()
                wm.TextFrame2.TextRange.Font.Fill.Solid()
                wm.TextFrame2.TextRange.Font.Line.Visible = MsoTriState.msoFalse

                wm.Width = pageWidth - 144
                wm.Height = pageHeight - 144
            }
        }
    }

    public addEvent( eventObj) {
//        def graphic
//        graphic = eventObj.draw( page, pagesSupplier, nodes, nodeXPos, currYpos)
//        currYpos += graphic.getSize().Height + vbuffer
//        return [ graphic, currYpos]
        def graphics
        def i = 0
        Scriptom.inApartment {
            graphics = eventObj.draw( page, nodes, nodeXPos, currYpos)
            for (gr in graphics) {
                // if the graphic doesn't fit on the page stop processing the list of graphics
                currYpos += gr.Height + vbuffer
                if ( currYpos + gr.Height + vbuffer > maxYpos) break
                // incrementing i will cause this shape to removed from the returned list of shapes
                i++
            }
        }

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
        graphic = noteObj.draw( page, currYpos)
        currYpos += graphic.Height
        return ( currYpos > maxYpos) ? [ graphic ] : []
    }

    public void copyShapes( shapes) {
        def nameList = []
        def nameArray
        // group the shapes
        shapes.each { nameList << it.name }
        nameArray = new SafeArray( (String[]) nameList)
        page.Shapes.Range( nameArray).Copy()
    }

    public paste() {
        // paste what is on the clipboard
        return page.Shapes.Paste()
       // return page.application.ActiveWindow.Selection.ShapeRange
    }

    public deleteShapes( shapes) {
        def nameList = []
        def nameArray
        // group the shapes
        shapes.each { nameList << it.name }
        nameArray = new SafeArray( (String[]) nameList)
        page.Shapes.Range( nameArray).Delete()
    }

    private strText = { full,f, str, l ->
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


}
