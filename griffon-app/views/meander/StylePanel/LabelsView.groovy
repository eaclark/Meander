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

import net.miginfocom.swing.MigLayout
import javax.swing.border.TitledBorder
import meander.LabelType

/**
 * GUI tab for label style specification
 */

panel( constraints: 'align center', layout: new MigLayout()) {
    panel( border: new TitledBorder( 'Event Label'), layout: new MigLayout()){
        label( text: '<html>Label<p>Placement</html>', constraints: 'cell 0 0, right')
        comboBox( id: 'labeltcb',
                  constraints: 'cell 1 0, left',
                  enabled: true,
                  items: LabelType.values().toList(),
                  selectedItem: bind( 'labelType', source: model.currStyle.label),
//                                      mutual: true, converter: { it }, reverseConverter: { it },
                  actionPerformed: {
                      model.currStyle.label.labelType = labeltcb.selectedItem
                      model.exampleOutOfDate = true
                      drawExample()
                  }
                )
        textField( id: 'labelftf',
                   constraints: 'cell 0 1, center, growx, spanx',
                   text: bind( 'labelFormat', source: model.currStyle.label, mutual: true),
                   actionPerformed: { drawExample() }
                 )

    }

    panel( border: new TitledBorder( 'Timestamp Label'), layout: new MigLayout()){
        label( text: '<html>Timestamp<p>Placement</html>', constraints: 'cell 0 0, right', enabled: false)
        comboBox( id: 'tstcb',
                  constraints: 'cell 1 0, left',
                  enabled: false,
                  items: LabelType.values().toList(),
                  selectedItem: bind( 'labelType', source: model.currStyle.timestamp),
//                                      mutual: true, converter: { it }, reverseConverter: { it }),
                  actionPerformed: {
                      model.currStyle.label.labelType = labeltcb.selectedItem
                      model.exampleOutOfDate = true //examplegp.repaint()
                  }
                )
        textField( id: 'tstampftf',
                   constraints: 'cell 0 1, center, growx, spanx',
                   enabled: false,
                   text: bind( 'labelFormat', source: model.currStyle.timestamp, mutual: true))


    }
}
