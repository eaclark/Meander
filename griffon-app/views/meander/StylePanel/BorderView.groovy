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

import static meander.LineType.*

import java.awt.Color
import javax.swing.JOptionPane
import meander.LinePatternType
import meander.LineType

/**
 * GUI tab for border style specification
 */

label( text: 'Line Type', constraints: 'cell 0 0, right')
comboBox( id: 'borderltcb',
          constraints: 'cell 1 0, left',
          enabled: true,
          items: LineType.values().toList(),
          selectedItem: bind( source: model.currStyle.border,
                              sourceProperty: 'line',
//                                  mutual:true
                        ),
          actionPerformed: {
              model.currStyle.border.line = borderltcb.selectedItem
              drawExample()
          }
        )

label( text: 'Line Pattern', constraints: 'cell 0 1, right')
comboBox( id: 'borderlpcb',
          constraints: 'cell 1 1, left',
          enabled: bind( source: borderltcb, sourceProperty: 'selectedItem', converter: { it != NOLINE }),
//          items: LinePatternType.values().toList(),
          items: [ 'Single'],
          selectedItem: bind( source: model.currStyle.border,
                              sourceProperty: 'pattern',
//                                  mutual:true
                        ),
          actionPerformed: {
              model.currStyle.border.pattern = borderlpcb.selectedItem
              drawExample()
          }
        )

label( text: 'Thickness', constraints: 'cell 0 2, right')
textField( enabled: bind( source: borderltcb, sourceProperty: 'selectedItem', converter: { it != NOLINE }),
           constraints: 'cell 1 2, left',
           text: bind( source: model.currStyle.border, sourceProperty: 'thickness'),
           actionPerformed: { drawExample() },
           columns: 3)

textArea( id: 'borderfca', columns: 2, constraints: 'cell 0 3, right',
//          background: Color.WHITE)
          background: bind( 'color', source: model.currStyle.border))
button( id: 'borderfcb',
        constraints: 'cell 1 3, left',
        enabled: bind( source: borderltcb, sourceProperty: 'selectedItem', converter: { it != NOLINE }),
        text: 'Color',
        actionPerformed: { c.setColor( borderfca.getBackground())
                           cd.show()
                           if ( cp.getValue() == JOptionPane.OK_OPTION) {
                               model.currStyle.border.color = c.getColor()
                               drawExample()
                           }
                         }
      )

