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
package meander.Uno

import com.sun.star.lang.XMultiServiceFactory

import com.sun.star.beans.XMultiPropertySet
import com.sun.star.beans.XPropertySet
import com.sun.star.beans.PropertyValue

import com.sun.star.drawing.XShapes
import com.sun.star.drawing.XShape

import com.sun.star.uno.UnoRuntime

import com.sun.star.awt.Point
import com.sun.star.awt.Size
import com.sun.star.container.XIndexAccess
import com.sun.star.container.XNameContainer

import com.sun.star.style.ParagraphAdjust
import meander.GradientVariant

import static meander.FillType.*
import meander.LineType
import static meander.LineType.*

import static meander.GradientType.*
import static meander.GradientVariant.*
import meander.Style
import com.sun.star.text.XText
import com.sun.star.drawing.TextFitToSizeType
import com.sun.star.text.XTextCursor
import com.sun.star.awt.FontWeight
import com.sun.star.awt.FontSlant
import com.sun.star.awt.Gradient
import com.sun.star.awt.GradientStyle
import java.awt.Color
import com.sun.star.frame.XDispatchProvider
import com.sun.star.view.XSelectionSupplier
import com.sun.star.frame.XDispatchHelper
import com.sun.star.frame.XController
import com.sun.star.frame.XModel
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.drawing.LineDash
import com.sun.star.drawing.EnhancedCustomShapeTextPathMode
import com.sun.star.drawing.XEnhancedCustomShapeDefaulter

/**
 * utilities for communicating through the UNO interface
 */

class UnoUtils {
    private static XShape createAndInsertShape( xDoc,
                                                xDrawPage,
                                                String sShapeType,
                                                aPosition,
                                                aSize ) {
        XShape xShape = null
        try {
            xShape = createShape( xDoc, sShapeType, aPosition, aSize)
            XShapes xShapes = (XShapes) UnoRuntime.queryInterface( XShapes.class, xDrawPage)
            xShapes.add(xShape)
        } catch (Exception e) {
            e.printStackTrace()
        } finally {

            return xShape
        }
    }

    private static XShape createShape( xDoc,
                                       String sShapeType,
                                       aPosition,
                                       aSize ) {
        XShape xShape = null
        try {
            XMultiServiceFactory xDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface( XMultiServiceFactory.class, xDoc)
            xShape = (XShape) UnoRuntime.queryInterface( XShape.class, xDocFactory.createInstance( "com.sun.star.drawing." + sShapeType + "Shape" ) )
            if (aPosition) xShape.setPosition(aPosition)
            if (aSize) xShape.setSize(aSize)
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            return xShape
        }
    }

    public static void copyShapes( xDoc, context, shapes) {
        try {

            // get a handle on the selection service
            XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, xDoc)
            XController xController = xModel.getCurrentController()
            XSelectionSupplier xSelectionSupp = (XSelectionSupplier) UnoRuntime.queryInterface(XSelectionSupplier.class, xController)

            // get a container for the graphics
            Object shapeContainer = context.getServiceManager().createInstanceWithContext( "com.sun.star.drawing.ShapeCollection", context)
            XShapes xShapes = UnoRuntime.queryInterface(XShapes.class, shapeContainer)

            // select the container to get the shapes
            shapes.each { xShape -> xShapes.add( xShape) }
            xSelectionSupp.select( xShapes)

            // copy to the clipboard
            XMultiComponentFactory mxMCF = context.getServiceManager()
            XMultiServiceFactory xFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, mxMCF)
            Object dispatchHelper = xFactory.createInstance("com.sun.star.frame.DispatchHelper")
            XDispatchHelper helper = (XDispatchHelper) UnoRuntime.queryInterface( XDispatchHelper.class, dispatchHelper)
            PropertyValue[] pv = []
//            XModel aModel = (XModel) UnoRuntime.queryInterface(XModel.class, xDoc)
            XDispatchProvider xDispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface( XDispatchProvider.class, xController.getFrame())
            helper.executeDispatch( xDispatchProvider, ".uno:Copy", "", (int) 0, pv)


        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    public static XShapes paste( xDoc, context) {
        XShapes getShapes
        try {
            // paste what is on the clipboard
            XMultiComponentFactory mxMCF = context.getServiceManager()
            XMultiServiceFactory xFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, mxMCF)
            Object dispatchHelper = xFactory.createInstance("com.sun.star.frame.DispatchHelper")
            XDispatchHelper helper = (XDispatchHelper) UnoRuntime.queryInterface( XDispatchHelper.class, dispatchHelper)
            PropertyValue[] pv = []
            XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, xDoc)
            XController xController = xModel.getCurrentController()
            XDispatchProvider xDispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface( XDispatchProvider.class, xController.getFrame())
            helper.executeDispatch( xDispatchProvider, ".uno:Paste", '', (int) 0, pv)

            // get a handle to the currently selected graphic(s)
            XSelectionSupplier xSelectionSupp = (XSelectionSupplier) UnoRuntime.queryInterface(XSelectionSupplier.class, xController)
            Object temp = xSelectionSupp.getSelection()
            getShapes = (XShapes) UnoRuntime.queryInterface( XShapes.class, temp)
        } catch (Exception e) {
            e.printStackTrace()
        }
        return getShapes
    }

    public static void addShape( xDrawPage, xShape) {
        try {
            XShapes xShapes = (XShapes) UnoRuntime.queryInterface( XShapes.class, xDrawPage)
            xShapes.add(xShape)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    public static XShape buildGroup( pageSupplier, drawPage, shapeList) {
        XShape group = createAndInsertShape( pageSupplier, drawPage, 'Group', new Point( 0, 0), null)
        use (UnoCategory) {
            shapeList.each { shape ->
                group.uno(XShapes).add( shape) }
        }

        enhanceShape( group)
        return group
    }

    private static buildGrad( fill, color1, color2) {
        def grad = new Gradient()
        def gradValue

        switch ( fill.gradient) {
        case NOGRADIENT:
            gradValue = [ FillColor: color1 ]
            break
        case DIAGONALDOWN:
            switch( fill.variant) {
            case BOTTOMLEFT:
                grad.Style = GradientStyle.LINEAR
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISIN:
                grad.Style = GradientStyle.AXIAL
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISOUT:
                grad.Style = GradientStyle.AXIAL
                break
            case TOPRIGHT:
            default:
                grad.Style = GradientStyle.LINEAR
                break
            }
            grad.StartColor = color1
            grad.EndColor = color2
            grad.Angle = -450i
            grad.Border = 0i
            grad.XOffset = 0i
            grad.YOffset = 0i
            grad.StartIntensity = 100i
            grad.EndIntensity = 100i
            grad.StepCount = 10i

            gradValue = [ FillStyle: com.sun.star.drawing.FillStyle.GRADIENT,
                          FillGradient: grad
                        ]
            break
        case DIAGONALUP:
            switch( fill.variant) {
            case BOTTOMRIGHT:
                grad.Style = GradientStyle.LINEAR
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISIN:
                grad.Style = GradientStyle.AXIAL
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISOUT:
                grad.Style = GradientStyle.AXIAL
                break
            case TOPRIGHT:
            default:
                grad.Style = GradientStyle.LINEAR
                break
            }
            grad.StartColor = color1
            grad.EndColor = color2
            grad.Angle = 450i
            grad.Border = 0i
            grad.XOffset = 0i
            grad.YOffset = 0i
            grad.StartIntensity = 100i
            grad.EndIntensity = 100i
            grad.StepCount = 10i

            gradValue = [ FillStyle: com.sun.star.drawing.FillStyle.GRADIENT,
                          FillGradient: grad
                        ]
            break
        case HORIZONTAL:
            switch( fill.variant) {
            case BOTTOMUP:
                grad.Style = GradientStyle.LINEAR
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISIN:
                grad.Style = GradientStyle.AXIAL
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISOUT:
                grad.Style = GradientStyle.AXIAL
                break
            case TOPDOWN:
            default:
                grad.Style = GradientStyle.LINEAR
                break
            }
            grad.StartColor = color1
            grad.EndColor = color2
            grad.Angle = 0i
            grad.Border = 0i
            grad.XOffset = 0i
            grad.YOffset = 0i
            grad.StartIntensity = 100i
            grad.EndIntensity = 100i
            grad.StepCount = 10i

            gradValue = [ FillStyle: com.sun.star.drawing.FillStyle.GRADIENT,
                          FillGradient: grad
                        ]
            break
        case FROMCENTER:
            grad = new Gradient()
            switch( fill.variant) {
            case AXISIN:
                grad.StartColor = color1                 // these are flipped to match
                grad.EndColor = color2                   // Powerpoint
                break
            case AXISOUT:
            default:
                grad.StartColor = color2                 // these are flipped to match
                grad.EndColor = color1                   // Powerpoint
                break
            }
            grad.Style = GradientStyle.RADIAL
            grad.Angle = 0i
            grad.Border = 0i
            grad.XOffset = 50i
            grad.YOffset = 50i
            grad.StartIntensity = 100i
            grad.EndIntensity = 100i
            grad.StepCount = 10i

            gradValue = [ FillStyle: com.sun.star.drawing.FillStyle.GRADIENT,
                          FillGradient: grad
                        ]
            break
        case FROMCORNER:
            int x, y
            switch( fill.variant) {
            case TOPRIGHT:
                x = 100i - ( 50i * (int)(fill.gradientScale/100i))
                y = 0i + ( 50i * (int)(fill.gradientScale/100i))
                break
            case BOTTOMRIGHT:
                x = 100i - ( 50i * (int)(fill.gradientScale/100i))
                y = 100i - ( 50i * (int)(fill.gradientScale/100i))
                break
            case BOTTOMLEFT:
                x = 0i + ( 70i * (int)(fill.gradientScale/100i))
                y = 100i - ( 50i * (int)(fill.gradientScale/100i))
                break
            case TOPLEFT:
            default:
                x = 0i + ( 70i * (int)(fill.gradientScale/100i))
                y = 0i + ( 60i * (int)(fill.gradientScale/100i))
                break
            }
            grad.Style = GradientStyle.ELLIPTICAL
            grad.StartColor = color2                 // these are flipped to match
            grad.EndColor = color1                   // Powerpoint
            grad.Angle = 0i
            grad.Border = 0i //style.fill.offset2
            grad.XOffset = x
            grad.YOffset = y
            grad.StartIntensity = 100i
            grad.EndIntensity = 100i
            grad.StepCount = 10i

            gradValue = [ FillStyle: com.sun.star.drawing.FillStyle.GRADIENT,
                          FillGradient: grad
                        ]
            break
        case VERTICAL:
            switch( fill.variant) {
            case RIGHTLEFT:
                grad.Style = GradientStyle.LINEAR
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISIN:
                grad.Style = GradientStyle.AXIAL
                // swap the colors
                ( color1, color2) = [ color2, color1]
                break
            case AXISOUT:
                grad.Style = GradientStyle.AXIAL
                break
            case LEFTRIGHT:
            default:
                grad.Style = GradientStyle.LINEAR
                break
            }
            grad.StartColor = color1
            grad.EndColor = color2
            grad.Angle = 900i
            grad.Border = 0i
            grad.XOffset = 0i
            grad.YOffset = 0i
            grad.StartIntensity = 100i
            grad.EndIntensity = 100i
            grad.StepCount = 10i

            gradValue = [ FillStyle: com.sun.star.drawing.FillStyle.GRADIENT,
                          FillGradient: grad
                        ]
            break
        }

        return gradValue
    }

    public static XShape insertBorder( pageSupplier, drawPage, pt, sz, Style style) {
        XShape rect = createAndInsertShape( pageSupplier, drawPage, "Rectangle", pt, sz)
        def propList = [:]
        use (UnoCategory) {
            switch ( style.border.line) {
            case NOLINE:
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.NONE ]
                break
            case DASH:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.RECT
                dash.Dashes = 1i
                dash.DashLen = 300i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case DASHDOT:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.RECT
                dash.Dots = 1i
                dash.DotLen = 150i
                dash.Dashes = 1i
                dash.DashLen = 300i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case DASHDOTDOT:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.RECT
                dash.Dots = 2i
                dash.DotLen = 150i
                dash.Dashes = 1i
                dash.DashLen = 300i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case LONGDASH:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.RECT
                dash.Dashes = 1i
                dash.DashLen = 600i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case LONGDASHDOT:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.RECT
                dash.Dots = 1
                dash.DotLen = 150i
                dash.Dashes = 1i
                dash.DashLen = 600i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case ROUNDDOT:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.ROUND
                dash.Dots = 1i
                dash.DotLen = 150i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case SOLID:
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.SOLID,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            case SQUAREDOT:
                def dash = new LineDash()
                dash.Style = com.sun.star.drawing.DashStyle.RECT
                dash.Dots = 1i
                dash.DotLen = 150i
                dash.Distance = 150i
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash,
                              LineColor: style.border.color.RGB,
                              LineWidth: (int)(style.border.thickness * 25.4),
                            ]
                break
            }

            switch ( style.fill.type) {
            case NOFILL:
                propList << [ FillStyle: com.sun.star.drawing.FillStyle.NONE ]
                break
            case ONECOLOR:
                def color1, color2

                color1 = style.fill.foreColor.RGB
                color2 = Color.WHITE.RGB

                // if there is a cached fill in the style, use it
                // otherwise create a fill, cache it, then use it
                if( style.fillCache[ 'uno'] == null) style.fillCache[ 'uno'] = buildGrad( style.fill, color1, color2)
                propList << style.fillCache[ 'uno']
                break
            case TWOCOLOR:
                def color1, color2

                color1 = style.fill.foreColor.RGB
                color2 = style.fill.backColor.RGB


                // if there is a cached fill in the style, use it
                // otherwise create a fill, cache it, then use it
                if( style.fillCache[ 'uno'] == null) style.fillCache[ 'uno'] = buildGrad( style.fill, color1, color2)
                propList << style.fillCache[ 'uno']
                break
            }
            rect.multiPropertySet = propList
        }

        enhanceShape( rect)

        return rect
    }

    public static XShape insertLine( pageSupplier, drawPage, pt, sz, color, thickness, LineType linetype) {
        def propList = [:]
        XShape line = createAndInsertShape( pageSupplier, drawPage, "Line", pt, sz)
        use (UnoCategory) {
            propList << [ LineColor: color,
                          LineWidth: thickness
                        ]
            switch ( linetype) {
            case NOLINE:
                // shouldn't be here - throw an exception?
                break
            case DASH:
                def dash = new LineDash()
                dash.Dots = 0
                dash.DotLen = 0
                dash.Dashes = 1
                dash.DashLen = 150
                dash.Distance = 150
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            case DASHDOT:
                def dash = new LineDash()
                dash.Dots = 1
                dash.DotLen = 25
                dash.Dashes = 1
                dash.DashLen = 150
                dash.Distance = 150
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            case DASHDOTDOT:
                def dash = new LineDash()
                dash.Dots = 2
                dash.DotLen = 25
                dash.Dashes = 1
                dash.DashLen = 150
                dash.Distance = 150
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            case LONGDASH:
                def dash = new LineDash()
                dash.Dots = 0
                dash.DotLen = 0
                dash.Dashes = 1
                dash.DashLen = 300
                dash.Distance = 150
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            case LONGDASHDOT:
                def dash = new LineDash()
                dash.Dots = 1
                dash.DotLen = 25
                dash.Dashes = 1
                dash.DashLen = 300
                dash.Distance = 150
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            case ROUNDDOT:
                def dash = new LineDash()
                dash.Dots = 1
                dash.DotLen = 25
                dash.Dashes = 0
                dash.DashLen = 0
                dash.Distance = 150
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            case SOLID:
                // don't add any properties, SOLID is the default
                break
            case SQUAREDOT:
                def dash = new LineDash()
                dash.Dots = 0
                dash.DotLen = 0
                dash.Dashes = 1
                dash.DashLen = 25
                dash.Distance = 50
                propList << [ LineStyle: com.sun.star.drawing.LineStyle.DASH,
                              LineDash: dash
                            ]
                break
            }
            line.multiPropertySet = propList
        }

        enhanceShape( line)
        return line
    }

    public static XShape insertPolyLine( pageSupplier, drawPage, pts, color, thickness) {
        XShape line = createAndInsertShape( pageSupplier, drawPage, "Line", null, null)
        use (UnoCategory) {
            //UnoRuntime.queryInterface( XPropertySet, line).setPropertyValue( 'PolyPolygon', pts)
            line.uno(XPropertySet)['PolyPolygon'] = pts
            line.multiPropertySet = [ LineColor: color,
                                      LineWidth: thickness,
                                    ]
        }

        enhanceShape( line)
        return line
    }

    public static XShape insertPolygon( pageSupplier, drawPage, pts, color, thickness) {
        XShape poly = createAndInsertShape( pageSupplier, drawPage, "PolyPolygon", null, null)
        use (UnoCategory) {
//            def pp = poly.uno(XPropertySet)['PolyPolygon']
//            pp = pts
            poly.uno(XPropertySet)['PolyPolygon'] = pts
            poly.multiPropertySet = [ LineColor: color,
                                      FillColor: color,
                                      LineWidth: thickness,
                                    ]
        }

        enhanceShape( poly)
        return poly
    }

    public static XShape insertEllipse( pageSupplier, drawPage, pt, sz, color) {
        XShape ellipse = createAndInsertShape( pageSupplier, drawPage, "Ellipse", pt, sz)
        use (UnoCategory) {
            ellipse.multiPropertySet = [ LineColor: color,
                                         FillColor: color,
                                       ]
        }

        enhanceShape( ellipse)
        return ellipse
    }

    public static XShape insertText( text,
                                     pageSupplier,
                                     drawPage,
                                     pt,
                                     textShapeInfo,
                                     ParagraphAdjust adjP,
                                     fontInfo
    ) {
        XShape textShape

        use (UnoCategory) {
            textShape = createAndInsertShape( pageSupplier, drawPage, "Text", pt, null)
            //text.uno(XPropertySet)['TextAutoGrowWidth'] = true
            //text.uno(XPropertySet)['TextAutoGrowHeight'] = true
            textShape.multiPropertySet = textShapeInfo
//            textShape.multiPropertySet = [ TextAutoGrowWidth: growWidth,
//                                           TextAutoGrowHeight: growHeight,
//                                           TextHorizontalAdjust: adjH,
//                                           TextVerticalAdjust: adjV,
//                                         ]
            XText xText = textShape.uno( XText)
            xText.multiPropertySet = [ TextFitToSize: TextFitToSizeType.NONE,
                                       TextLeftDistance: 30,
                                       TextRightDistance: 30,
                                       TextUpperDistance: 30,
                                       TextLowerDistance: 30,
                                     ]
            XTextCursor xTextCursor = xText.createTextCursor()
            xTextCursor.multiPropertySet = [ ParaAdjust: adjP,
                                             CharColor: fontInfo.color.RGB,
                                             CharFontName: fontInfo.face,
                                             CharHeight: fontInfo.size,
                                             CharWeight: fontInfo.bold ? FontWeight.BOLD : FontWeight.NORMAL,
                                             CharPosture: fontInfo.italic ? FontSlant.ITALIC : FontSlant.NONE
                                           ]
            xText.insertString( xTextCursor, text, true )
        }

        enhanceShape( textShape)
        return textShape
    }

    public static XShape insertWatermark( text,
                                          pageSupplier,
                                          drawPage,
//                                          textShapeInfo,
//                                          ParagraphAdjust adjP,
                                          pt,
                                          size,
                                          fontInfo
                                        ) {
        XShape watermark

        use (UnoCategory) {
            watermark = createAndInsertShape( pageSupplier, drawPage, "Custom", pt, size)

            XEnhancedCustomShapeDefaulter xShape = watermark.uno( XEnhancedCustomShapeDefaulter)

            // build a set of default shape properties for 'fontwork-slant-up'
            //
            // but, note that these defaults won't have the TextPath property setup properly
            xShape.createCustomShapeDefaults("fontwork-slant-up")

            //grab the CustomShapeGeometry property so we can add TextPath
            def cg = watermark.uno(XPropertySet)['CustomShapeGeometry']

            // build the TextPath property with its necessary four sub properties
            def mProp = new PropertyValue[4]
            mProp[0] = new PropertyValue( Name: 'TextPath', Value: true)
            mProp[1] = new PropertyValue( Name: 'TextPathMode', Value: EnhancedCustomShapeTextPathMode.SHAPE)
            mProp[2] = new PropertyValue( Name: 'TextPathScale', Value: EnhancedCustomShapeTextPathMode.PATH)
            mProp[3] = new PropertyValue( Name: 'TextPathSameLetterHeights', Value: false)

            // build a new property array with room for the new TextPath info
            // copy the default props over, add the new property, and then save it away
            def pv = new PropertyValue[ cg.size() + 1]
            cg.eachWithIndex { p, i -> pv[i] = p }
            pv[-1] = new PropertyValue( Name: 'TextPath', Value: mProp)
            watermark.uno(XPropertySet)['CustomShapeGeometry'] = pv

            // insert the actual watermark string
            XText xText = watermark.uno( XText)
            XTextCursor xTextCursor = xText.createTextCursor()
            xText.insertString( xTextCursor, text, true )

            // finally, set the appropriate color characteristics
            watermark.multiPropertySet = [ FillColor: fontInfo.color.RGB,
                                           FillStyle: com.sun.star.drawing.FillStyle.SOLID,
                                           FillTransparence: 50,
                                           LineStyle: com.sun.star.drawing.LineStyle.NONE,
                                         ]


        }
        return watermark
    }

    public static deleteShape( page, xShape) {
        try {
            XShapes xShapes = (XShapes) UnoRuntime.queryInterface( XShapes.class, page)
            xShapes.remove( xShape)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    public static setZOrder( XShape xShape, int zo) {
        use (UnoCategory) {
            xShape.uno(XPropertySet)[ 'ZOrder'] = zo
        }
    }


    public static enhanceShape( shape) {
        shape.metaClass.propertyMissing = { String name ->
            switch( name) {
            case 'Width':
                shape.getSize().Width
                break
            case 'Height':
                shape.getSize().Height
                break
            case 'Size':
                shape.getSize()
                break
            case 'Top':
            case 'Y':
            case 'y':
                shape.getPosition().Y
                break
            case 'Left':
            case 'X':
            case 'x':
                shape.getPosition().X
                break
            case 'Position':
                shape.getPosition()
                break
            default:
                break
            }
        }
        shape.metaClass.propertyMissing = { String name, value ->
            switch( name) {
            case 'Width':
                shape.setSize( createSize( shape.getSize().Height, value))
                break
            case 'Height':
                shape.setSize( createSize( value, shape.getSize().Width))
                break
            case 'Size':
                shape.setSize( value)
                break
            case 'Top':
            case 'Y':
            case 'y':
                shape.setPosition( createPoint( shape.getPosition().X, value))
                break
            case 'Left':
            case 'X':
            case 'x':
                shape.setPosition( createPoint( value, shape.getPosition().Y))
                break
            case 'Position':
                shape.setPosition( value)
                break
            default:
                break
            }
        }
    }





    static createPoint( x, y) {
        new Point( (int) x, (int) y)
    }

    static createSize( w, h) {
        new Size( Width: (int) w, Height: (int) h)
    }
}

// based on the code at http://wiki.services.openoffice.org/wiki/API/Samples/Groovy/Office/RuntimeDialog
class UnoCategory {
    public static Object uno(Object unoObj, Class clazz) { UnoRuntime.queryInterface(clazz, unoObj) }

    public static Object getAt(XPropertySet pset, String pname) { pset.getPropertyValue(pname) }

    public static void putAt(XPropertySet pset, String pname, Object newValue) { pset.setPropertyValue(pname, newValue) }

    public static Object getAt(XIndexAccess ndx, int x) { ndx.getByIndex(x) }

    public static void setMultiPropertySet(Object unoObj, Map properties) {
        XMultiPropertySet pMSet = unoObj.uno(XMultiPropertySet)

        // Nasty hack to workaround problem when setting Height in the same setPropertyValues
        // call as other properties.
        // You also have to set Width after PositionX, fortunately the default for a map
        // literal in Groovy happens to be LinkedHashMap so the order of the keys is preserved.
        // If that weren't the case we'd have to deal with that here too.

        if ((properties.size() > 1) && properties.containsKey('Height')) {
            String[] propNames = ((properties.keySet() as java.util.List) - ['Height']) as String[]
            Object[] values = new Object[propNames.length]

            propNames.eachWithIndex { String n, int x -> values[x] = properties.get(n) }

            pMSet.setPropertyValues(propNames, values)

            // We use setProertyValues rather than setPropertyValue because it is probably
            // more performant than making the two UNO calls that would require.
            pMSet.setPropertyValues(['Height'] as String[], [properties.get('Height')] as Object[])

           //      println("setMultiPropertySet-Height $propNames $values ${properties.get('Height')} ")
        } else {
            String[] propNames = properties.keySet() as String[]
            Object[] values = new Object[propNames.length]

            propNames.eachWithIndex { String n, int x -> values[x] = properties.get(n) }

            pMSet.setPropertyValues(propNames, values)

            //      println("setMultiPropertySet $propNames $values ${pMSet instanceof XMultiPropertySet} ")
        }
    }

    public static XNameContainer leftShift(XNameContainer container, Map values) {
        //    println "leftShifting $values"
        values.each { name, value -> container.insertByName(name, value) }
        return container
    }
}
