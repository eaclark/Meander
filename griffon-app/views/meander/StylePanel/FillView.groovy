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

import ca.odell.glazedlists.swing.EventComboBoxModel
import javafx.scene.Group
import javafx.scene.Scene
import meander.Jfx.JfxUtils

import javax.swing.JOptionPane
import meander.GradientType
import static meander.GradientType.*
import static meander.GradientVariant.*

import java.awt.Dimension

import net.miginfocom.swing.MigLayout
import static meander.FillType.*
import meander.FillType


/**
 * GUI tab for fill specification
 */

updateFill = {
    switch ( model.currStyle.fill.type) {
    case NOFILL:
        fillc1a.enabled = false
        fillc1ll.enabled = false
//        fillc1s.enabled = false
//        fillc1il.enabled = false

        fillc2a.enabled = false
        fillc2ll.enabled = false
//        fillc2s.enabled = false
//        fillc2il.enabled = false

        fillgcb.enabled = false
        fillgs.enabled = false
//        fillgm.enabled = false
        break
    case ONECOLOR:
        fillc1a.enabled = true
        fillc1ll.enabled = true
//        fillc1s.enabled = true
//        fillc1il.enabled = true

        fillc2a.enabled = false
        fillc2ll.enabled = false
//        fillc2s.enabled = false
//        fillc2il.enabled = false

        fillgcb.enabled = true
        fillgs.enabled = true
//        fillgm.enabled = true
        break
    case TWOCOLOR:
        fillc1a.enabled = true
        fillc1ll.enabled = true
//        fillc1s.enabled = true
//        fillc1il.enabled = true

        fillc2a.enabled = true
        fillc2ll.enabled = true
//        fillc2s.enabled = true
//        fillc2il.enabled = true

        fillgcb.enabled = true
        fillgs.enabled = true
//        fillgm.enabled = true
        break
    }

    if( model.currStyle) {
        def fw = fillgp.width
        def fh = fillgp.height
        fillgb.defer {
//            // I would hope that this would work, but it doesn't
//            fillgb.gradDemo.fill = JfxUtils.initFxGrad( fillgb, model.currStyle)

            fillgb.currentScene.root.children.remove gradInnerRect
            gradInnerRect = fillgb.with {
                rectangle( id: 'gradDemo',
                        fill: JfxUtils.initFxGrad( fillgb, model.currStyle),
                        x: 5, y: 5,
                        width: bind { fw - 10 },
                        height: bind { fh - 10 }
                )
            }
            fillgb.currentScene.root.children.add gradInnerRect
            fillgp.repaint()
        }
    }

//    drawExample()
//    examplegp.repaint()
}

panel( constraints: 'cell 0 0, center') {
    label( text: 'Fill Type')
    comboBox( id: 'fillcb',
              items: FillType.values().toList() as java.util.List,
              selectedItem: bind( 'type', source: model.currStyle.fill),
              actionPerformed: {
                  model.currStyle.fill.type = fillcb.selectedItem
                  updateFill()
                  drawExample()
                  //examplegp.repaint()
                  model.exampleOutOfDate = true
            }
          )
}

panel( constraints: 'cell 0 1, center', layout: new MigLayout()) {
    panel( constraints: 'cell 0 0, center', layout: new MigLayout()) {
        comboBox( id: 'fillgcb',
                  constraints: 'wrap, center',
                  enabled: false,
                  items: GradientType.values().toList(),
                  selectedItem: bind( 'gradient', source: model.currStyle.fill),
                  actionPerformed: {
                      model.currStyle.fill.gradient = fillgcb.selectedItem

                      // setup the gradient varient types
                      switch( fillgcb.selectedItem) {
//                      case LINEAR:
//                          model.gradientVarients.clear()
//                          model.gradientVarients.addAll( [ TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT,
//                                                           TOPDOWN, BOTTOMUP, LEFTRIGHT, RIGHTLEFT ].collect { it })
//                          fillvcb.selectedIndex = 0
//                          fillvcb.enabled = true
//                          fillgs.enabled = true
//                          break
//                      case RADIAL:
//                          model.gradientVarients.clear()
//                          model.gradientVarients.addAll( [ TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT, CENTER ].collect { it })
//                          fillvcb.selectedIndex = 0
//                          fillvcb.enabled = true
//                          fillgs.enabled = true
//                          break
//                      case RECTANGULAR:
//                          model.gradientVarients.clear()
//                          model.gradientVarients.addAll( [ TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT, CENTER ].collect { it })
//                          fillvcb.selectedIndex = 0
//                          fillvcb.enabled = true
//                          fillgs.enabled = true
//                          break
                      case DIAGONALDOWN:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ TOPRIGHT, BOTTOMLEFT, AXISIN, AXISOUT ].collect { it })
                          fillvcb.selectedIndex = 0
                          fillvcb.enabled = true
                          fillgs.enabled = true
                          break
                      case DIAGONALUP:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ TOPLEFT, BOTTOMRIGHT, AXISIN, AXISOUT ].collect { it })
                          fillvcb.selectedIndex = 0
                          fillvcb.enabled = true
                          fillgs.enabled = true
                          break
                      case HORIZONTAL:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ TOPDOWN, BOTTOMUP, AXISIN, AXISOUT ].collect { it })
                          fillvcb.selectedIndex = 0
                          fillvcb.enabled = true
                          fillgs.enabled = true
                          break
                          break
                      case FROMCENTER:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ AXISOUT, AXISIN ].collect { it })
//                          model.gradientVarients.addAll( [ AXISOUT, AXISIN, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT].collect { it })
                          fillvcb.selectedIndex = 0
                          fillvcb.enabled = true
                          fillgs.enabled = true
                          break
                      case FROMCORNER:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT ].collect { it })
                          fillvcb.selectedIndex = 0
                          fillvcb.enabled = true
                          fillgs.enabled = true
                          break
                      case VERTICAL:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ LEFTRIGHT, RIGHTLEFT, AXISIN, AXISOUT ].collect { it })
                          fillvcb.selectedIndex = 0
                          fillvcb.enabled = true
                          fillgs.enabled = true
                          break
                      default:
                          model.gradientVarients.clear()
                          model.gradientVarients.addAll( [ NOVARIANT ].collect { it })
                          fillvcb.enabled = false
                          fillgs.enabled = false
                          break
                      }

                      updateFill()
                      drawExample()
                      //examplegp.repaint()
                      model.exampleOutOfDate = true
                  }
                )
        label( text: 'Gradient Type', constraints: 'center')
    }
    panel( constraints: 'cell 1 0, center', layout: new MigLayout()) {
        slider( id: 'fillgs',
                constraints: 'wrap, wmax 100, center',
                enabled: false,
                minimum: 0,
                maximum: 100,
        )
        bind( source: fillgs,
              sourceProperty:'value',
              target:model.currStyle.fill,
              targetProperty:'gradientScale',
              converter: { doLater { updateFill(); drawExample() }; it }
        )
        label( text: 'Gradient Degree', constraints: 'center, wrap')
//        slider( id: 'fillgm',
//                constraints: 'wrap, wmax 100, center',
//                enabled: false,
//                minimum: 0,
//                maximum: 50,
//        )
//        bind( source: fillgm,
//              sourceProperty:'value',
//              target:model.currStyle.fill,
//              targetProperty:'gradientMixing',
//              converter: { doLater { updateFill(); drawExample() }; it }
//        )
//        label( text: 'Gradient Mixing', constraints: 'center')
    }
    panel( constraints: 'cell 2 0, center', layout: new MigLayout()) {
        comboBox( id: 'fillvcb',
                  constraints: 'wrap, center',
                  enabled: false,
//                  items: bind( 'value', source: fillgcb,
//                               converter: {
//                                   println 'in converter with ' + it
//                                   [ 1, 2]
//                               }
//                             ),
//                  items: [1,2,3,4,5],
                  model: new EventComboBoxModel( model.gradientVarients),
//                  selectedItem: bind( 'gradient', source: model.currStyle.fill),
                  actionPerformed: {
                      if( fillvcb.selectedItem ) {
                          model.currStyle.fill.variant = fillvcb.selectedItem
                          updateFill()
                          drawExample()
                          model.exampleOutOfDate = true
                      }
                  }
                )
        label( text: 'Gradient Direction', constraints: 'center')
    }
}



panel( constraints: 'cell 0 2, center', layout: new MigLayout('','[] 100 []')) {
    label( 'Color 1', id: 'fillc1ll', constraints: 'cell 0 0, center')
    panel( id: 'fillc1a',
           constraints: 'cell 0 1, center, wmax 30, hmax 30',
           background: bind( 'foreColor', source: model.currStyle.fill),
           border: compoundBorder([ raisedBevelBorder(), loweredBevelBorder()]),
           minimumSize: new Dimension(60, 60),
           mouseClicked: { e ->
               if( fillc1a.enabled) {
                   c.setColor( fillc1a.getBackground())
                   cd.show()
                   if ( cp.getValue() == JOptionPane.OK_OPTION) {
                       model.currStyle.fill.foreColor = c.getColor()
                       updateFill()
                       drawExample()
                       //examplegp.repaint()
                       model.exampleOutOfDate = true
                   }
                }
           }
    )
//    slider( id: 'fillc1s',
//            constraints: 'cell 0 2, wmax 100, center',
//            minimum: 0,
//            maximum: 100,
//    )
//    bind( source: fillc1s,
//          sourceProperty:'value',
//          target:model.currStyle.fill,
//          targetProperty:'offset1',
//          converter: { doLater { updateFill(); drawExample() }; it }
//    )
//    label( 'Intensity', id: 'fillc1il', constraints: 'cell 0 3, center')

    label( 'Color 2', id: 'fillc2ll', constraints: 'cell 1 0, center')
    panel( id: 'fillc2a',
           constraints: 'cell 1 1, center, wmax 30, hmax 30',
           background: bind( 'backColor', source: model.currStyle.fill),
           border: compoundBorder([ raisedBevelBorder(), loweredBevelBorder()]),
           minimumSize: new Dimension(60, 60),
           mouseClicked: { e ->
               if( fillc2a.enabled ) {
                   c.setColor( fillc2a.getBackground())
                   cd.show()
                   if ( cp.getValue() == JOptionPane.OK_OPTION) {
                       model.currStyle.fill.backColor = c.getColor()
                       updateFill()
                       drawExample()
                       //examplegp.repaint()
                       model.exampleOutOfDate = true
                   }
               }
           }
    )
//    slider( id: 'fillc2s',
//            constraints: 'cell 1 2, wmax 100, center',
//            minimum: 0,
//            maximum: 100,
//    )
//    bind( source: fillc2s,
//            sourceProperty:'value',
//            target:model.currStyle.fill,
//            targetProperty:'offset2',
//            converter: { doLater { updateFill(); drawExample() }; it }
//    )
//    label( 'Intensity', id: 'fillc2il', constraints: 'cell 1 3, center')
}

noparent {
    def fw = fillgp.width
    def fh = fillgp.height

    gradOuterRect = fillgb.with {
        rectangle( id: 'gradDemoOuter',
                   fill: lightgray,
                   x: 3, y: 3,
                   width: bind { fw - 1 },
                   height: bind { fh - 1 }
                 )
    }
    gradInnerRect = fillgb.with {
        rectangle( id: 'gradDemo',
                   fill: JfxUtils.initFxGrad( fillgb, model.currStyle),
                   x: 5, y: 5,
                   width: bind { fw - 10 },
                   height: bind { fh - 10 }
                 )
    }

    fillgb.defer {
        fillgp.scene = new Scene( new Group())
        fillgb.currentScene = fillgp.scene
        fillgb.currentScene.root.children.add gradOuterRect
        fillgb.currentScene.root.children.add gradInnerRect
    }
}

fillgp.repaint()
