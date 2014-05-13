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

import meander.PageType
import net.miginfocom.swing.MigLayout
import javax.swing.border.EtchedBorder
import javax.swing.JTextField
import meander.Options

/**
 * GUI panel for output page control
 */

panel( constraints: 'cell 0 0, spanx, align center, grow') {
  paramGroup = buttonGroup().with {
    // http://josh-in-antarctica.blogspot.com/2009/10/griffon-tip-silly-swingbuilder-tricks.html
    add radioButton( text: 'Source',
                     id: 'optSource',
                     selected: true,
                     actionCommand: 'Source',
                     actionPerformed: {
                         // copy the items in 'base' to 'user' and link from 'source' to 'base'
                         if ( model.baseOptions.mirror == 'user') {
                             Options.bools.each {
                                 model.userOptions."$it" = model.baseOptions."$it"
                             }
                             Options.bools.each {
                                 model.baseOptions."$it" = model.sourceOptions."$it"
                             }
                             model.baseOptions.mirror = 'source'
                             model.sourceOptions.mirror = model.baseOptions
                             model.userOptions.mirror = null

                             model.pageModels['user'].portrait = model.pageModels['base'].portrait
                             model.pageModels['user'].size = model.pageModels['base'].size
                             model.pageModels['base'].portrait = model.pageModels['source'].portrait
                             model.pageModels['base'].size = model.pageModels['source'].size
                         }
                     }
    )
    add radioButton( text: 'Override',
                     id: 'optOverride',
                     actionCommand: 'Override',
                     actionPerformed: {
                         // copy the items in 'base' to 'source' and link from 'user' to 'base'
                         if ( model.baseOptions.mirror == 'source') {
                             Options.bools.each {
                                 model.baseOptions."$it" = model.userOptions."$it"
                             }
                             model.baseOptions.mirror = 'user'
                             model.userOptions.mirror = model.baseOptions
                             model.sourceOptions.mirror = null

                             model.pageModels['base'].portrait = model.pageModels['user'].portrait
                             model.pageModels['base'].size = model.pageModels['user'].size
                         }
                     }
    )
  }
}
panel( constraints: 'cell 0 1, span 1 2, grow', layout: new MigLayout( 'fill'), border: new EtchedBorder()) {
  panel( constraints: 'center', layout: new MigLayout( 'wrap')) {
    orientGroup = buttonGroup().with {
      add radioButton( text: 'Landscape',
                       id: 'optLandscape',
                       enabled: bind { optOverride.selected },
                       selected: bind { !model.pageModels['base'].portrait },
                       actionPerformed: initSliders)
      add radioButton( text: 'Portrait',
                       id: 'optPortrait',
                       enabled: bind { optOverride.selected },
//                       selected: bind { model.pageModels['base'].portrait },
                       selected: bind( source: model.pageModels['base'],
                                       sourceProperty: 'portrait',
                                       mutual:true),
                       actionPerformed: initSliders)
    }
  }
}
panel( constraints: 'cell 1 1, span 1 2, grow', layout: new MigLayout( 'fill'), border: new EtchedBorder()) {
  panel( constraints: 'center', layout: new MigLayout( 'wrap','center')) {
    label( text: 'Page Size', enabled: bind { optOverride.selected })
    ps = comboBox( items: PageType.values().toList(),
                   id: 'optPageSize',
                   selectedItem: bind( source: model.pageModels['base'],
                                       sourceProperty: 'size',
                                       mutual: true),
                   enabled: bind { optOverride.selected },
                   actionPerformed: initSliders
//                   actionPerformed: {
//                     model.pageModels['base'].size = ps.selectedItem
//                     vres = model.pageModels['base'].ypts as Integer
//                     tmslider.maximum = vres
//                     bmslider.maximum = vres
//                     hres = model.pageModels['base'].xpts as Integer
//                     rmslider.maximum = hres
//                     lmslider.maximum = hres
//                     sizeExamplePage()
//                   }
                 )
  }
}
panel( constraints: 'cell 0 3, growx', layout: new MigLayout('','push[fill][][]',''), border: new EtchedBorder()) {
  checkBox( text: 'Trim  ',
            id: 'optTrim',
            enabled: bind { optOverride.selected },
            constraints: 'align right',
            selected: bind( source: model.baseOptions, sourceProperty: 'trim', mutual: true))
  textField( columns: 3,
             id: 'trimValue',
             enabled: bind { optOverride.selected },
             horizontalAlignment: JTextField.RIGHT,
             text: bind( source: model.baseOptions, sourceProperty: 'trimLen'),
             actionPerformed: {
               // make sure the user put in a number greater than or equal to zero
               // otherwise just the the old value back
               if (trimValue.text.isInteger()) {
                 i = trimValue.text.toInteger()
                 if ( i < 0) i = 0
                 model.baseOptions.trimLen = i
               } else trimValue.text = model.baseOptions.trimLen
             }
           )
  label( 'chars  ', enabled: bind { optOverride.selected })
}
panel( constraints: 'cell 1 3, growx', layout: new MigLayout('','push[fill][][]',''), border: new EtchedBorder()) {
  checkBox( text: 'Terse',
            id: 'optTerse',
            enabled: bind { optOverride.selected },
            constraints: 'align right',
            selected: bind( source: model.baseOptions, sourceProperty: 'terse', mutual: true))
  textField( columns: 3,
             id: 'terseValue',
             enabled: bind { optOverride.selected },
             horizontalAlignment: JTextField.RIGHT,
             text: bind( source: model.baseOptions, sourceProperty: 'terseLen'),
             actionPerformed: {
               // make sure the user put in a number greater than or equal to zero
               // otherwise just the the old value back
               if (terseValue.text.isInteger()) {
                 i = terseValue.text.toInteger()
                 if ( i < 0) i = 0
                 model.baseOptions.terseLen = i
               } else terseValue.text = model.baseOptions.terseLen
             }
           )
  label( 'lines   ', enabled: bind { optOverride.selected })
}
panel( constraints: 'cell 0 4, spanx 2, grow', layout: new MigLayout('fill'), border: new EtchedBorder()) {
  panel( constraints: 'center', layout: new MigLayout( 'wrap')) {
    checkBox( text: 'Notes',
              id: 'optNotes',
              enabled: bind { optOverride.selected },
              selected: bind( source: model.baseOptions, sourceProperty: 'notes', mutual: true))
    checkBox( text: 'Postfix',
              id: 'optPostfix',
              enabled: bind { optOverride.selected },
              selected: bind( source: model.baseOptions, sourceProperty: 'postfix', mutual: true))
    checkBox( text: 'Animate',
              id: 'optAnimate',
              enabled: bind { optOverride.selected },
              selected: bind( source: model.baseOptions, sourceProperty: 'animate', mutual: true))
  }
}
