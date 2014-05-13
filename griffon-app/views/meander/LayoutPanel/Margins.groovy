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
package meander.LayoutPanel

import net.miginfocom.swing.MigLayout
import javax.swing.JTextField
import javax.swing.JLabel
import javax.swing.JSlider

/**
 * GUI panel holding the margins information and sliders
 */

// Build the top margin interface
panel( constraints: 'cell 1 0', layout: new MigLayout()) {
  // start by getting a slider model to which others can bind
  tms_model = swing.boundedRangeModel( value: 0, minimum: 0, maximum: vres)
  label( constraints: 'w 30::, align right',
         horizontalAlignment: JLabel.RIGHT,
         text: 'pts',
         enabled: bind { optOverride.selected })
  textField( id: 'tmpf',
             enabled: bind { optOverride.selected },
             columns: 4,
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: tms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { tms_model.value }
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max points
               // otherwise just the the old value back
               if (tmpf.text.isInteger()) {
                 i = tmpf.text.toInteger()
                 if ( i < 0) i = 0
                 tmslider.value = (i < vres) ? i : vres
               } else tmpf.text = tms_model.value
             }
           )
  tmslider = slider( model: tms_model,
                     enabled: bind { optOverride.selected },
                     orientation: JSlider.HORIZONTAL)
  swing.bind( source:tmslider,
              sourceProperty:'value',
              target:model.pageModels['base'],
              targetProperty:'top',
              group: sbg,
//                 mutual: true,
              converter: { doLater { gp.repaint() }; it },
//              reverseConverter: { it }
            )
  textField( id: 'tmdf',
             enabled: bind { optOverride.selected },
             columns: 4,
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: tms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { String.format( '%1.1f', tms_model.value * model.pageModels['base'].vlen / vres) }
                       ),
                   actionPerformed: {
                     // make sure the user put in a number smaller than the max dimension
                     // otherwise just the the old value back
                     if (tmdf.text.isFloat()) {
                       f = tmdf.text.toFloat()
                       if ( f < 0) f = 0
                       tmslider.value = (f < model.pageModels['base'].vlen) ? (int)(f * model.pageModels['base'].size.p) : vres
                     } else tmdf.text = String.format( '%1.1f', tms_model.value * model.pageModels['base'].vlen / vres)
                   }
                 )
  label( constraints: 'w 30::, align left',
         horizontalAlignment: JLabel.LEFT,
         enabled: bind { optOverride.selected },
         text: bind( group: sbg,
                     source: model.pageModels['base'],
                     sourceProperty: 'size',
                     converter: { " ${it.d}" }
                   )
       )
}

// Build the right margin interface
panel( constraints: 'cell 2 1', layout: new MigLayout() ) {
  rms_model = swing.boundedRangeModel( value: 0, minimum: 0, maximum: hres)
  textField( id: 'rmdf',
             enabled: bind { optOverride.selected },
             columns: 4,
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: rms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { String.format( '%1.1f', rms_model.value * model.pageModels['base'].hlen / hres) }
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max dimension
               // otherwise just the the old value back
               if (rmdf.text.isFloat()) {
                 f = rmdf.text.toFloat()
                 if ( f < 0f) f = 0f
                   rmslider.value = (f < model.pageModels['base'].hlen) ? (f * model.pageModels['base'].size.p) as Integer : hres
                 } else rmdf.text = String.format( '%1.1f', rms_model.value * model.pageModels['base'].hlen / hres)
               }
           )
  label( constraints: 'w 30::, align left, wrap',
         enabled: bind { optOverride.selected },
         horizontalAlignment: JLabel.LEFT,
         text: bind( group: sbg,
                     source: model.pageModels['base'],
                     sourceProperty: 'size',
                     converter: { " ${it.d}" }
                   )
       )
  rmslider = slider( constraints: 'wrap', model: rms_model,
                     enabled: bind { optOverride.selected },
                     orientation: JSlider.VERTICAL)
  swing.bind( source:rmslider,
              sourceProperty:'value',
              target:model.pageModels['base'],
              targetProperty:'right',
              group: sbg,
              converter: { doLater { gp.repaint() }; it }
            )
  textField( id: 'rmpf',
             enabled: bind { optOverride.selected },
             columns: 4,
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: rms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue:  { rms_model.value }
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max points
               // otherwise just the the old value back
               if (rmpf.text.isInteger()) {
                 i = rmpf.text.toInteger()
                 if ( i < 0) i = 0
                 rmslider.value = (i < hres) ? i : hres
               } else rmpf.text = rms_model.value
             }
           )
  label( constraints: 'w 30::, align left',
         enabled: bind { optOverride.selected },
         horizontalAlignment: JLabel.LEFT,
         text: 'pts')
}

// Build the bottom margin interface
panel( constraints: 'cell 1 2', layout: new MigLayout() ) {
  bms_model = swing.boundedRangeModel( value: 0, minimum: 0, maximum: vres)
  label( constraints: 'w 30::, align right',
         enabled: bind { optOverride.selected },
         horizontalAlignment: JLabel.RIGHT,
         text: 'pts')
  textField( id: 'bmpf',
             columns: 4,
             enabled: bind { optOverride.selected },
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: bms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { bms_model.value }
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max points
               // otherwise just the the old value back
               if (bmpf.text.isInteger()) {
                 i = bmpf.text.toInteger()
                 if ( i < 0) i = 0
                 bmslider.value = (i < vres) ? i : vres
               } else bmpf.text = bms_model.value
             }
           )
  bmslider = slider( model: bms_model,
                     enabled: bind { optOverride.selected },
                     orientation: JSlider.HORIZONTAL)
  swing.bind( source:bmslider,
              sourceProperty:'value',
              target:model.pageModels['base'],
              targetProperty:'bottom',
              group: sbg,
              converter: { doLater { gp.repaint() }; it }
            )
  textField( id: 'bmdf',
             columns: 4,
             enabled: bind { optOverride.selected },
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: bms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { String.format( '%1.1f', bms_model.value * model.pageModels['base'].vlen / vres) }
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max dimension
               // otherwise just the the old value back
               if (bmdf.text.isFloat()) {
                 f = bmdf.text.toFloat()
                 if ( f < 0f) f = 0f
                   bmslider.value = (f < model.pageModels['base'].vlen) ? (f * model.pageModels['base'].size.p) as Integer : vres
                 } else bmdf.text = String.format( '%1.1f', bms_model.value * model.pageModels['base'].vlen / vres)
               }
           )
  label( constraints: 'w 30::, align left, wrap',
         enabled: bind { optOverride.selected },
         horizontalAlignment: JLabel.LEFT,
         text: bind( group: sbg,
                     source: model.pageModels['base'],
                     sourceProperty: 'size',
                     converter: { " ${it.d}" }
                   )
       )
}

// Build the left margin interface
panel( constraints: 'cell 0 1', layout: new MigLayout()) {
  lms_model = swing.boundedRangeModel( value: 0, minimum: 0, maximum: hres)
  label( constraints: 'w 30::, align right',
         enabled: bind { optOverride.selected },
         horizontalAlignment: JLabel.RIGHT,
         text: bind( group: sbg,
                     source: model.pageModels['base'],
                     sourceProperty: 'size',
                     converter: { " ${it.d}" }
                   )
       )
  textField( constraints: 'wrap',
             id: 'lmdf',
             columns: 4,
             enabled: bind { optOverride.selected },
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: lms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { String.format( '%1.1f', lms_model.value * model.pageModels['base'].hlen / hres) }
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max dimension
               // otherwise just the the old value back
               if (lmdf.text.isFloat()) {
                 f = lmdf.text.toFloat()
                 if ( f < 0f) f = 0f
                   lmslider.value = (f < model.pageModels['base'].hlen) ? (f * model.pageModels['base'].size.p) as Integer : hres
                 } else lmdf.text = String.format( '%1.1f', lms_model.value * model.pageModels['base'].hlen / hres)
               }
           )
  lmslider = slider( constraints: 'span, align right, wrap',
                     enabled: bind { optOverride.selected },
                     model: lms_model,
                     orientation: JSlider.VERTICAL,
                     value: bind( target: model.pageModels['base'],
                                  targetProperty: 'left',
//                                   mutual: true,
                                  converter: {doLater { gp.repaint() }; it },
//                                   reverseConverter: {
//                                     it
//                                   }
                                )
                   )
  label( constraints: 'w 30::, align right',
         enabled: bind { optOverride.selected },
         horizontalAlignment: JLabel.RIGHT,
         text: 'pts')
  textField( id: 'lmpf', // left margin points field
             columns: 4,
             enabled: bind { optOverride.selected },
             horizontalAlignment: JTextField.RIGHT,
             text: bind( group: sbg,
                         source: lms_model,
                         sourceEvent: 'stateChanged',
                         sourceValue: { lms_model.value },
                       ),
             actionPerformed: {
               // make sure the user put in a number smaller than the max points
               // otherwise just the the old value back
               if (lmpf.text.isInteger()) {
                 i = lmpf.text.toInteger()
                 if ( i < 0) i = 0
                 lmslider.value = (i < hres) ? i : hres
               } else lmpf.text = lms_model.value
             }
           )
}
