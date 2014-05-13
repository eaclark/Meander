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

import static meander.PlacementType.*
import static meander.LabelType.*
import static meander.GraphicType.*
import meander.Style
import meander.FlowEvent
import com.sun.star.awt.Point
import org.codehaus.groovy.scriptom.SafeArray
import com.sun.star.awt.Size
import org.codehaus.groovy.scriptom.tlb.office2007.MsoZOrderCmd
import org.codehaus.groovy.scriptom.Scriptom

/**
 * PowerPoint shape that represents a Flow Event
 */
class ComPPTEvent {
    FlowEvent event
    Style style
    def options
    def counts
    def leftM
    def rightM


    public draw( page, nodes, nodeXPos, currY) {
        Point ip
        int adj = 2i
        def gPosInfo = [:]
        def tPosInfo = [:]
        def nodeInfo = [:]
        def compList = []
        def compPosList = []
        def strs
        def rect
        def label, text1, text2
        def graphic, gh
        def group1, group2
        def trim, tl
        def terselen

        // need to call out 'bearerpath' separately because it can have multiple
        // destinations.  All other event types only have one
        if ( event.command == 'bearerpath') {
            // ok we're going to have to go through the set of nodes in both the event source
            // and event destination to find the leftmost one and the rightmost one
            def temp
            event.source?.split(',')?.each { compList.add it.trim() }
            event.destination?.split(',')?.each { compList.add it.trim() }
            nodeInfo.src = nodeXPos.size() - 1                              // just pick the last index as a seed
            nodeInfo.dest = 0                                               // just pick the first index as a seed
            compList.each { comp ->
                temp = nodes.findIndexOf { it == comp }
                compPosList << nodeXPos[ temp]                                 // save for later use
                if ( nodeInfo.src > temp) nodeInfo.src = temp
                if ( nodeInfo.dest < temp) nodeInfo.dest = temp
            }
        } else {
            if ( event.source != null &&
                 event.source != '') nodeInfo.src = nodes.findIndexOf { it == event.source.split(',')[0]}
            else nodeInfo.src = null
            if ( event.destination != null &&
                 event.destination != '') nodeInfo.dest = nodes.findIndexOf { it == event.destination.split(',')[0]}
            else nodeInfo.dest = null
        }


        // get the source's position in the node list and use
        // that to index into the list of x positions

        // determine the insertion point and the horizontal adjust
        nodeInfo.width = nodeXPos[1] - nodeXPos[0]
        switch ( style.placement) {
        case SRCJUST:
            // probably need an audit earlier in the process to verify that the event has
            // a valid source and destination node - either being empty just doesn't
            // make sense for SRCJUST, DESTJUST, OVERLAP, or SPAN
            gPosInfo.x1 = nodeXPos[ nodeInfo.src]
            tPosInfo.x = gPosInfo.x1
            gPosInfo.x2 = nodeXPos[ nodeInfo.dest]
            tPosInfo.width = 0i   // there is no predetermined width

            // tweak the text position to avoid the nodal line
            tPosInfo.x += ( gPosInfo.x1 < gPosInfo.x2) ? adj : -adj
            break
        case DESTJUST:
            // probably need an audit earlier in the process to verify that the event has
            // a valid source and destination node - either being empty just doesn't
            // make sense for SRCJUST, DESTJUST, OVERLAP, or SPAN
            gPosInfo.x1 = nodeXPos[ nodeInfo.dest]
            tPosInfo.x = gPosInfo.x1
            gPosInfo.x2 = nodeXPos[ nodeInfo.src]
            tPosInfo.width = 0i   // there is no predetermined width

            // tweak the text position to avoid the nodal line
            tPosInfo.x += ( gPosInfo.x1 < gPosInfo.x2) ? adj : -adj
            break
        case MIDDLE:
            // now things are trickier - the destination might be a number representing a
            // width rather than the name of a node
            //
            // likewise, the source might be null, which indicates that this relates to
            // the whole flow and not to a specific node
            if (event.source == null || event.source == '') {
                nodeInfo.src = null
                gPosInfo.x1 = (int) (leftM + (rightM - leftM)/2)
            } else {
                gPosInfo.x1 = nodeXPos[ nodeInfo.src]
            }
            switch (event.destination) {
            case null:
            case '':
            case '0':
            case '1':
                nodeInfo.dest = null
                gPosInfo.x2 = null

                tPosInfo.x = gPosInfo.x1
                tPosInfo.width = (int)(nodeInfo.width - 2*adj)
                break
            case '2' .. '9':
            case '10' .. '19':
                nodeInfo.dest = null
                gPosInfo.x2 = null

                int i = event.destination.toInteger()
                tPosInfo.x = gPosInfo.x1
                tPosInfo.width = (int)((nodeInfo.width * Math.min( nodes.size(), i )) - 2*adj)
                break
            default:
                // at this point let's assume it is a nodal name
                // in this case, the position is half way between the two nodes and the
                // width is the distance between them
                gPosInfo.x2 = nodeXPos[ nodeInfo.dest]

                // note, for MIDDLE between two nodes, the width of the event can go past the
                // two nodal lines, so no adjust is required
                tPosInfo.width = (int) Math.abs( gPosInfo.x1 - gPosInfo.x2)
                tPosInfo.x = (int)( Math.min( gPosInfo.x1, gPosInfo.x2) + tPosInfo.width/2)
            }
            break
        case OVERLAP:
            // probably need an audit earlier in the process to verify that the event has
            // a valid source and destination node - either being empty just doesn't
            // make sense for SRCJUST, DESTJUST, OVERLAP, or SPAN
            gPosInfo.x1 = nodeXPos[ nodeInfo.dest]
            gPosInfo.x2 = nodeXPos[ nodeInfo.src]

            // determine the distance between nodes and find the midpoint
            tPosInfo.width = (int) Math.abs( gPosInfo.x1 - gPosInfo.x2)
            tPosInfo.x = (int)( Math.min( gPosInfo.x1, gPosInfo.x2) + tPosInfo.width/2)
            // now widen the span to create the overlap
            tPosInfo.width = (int)( tPosInfo.width + nodeInfo.width/2)
            break
        case SPAN:
            // probably need an audit earlier in the process to verify that the event has
            // a valid source and destination node - either being empty just doesn't
            // make sense for SRCJUST, DESTJUST, OVERLAP, or SPAN
            gPosInfo.x1 = nodeXPos[ nodeInfo.dest]
            gPosInfo.x2 = nodeXPos[ nodeInfo.src]

            // determine the distance between nodes and find the midpoint
            tPosInfo.width = (int) Math.abs( gPosInfo.x1 - gPosInfo.x2)
            tPosInfo.x = (int)( Math.min( gPosInfo.x1, gPosInfo.x2) + tPosInfo.width/2)
            // now adjust the span so the text won't overlap the nodal lines
            tPosInfo.width = (int)( tPosInfo.width - 2*adj)
            break
        }

        // build the label if required
        // determine the insertion point and the horizontal adjust
        switch ( style.label.labelType) {
        case EMBEDDED:
//            ip = null
//            label = drawLabel( ip, page, pagesSupplier)
            // no label actually drawn, the label text is embedded in the event string
            event.label = genLabel()
            break
        case LEFTSIDE:
 //           ip = new Point( xpos2, currY)
 //           drawLabel( ip, page, pagesSupplier)
            break
        case PREPEND:
            ip = new Point( tPosInfo.x, currY)
            event.label = genLabel()
            label = drawLabel( ip, page)
            // if the label is prepended and the text is placed next to the source or destination,
            // then we've got to shift the x1 point for text
            if( style.placement == SRCJUST || style.placement == DESTJUST ) tPosInfo.x += label.Width
            break
        case RIGHTSIDE:
 //           ip = new Point( Math.abs( xpos1 - xpos2), currY)
 //           label = drawLabel( ip, page, pagesSupplier)
            break
//        case NOLABEL:
//        default:
//            label = null
//            break
        }

        // now start to work on the main event
        rect = ComPPTUtils.insertBorder( page, new Point( (int)tPosInfo.x, currY), new Size( tPosInfo.width, 10), style)

        // there can be a global trim option and a per style trim option
        if (options.trim && ((style.trimLen > 0) || (options.trimLen > 0) )) {
            // pick the smaller of the the two trimLens that are greater than 0
            if (style.trimLen == 0) tl = options.trimLen
            else if ( options.trimLen == 0) tl = style.trimLen
            else tl = (style.trimLen > options.trimLen) ? options.trimLen : style.trimLen
            // set up the closure to trim only if the string is longer than the trimLen
            trim = { if ( it.size() > tl ) it[ 0 .. tl-1] + '...'
                     else it }
        } else trim = { it }   // no trimming, just return the string

        // same with a global terse option
        terselen = 0
        if ((style.terseLen > 0) || (options.terseLen > 0) ) {
            // pick the smaller of the the two trimLens that are greater than 0
            if (style.terseLen == 0) terselen = options.terseLen
            else if ( options.terseLen == 0) terselen = style.terseLen
            else terselen = (style.terseLen > options.terseLen) ? options.terseLen : style.terseLen
        }

        strs = event.data.split( '\\r|\\n')

        // if we're in terse mode, only use the number of terse lines (if the event is longer)
        if( options.terse && (terselen > 0) && (terselen < strs.size())) {
            strs = strs[ 0 .. terselen-1]//.collect { it }
        }

        // if the label is embedded, now's the time to do it
        if (style.label.labelType == EMBEDDED) strs[0] = event.label + strs[0]

        // there are three ways to handle lines and graphic
        // - if the 'linesAbove' value is negative, then all the text is below the graphic
        // - if the 'linesAbove' value is zero, then all the text is above the graphic
        // - if the 'linesAbove' value is positive, then only the first linesAbove lines of text are above the graphic
        //
        if ( style.linesAbove < 0) {
            // graphics (if requested) first
            if( style.graphic.graphic != NOGRAPHIC) {
                graphic = ComPPTGraphic.draw( event, page, style, nodeInfo, gPosInfo, currY, compPosList)
                currY += graphic.Height + 6

                // if the label was drawn, gotta move it down
                if( label) {
                    label.Y = currY
                }
            }

            // place the first part of the text
            text1 = ComText.draw( strs[ 0 .. style.linesAbove-1].collect( trim).join( '\n'),
                                  page,
                                  style,
                                  nodeInfo,
                                  tPosInfo,
                                  currY
                                )
            currY += text1.Height

            // the text needs to be centered for MIDDLE, SPAN, and OVERLAP
            if ( style.placement == MIDDLE || style.placement == SPAN || style.placement == OVERLAP) {
                text1.Left = tPosInfo.x - text1.Width/2
            }

            // before doing the graphic we need to check if the position of the label needs
            // to be tweaked - if the text placement is 'MIDDLE" and the label is prepended
            if ( style.placement == MIDDLE && style.label.labelType == PREPEND) {
                label.Left = text1.Left - label.Width
            }

        } else {
            // text first - start with the first part of the text
            if( (style.linesAbove > 0) && (style.linesAbove < strs.size())) {
            // text first - start with the first part of the text
                text1 = ComText.draw( strs[ 0 .. style.linesAbove-1].collect( trim).join( '\n'),
                                      page,
                                      style,
                                      nodeInfo,
                                      tPosInfo,
                                      currY
                                    )
            } else {
                // either linesAbove == 0 or the event is short - use all the lines
                text1 = ComText.draw( strs.collect( trim).join( '\n'),
                                      page,
                                      style,
                                      nodeInfo,
                                      tPosInfo,
                                      currY
                                    )
            }
            currY += text1.Height

            // the text needs to be centered for MIDDLE, SPAN, and OVERLAP
            if ( style.placement == MIDDLE || style.placement == SPAN || style.placement == OVERLAP) {
                text1.Left = tPosInfo.x - text1.Width/2
            }

            // before doing the graphic we need to check if the position of the label needs
            // to be tweaked - if the text placement is 'MIDDLE" and the label is prepended
            if ( style.placement == MIDDLE && style.label.labelType == PREPEND) {
                label.Left = text1.Left - label.Width
            }

            if( style.graphic.graphic != NOGRAPHIC) {
                graphic = ComPPTGraphic.draw( event, page, style, nodeInfo, gPosInfo, currY, compPosList)
                currY += graphic.Height + 6
            }

            // now any remaining text
            if( (style.linesAbove > 0) && (style.linesAbove < strs.size())) {
                text2 = ComText.draw( strs[ style.linesAbove .. -1].collect( trim).join( '\n'),
                                      page,
                                      style,
                                      nodeInfo,
                                      tPosInfo,
                                      currY
                                    )
                // the text needs to be centered for MIDDLE, SPAN, and OVERLAP
                if ( style.placement == MIDDLE || style.placement == SPAN || style.placement == OVERLAP) {
                    text2.Left = tPosInfo.x - text2.Width/2
                }
            }
        }

        // group the two text shapes together to form a shape to size the rect around
        // if only one, then pretend text1 is the group...
        def nameArray
        if ( text2) {
            nameArray = new SafeArray( (String[])[text1.name, text2.name])
            group1 = page.Shapes.Range( nameArray).Group()
        } else group1 = text1

        // reposition and resize the border (if any) to be around the text
        if (rect) {
            rect.Left = group1.Left
            rect.Height = group1.Height
            rect.Width = group1.Width
            // also verify that the border covers the graphic (if any)
            if (graphic) {
                if( rect.Top + rect.Height < graphic.Top + graphic.Height) {
                    rect.Height = (graphic.Top + graphic.Height + 3) - rect.Top
                }
            }
        }

        def nameList = [ group1.name ]
        if ( rect || label) {
            if (rect) nameList << rect.name
            if (label) nameList << label.name
        }

        // now group the two text areas, the rect, and the label together
        if (nameList.size() > 1) {
            nameArray = new SafeArray( (String[])nameList)
            group2 = page.Shapes.Range( nameArray).Group()
        } else group2 = group1

        // Note, this last grouping (if nameList contained the rect name) could bury the
        // graphic under the group's Z-order.  The graphic needs to be on top
        if (graphic) graphic.ZOrder( MsoZOrderCmd.msoBringToFront)

        // now, check the placement of the text
        switch ( style.placement) {
        case SRCJUST:
            // left to right case (x2 > x1) is ok; need to slide right to left case
            if ( gPosInfo.x1 > gPosInfo.x2) group2.Left = group2.Left - group2.Width
          break
        case DESTJUST:
            // right to left case (x1 > x2) is ok; need to slide left to right case
            if ( gPosInfo.x1 < gPosInfo.x2) group2.Left = group2.Left - group2.Width
            break
        case MIDDLE:
        case SPAN:
            break
        }

        // look to see if the group has been 'repositioned' off either side of the page
        // first look to the right side of the page
        if (group2.Left + group1.Width > rightM) group2.Left =  rightM - group2.Width
        // and then to the left side of the page
        if (group2.Left < leftM) group2.Left = leftM

        // now check that the bottom of group1 extends down to currY
        // if the terse option was used, then it won't extend over the graphic, need to do that
        if ( group1.Top + group1.Height < currY) group1.Height = currY - group2.Top

        // now group the graphic and text areas
        if (  style.graphic.graphic != NOGRAPHIC) {
            nameArray = new SafeArray( (String[])[ group2.name, graphic.name])
            group1 = page.Shapes.Range( nameArray).Group()
        } else group1 = group2
        group1.name = event.command + event.count

        return  [ group1 ]

    }

    def genLabel() {
        def text
        def res = [ /(.*)<(MC)>(.*)/,
                    /(.*)<(CC)>(.*)/,
                    /(.*)<(AC)>(.*)/,
                    /(.*)<(BC)>(.*)/,
                    /(.*)<(BCC)>(.*)/
                  ]

        text = style.label.labelFormat
        res.each { re ->
            def match
            if( text ==~ re) {
                match = ( text =~ re)
                // look up the count
                text =  match[0][1 .. -1].collect( { counts[ it]?: it}).join('')
            }
        }

        return text
    }

    def drawLabel( ip, page) {
        def label
        def rect
        def group

        Scriptom.inApartment {
            rect = ComPPTUtils.insertBorder( page, ip, null, style)
            label = ComText.drawPositioned( event.label,
                                            page,
                                            style,
                                            ip,
                                            null
                                          )
            // reposition and resize the border to be around the text
            rect.Width = label.Width + 2i
            rect.Left = label.Left - 1i
            rect.Height = label.Height + 2i

            // wrap it into a group
            def nameArray = new SafeArray( (String[])[ rect.name, label.name])
            group = page.Shapes.Range( nameArray).Group()
        }
        return group
    }

    def createPoint( x, y) {
        ComPPTUtils.createPoint( x, y)
    }

}
