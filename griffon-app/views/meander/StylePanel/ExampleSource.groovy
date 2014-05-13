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
import javax.swing.JScrollPane
import javax.swing.event.DocumentListener

/**
 * GUI panel that allows the user to change the example Flow Event configuration
 */

updateExampleSource = {
    // prep the source node comboBox
    exSrcSrcNode.removeAllItems()
    model.examplesInfo[ model.currExample].srcList.each { exSrcSrcNode.addItem( it ) }
    exSrcSrcNode.selectedItem = model.examplesInfo[ model.currExample].src

    // prep the destination node comboBox
    exSrcDestNode.removeAllItems()
    model.examplesInfo[ model.currExample].destList.each { exSrcDestNode.addItem( it ) }
    exSrcDestNode.selectedItem = model.examplesInfo[ model.currExample].dest

    // update the example text
    exSrcText.text = model.examplesInfo[ model.currExample].str
}

saveExampleSource = {
    model.examplesInfo[ model.currExample].src = exSrcSrcNode.selectedItem
    model.examplesInfo[ model.currExample].dest = exSrcDestNode.selectedItem
    model.examplesInfo[ model.currExample].str = exSrcText.text
}

panel( constraints: '', layout: new MigLayout( 'wrap')) {
    label( text: '<html>Source<br>Node</html>', constraints: 'align center')
    comboBox( id: 'exSrcSrcNode',
              constraints: 'center',
              items: [ ' ', 'Node1', 'Node2', 'Node3', 'Node4'],
              actionPerformed: {
                  if( exSrcSrcNode.enabled) {
                      drawExample()
                      examplegp.repaint()
                  }
              }
            )
}

panel( constraints: '') {
    scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER ) {
    textArea( id: 'exSrcText',
              text: bind { model.currExampleEvent.data },
              columns: 40,
              rows: 4) {
            current.document.addDocumentListener( [
                insertUpdate: { e-> if( exSrcText.enabled) drawExample() },
                removeUpdate:{e-> if( exSrcText.enabled) drawExample() },
                changedUpdate:{e-> if( exSrcText.enabled) drawExample() }
            ] as DocumentListener )
        }
    }
}

panel( constraints: '', layout: new MigLayout( 'wrap')) {
    label( text: '<html>Destination<br>Node</html>', constraints: 'align center')
    comboBox( id: 'exSrcDestNode',
              items: [ ' ', 'Node1', 'Node2', 'Node3', 'Node4', 1, 2, 3],
              actionPerformed: {
                  if( exSrcDestNode.enabled) {
                      drawExample()
                      examplegp.repaint()
                  }
              }
            )
}
