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

import com.sun.star.awt.Point
import com.sun.star.awt.Size

import meander.Style
import org.codehaus.groovy.scriptom.tlb.office2007.MsoShapeType

import static meander.FillType.*
import static meander.LineType.*
import static meander.GradientType.*
import static meander.GradientVariant.*

import org.codehaus.groovy.scriptom.tlb.office2007.MsoTextOrientation
import org.codehaus.groovy.scriptom.tlb.office2007.MsoAutoShapeType
import org.codehaus.groovy.scriptom.tlb.office2007.MsoTriState
import org.codehaus.groovy.scriptom.tlb.office2007.MsoLineDashStyle
import org.codehaus.groovy.scriptom.tlb.office2007.MsoGradientStyle
import org.codehaus.groovy.scriptom.tlb.office2007.MsoGradientColorType
import org.codehaus.groovy.scriptom.Scriptom

/**
 * a set of utilities for drawing PowerPoint shapes
 */
class ComPPTUtils {

    public static insertBorder( drawPage, Point pt, Size sz, Style style) {
        def rect

        Scriptom.inApartment {

            rect = drawPage.Shapes.AddShape( MsoAutoShapeType.msoShapeRectangle, // MsoTextOrientation.msoTextOrientationHorizontal,
                                             pt.X,
                                             pt.Y,
                                             sz?.Width,
                                             sz?.Height)

     /*       if (style.fill == 'None') {
                rect.fill.Visible = MsoTriState.msoFalse
            } else {
                rect.fill.Visible = MsoTriState.msoTrue
                rect.fill.ForeColor.RGB = style.fill.foreColor.RGB
            }
            if (style.border == 'None') {
                rect.Line.Visible = MsoTriState.msoFalse
            } else {
                rect.Line.Visible = MsoTriState.msoTrue
                rect.Line.ForeColor.RGB = style.border.color.RGB
                rect.Line.Weight = 1.0
            }  */

            rect.Line.Visible = MsoTriState.msoTrue
            rect.Line.ForeColor.RGB = style.border.color.getBlue()*256*256 +
                                      style.border.color.getGreen()*256 +
                                      style.border.color.getRed()
            rect.Line.Weight = style.border.thickness
            switch ( style.border.line) {
            case NOLINE:
                rect.Line.Visible = MsoTriState.msoFalse
                break
            case DASH:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineDash
                break
            case DASHDOT:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineDashDot
                break
            case DASHDOTDOT:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineDashDotDot
                break
            case LONGDASH:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineLongDash
                break
            case LONGDASHDOT:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineLongDashDot
                break
            case ROUNDDOT:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineRoundDot
                break
            case SOLID:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineSolid
                break
            case SQUAREDOT:
                rect.Line.DashStyle = MsoLineDashStyle.msoLineSquareDot
                break
            }

            rect.Fill.Visible = MsoTriState.msoTrue
            switch ( style.fill.type) {
            case NOFILL:
                rect.Fill.Visible = MsoTriState.msoFalse
                break
            case ONECOLOR:
                switch ( style.fill.gradient) {
                case NOGRADIENT:
                    break
                case DIAGONALDOWN:
                    int v = 1i
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        break
                    case TOPRIGHT:
                        v = 1i
                        break
                    case BOTTOMLEFT:
                        v = 2i
                        break
                    case BOTTOMRIGHT:
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3i
                        break
                    case AXISOUT:
                        v = 4i
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.OneColorGradient( MsoGradientStyle.msoGradientDiagonalDown, v, 1.0f)
                    break
                case DIAGONALUP:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        v = 1
                        break
                    case TOPRIGHT:
                        break
                    case BOTTOMLEFT:
                        break
                    case BOTTOMRIGHT:
                        v = 2
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3
                        break
                    case AXISOUT:
                        v = 4
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.OneColorGradient( MsoGradientStyle.msoGradientDiagonalUp, v, 1.0f)
                    break
                case HORIZONTAL:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        break
                    case TOPRIGHT:
                        break
                    case BOTTOMLEFT:
                        break
                    case BOTTOMRIGHT:
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3
                        break
                    case AXISOUT:
                        v = 4
                        break
                    case TOPDOWN:
                        v = 1
                        break
                    case BOTTOMUP:
                        v = 2
                        break
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.OneColorGradient( MsoGradientStyle.msoGradientHorizontal, v, 1.0f)
                    break
                case FROMCENTER:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
//                        v = 3
                        break
                    case TOPRIGHT:
//                        v = 4
                        break
                    case BOTTOMLEFT:
//                        v = 5
                        break
                    case BOTTOMRIGHT:
//                        v = 6
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 2
                        break
                    case AXISOUT:
                        v = 1
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.OneColorGradient( MsoGradientStyle.msoGradientFromCenter, v, 1.0f)
                    break
                case FROMCORNER:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        v = 1
                        break
                    case TOPRIGHT:
                        v = 2
                        break
                    case BOTTOMLEFT:
                        v = 3
                        break
                    case BOTTOMRIGHT:
                        v = 4
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        break
                    case AXISOUT:
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.OneColorGradient( MsoGradientStyle.msoGradientFromCorner, v, 1.0f)
                    break
                case VERTICAL:
                    int v = 1i
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        break
                    case TOPRIGHT:
                        break
                    case BOTTOMLEFT:
                        break
                    case BOTTOMRIGHT:
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3i
                        break
                    case AXISOUT:
                        v = 4i
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                        v = 1i
                        break
                    case RIGHTLEFT:
                        v = 2i
                        break
                    }
                    rect.Fill.OneColorGradient( MsoGradientStyle.msoGradientVertical, v, 1.0f)
                    break
                }

                // now that the gradient type is set, we can fill in the color
                rect.Fill.ForeColor.RGB = style.fill.foreColor.getBlue()*256*256 +
                                          style.fill.foreColor.getGreen()*256 +
                                          style.fill.foreColor.getRed()
                break


            case TWOCOLOR:
                switch ( style.fill.gradient) {
                case NOGRADIENT:
                    break
                case DIAGONALDOWN:
                    int v = 1i
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        break
                    case TOPRIGHT:
                        v = 1i
                        break
                    case BOTTOMLEFT:
                        v = 2i
                        break
                    case BOTTOMRIGHT:
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3i
                        break
                    case AXISOUT:
                        v = 4i
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.TwoColorGradient( MsoGradientStyle.msoGradientDiagonalDown, v)
                    break
                case DIAGONALUP:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        v = 1
                        break
                    case TOPRIGHT:
                        break
                    case BOTTOMLEFT:
                        break
                    case BOTTOMRIGHT:
                        v = 2
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3
                        break
                    case AXISOUT:
                        v = 4
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.TwoColorGradient( MsoGradientStyle.msoGradientDiagonalUp, v)
                    break
                case HORIZONTAL:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        break
                    case TOPRIGHT:
                        break
                    case BOTTOMLEFT:
                        break
                    case BOTTOMRIGHT:
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3
                        break
                    case AXISOUT:
                        v = 4
                        break
                    case TOPDOWN:
                        v = 1
                        break
                    case BOTTOMUP:
                        v = 2
                        break
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.TwoColorGradient( MsoGradientStyle.msoGradientHorizontal, v)
                    break
                case FROMCENTER:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
//                        v = 3
                        break
                    case TOPRIGHT:
//                        v = 4
                        break
                    case BOTTOMLEFT:
//                        v = 5
                        break
                    case BOTTOMRIGHT:
//                        v = 6
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 2
                        break
                    case AXISOUT:
                        v = 1
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.TwoColorGradient( MsoGradientStyle.msoGradientFromCenter, v)
                    break
                case FROMCORNER:
                    int v = 1
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        v = 1
                        break
                    case TOPRIGHT:
                        v = 2
                        break
                    case BOTTOMLEFT:
                        v = 3
                        break
                    case BOTTOMRIGHT:
                        v = 4
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        break
                    case AXISOUT:
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        break
                    }
                    rect.Fill.TwoColorGradient( MsoGradientStyle.msoGradientFromCorner, v)
                    break
                case VERTICAL:
                    int v = 1i
                    switch( style.fill.variant) {
                    case TOPLEFT:
                        break
                    case TOPRIGHT:
                        break
                    case BOTTOMLEFT:
                        break
                    case BOTTOMRIGHT:
                        break
                    case CENTER:
                        break
                    case AXISIN:
                        v = 3i
                        break
                    case AXISOUT:
                        v = 4i
                        break
                    case TOPDOWN:
                    case BOTTOMUP:
                    case LEFTRIGHT:
                        v = 1i
                        break
                    case RIGHTLEFT:
                        v = 2i
                        break
                    }
                    rect.Fill.TwoColorGradient( MsoGradientStyle.msoGradientVertical, v)
                    break
                }

                // now that the gradient type is set, we can fill in the colors
                rect.Fill.ForeColor.RGB = style.fill.foreColor.getBlue()*256*256 +
                                          style.fill.foreColor.getGreen()*256 +
                                          style.fill.foreColor.getRed()
                rect.Fill.BackColor.RGB = style.fill.backColor.getBlue()*256*256 +
                                          style.fill.backColor.getGreen()*256 +
                                          style.fill.backColor.getRed()
                break
            }
        }

        return rect
    }

    def createPoint( x, y) {
        new Point( x, y)
    }
}
