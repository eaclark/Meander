package meander.Jfx

import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeType
import javafx.scene.paint.Stop
import static javafx.scene.paint.CycleMethod.*
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.scene.text.TextBoundsType
import meander.AlignmentType
import meander.Style

import static meander.FillType.*
import static meander.LineType.*
import static meander.GradientType.*
import static meander.GradientVariant.*


/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/3/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class JfxUtils {

//    private static createAndInsertShape( builder,
//                                         page,
//                                         plane,
//                                         String shapeType,
//                                         propList,
//                                         Point2D position,
//                                         Dimension2D aSize ) {
//
//
//        def shape
//        shape = createShape( builder,
//                             shapeType,
//                             propList,
//                             position)
//        // no need to 'enhance' the shape here... it has already been done
//        insertShape( shape, builder,plane)
//
//        return shape
//    }

    public static createShape( builder,
                               String shapeType,
                               propList,
                               position) {
        def shape = null
        switch( shapeType) {
        case 'Rectangle':
            builder.with {
                shape = rectangle( propList)
                shape.x = position.x
                shape.y = position.y
            }
            enhanceGeomShape( shape)
            break
        case 'Line':
            builder.with {
                shape = line( stroke: Color.rgb( propList.style.color.red,
                                                 propList.style.color.green,
                                                 propList.style.color.blue),
                              strokeWidth: propList.strokeWidth,
                              strokeLineCap: propList.strokeLineCap,
                              strokeDashArray: propList.strokeDashArray,
                              startX: propList.startX, startY: propList.startY,
                              endX: propList.endX, endY: propList.endY)
            }
            enhanceGeomShape( shape)
            break
        case 'Polygon':
            builder.with {
                shape = polygon( strokeWidth: propList.style.thickness,
                                 strokeType: StrokeType.INSIDE,
                                 stroke: Color.rgb( propList.style.color.red,
                                                    propList.style.color.green,
                                                    propList.style.color.blue),
                                 points: propList.points,
                                 fill: Color.rgb( propList.style.color.red,
                                                  propList.style.color.green,
                                                  propList.style.color.blue),
                               )
            }
            enhanceGeomShape( shape)
            break
        case 'Ellipse':
            builder.with {
                shape = ellipse( strokeWidth: propList.style.thickness,
                                 strokeType: StrokeType.INSIDE,
                                 stroke: Color.rgb( propList.style.color.red,
                                                    propList.style.color.green,
                                                    propList.style.color.blue),
                                 fill: Color.rgb( propList.style.color.red,
                                                  propList.style.color.green,
                                                  propList.style.color.blue),
                                 centerX: propList.centerX,
                                 centerY: propList.centerY,
                                 radiusX: propList.radiusX,
                                 radiusY: propList.radiusY,
                               )
            }
            enhanceGeomShape( shape)
            break
        case 'Text':
            shape = createText( propList.text, builder, position, propList)
            // shape has already been 'enhanced'
            break
        case 'Group':
            builder.with {
                shape = group()
                enhanceGroupShape( shape)
            }
            break
        }
        return shape
    }

    public static insertShape( shape,
                               builder,
                               plane) {
        builder.defer {
            plane.children.add shape
        }
    }

    public static deleteShape( plane,
                               builder,
                               shape) {
        builder.defer {
            plane.children.remove shape
        }
    }
    
    public static createText( String txt,
                              builder,
                              pt,
                              propList
    ) {
        def textShape
        def pad = (0.03d * 72.0d)/2.54d           // padding for text
        def ts, tb
        def fw, fp, f         // font info vars

        builder.with {
            // the text
            ts = text( text: propList.text,
                       textOrigin: VPos.TOP,
                       x: pt.x + pad,
                       y: pt.y + pad,
                       fill: Color.rgb( propList.style.color.red,
                                        propList.style.color.green,
                                        propList.style.color.blue),
            )
            enhanceTextShape( ts)

            // set the bounds type - this should provide more
            // accurate placement of the text object
            ts.boundsType = TextBoundsType.VISUAL

            // if the text is suppose to be a max width, then set it
            // up to wrap
            if( propList.wrappingWidth) ts.wrappingWidth = propList.wrappingWidth

            switch( propList.alignment) {
            case AlignmentType.CENTER:
                ts.textAlignment = TextAlignment.CENTER
                break
            case AlignmentType.RIGHT:
                ts.textAlignment = TextAlignment.RIGHT
                break
            default:
                ts.textAlignment = TextAlignment.LEFT
                break
            }

            // prep the font
            fw = propList.style.bold ? FontWeight.BOLD : FontWeight.NORMAL
            fp = propList.style.italic ? FontPosture.ITALIC : FontPosture.REGULAR
            f = Font.font( propList.style.face,// as String,
                           fw,
                           fp,
                           propList.style.size)// as Double)
            ts.font = f

            // check to see if the text is too wide
            if( propList.maxWidth && ts.Width > propList.maxWidth) {
                ts.wrappingWidth = propList.maxWidth
            }

            // the bounding rectangle
            tb = rectangle( x: pt.x,
                            y: pt.y,
                            fill: Color.TRANSPARENT)    // got to give it a color to let it have an actual size

            // size it around the text
            tb.height = ts.Height + 2*pad
            // check if the backing rect should be wider than the text
            if( propList.minWidth) tb.width = propList.minWidth + 2*pad
            else tb.width = ts.Width + 2*pad
        }

        // now group the text and bounding rectangle
        textShape = buildGroup( builder, null, null, [tb, ts])

        return textShape
    }

//    public static createAndInsertBorder( builder, page, plane, Point2D pt, Dimension2D sz, Style style) {
//        def border
//        border = createBorder(builder, page, plane, pt, sz, style)
//        enhanceGeomShape( border)
//        insertShape( border, builder, plane)
//        return border
//    }

    public static createBorder( builder, pt, sz, Style style) {
        def rect
        def propList = [:]

        if ( sz) propList << [ width: sz.width, height: sz.height ]
        else propList << [ width: 10, height: 10 ]

        switch ( style.border.line) {
        case NOLINE:
            propList << [ strokeWidth: 0i ]
            break
        case DASH:
            propList << [ strokeDashArray: [ 6d, 4d ],
                          strokeType: StrokeType.INSIDE,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case DASHDOT:
            propList << [ strokeDashArray: [6d, 2d, 2d,2d ],
                          strokeType: StrokeType.INSIDE,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case DASHDOTDOT:
            propList << [ strokeDashArray: [10d, 2d, 4d,2d, 4d,2d ],
                          strokeType: StrokeType.INSIDE,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case LONGDASH:
            propList << [ strokeDashArray: [ 12d, 4d ],
                          strokeType: StrokeType.INSIDE,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case LONGDASHDOT:
            propList << [ strokeDashArray: [6d, 2d, 2d,2d, 2d,2d ],
                          strokeType: StrokeType.INSIDE,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case ROUNDDOT:
            propList << [ strokeDashArray: [2d, 2d ],
                          strokeType: StrokeType.INSIDE,
                          strokeLineCap: StrokeLineCap.ROUND,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case SOLID:
            propList << [ stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeType: StrokeType.INSIDE,
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        case SQUAREDOT:
            propList << [ strokeDashArray: [2d, 2d ],
                          strokeType: StrokeType.INSIDE,
                          stroke: Color.rgb( style.border.color.red,
                                             style.border.color.green,
                                             style.border.color.blue),
                          strokeWidth: style.border.thickness //(int)(style.border.thickness * 25.4),
            ]
            break
        }

        // if there is a cached fill in the style, use it
        // otherwise create a fill, cache it, then use it
        if( style.fillCache[ 'jfx'] == null) style.fillCache[ 'jfx'] = initFxGrad( builder, style)
        propList << [ fill: style.fillCache[ 'jfx']]

        rect = createShape( builder, "Rectangle", propList, pt)
        enhanceGeomShape( rect)
        return rect
    }

    public static buildGroup( builder, page, plane, shapeList) {
        def grp = null

        builder.with {
            grp = group()
        }
        shapeList.each { s -> if( s) grp.children.add( s) }
        enhanceGroupShape( grp)

        return grp
    }

    public static initFxGrad( builder, style) {
        def fillType = style.fill.type
        def p1

        // setup default colors
        def color1 = Color.BLACK
        def color2 = Color.WHITE
        double off1, off2
        double startX, startY, endX, endY
        def cycle

        // default to white
        p1 = color2

        if ( fillType != NOFILL) {
            if ( fillType == ONECOLOR) {
                color1 = Color.rgb( style.fill.foreColor.red,
                                    style.fill.foreColor.green,
                                    style.fill.foreColor.blue)
            } else {
                color1 = Color.rgb( style.fill.foreColor.red,
                                    style.fill.foreColor.green,
                                    style.fill.foreColor.blue)
                color2 = Color.rgb( style.fill.backColor.red,
                                    style.fill.backColor.green,
                                    style.fill.backColor.blue)
            }
            // check the gradient desired
            switch (style.fill.gradient) {
            case DIAGONALDOWN:
                off1 = 0d
                off2 = (double)(style.fill.gradientScale/100.0d)

                switch( style.fill.variant) {
//                case TOPLEFT:
//                    break
//                case BOTTOMRIGHT:
//                    break
//                case CENTER:
//                    break
//                case TOPDOWN:
//                    break
//                case BOTTOMUP:
//                    break
//                case LEFTRIGHT:
//                    break
//                case RIGHTLEFT:
//                    break
                case BOTTOMLEFT:
                    startX = 0d
                    startY = 1d
                    endX = 2d
                    endY = -1d
                    cycle = NO_CYCLE
                    break
                case AXISIN:
                    startX = 0.5d
                    startY = 0.5d
                    endX = 1.5d
                    endY = -0.5d
                    cycle = REFLECT
                    // swap the colors
                    ( color1, color2) = [ color2, color1]
                    break
                case AXISOUT:
                    startX = 0.5d
                    startY = 0.5d
                    endX = 1.5d
                    endY = -0.5d
                    cycle = REFLECT
                    break
                case TOPRIGHT:
                default:
                    startX = 1d
                    startY = 0d
                    endX = -1d
                    endY = 2d
                    cycle = NO_CYCLE
                    break
                }
                p1 = builder.linearGradient( start: [ startX, startY], end: [ endX, endY],
                                             proportional: true, cycleMethod: cycle,
                                             stops: [[ off1, color1], [ off2, color2]]
                                           )
                break
            case DIAGONALUP:
                off1 = 0d
                off2 = (double)(style.fill.gradientScale/100.0d)

                switch( style.fill.variant) {
//                case BOTTOMLEFT:
//                    break
//                case TOPRIGHT:
//                    break
//                case CENTER:
//                    break
//                case TOPDOWN:
//                    break
//                case BOTTOMUP:
//                    break
//                case LEFTRIGHT:
//                    break
//                case RIGHTLEFT:
//                    break
                case BOTTOMRIGHT:
                    startX = 1d
                    startY = 1d
                    endX = -1d
                    endY = -1d
                    cycle = NO_CYCLE
                    break
                case AXISIN:
                    startX = 0.5d
                    startY = 0.5d
                    endX = 1.5d
                    endY = 1.5d
                    cycle = REFLECT
                    // swap the colors
                    ( color1, color2) = [ color2, color1]
                    break
                case AXISOUT:
                    startX = 0.5d
                    startY = 0.5d
                    endX = 1.5d
                    endY = 1.5d
                    cycle = REFLECT
                    break
                    break
                case TOPLEFT:
                default:
                    startX = 0d
                    startY = 0d
                    endX = 2d
                    endY = 2d
                    cycle = NO_CYCLE
                    break
                }
                p1 = builder.linearGradient( start: [ startX, startY], end: [ endX, endY],
                                             proportional: true, cycleMethod: cycle,
                                             stops: [[ off1, color1], [ off2, color2]]
                                           )
                break
            case HORIZONTAL:
                off1 = 0d
                off2 = (double)(style.fill.gradientScale/100.0d)

                switch( style.fill.variant) {
//                case TOPLEFT:
//                    break
//                case TOPRIGHT:
//                    break
//                case BOTTOMLEFT:
//                    break
//                case BOTTOMRIGHT:
//                    break
//                case CENTER:
//                    break
//                case LEFTRIGHT:
//                    break
//                case RIGHTLEFT:
//                    break
                case AXISIN:
                    startX = 0d
                    startY = 0.5d
                    endX = 0d
                    endY = 1.5d
                    cycle = REFLECT
                    // swap the colors
                    ( color1, color2) = [ color2, color1]
                    break
                case AXISOUT:
                    startX = 0d
                    startY = 0.5d
                    endX = 0d
                    endY = 1.5d
                    cycle = REFLECT
                    break
                case BOTTOMUP:
                    startX = 0d
                    startY = 1d
                    endX = 0d
                    endY = -1d
                    cycle = NO_CYCLE
                    break
                case TOPDOWN:
                default:
                    startX = 0d
                    startY = 0d
                    endX = 0d
                    endY = 2d
                    cycle = NO_CYCLE
                    break
                }
                p1 = builder.linearGradient( start: [ startX, startY], end: [ endX, endY],
                                             proportional: true, cycleMethod: cycle,
                                             stops: [[ off1, color1], [ off2, color2]]
                                           )
                break
            case FROMCENTER:
//                if ( fillType == ONECOLOR) {
//                    off1 = 0d //(double)(style.fill.offset1/100.0d)
//                    off2 = (double)(style.fill.offset1/100.0d) //1d - off1
//                } else {
//                    off1 = 0d //   (double)(style.fill.offset1/100.0d)
//                    off2 = (double)(style.fill.offset2/100.0d)
//                }
                off1 = 0d
                off2 = (double)(style.fill.gradientScale/100.0d)

                switch( style.fill.variant) {
//                case TOPLEFT:
//                    break
//                case TOPRIGHT:
//                    break
//                case BOTTOMLEFT:
//                    break
//                case BOTTOMRIGHT:
//                    break
//                case CENTER:
//                    break
                case AXISIN:
                    // swap the colors to match how PowerPoint does it
                    ( color1, color2) = [ color2, color1]
                    break
                case AXISOUT:
                default:
                    break
//                case TOPDOWN:
//                    break
//                case BOTTOMUP:
//                    break
//                case LEFTRIGHT:
//                    break
//                case RIGHTLEFT:
//                    break
                }
                p1 = builder.radialGradient( radius: 2.0d, center: [ 0.5d, 0.5d],
                                             stops: [[ off1, color1], [ off2, color2]]
                                           )
                break
            case FROMCORNER:
                off1 = 0d
                off2 = (double)( style.fill.gradientScale/100.0d)
//                off1 = 0d + (0.5d - (double)(style.fill.gradientMixing)/100.0d)*((double) style.fill.gradientScale/100.0d)
//                off2 = (double)( style.fill.gradientScale/100.0d)

                switch( style.fill.variant) {
//                case CENTER:
//                    break
//                case AXISIN:
//                    break
//                case AXISOUT:
//                    break
//                case TOPDOWN:
//                    break
//                case BOTTOMUP:
//                    break
//                case LEFTRIGHT:
//                    break
//                case RIGHTLEFT:
//                    startX = 0d
//                    startY = 0d
//                    endX = 0d
//                    endY = 2d
//                    break
                case TOPRIGHT:
                    startX = 1d
                    startY = 0d
                    endX = 2d      // use as the radius
                    break
                case BOTTOMRIGHT:
                    startX = 1d
                    startY = 1d
                    endX = 2d      // use as the radius
                    break
                case BOTTOMLEFT:
                    startX = 0d
                    startY = 1d
                    endX = 2d      // use as the radius
                    break
                case TOPLEFT:
                default:
                    startX = 0d
                    startY = 0d
                    endX = 2d      // use as the radius
                    break
                }
                p1 = builder.radialGradient( radius: endX, center: [ startX, startY],
                                             stops: [[ off1, color1], [ off2, color2]]
                                           )
                break
            case VERTICAL:
                off1 = 0d
                off2 = (double)(style.fill.gradientScale/100.0d)

                switch( style.fill.variant) {
//                case TOPLEFT:
//                    break
//                case TOPRIGHT:
//                    break
//                case BOTTOMLEFT:
//                    break
//                case BOTTOMRIGHT:
//                    break
//                case CENTER:
//                    break
//                case TOPDOWN:
//                    break
//                case BOTTOMUP:
//                    break
                case AXISIN:
                    startX = 0.5d
                    startY = 0d
                    endX = 1.5d
                    endY = 0d
                    cycle = REFLECT
                    // swap the colors
                    ( color1, color2) = [ color2, color1]
                    break
                case AXISOUT:
                    startX = 0.5d
                    startY = 0d
                    endX = 1.5d
                    endY = 0d
                    cycle = REFLECT
                    break
                case RIGHTLEFT:
                    startX = 1d
                    startY = 0d
                    endX = -1d
                    endY = 0d
                    cycle = NO_CYCLE
                    break
                case LEFTRIGHT:
                default:
                    startX = 0d
                    startY = 0d
                    endX = 2d
                    endY = 0d
                    cycle = NO_CYCLE
                    break
                }
                p1 = builder.linearGradient( start: [ startX, startY], end: [ endX, endY],
                                             proportional: true, cycleMethod: cycle,
                                             stops: [[ off1, color1], [ off2, color2]]
                                           )
                break
            case NOGRADIENT:
            default:
                p1 = color1//Color.rgb( style.fill.foreColor.red, style.fill.foreColor.green, style.fill.foreColor.blue)
            }
        }
        return p1
    }

    public static setZOrder( shape, builder, int zo) {
        builder.defer {
            if (zo == 0) shape.toBack()
            else shape.toFront()
        }
    }

    public static enhanceTextShape( shape) {
        shape.metaClass.propertyMissing = { String name ->
            switch( name) {
            case 'Width':
                shape.layoutBounds.width
                break
            case 'Height':
//                shape.layoutBounds.height
                shape.layoutBounds.maxY - shape.layoutBounds.minY
                break
            case 'Size':
                shape//.layoutBounds
                break
            case 'Top':
            case 'Y':
            case 'y':
                // adjust for one line of text
//                shape.layoutBounds.minY
                shape.localToScene( shape.layoutBounds).minY
                break
            case 'Left':
            case 'X':
            case 'x':
                //shape.x
//                shape.layoutBounds.minX
                shape.localToScene( shape.layoutBounds).minX
                break
            case 'Position':
                //shape //new Point2D( shape.layoutX, shape.layoutY)
                return createPoint( shape.localToScene( shape.layoutBounds).minX,
                                    shape.localToScene( shape.layoutBounds).minY)
                break
            default:
                break
            }
        }
        shape.metaClass.propertyMissing = { String name, value ->
            switch( name) {
            case 'Width':
                shape.layoutBounds.width = value
                shape.wrappingWidth = value
                break
            case 'Height':                                         // and height of a text object
                shape.layoutBounds.height = value
                break
            case 'Size':                                           // or size
                shape.layoutBounds.width = value.width
                shape.wrappingWidth = value.width
                shape.layoutBounds.height = value.height
                break
            case 'Top':
            case 'Y':
            case 'y':
////                shape.y = value + shape.y - shape.layoutBounds.minY
//                println 'text value = ' + value
//                println 'text current layoutY = ' + shape.layoutY
//                println 'text current layoutBounds.minY = ' + shape.layoutBounds.minY
//                println 'text current boundsInParent.minY = ' + shape.boundsInParent.minY

                shape.layoutY = value - shape.layoutBounds.minY
//                println 'new text layoutY = ' + shape.layoutY
//                println 'new text layoutBounds.minY = ' + shape.layoutBounds.minY
//                println 'new text boundsInParent.minY = ' + shape.boundsInParent.minY
                break
            case 'Left':
            case 'X':
            case 'x':
////                shape.x = value
//                println 'text value = ' + value
//                println 'text current layoutX = ' + shape.layoutX
//                println 'text current layoutBounds.minX = ' + shape.layoutBounds.minX
//                println 'text current boundsInParent.minX = ' + shape.boundsInParent.minX

                shape.layoutX = value - shape.layoutBounds.minX
//                println 'new text layoutX = ' + shape.layoutX
//                println 'new text layoutBounds.minX = ' + shape.layoutBounds.minX
//                println 'new text boundsInParent.minX = ' + shape.boundsInParent.minX
                break
            case 'Position':
//                shape.x = value.X
                shape.layoutX = value.x - shape.layoutBounds.minX
                shape.layoutY = value.y - shape.layoutBounds.minY
                break
            default:
                break
            }
        }
    }

    public static enhanceGroupShape( shape) {
        shape.metaClass.propertyMissing = { String name ->
            switch( name) {
                case 'Width':
                case 'width':
                    shape.layoutBounds.width
                    break
                case 'Height':
                case 'height':
                    //shape.layoutBounds.height
                    shape.layoutBounds.maxY - shape.layoutBounds.minY
                    break
                case 'Size':
                    shape
                    break
                case 'Top':
                case 'Y':
                case 'y':
                    //shape.layoutBounds.minY
                    shape.localToScene( shape.layoutBounds).minY
                    break
                case 'Left':
                case 'X':
                case 'x':
                    //shape.layoutBounds.minX
                    shape.localToScene( shape.layoutBounds).minX
                    break
                case 'Position':
                    //shape //new Point2D( shape.layoutX, shape.layoutY)
                    createPoint( shape.localToScene( shape.layoutBounds).minX,
                                 shape.localToScene( shape.layoutBounds).minY)
                    break
                default:
                    break
            }
        }
        shape.metaClass.propertyMissing = { String name, value ->
            switch( name) {
                case 'Width':
                case 'width':
                    shape.layoutBounds.width = value
                    break
                case 'Height':
                case 'height':
                    shape.layoutBounds.height = value
                    break
                case 'Size':
                    shape.layoutBounds.width = value.width
                    shape.layoutBounds.height = value.height
                    break
                case 'Top':
                case 'Y':
                case 'y':
                    // JavaFX translations are relative - need to convert the aboslute value that was
                    // passed in to a relative one
//                    println 'group value = ' + value
//                    println 'group current layoutY = ' + shape.layoutY
//                    println 'group current layoutBounds.minY = ' + shape.layoutBounds.minY
//                    println 'group current boundsInParent.minY = ' + shape.boundsInParent.minY
                    shape.layoutY = value - shape.layoutBounds.minY
//                    println 'new group layoutY = ' + shape.layoutY
//                    println 'new group layoutBounds.minY = ' + shape.layoutBounds.minY
//                    println 'new group boundsInParent.minY = ' + shape.boundsInParent.minY
                    break
                case 'Left':
                case 'X':
                case 'x':
                    // JavaFX translations are relative - need to convert the aboslute value that was
                    // passed in to a relative one
//                    println 'group value = ' + value
//                    println 'group current layoutX = ' + shape.layoutX
//                    println 'group current layoutBounds.minX = ' + shape.layoutBounds.minX
//                    println 'group current boundsInParent.minX = ' + shape.boundsInParent.minX
                    shape.layoutX = value - shape.layoutBounds.minX
//                    println 'new group layoutX = ' + shape.layoutX
//                    println 'new group layoutBounds.minX = ' + shape.layoutBounds.minX
//                    println 'new group boundsInParent.minX = ' + shape.boundsInParent.minX
                    break
                case 'Position':
                    // JavaFX translations are relative - need to convert the aboslute value that was
                    // passed in to a relative one
//                    println 'group value = ' + value
//                    println 'group current layoutX = ' + shape.layoutX
//                    println 'group current layoutBounds.minX = ' + shape.layoutBounds.minX
//                    println 'group current layoutY = ' + shape.layoutY
//                    println 'group current layoutBounds.minY = ' + shape.layoutBounds.minY
                    shape.layoutX = value.x - shape.layoutBounds.minX
                    shape.layoutY = value.y - shape.layoutBounds.minY
//                    println 'new group layoutX = ' + shape.layoutX
//                    println 'new group layoutBounds.minX = ' + shape.layoutBounds.minX
//                    println 'new group boundsInParent.minX = ' + shape.boundsInParent.minX
//                    println 'new group layoutY = ' + shape.layoutY
//                    println 'new group layoutBounds.minY = ' + shape.layoutBounds.minY
//                    println 'new group boundsInParent.minY = ' + shape.boundsInParent.minY
                    break
                default:
                    break
            }
        }
    }

    public static enhanceGeomShape( shape) {
        shape.metaClass.propertyMissing = { String name ->
            switch( name) {
            case 'Width':
                shape.width
                break
            case 'Height':
//                shape.height
                shape.layoutBounds.maxY - shape.layoutBounds.minY
                break
            case 'Size':
                shape//.layoutBounds
                break
            case 'Top':
            case 'Y':
//                shape.y
                shape.localToScene( shape.layoutBounds).minY
                break
            case 'Left':
            case 'X':
//                shape.x
                shape.localToScene( shape.layoutBounds).minX
                break
            case 'Position':
                //shape//new Point2D( shape.x, shape.y)
                createPoint( shape.localToScene( shape.layoutBounds).minX,
                             shape.localToScene( shape.layoutBounds).minY)
                break
            default:
                break
            }
        }
        shape.metaClass.propertyMissing = { String name, value ->
            switch( name) {
            case 'Width':
                shape.width = value
                break
            case 'Height':
                shape.height = value
                break
            case 'Size':
                shape.width = value.width
                shape.height = value.height
                break
            case 'Top':
            case 'Y':
////                shape.y = value
//                println 'geom value = ' + value
//                println 'geom current layoutY = ' + shape.layoutY
//                println 'geom current layoutBounds.minY = ' + shape.layoutBounds.minY

                shape.layoutY = value - shape.layoutBounds.minY
//                println 'new geom layoutY = ' + shape.layoutY
//                println 'new geom layoutBounds.minY = ' + shape.layoutBounds.minY
                break
            case 'Left':
            case 'X':
////                shape.x = value
//                println 'geom value = ' + value
//                println 'geom current layoutX = ' + shape.layoutX
//                println 'geom current layoutBounds.minX = ' + shape.layoutBounds.minX

                shape.layoutX = value - shape.layoutBounds.minX
//                println 'new geom layoutX = ' + shape.layoutX
//                println 'new geom layoutBounds.minX = ' + shape.layoutBounds.minX
                break
            case 'Position':
//                shape.x = value.x
//                shape.y = value.y

                shape.layoutX = value.x - shape.layoutBounds.minX
                shape.layoutY = value.y - shape.layoutBounds.minY
                break
            default:
                break
            }
        }
    }

    static createPoint( x, y) {
        //new Point2D( x, y)
        [ x: x, X: x, y: y, Y: y]
    }

    static createSize( w, h) {
        [ width: w, Width: w, height: h, Height: h]
    }

}
