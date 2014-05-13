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

import javax.swing.UIManager
import javax.swing.JOptionPane
import java.awt.GraphicsEnvironment
import java.awt.Font
import net.miginfocom.swing.MigLayout
import javax.swing.border.LineBorder
import javax.swing.border.EtchedBorder

/**
 * GUI tab for text style specification
 */

def newFont() {
    if ((ff?.selectedItem == null) || (fs?.selectedItem == null)) {
        return new Font( 'Times New Roman',
                         Font.PLAIN,
                         (int) 10)
    }
    if (fb.selected && fi.selected) {
        return new Font( ff.selectedItem as String,
                       Font.BOLD + Font.ITALIC,
                         fs.selectedItem as int)
    } else if (fb.selected) {
        return new Font( ff.selectedItem as String,
                         Font.BOLD,
                         fs.selectedItem as int)
    } else if (fi.selected) {
        return new Font( ff.selectedItem as String,
                       Font.ITALIC,
                         fs.selectedItem as int)
    } else {
        return new Font( ff.selectedItem as String,
                         Font.PLAIN,
                         fs.selectedItem as int)
    }
}

def fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()

panel( constraints: 'align center', layout: new MigLayout()) {
    panel( constraints: 'wrap') {
        panel() {
            label( text: 'Font Family')
            comboBox( id: 'ff',
                      items: fontNames,
                      selectedItem: bind( 'face', source: model.currStyle.text),
                      actionPerformed: {
//                          fl.font = newFont()
                          model.currStyle.text.face = ff.selectedItem
                          drawExample()
                      }
                    )
        }

        panel() {
            label( text: 'Font Size')
            comboBox( id: 'fs',
                      items: 6 .. 60,
                      selectedItem: bind( 'size', source: model.currStyle.text),
                      actionPerformed: {
//                          fl.font = newFont()
                          model.currStyle.text.size = fs.selectedItem
                          drawExample()
                      }
                    )
        }
        panel() {
            textArea( id: 'fca',
                      background: bind( 'color', source: model.currStyle.text),
                      columns: 2)
            button( text: 'Color',
                    actionPerformed: {
                          c.setColor( fca.getBackground())
                          cd.show()
                          if ( cp.getValue() == JOptionPane.OK_OPTION) {
                              model.currStyle.text.color = c.getColor()
                              drawExample()
                          }
                    }
                  )
        }
    }

    panel( constraints: 'growx, wrap', layout: new MigLayout('fill'), border: new EtchedBorder() ) {
        checkBox( id: 'fb',
                  constraints: 'center',
                  label: 'Bold',
                  selected: bind( 'bold', source: model.currStyle.text, mutual: true),
                  actionPerformed: {
//                      fl.font = newFont()
                      drawExample()
                  })
        checkBox( id: 'fi',
                  constraints: 'center',
                  label: 'Italics',
                  selected: bind( 'italic', source: model.currStyle.text, mutual: true),
                  actionPerformed: {
//                      fl.font = newFont()
                      drawExample()
                  })
        checkBox( id: 'textwrap',
                  constraints: 'center',
                  selected: bind( 'wrap', source: model.currStyle, mutual: true),
                  label: '<html>wrap<p>text</html>',
                  actionPerformed: { drawExample() })
    }

    panel( constraints: 'growx, wrap') {
        panel() {
            label( 'Trim Length')
            textField( id: 'trimvalue',
                       text: bind( 'trimLen',
                                   target: model.currStyle,
                                   validator: { trimvalue.text.isInteger()},
                                   converter: { v -> v.toInteger()},
                                   reverseConverter: { v -> v.toString()},
                                   mutual: true),
                       actionPerformed: { drawExample() },
                       columns: 2 )
        }

        panel( ) {
            label( '<html>Terse Line<br>Count</html>')
            textField( id: 'tersecount',
                       text: bind( 'terseLen',
                                   target: model.currStyle,
                                   validator: { tersecount.text.isInteger()},
                                   converter: { v -> v.toInteger()},
                                   reverseConverter: { v -> v.toString()},
                                   mutual: true),
                       actionPerformed: { drawExample() },
                       columns: 2)
        }

        panel() {
            label( '<html>Lines Above<br>Graphic</html>')
            textField( id: 'laValue',
                       text: bind( 'linesAbove',
                                   target: model.currStyle,
                                   validator: { laValue.text.isInteger()},
                                   converter: { v -> v.toInteger()},
                                   reverseConverter: { v -> v.toString()},
                                   mutual: true),
                       actionPerformed: { drawExample() },
                       columns: 2)
        }
    }

//    currFontName = UIManager.getDefaults().'Label.font'.name
//    currFontSize = UIManager.getDefaults().'Label.font'.size
//    label( id: 'fl',
//           constraints: 'spanx, center',
//           text: bind( 'color', source: model.currStyle.text,
//                       converter: {
//                           fl.setForeground( it)
////                           "This is a test of $currFontName with size $currFontSize"
//                           "This is a test of ${model.currStyle.text.face} with size ${model.currStyle.text.size}"
//                       }
//                     )
//         )
}
