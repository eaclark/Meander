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
package meander.StylePanel

import meander.Jfx.JfxGraphic
import meander.Jfx.JfxUtils

import static meander.FillType.*
import static meander.LinePatternType.*
import static meander.LineType.*
import static meander.GraphicType.*

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight

/**
 * GfxBuilder specification for building the example flow event
 */

exampleMessage = { gbuilder, view, model, style ->
    // get the node index for the source and destination
    def srcIdx = exSrcSrcNode.selectedIndex
    def destIdx = exSrcDestNode.selectedIndex

    def r, t, f, l, fw, fp, p1, p2, grp

    def y_start = 110

    def lblfrmt = style.label.labelFormat

    gbuilder.with {
        if ((style.fill.type != NOFILL)  || (style.border.line != NOLINE)) {
            r = rectangle( id: 'exampleBoundBox',
                           fill: view.initFxGrad( style),
                           strokeWidth: style.border.thickness,
                           stroke: Color.rgb( style.border.color.red,
                                              style.border.color.green,
                                              style.border.color.blue),
                           x: (model.nodeX1 * srcIdx) + model.nodeX2 + 2,
                           y: 100,
                           width: model.nodeW,
                           height: model.nodeH,
                         )


            currentScene.root.children.add r
        }

        // the text
        t = text( id: 'exampleText1', text: ' ',
                  fill: Color.rgb( style.text.color.red,
                                   style.text.color.green,
                                   style.text.color.blue),
                )


        // prep the font
        fw = FontWeight.NORMAL
        fp = FontPosture.REGULAR
        if ( style.text.bold) fw = FontWeight.BOLD
        if ( style.text.italic) fp = FontPosture.ITALIC
        f = Font.font( style.text.face as String,
                       fw,
                       fp,
                       style.text.size as Double)
        t.font = f

        // break the text into individual lines to get
        // at the "terse lines"
        def textStrs = model.examplesInfo[ model.currExample].str.split('\n')
        t.text = textStrs[0]
        t.x = (model.nodeX1 * srcIdx) + model.nodeX2 + 4
        t.y = y_start
        currentScene.root.children.add t

        // check on the graphic to build for this style
        grp = group( id: 'exampleG')
        def x1, x2
            if ( srcIdx < destIdx) {
                x1 = (model.nodeX1 * srcIdx) + model.nodeX2 + 2
                x2 = (model.nodeX1 * destIdx) + model.nodeX2 - 2
            } else {
                x1 = (model.nodeX1 * srcIdx) + model.nodeX2 - 2
                x2 = (model.nodeX1 * destIdx) + model.nodeX2 + 2
            }
        l = JfxGraphic.draw( null, null, gbuilder, style, null, [ x1: x1, x2: x2], 125, [])
        currentScene.root.children.add l
//        switch( style.graphic.graphic) {
//        case ARROW:
//            // the line part of the graphic
//            if ( srcIdx < destIdx) {
//                def x1 = (model.nodeX1 * srcIdx) + model.nodeX2 + 2
//                def x2 = (model.nodeX1 * destIdx) + model.nodeX2 - 2
//                def y1 = 125
//                def y2 = 125
//                l = line( id: "exampleG1",
//                          strokeWidth: style.graphic.thickness,
//                          stroke: Color.rgb( style.graphic.color.red,
//                                             style.graphic.color.green,
//                                             style.graphic.color.blue),
//                          startX: x1,
//                          startY: y1,
//                          endX: x2,
//                          endY: y2
//                        )
//                grp.children.add l
//                // the arrow part of the graphic
//                p1 = polygon( id: 'exampleG2',
//                              strokeWidth: style.graphic.thickness,
//                              stroke: Color.rgb( style.graphic.color.red,
//                                                 style.graphic.color.green,
//                                                 style.graphic.color.blue)
//                            )
//                p1.points.addAll( [(x2 - 10) as Double,
//                                   (y2 -5) as Double,
//                                   x2 as Double,
//                                   y2 as Double,
//                                   (x2 - 10) as Double,
//                                   (y2 + 5) as Double ])
//                grp.children.add p1
//            } else {
//                def x1 = (model.nodeX1 * srcIdx) + model.nodeX2 - 2
//                def x2 = (model.nodeX1 * destIdx) + model.nodeX2 + 2
//                def y1 = 125
//                def y2 = 125
//                l = line( id: "exampleG1",
//                          strokeWidth: style.graphic.thickness,
//                          stroke: Color.rgb( style.graphic.color.red,
//                                             style.graphic.color.green,
//                                             style.graphic.color.blue),
//                          startX: x1,
//                          startY: y1,
//                          endX: x2,
//                          endY: y2
//                        )
//                grp.children.add l
//                // the arrow part of the graphic
//                p1 = polygon( id: 'exampleG2',
//                              strokeWidth: style.graphic.thickness,
//                              stroke: Color.rgb( style.graphic.color.red,
//                                                 style.graphic.color.green,
//                                                 style.graphic.color.blue)
//                            )
//                p1.points.addAll( [(x2 + 10) as Double,
//                                   (y2 - 5) as Double,
//                                   x2 as Double,
//                                   y2 as Double,
//                                   (x2 + 10) as Double,
//                                   (y2 + 5) as Double ])
//                grp.children.add p1
//            }
//            currentScene.root.children.add grp
//            break
//        case DOUBLEARROW:
//            group( id: 'exampleG') {
//                def (left, right) = (srcIdx < destIdx) ? [ srcIdx, destIdx] : [ destIdx, srcIdx]
//                // the line
//                switch( style.graphic.pattern) {
//                case SINGLE:
//                    line( id: "exampleG1",
//                       bw: style.graphic.thickness,
//                       bc: style.graphic.color,
//                       x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                       y1: 125,
//                       x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                       y2: 125
//                     )
//                    break
//
//                case THICKBETWEENTHIN:
//                    line( id: "exampleG1a",
//                       bw: style.graphic.thickness/2,
//                       bc: style.graphic.color,
//                       x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                       y1: 123,
//                       x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                       y2: 123
//                     )
//                    line( id: "exampleG1b",
//                       bw: style.graphic.thickness,
//                       bc: style.graphic.color,
//                       x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                       y1: 125,
//                       x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                       y2: 125
//                     )
//                    line( id: "exampleG1c",
//                       bw: style.graphic.thickness/2,
//                       bc: style.graphic.color,
//                       x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                       y1: 127,
//                       x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                       y2: 127
//                     )
//                    break
//                case THICKTHIN:
//                    line( id: "exampleG1a",
//                           bw: style.graphic.thickness,
//                           bc: style.graphic.color,
//                           x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                           y1: 123,
//                           x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                           y2: 123
//                         )
//                    line( id: "exampleG1b",
//                           bw: style.graphic.thickness/2,
//                           bc: style.graphic.color,
//                           x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                           y1: 127,
//                           x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                           y2: 127
//                         )
//                    break
//                case THINTHICK:
//                    line( id: "exampleG1a",
//                           bw: style.graphic.thickness/2,
//                           bc: style.graphic.color,
//                           x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                           y1: 123,
//                           x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                           y2: 123
//                         )
//                    line( id: "exampleG1b",
//                           bw: style.graphic.thickness,
//                           bc: style.graphic.color,
//                           x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                           y1: 127,
//                           x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                           y2: 127
//                         )
//                    break
//
//                case THINTHIN:
//                    line( id: "exampleG1a",
//                           bw: style.graphic.thickness/2,
//                           bc: style.graphic.color,
//                           x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                           y1: 123,
//                           x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                           y2: 123
//                         )
//                    line( id: "exampleG1b",
//                           bw: style.graphic.thickness/2,
//                           bc: style.graphic.color,
//                           x1: (model.nodeX1 * left) + model.nodeX2 + 2,
//                           y1: 127,
//                           x2: (model.nodeX1 * right) + model.nodeX2 - 2,
//                           y2: 127
//                         )
//                    break
//                }
//                // the left arrow
//                p2 = polygon( id: 'exampleG2',
//                              strokeWidth: style.graphic.thickness,
//                              stroke: Color.rgb( style.graphic.color.red,
//                                                 style.graphic.color.green,
//                                                 style.graphic.color.blue)
//                            )
//                p2.points.addAll( [((model.nodeX1 * left) + model.nodeX2 + 12) as Double,
//                                   120d,
//                                   ((model.nodeX1 * left) + model.nodeX2) as Double,
//                                   120d,
//                                   ((model.nodeX1 * left) + model.nodeX2 + 12) as Double,
//                                   120d ])
//                // the right arrow
//                p1 = polygon( id: 'exampleG2',
//                              strokeWidth: style.graphic.thickness,
//                              stroke: Color.rgb( style.graphic.color.red,
//                                                 style.graphic.color.green,
//                                                  style.graphic.color.blue)
//                            )
//                p1.points.addAll( [((model.nodeX1 * left) + model.nodeX2 - 12) as Double,
//                                   120d,
//                                   ((model.nodeX1 * left) + model.nodeX2 + 12) as Double,
//                                   125d,
//                                   ((model.nodeX1 * left) + model.nodeX2 + 12) as Double,
//                                   130d ])
//            }
//            currentScene.root.children.add grp
//            break
//        case DOTTED:
//            break
//        }


        // and the rest of the message
        t = text( id: 'exampleText2', text: ' ',
                  fill: Color.rgb( style.text.color.red,
                                   style.text.color.green,
                                   style.text.color.blue),
        )
        t.font = f

        t.text = textStrs[ 1 .. -1].join('\n')
        t.x = (model.nodeX1 * srcIdx) + model.nodeX2 + 4
        t.y = 140
        currentScene.root.children.add t

    }
}