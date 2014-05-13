package meander.Jfx

import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.shape.StrokeLineCap

import static meander.GraphicType.*
import static meander.LinePatternType.*
import static meander.LineType.*

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/5/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
class JfxGraphic {
    static def arrowUnit = 6.0d
    static def dotUnit = 3.0d

    static public draw( event, page, builder, style, nodeInfo, xPosInfo, ypos, compList) {
        def gr, gr1, gr2, gr3, lw
        int xpos1, xpos2

        xpos1 = xPosInfo.x1
        xpos2 = xPosInfo.x2

        switch( style.graphic.graphic) {
        case ARROW:
            // shift everything down to account for the arrowhead thickness
            switch( style.graphic.pattern) {
            case SINGLE:
                ypos += (arrowUnit + style.graphic.thickness)/4
                break
            case THICKBETWEENTHIN:
                ypos += (arrowUnit + style.graphic.thickness*5d)/4
                break
            case THICKTHIN:
            case THINTHICK:
                ypos += (arrowUnit + style.graphic.thickness*4d)/4
                break
            case THINTHIN:
                ypos += (arrowUnit + style.graphic.thickness*3d)/4
                break
            }
            return drawArrow( page, builder, style, xpos1, xpos2, ypos)
            break
        case DOUBLEARROW:
            // shift everything down to account for the arrowhead thickness
            switch( style.graphic.pattern) {
            case SINGLE:
                ypos += (arrowUnit + style.graphic.thickness)/4
                break
            case THICKBETWEENTHIN:
                ypos += (arrowUnit + style.graphic.thickness*5d)/4
                break
            case THICKTHIN:
            case THINTHICK:
                ypos += (arrowUnit + style.graphic.thickness*4d)/4
                break
            case THINTHIN:
                ypos += (arrowUnit + style.graphic.thickness*3d)/4
                break
            }
            return drawDoubleArrow( page, builder, style, xpos1, xpos2, ypos)
            break
        case DOTTED:
            // shift everything down to account for the dot thickness
            switch( style.graphic.pattern) {
            case SINGLE:
                ypos += (dotUnit + style.graphic.thickness/2d)/2
                break
            case THICKBETWEENTHIN:
                ypos += (dotUnit + style.graphic.thickness*3d)/2
                break
            case THICKTHIN:
            case THINTHICK:
                ypos += (dotUnit + style.graphic.thickness*2d)/2
                break
            case THINTHIN:
                ypos += (dotUnit + style.graphic.thickness*1.5d)/2
                break
            }
            return drawDotted( page, builder, style, xpos1, xpos2, ypos, compList)
            break
        case NOGRAPHIC:
        default:
            return null
        }
    }

    static private drawBaseLine( builder, style, xpos1, xpos2, ypos1, ypos2, thickness) {
        def propList = [:]
        def gr = []

        switch( style.graphic.line) {
        case NOLINE:
//                propList.strokeWidth = 0i
//                propList.strokeDashArray = 0i
            break
        case DASH:
            propList.strokeDashArray = [ 6d, 4d ]
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        case DASHDOT:
            propList.strokeDashArray = [6d, 4d, 1.5d,4d ]
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        case DASHDOTDOT:
            propList.strokeDashArray = [6d, 4d, 1.5d, 4d, 1.5d,4d ]
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        case LONGDASH:
            propList.strokeDashArray = [ 10d, 5d ]
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        case LONGDASHDOT:
            propList.strokeDashArray = [10d, 5d, 1.5d, 5d ]
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        case ROUNDDOT:
            propList.strokeDashArray =[1d, 5d ]
            propList.strokeLineCap = StrokeLineCap.ROUND
            break
        case SOLID:
//                propList.strokeDashArray = 0i
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        case SQUAREDOT:
            propList.strokeDashArray = [1.5d, 4d ]
            propList.strokeLineCap = StrokeLineCap.BUTT
            break
        }

        switch( style.graphic.pattern) {
        case SINGLE:
            propList += [ startX: xpos1,
                          startY: ypos1,
                          endX: xpos2,
                          endY: ypos2,
                          strokeWidth: thickness,
                          style: style.graphic,
                        ]
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            break
        case THICKBETWEENTHIN:
            propList += [ startX: xpos1,
                          startY: ypos1 - thickness*2.5d,
                          endX: xpos2,
                          endY: ypos2 - thickness*2.5d,
                          strokeWidth: thickness,
                          strokeLineCap: StrokeLineCap.BUTT,
                          style: style.graphic,
                        ]
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            propList.startY = ypos1
            propList.endY = ypos2
            propList.strokeWidth = thickness*2
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            propList.startY = ypos1 + thickness*2.5d
            propList.endY = ypos2 + thickness*2.5d
            propList.strokeWidth = thickness
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            break
        case THICKTHIN:
            propList += [ startX: xpos1,
                          startY: ypos1 - thickness,
                          endX: xpos2,
                          endY: ypos2 - thickness,
                          strokeWidth: thickness*2,
                          strokeLineCap: StrokeLineCap.BUTT,
                          style: style.graphic,
                        ]
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            propList.startY = ypos1 + thickness*1.5d
            propList.endY = ypos2 + thickness*1.5d
            propList.strokeWidth = thickness
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            break
        case THINTHICK:
            propList += [ startX: xpos1,
                          startY: ypos1 - thickness*1.5d,
                          endX: xpos2,
                          endY: ypos2 - thickness*1.5d,
                          strokeWidth: thickness,
                          strokeLineCap: StrokeLineCap.BUTT,
                          style: style.graphic,
                        ]
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            propList.startY = ypos1 + thickness
            propList.endY = ypos2 + thickness
            propList.strokeWidth = thickness*2
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            break
        case THINTHIN:
            propList += [ startX: xpos1,
                          startY: ypos1 -thickness,
                          endX: xpos2,
                          endY: ypos2 - thickness,
                          strokeWidth: thickness,
                          strokeLineCap: StrokeLineCap.BUTT,
                          style: style.graphic,
                        ]
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            propList.startY = ypos1 + thickness
            propList.endY = ypos2 + thickness
            gr << JfxUtils.createShape( builder,
                                        'Line',
                                        propList,
                                        null
                                      )
            break
        }

        return gr
    }


    static public drawArrow( page, builder, style, xpos1, xpos2, ypos) {
        def thickness = style.graphic.thickness*0.75d
        def gr
        def gr1 = []
        def arrowSize = arrowUnit

        // adjust the arrowUnit to account for line thickness
        switch( style.graphic.pattern) {
        case SINGLE:
            arrowSize += thickness
            break
        case THICKBETWEENTHIN:
            arrowSize += thickness * 5d
            break
        case THICKTHIN:
        case THINTHICK:
            arrowSize += thickness * 4d
            break
        case THINTHIN:
            arrowSize += thickness * 3d
            break
        }

        // shorten the line so it just touches the arrowhead
        // otherwise a thick line (specified by the user) might show
        // underneath the tip of the arrow
        if ( xpos2 > xpos1) {
            xpos2 -= arrowSize
        } else {
            // in this case, the size will be negative - causing the line to be drawn to the left
            // so to shorten it, we have to *add* the arrowhead width
            xpos2 += arrowSize
        }

        gr1.addAll drawBaseLine( builder, style, xpos1, xpos2, ypos, ypos, thickness)

        // now draw the arrowhead
        if ( xpos2 > xpos1) {
            // arrow on the right
            gr1 << JfxUtils.createShape( builder,
                                         'Polygon',
                                         [ points: [ xpos2,             ypos - (arrowSize/2i),
                                                     xpos2 + arrowSize, ypos,
                                                     xpos2,             ypos + (arrowSize/2i)
                                                   ],
                                           style: style.graphic
                                         ],
                                         null
                                       )
        } else {
            // arrow on the left
            gr1 << JfxUtils.createShape( builder,
                                         'Polygon',
                                         [ points: [ xpos2,             ypos - (arrowSize/2i),
                                                     xpos2 - arrowSize, ypos,
                                                     xpos2,             ypos + (arrowSize/2i)
                                                   ],
                                           style: style.graphic
                                         ],
                                         null
                                       )
        }

        // group the pieces into one graphics
//        gr = JfxUtils.createShape( builder, 'Group', null, null)
//
//        gr.children.addAll gr1
        gr = JfxUtils.buildGroup( builder, page, null, gr1 )
        return gr
    }

    static public drawDoubleArrow( page, builder, style, xpos1, xpos2, ypos) {
        def thickness = style.graphic.thickness*0.75d
        def gr
        def gr1 = []
        def arrowSize = arrowUnit

        // adjust the arrowUnit to account for line thickness
        switch( style.graphic.pattern) {
        case SINGLE:
            arrowSize += thickness
            break
        case THICKBETWEENTHIN:
            arrowSize += thickness * 5d
            break
        case THICKTHIN:
        case THINTHICK:
            arrowSize += thickness * 4d
            break
        case THINTHIN:
            arrowSize += thickness * 3d
            break
        }

        // since there's an arrow at each end, the direction doesn't matter
        // normalize the x1 and x2 so x1 < x2
        if (xpos1 > xpos2) (xpos1, xpos2) = [ xpos2, xpos1]

        // shorten the line so it just touches the arrowheads
        // otherwise a thick line (specified by the user) might show
        // underneath the tip of the arrow
        xpos1 += arrowSize
        xpos2 -= arrowSize


        gr1.addAll drawBaseLine( builder, style, xpos1, xpos2, ypos, ypos, thickness)

        // now draw both arrowheads
        // arrow on the right
        gr1 << JfxUtils.createShape( builder,
                                     'Polygon',
                                     [ points: [ xpos2,             ypos - (arrowSize/2i),
                                                 xpos2 + arrowSize, ypos,
                                                 xpos2,             ypos + (arrowSize/2i)
                                               ],
                                       style: style.graphic
                                     ],
                                     null
                                   )
        // arrow on the left
        gr1 << JfxUtils.createShape( builder,
                                     'Polygon',
                                     [ points: [ xpos1            , ypos - (arrowSize/2i),
                                                 xpos1 - arrowSize, ypos,
                                                 xpos1,             ypos + (arrowSize/2i)
                                               ],
                                        style: style.graphic
                                     ],
                                     null
                                   )

        // and group the graphic pieces together
        gr = JfxUtils.buildGroup( builder, page, null, gr1 )
        return gr
    }

    static public drawDotted( page, builder, style, xpos1, xpos2, ypos, compList) {
        def thickness = style.graphic.thickness*0.75d
        def gr = []
        def dotSize = dotUnit

        // adjust the dotUnit to account for line thickness
        switch( style.graphic.pattern) {
        case SINGLE:
            dotSize += thickness/2d
            break
        case THICKBETWEENTHIN:
            dotSize += thickness*3d
            break
        case THICKTHIN:
        case THINTHICK:
            dotSize += thickness*2d
            break
        case THINTHIN:
            dotSize += thickness*1.5d
            break
        }

        gr.addAll drawBaseLine( builder, style, xpos1, xpos2, ypos, ypos, thickness)

        // add the dots, one per node
        compList.each { x ->
            gr << JfxUtils.createShape( builder,
                                        'Ellipse',
                                        [ centerX: x,
                                          centerY: ypos,
                                          radiusX: dotSize*2d,
                                          radiusY: dotSize,
                                          style: style.graphic,
                                        ],
                                        null
                                      )
        }
        gr = JfxUtils.buildGroup( builder, page, null, gr )
        return gr
    }
}
