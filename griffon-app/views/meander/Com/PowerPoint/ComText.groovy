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

import meander.Style
import meander.AlignmentType
import static meander.PlacementType.*
import org.codehaus.groovy.scriptom.tlb.office2007.powerpoint.PpAutoSize
import org.codehaus.groovy.scriptom.tlb.office2007.MsoVerticalAnchor
import org.codehaus.groovy.scriptom.tlb.office2007.MsoHorizontalAnchor
import org.codehaus.groovy.scriptom.tlb.office2007.MsoTextOrientation
import org.codehaus.groovy.scriptom.tlb.office2007.MsoTriState
import org.codehaus.groovy.scriptom.tlb.office2007.powerpoint.PpParagraphAlignment

/**
 * The different ways to draw Flow related text
 */
class ComText {

    static public drawCentered( String str, page, Style style, ip) {
        def text

        text = page.Shapes.AddShape( MsoTextOrientation.msoTextOrientationHorizontal,
                                     ip.X,
                                     ip.Y,
                                     0,
                                     0
                                   )

        text.TextFrame.VerticalAnchor = MsoVerticalAnchor.msoAnchorTop
        text.TextFrame.AutoSize = PpAutoSize.ppAutoSizeShapeToFitText
        text.TextFrame.HorizontalAnchor = MsoHorizontalAnchor.msoAnchorCenter
        text.TextFrame.WordWrap = MsoTriState.msoFalse
        text.TextFrame.MarginTop = 1
        text.TextFrame.MarginLeft = 2
        text.TextFrame.MarginRight = 2
        text.TextFrame.MarginBottom = 1

        text.TextFrame.TextRange.Font.name = style.text.face
        text.TextFrame.TextRange.Font.size = style.text.size
        text.TextFrame.TextRange.Font.color.RGB = style.text.color.getBlue()*256*256 +
                                                  style.text.color.getGreen()*256 +
                                                  style.text.color.getRed()
        text.TextFrame.TextRange.Font.bold = style.text.bold ? MsoTriState.msoTrue : MsoTriState.msoFalse
        text.TextFrame.TextRange.Font.italic = style.text.italic ? MsoTriState.msoTrue : MsoTriState.msoFalse
        text.TextFrame.TextRange.text = str
        text.height = text.TextFrame.TextRange.BoundHeight
        text.width = text.TextFrame.TextRange.BoundWidth
        text.fill.Visible = MsoTriState.msoFalse
        text.line.Visible = MsoTriState.msoFalse

        // now center the horizontally text about the ip point
//        text.Left = text.Left - text.width/2


        def align
        switch (style.alignment) {
        case AlignmentType.LEFT:
            align = PpParagraphAlignment.ppAlignLeft
            break
        case AlignmentType.CENTER:
            align = PpParagraphAlignment.ppAlignCenter
            break
        case AlignmentType.RIGHT:
            align = PpParagraphAlignment.ppAlignRight
        }
        text.TextFrame.TextRange.ParagraphFormat.Alignment = align

        return text
    }

    static public drawPositioned( String str, page, Style style, ip, width) {
        def text

        text = page.Shapes.AddShape( MsoTextOrientation.msoTextOrientationHorizontal,
                                     ip.X,
                                     ip.Y,
                                     width,
                                     0
                                   )
        def frame = text.TextFrame
        frame.VerticalAnchor = MsoVerticalAnchor.msoAnchorTop
        if ( width == null) {
            frame.AutoSize = PpAutoSize.ppAutoSizeShapeToFitText
            frame.WordWrap = MsoTriState.msoFalse
        } else {
            frame.WordWrap = MsoTriState.msoTrue
        }
        frame.HorizontalAnchor = MsoHorizontalAnchor.msoAnchorNone
        frame.MarginTop = 1
        frame.MarginLeft = 2
        frame.MarginRight = 2
        frame.MarginBottom = 1

        def range = frame.TextRange
        range.Font.name = style.text.face
        range.Font.size = style.text.size
        text.TextFrame.TextRange.Font.color.RGB = style.text.color.getBlue()*256*256 +
                                                  style.text.color.getGreen()*256 +
                                                  style.text.color.getRed()
        range.Font.bold = style.text.bold ? MsoTriState.msoTrue : MsoTriState.msoFalse
        range.Font.italic = style.text.italic ? MsoTriState.msoTrue : MsoTriState.msoFalse

        switch (style.alignment) {
        case AlignmentType.LEFT:    range.ParagraphFormat.Alignment = PpParagraphAlignment.ppAlignLeft; break
        case AlignmentType.CENTER:  range.ParagraphFormat.Alignment = PpParagraphAlignment.ppAlignCenter; break
        case AlignmentType.RIGHT:   range.ParagraphFormat.Alignment = PpParagraphAlignment.ppAlignRight;  break
        }

        // insert the text string
        range.text = str

        text.height = range.BoundHeight
        // if a width was passed in, use it.  Otherwise go with the drawn width
        text.Width = width ?: range.BoundWidth

        text.fill.Visible = MsoTriState.msoFalse
        text.line.Visible = MsoTriState.msoFalse

        return text
    }

    static public draw( String str, page, Style style, nodeInfo, xPosInfo, currY) {
        int width, nodeWidth
        def text


        text = page.Shapes.AddShape( MsoTextOrientation.msoTextOrientationHorizontal,
                                     xPosInfo.x,
                                     currY,
                                     xPosInfo.width,
                                     0
                                   )
        text.TextFrame.VerticalAnchor = MsoVerticalAnchor.msoAnchorTop
        text.TextFrame.AutoSize = PpAutoSize.ppAutoSizeShapeToFitText
        text.TextFrame.HorizontalAnchor = MsoHorizontalAnchor.msoAnchorCenter
        text.TextFrame.WordWrap = MsoTriState.msoFalse
        text.TextFrame.MarginTop = 1
        text.TextFrame.MarginLeft = 2
        text.TextFrame.MarginRight = 2
        text.TextFrame.MarginBottom = 1

        text.TextFrame.TextRange.Font.name = style.text.face
        text.TextFrame.TextRange.Font.size = style.text.size
        text.TextFrame.TextRange.Font.color.RGB = style.text.color.getBlue()*256*256 +
                                                  style.text.color.getGreen()*256 +
                                                  style.text.color.getRed()
        text.TextFrame.TextRange.Font.bold = style.text.bold ? MsoTriState.msoTrue : MsoTriState.msoFalse
        text.TextFrame.TextRange.Font.italic = style.text.italic ? MsoTriState.msoTrue : MsoTriState.msoFalse
        text.TextFrame.TextRange.text = str
        text.height = text.TextFrame.TextRange.BoundHeight
        if (width) text.Width = width//text.width = text.TextFrame.TextRange.BoundWidth
        text.fill.Visible = MsoTriState.msoFalse
        text.line.Visible = MsoTriState.msoFalse

        // determine the horizontal adjust
        switch ( style.placement) {
        case SRCJUST:
//            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
//            textShapeInfo.TextAutoGrowWidth = true
            break
        case DESTJUST:
//            textShapeInfo.TextHorizontalAdjust = TextHorizontalAdjust.LEFT
//            textShapeInfo.TextAutoGrowWidth = true
            break
        case MIDDLE:
            text.TextFrame.HorizontalAnchor = MsoHorizontalAnchor.msoAnchorCenter
            // if the text is narrower than requested, just leave it - otherwise wrap the text and limit the width
            if (text.Width > xPosInfo.width) {
                text.TextFrame.WordWrap = MsoTriState.msoTrue
                text.Width = xPosInfo.width
            }
            break
        case OVERLAP:
            text.TextFrame.HorizontalAnchor = MsoHorizontalAnchor.msoAnchorCenter
            text.TextFrame.WordWrap = MsoTriState.msoTrue
            text.Width = xPosInfo.width
            break
        case SPAN:
            text.TextFrame.HorizontalAnchor = MsoHorizontalAnchor.msoAnchorCenter
            text.TextFrame.WordWrap = MsoTriState.msoTrue
            text.Width = xPosInfo.width
            break
        }

        def align
        switch (style.alignment) {
        case AlignmentType.LEFT:
            align = PpParagraphAlignment.ppAlignLeft
            break
        case AlignmentType.CENTER:
            align = PpParagraphAlignment.ppAlignCenter
            break
        case AlignmentType.RIGHT:
            align = PpParagraphAlignment.ppAlignRight
        }
        text.TextFrame.TextRange.ParagraphFormat.Alignment = align

        return text
    }
}
