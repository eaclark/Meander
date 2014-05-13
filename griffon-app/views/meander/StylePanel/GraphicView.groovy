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

import static meander.GraphicType.*

import javax.swing.JOptionPane
import meander.GraphicType
import meander.LinePatternType
import meander.LineType

/**
 * GUI tab for graphic specification
 */
label( text: 'Graphic Type', constraints: 'cell 0 0, right')
comboBox( id: 'graphicgtcb',
          constraints: 'cell 1 0, left',
          enabled: true,
          items: GraphicType.values().toList(),
          selectedItem: bind( 'graphic', source: model.currStyle.graphic ),
          actionPerformed: {
              model.currStyle.graphic.graphic = graphicgtcb.selectedItem
              drawExample()
          }
        )

label( text: 'Line Type', constraints: 'cell 0 1, right')
comboBox( id: 'graphicltcb',
          constraints: 'cell 1 1, left',
          enabled: bind( source: graphicgtcb, sourceProperty: 'selectedItem', converter: { it != NOGRAPHIC }),
          items: LineType.values().toList(),
          selectedItem: bind( 'line', source: model.currStyle.graphic ),
          actionPerformed: {
              model.currStyle.graphic.line = graphicltcb.selectedItem
              drawExample()
          }
        )

label( text: 'Line Pattern', constraints: 'cell 0 2, right')
comboBox( id: 'graphiclpcb',
          constraints: 'cell 1 2, left',
          enabled: bind( source: graphicgtcb, sourceProperty: 'selectedItem', converter: { it != NOGRAPHIC }),
          items: LinePatternType.values().toList(),
          selectedItem: bind( 'pattern', source: model.currStyle.graphic ),
          actionPerformed: {
              model.currStyle.graphic.pattern = graphiclpcb.selectedItem
              drawExample()
          }
        )

label( text: 'Thickness', constraints: 'cell 2 1, right')
textField( id: 'graphictt',
           constraints: 'cell 3 1, left',
           enabled: bind( source: graphicgtcb, sourceProperty: 'selectedItem', converter: { it != NOGRAPHIC }),
           text: bind( source: model.currStyle.graphic, sourceProperty: 'thickness' ),
           actionPerformed: {
               // make sure the user put in a number, otherwise just the the old value back
               if (graphictt.text.isFloat()) {
                   def f = graphictt.text.toFloat()
                   if ( f < 0) f = 0
                   model.currStyle.graphic.thickness = f
                   drawExample()
               } else graphictt.text = String.format('%1.2f', model.currStyle.graphic.thickness)
           },
           columns: 3)

textArea( id: 'graphicfca', constraints: 'cell 2 2, right',
          background: bind( 'color', source: model.currStyle.graphic),
          columns: 2)
button( id: 'graphicfcb',
        constraints: 'cell 3 2, left',
        enabled: bind( source: model.currStyle.graphic,
                       sourceProperty: 'graphic',
                       converter: { val -> val != NOGRAPHIC }
                     ),
        text: 'Color',
        actionPerformed: { c.setColor( graphicfca.getBackground())
                           cd.show()
                           if ( cp.getValue() == JOptionPane.OK_OPTION) {
                               model.currStyle.graphic.color = c.getColor()
                               drawExample()
                           }
                         }
      )
