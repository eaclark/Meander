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

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import meander.Jfx.JfxUtils

/**
 * build the example background used on the StylePanel to demonstrate the selected style
 */
// do the background
exampleBackground = { gbuilder, view, model ->
    gbuilder.with {
        r = rectangle( fill: white,
                       x: 3, y: 3,
                       width: view.examplegp.width - 1,
                       height: view.examplegp.height - 1
        )
        currentScene.root.children.add r

        ( 1 .. model.currNodeCount).each { nodenum ->
            def borderWidth, borderColor
            def lineWidth, lineColor
            def node_style


            // handle the node boxes
            def fw, fp, tw, th
            if ( view.styleSelectGroup.selection.actionCommand == 'node') {
                node_style = model.currStyle
            } else {
                node_style = app.models.Meander.styles.editStyles['node']
            }
            borderWidth = node_style.border.thickness
            borderColor = node_style.border.color
            // and now the node lines
            lineWidth = node_style.border.thickness
            lineColor = node_style.border.color

            t = text( id: "cl$nodenum", text: "Node$nodenum",
                      fill: Color.rgb( node_style.text.color.red,
                                       node_style.text.color.green,
                                       node_style.text.color.blue),
                    )

            fw = FontWeight.NORMAL
            fp = FontPosture.REGULAR
            if ( node_style.text.bold) fw = FontWeight.BOLD
            if ( node_style.text.italic) fp = FontPosture.ITALIC
            t.font = Font.font( node_style.text.face as String,
                                fw,
                                fp,
                                node_style.text.size as Double)
            bbox = t.layoutBounds
            tw = bbox.width
            th = bbox.height
            t.x = (model.nodeX1*nodenum - model.nodeX2 - (tw/2)) as Integer
            t.y = model.nodeY
            currentScene.root.children.add t

            r = rectangle( id: "nodeR$nodenum",
                           fill: JfxUtils.initFxGrad(gbuilder, node_style),
                           strokeWidth: borderWidth,
                           stroke: Color.rgb( borderColor.red, borderColor.green, borderColor.blue),
                           x: t.x - 3,
                           y: t.y - th,
                           width: tw + 6,
                           height: th + 6
            )
            currentScene.root.children.add r

            // bring the text in front of the rectangle
            t.toFront()


            l = line( id: "nodeL$nodenum",
                      strokeWidth: lineWidth,
                      stroke: Color.rgb( lineColor.red, lineColor.green, lineColor.blue),
                      startX: model.nodeX1*nodenum - model.nodeX2, startY: r.y + r.height,
                      endX: model.nodeX1*nodenum - model.nodeX2, endY: examplegp.height - 25
            )
            currentScene.root.children.add l
        }
    }
}
