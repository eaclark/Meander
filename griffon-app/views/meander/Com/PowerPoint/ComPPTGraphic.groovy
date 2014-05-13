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

import static meander.GraphicType.*
import static meander.LinePatternType.*
import static meander.LineType.*
import org.codehaus.groovy.scriptom.tlb.office2007.MsoLineDashStyle
import org.codehaus.groovy.scriptom.tlb.office2007.MsoLineStyle
import org.codehaus.groovy.scriptom.tlb.office2007.MsoAutoShapeType
import org.codehaus.groovy.scriptom.SafeArray
import com.jacob.com.Variant

/**
 * the PowerPoint shape corresponding to the Flow Event graphic
 */
class ComPPTGraphic {
    static int arrowUnit = 4i

    static public draw( event, page, style, nodeInfo, xPosInfo, ypos, compList) {

        // shift everything down to account for the arrowhead thickness
        ypos += arrowUnit

        switch( style.graphic.graphic) {
        case ARROW:
            return drawArrow( event, page, style, nodeInfo, xPosInfo, ypos, compList)
            break
        case DOUBLEARROW:
            return drawDoubleArrow( event, page, style, nodeInfo, xPosInfo, ypos, compList)
            break
        case DOTTED:
            return drawDotted( event, page, style, nodeInfo, xPosInfo, ypos, compList)
            break
        case NOGRAPHIC:
        default:
            return null
        }
    }

    static private drawArrow( event, page, style, nodeInfo, xPosInfo, ypos, compList) {
        def gr, gr1, gr2, gr3
        int xpos1, xpos2

        xpos1 = (int) xPosInfo.x1
        xpos2 = (int) xPosInfo.x2

        gr1 = page.Shapes.AddLine( (float) xpos1,
                                   (float) ypos,
                                   (float) xpos2,
                                   (float) ypos)

        gr1.line.Weight = style.graphic.thickness
        gr1.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()

        switch( style.graphic.line) {
        case NOLINE:
            // shouldn't get here - let's just fall through for 'til I figure
            // out what to do with error reporting/recovery
        case DASH:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDash
            break
        case DASHDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDashDot
            break
        case DASHDOTDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDashDotDot
            break
        case LONGDASH:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineLongDash
            break
        case LONGDASHDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineLongDashDot
            break
        case ROUNDDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineRoundDot
            break
        case SOLID:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineSolid
            break
        case SQUAREDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineSquareDot
            break
        }

        switch( style.graphic.pattern) {
        case SINGLE:
            gr1.Line.Style = MsoLineStyle.msoLineSingle
            break
        case THICKBETWEENTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThickBetweenThin
            break
        case THICKTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThickThin
            break
        case THINTHICK:
            gr1.Line.Style = MsoLineStyle.msoLineThinThick
            break
        case THINTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThinThin
            break
        }

        // now draw the arrowhead
//        gr1.Line.BeginArrowheadStyle = MsoArrowheadStyle.msoArrowheadNone
//        gr1.Line.EndArrowheadStyle = MsoArrowheadStyle.msoArrowheadTriangle
        if ( xpos1 < xpos2) {
            com.jacob.com.SafeArray pts = new com.jacob.com.SafeArray( Variant.VariantFloat, 4, 2)
            pts.setFloat( 0, 0, (float) xpos2-style.graphic.thickness);    pts.setFloat( 0, 1, (float) ypos)
            pts.setFloat( 1, 0, (float) xpos2-style.graphic.thickness-4);  pts.setFloat( 1, 1, (float) ypos-2)
            pts.setFloat( 2, 0, (float) xpos2-style.graphic.thickness-4);  pts.setFloat( 2, 1, (float) ypos+2)
            pts.setFloat( 3, 0, (float) xpos2-style.graphic.thickness);    pts.setFloat( 3, 1, (float) ypos)
//            SafeArray pts = new SafeArray( SafeArray.FLOAT, (0..3), (0..1))
//            pts[0][0] = (float) xpos2;    pts[0][1] = (float) ypos
//            pts[1][0] = (float) xpos2-4i; pts[1][1] = (float) ypos-2i
//            pts[2][0] = (float) xpos2-4i; pts[2][1] = (float) ypos+2i
//            pts[3][0] = (float) xpos2;    pts[3][1] = (float) ypos
            gr2 = page.Shapes.AddPolyLine( pts)
            gr2.line.Weight = style.graphic.thickness + 0.5
            gr2.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                     style.text.color.getGreen()*256 +
                                     style.text.color.getRed()
            gr2.fill.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                     style.text.color.getGreen()*256 +
                                     style.text.color.getRed()
        } else{
            com.jacob.com.SafeArray pts = new com.jacob.com.SafeArray( Variant.VariantFloat, 4, 2)
            pts.setFloat( 0, 0, (float) xpos2+style.graphic.thickness);    pts.setFloat( 0, 1, (float) ypos)
            pts.setFloat( 1, 0, (float) xpos2+style.graphic.thickness+4);  pts.setFloat( 1, 1, (float) ypos-2)
            pts.setFloat( 2, 0, (float) xpos2+style.graphic.thickness+4);  pts.setFloat( 2, 1, (float) ypos+2)
            pts.setFloat( 3, 0, (float) xpos2+style.graphic.thickness);    pts.setFloat( 3, 1, (float) ypos)
            gr2 = page.Shapes.AddPolyLine( pts)
            gr2.line.Weight = style.graphic.thickness + 0.5
            gr2.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                     style.text.color.getGreen()*256 +
                                     style.text.color.getRed()
            gr2.fill.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                     style.text.color.getGreen()*256 +
                                     style.text.color.getRed()
        }

        // group the pieces into one graphics
        def nameArray = new SafeArray( (String[])[gr1.name, gr2.name])
        gr = page.Shapes.Range( nameArray).Group()
        return gr
    }

    static private drawDoubleArrow( event, page, style, nodeInfo, xPosInfo, ypos, compList) {
        def gr, gr1, gr2, gr3
        int xpos1, xpos2

        xpos1 = xPosInfo.x1
        xpos2 = xPosInfo.x2

        gr1 = page.Shapes.AddLine( xpos1 + arrowUnit*2i,
                                   ypos,
                                   xpos2 - arrowUnit*2i,
                                   ypos)

        gr1.line.Weight = style.graphic.thickness
        gr1.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()
        gr1.name = event.command + event.count + 'Graphic'

        switch( style.graphic.line) {
        case NOLINE:
            // shouldn't get here - let's just fall through for 'til I figure
            // out what to do with error reporting/recovery
        case DASH:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDash
            break
        case DASHDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDashDot
            break
        case DASHDOTDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDashDotDot
            break
        case LONGDASH:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineLongDash
            break
        case LONGDASHDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineLongDashDot
            break
        case ROUNDDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineRoundDot
            break
        case SOLID:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineSolid
            break
        case SQUAREDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineSquareDot
            break
        }

        switch( style.graphic.pattern) {
        case SINGLE:
            gr1.Line.Style = MsoLineStyle.msoLineSingle
            break
        case THICKBETWEENTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThickBetweenThin
            break
        case THICKTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThickThin
            break
        case THINTHICK:
            gr1.Line.Style = MsoLineStyle.msoLineThinThick
            break
        case THINTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThinThin
            break
        }

        // now draw the arrowheads
//        gr1.Line.BeginArrowheadStyle = MsoArrowheadStyle.msoArrowheadTriangle
//        gr1.Line.EndArrowheadStyle = MsoArrowheadStyle.msoArrowheadTriangle

        // first one
        com.jacob.com.SafeArray pts = new com.jacob.com.SafeArray( Variant.VariantFloat, 4, 2)
        pts.setFloat( 0, 0, (float) xpos2-style.graphic.thickness);    pts.setFloat( 0, 1, (float) ypos)
        pts.setFloat( 1, 0, (float) xpos2-style.graphic.thickness-4);  pts.setFloat( 1, 1, (float) ypos-2)
        pts.setFloat( 2, 0, (float) xpos2-style.graphic.thickness-4);  pts.setFloat( 2, 1, (float) ypos+2)
        pts.setFloat( 3, 0, (float) xpos2-style.graphic.thickness);    pts.setFloat( 3, 1, (float) ypos)
//            SafeArray pts = new SafeArray( SafeArray.FLOAT, (0..3), (0..1))
//            pts[0][0] = (float) xpos2;    pts[0][1] = (float) ypos
//            pts[1][0] = (float) xpos2-4i; pts[1][1] = (float) ypos-2i
//            pts[2][0] = (float) xpos2-4i; pts[2][1] = (float) ypos+2i
//            pts[3][0] = (float) xpos2;    pts[3][1] = (float) ypos
        gr2 = page.Shapes.AddPolyLine( pts)
        gr2.line.Weight = style.graphic.thickness + 0.5
        gr2.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()
        gr2.fill.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()
        // then the other
        pts.setFloat( 0, 0, (float) xpos1+style.graphic.thickness);    pts.setFloat( 0, 1, (float) ypos)
        pts.setFloat( 1, 0, (float) xpos1+style.graphic.thickness+4);  pts.setFloat( 1, 1, (float) ypos-2)
        pts.setFloat( 2, 0, (float) xpos1+style.graphic.thickness+4);  pts.setFloat( 2, 1, (float) ypos+2)
        pts.setFloat( 3, 0, (float) xpos1+style.graphic.thickness);    pts.setFloat( 3, 1, (float) ypos)
        gr3 = page.Shapes.AddPolyLine( pts)
        gr3.line.Weight = style.graphic.thickness + 0.5
        gr3.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()
        gr3.fill.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()

        // group the pieces into one graphics
        def nameArray = new SafeArray( (String[])[gr1.name, gr2.name, gr3.name])
        gr = page.Shapes.Range( nameArray).Group()
        return gr

    }

    static private drawDotted( event, page, style, nodeInfo, xPosInfo, ypos, compList) {
        def gr = []
        def gr1, gr2
        int xpos1, xpos2

        xpos1 = xPosInfo.x1
        xpos2 = xPosInfo.x2

        gr1 = page.Shapes.AddLine( xpos1,
                                   ypos+3i,
                                   xpos2,
                                   ypos+3i)

        gr1.line.Weight = style.graphic.thickness
        gr1.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                 style.text.color.getGreen()*256 +
                                 style.text.color.getRed()

        switch( style.graphic.line) {
        case NOLINE:
            // shouldn't get here - let's just fall through for 'til I figure
            // out what to do with error reporting/recovery
        case DASH:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDash
            break
        case DASHDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDashDot
            break
        case DASHDOTDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineDashDotDot
            break
        case LONGDASH:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineLongDash
            break
        case LONGDASHDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineLongDashDot
            break
        case ROUNDDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineRoundDot
            break
        case SOLID:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineSolid
            break
        case SQUAREDOT:
            gr1.Line.DashStyle = MsoLineDashStyle.msoLineSquareDot
            break
        }

        switch( style.graphic.pattern) {
        case SINGLE:
            gr1.Line.Style = MsoLineStyle.msoLineSingle
            break
        case THICKBETWEENTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThickBetweenThin
            break
        case THICKTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThickThin
            break
        case THINTHICK:
            gr1.Line.Style = MsoLineStyle.msoLineThinThick
            break
        case THINTHIN:
            gr1.Line.Style = MsoLineStyle.msoLineThinThin
            break
        }

        gr << gr1

        // add the dots, one per node
        compList.each { x ->

            gr1 = page.Shapes.AddShape( MsoAutoShapeType.msoShapeOval,
                                        x - 5,
                                        ypos,
                                        10, 5)
            gr1.Line.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                     style.text.color.getGreen()*256 +
                                     style.text.color.getRed()
            gr1.fill.ForeColor.RGB = style.graphic.color.getBlue()*256*256 +
                                     style.text.color.getGreen()*256 +
                                     style.text.color.getRed()

            gr << gr1
        }

        String[] nameList = gr.collectAll { it.name }
        def nameArray = new SafeArray( (String[])nameList)
        gr2 = page.Shapes.Range( nameArray).Group()
        return gr2
    }

}
