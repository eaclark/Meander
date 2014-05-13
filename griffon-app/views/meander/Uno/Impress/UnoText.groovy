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

import com.sun.star.awt.Point
import com.sun.star.drawing.TextHorizontalAdjust
import static meander.PlacementType.*
import static meander.AlignmentType.*
import com.sun.star.drawing.TextVerticalAdjust
import com.sun.star.style.ParagraphAdjust
import com.sun.star.awt.Size
import meander.Uno.UnoUtils
import meander.Style
import meander.AlignmentType
import com.sun.star.drawing.XShape

/**
 * routine for the different ways to draw the text associated with flow shapes
 */
class UnoText {

    static public drawCentered( String str, page, pagesSupplier, Style style, ip) {
        def fontInfo = [:]
        def textShapeInfo = [:]
        XShape text

        fontInfo.face = style.text.face
        fontInfo.size = style.text.size
        fontInfo.color = style.text.color
        fontInfo.bold = style.text.bold
        fontInfo.italic = style.text.italic

        textShapeInfo.TextVerticalAdjust = TextVerticalAdjust.TOP
        textShapeInfo.TextAutoGrowHeight = true
        textShapeInfo.TextAutoGrowWidth = true


        textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.CENTER

        def align
        switch (style.alignment) {
        case AlignmentType.LEFT:
            align = ParagraphAdjust.LEFT
            break
        case AlignmentType.CENTER:
            align = ParagraphAdjust.CENTER
            break
        case AlignmentType.RIGHT:
            align = ParagraphAdjust.RIGHT
        }

        text = UnoUtils.insertText( str,
                                    pagesSupplier,
                                    page,
                                    ip,
                                    textShapeInfo,
                                    align,
                                    fontInfo
                                  )
         return text
    }

    static public drawPositioned( String str, page, pagesSupplier, Style style, ip, width) {
        def fontInfo = [:]
        def textShapeInfo = [:]
        XShape text

        fontInfo.face = style.text.face
        fontInfo.size = style.text.size
        fontInfo.color = style.text.color
        fontInfo.bold = style.text.bold
        fontInfo.italic = style.text.italic

        textShapeInfo.TextVerticalAdjust = TextVerticalAdjust.TOP
        textShapeInfo.TextAutoGrowHeight = true
        textShapeInfo.TextAutoGrowWidth = true

        switch (style.alignment) {
        case AlignmentType.LEFT:    textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT;   break
        case AlignmentType.CENTER:  textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.CENTER; break
        case AlignmentType.RIGHT:   textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.RIGHT;  break
        }

        textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
        if (width) textShapeInfo.TextMaximumFrameWidth = width

        def align
        switch (style.alignment) {
        case AlignmentType.LEFT:
            align = ParagraphAdjust.LEFT
            break
        case AlignmentType.CENTER:
            align = ParagraphAdjust.CENTER
            break
        case AlignmentType.RIGHT:
            align = ParagraphAdjust.RIGHT
        }

        text = UnoUtils.insertText( str,
                                    pagesSupplier,
                                    page,
                                    ip,
                                    textShapeInfo,
                                    align,
                                    fontInfo
                                  )
        return text
    }

    static public draw( String str, page, pagesSupplier, Style style, nodeInfo, xPosInfo, currY) {
        int width, nodeWidth
        def fontInfo = [:]
        def textShapeInfo = [:]
        XShape text
        Point ip

        fontInfo.face = style.text.face
        fontInfo.size = style.text.size
        fontInfo.color = style.text.color
        fontInfo.bold = style.text.bold
        fontInfo.italic = style.text.italic

        textShapeInfo.TextVerticalAdjust = TextVerticalAdjust.TOP
        textShapeInfo.TextAutoGrowHeight = true
        textShapeInfo.TextAutoGrowWidth = true //!style.wrap

        // determine the insertion point and the horizontal adjust
        ip = new Point( xPosInfo.x, currY)
        switch ( style.placement) {
        case SRCJUST:
            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
            textShapeInfo.TextAutoGrowWidth = true
            break
        case DESTJUST:
            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
            textShapeInfo.TextAutoGrowWidth = true
            break
        case MIDDLE:
            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.CENTER
            textShapeInfo.TextAutoGrowWidth = true
            if (nodeInfo.dest == null) {
                // if there is no 'destination' node, then the maximum width should be fixed
                textShapeInfo.TextMaximumFrameWidth = xPosInfo.width
            }
            break
        case OVERLAP:
            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.CENTER
            textShapeInfo.TextMaximumFrameWidth = xPosInfo.width
            textShapeInfo.TextMinimumFrameWidth = xPosInfo.width
            break
        case SPAN:
            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.CENTER
            textShapeInfo.TextMaximumFrameWidth = xPosInfo.width
            textShapeInfo.TextMinimumFrameWidth = xPosInfo.width
            break
        }

        def align
        switch (style.alignment) {
        case AlignmentType.LEFT:
            align = ParagraphAdjust.LEFT
            break
        case AlignmentType.CENTER:
            align = ParagraphAdjust.CENTER
            break
        case AlignmentType.RIGHT:
            align = ParagraphAdjust.RIGHT
        }

        text = UnoUtils.insertText( str,
                                    pagesSupplier,
                                    page,
                                    ip,
                                    textShapeInfo,
                                    align,
                                    fontInfo
                                  )

        return text
    }
}
