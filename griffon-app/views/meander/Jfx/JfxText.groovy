package meander.Jfx

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import meander.AlignmentType
import meander.PlacementType
import meander.Style
import org.fusesource.jansi.Ansi

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/3/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
class JfxText {

    static public drawPositioned( String str, page, builder, Style style, ip, width) {
        def fontInfo = [:]
        def text

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     [ text: str,
                                       style: style.text,
                                       alignment: style.alignment,
                                       wrappingWidth: width
                                     ],
                                     JfxUtils.createPoint( ip.x, ip.y)
                                   )

        return text
    }

    static public draw( String str, builder, Style style, nodeInfo, xPosInfo, currY) {
        int width, nodeWidth
        def fontInfo = [:]
        def textShapeInfo = [:]
        def text
        def backingRect
        def newPos

        switch ( style.placement) {
        case PlacementType.SRCJUST:
//            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
//            textShapeInfo.TextAutoGrowWidth = true
            break
        case PlacementType.DESTJUST:
//            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
//            textShapeInfo.TextAutoGrowWidth = true
            break
        case PlacementType.MIDDLE:
     //       if (nodeInfo.dest == null) {
                // if there is no 'destination' node, then the maximum width should be fixed
            textShapeInfo.maxWidth = xPosInfo.width
//                textShapeInfo.minWidth = xPosInfo.width
     //       }

            break
        case PlacementType.OVERLAP:
            textShapeInfo.wrappingWidth = xPosInfo.width
            textShapeInfo.minWidth = xPosInfo.width
//            textShapeInfo.maxWidth = xPosInfo.width
            break
        case PlacementType.SPAN:
            textShapeInfo.wrappingWidth = xPosInfo.width
            textShapeInfo.minWidth = xPosInfo.width
//            textShapeInfo.maxWidth = xPosInfo.width
            break
        }

        textShapeInfo.text = str
        textShapeInfo.style = style.text
        textShapeInfo.alignment = style.alignment

        text = JfxUtils.createShape( builder,
                                     'Text',
                                     textShapeInfo,
                                     JfxUtils.createPoint( xPosInfo.x, currY)
                                   )

        return text
    }
}
