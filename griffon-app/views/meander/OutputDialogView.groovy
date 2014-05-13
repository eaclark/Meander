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
package meander

import net.miginfocom.swing.MigLayout

actions {
  action( id: 'genImpress',
          name: 'GenODP',
          closure: controller.actions.genImpress,
          accelerator: shortcut('G'),
          mnemonic: 'G',
          shortDescription: "Generate the Impress file"
  )

  action( id: 'canImpress',
          name: 'CanODP',
          closure: controller.actions.canImpress,
          accelerator: shortcut('C'),
          mnemonic: 'C',
          shortDescription: "Cancel the Impress file generation"
  )

  action( id: 'genPowerpoint',
          name: 'GenPPT',
          closure: controller.actions.genPowerpoint,
          accelerator: shortcut('G'),
          mnemonic: 'G',
          shortDescription: "Generate the PowerPoint file"
  )

  action( id: 'canPowerpoint',
          name: 'CanPPT',
          closure: controller.actions.canImpress,
          accelerator: shortcut('C'),
          mnemonic: 'C',
          shortDescription: "Cancel the PowerPoint file generation"
  )
}

dialog( title: 'Impress Generator', id: "impressDialog",
        modal: false, pack:true, locationByPlatform:true) {
    panel(id:'odpDlg', layout: new MigLayout('fill'), border: etchedBorder()) {
        label( text: ' test ', constraints: 'align center, wrap, spanx')
        button( id: 'odpGen', action: genImpress)
        button( id: 'odpCan', action: canImpress)
    }
}

dialog( title: 'PowerPoint Generator', id: "powerpointDialog",
        modal: false, pack:true, locationByPlatform:true) {
    panel(id:'pptDlg', layout: new MigLayout('fill'), border: etchedBorder()) {
        label( text: ' test ', constraints: 'align center, wrap, spanx')
        button( id: 'pptGen', action: genPowerpoint)
        button( id: 'pptCan', action: canPowerpoint)
    }
}
