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

import java.awt.BorderLayout

actions {
    action( id: 'openAction',
            name: 'Open',
            mnemonic: 'O',
            accelerator: shortcut('0'),
            closure: controller.openFile)
    action( id: 'saveAction',
            name: 'Save',
            mnemonic: 'S',
            accelerator: shortcut('S'),
            closure: controller.saveFile)
    action( id: 'saveAsAction',
            name: 'SaveAs',
            mnemonic: 'A',
            accelerator: shortcut('A'),
            closure: controller.saveAsFile)
    action( id: 'quitAction',
            name: 'Quit',
            mnemonic: 'Q',
            accelerator: shortcut('Q'),
            closure: controller.quitApp)
    action( id: 'buildPreviewAction',
            name: 'Preview',
            mnemonic: 'V',
            accelerator: shortcut('V'),
            closure: controller.buildPreview)
    action( id: 'buildImpressAction',
            name: 'Impress',
            mnemonic: 'I',
            accelerator: shortcut('I'),
            closure: controller.buildImpress)
    action( id: 'buildPowerpointAction',
            name: 'Powerpoint',
            mnemonic: 'P',
            accelerator: shortcut('P'),
            closure: controller.buildPowerpoint)
}

// http://josh-in-antarctica.blogspot.com/2009/10/griffon-tip-mvc-groups.html
//def withMVC(String type, String id, Map params, Closure closure) {
//  closure(buildMVCGroup(params, type, id))
//  destroyMVCGroup(id)
//}

meanderWindow = application( title: 'Meander',
                             size: [320,480],
                             pack: true,
                             //location: [50,50],
                             locationByPlatform: true,
                             iconImage: imageIcon('/meander-icon-48x48.png').image,
                             iconImages: [ imageIcon('/meander-icon-48x48.png').image,
                                           imageIcon('/meander-icon-32x32.png').image,
                                           imageIcon('/meander-icon-16x16.png').image]) {
    // add content here
//    menuBar {
//    menu('File') {
//      menuItem openAction
//      separator()
//      menuItem quitAction
//    }
//    }
    borderLayout()
//    panel( constraints: CENTER) {
//        build DataPanelView
//    }
    tabbedPane( id: 'tabGroup', constraints: BorderLayout.CENTER,
                stateChanged: { evt ->
                    if( evt.source.selectedIndex == 2) {
    //                  println 'I was here with ' + app.views.Styles.fillgb.cl1.text
                    }
                }
              )

    noparent {
        // file dialog
        fd = fileChooser()
    }

    // create the panel used to edit the flow model
    //String mvcId = 'Data' + System.currentTimeMillis()
    def m, v, c      // temporary variables until multiple assignments can handle complex variables
    mvcId = 'Data'
    (m, v, c) = createMVCGroup( 'DataPanel', mvcId, [ tabGroup: view.tabGroup,
                                                      tabName: 'Flow Data',
                                                      pageModels: model.pageModels,
                                                      counts: model.counts,
                                                      baseOptions: model.baseOptions,
                                                      sourceOptions: model.sourceOptions,
                                                      userOptions: model.userOptions,
                                                      stringsModels: model.stringsModels,
                                                      flowEvents: model.flowEvents,
                                                      nodes: model.nodes,
                                                      styles: model.styles,
                                                      fd: fd,
                                                      mvcId: mvcId])
    model.dm = m; model.dv = v; model.dc = c

    // create the panel used to select the flow layout
    //mvcId = 'Layout' + System.currentTimeMillis()
    mvcId = 'Layout'
    (m, v, c) = createMVCGroup( 'LayoutPanel', mvcId, [ tabGroup: view.tabGroup,
                                                        tabName: 'Page Layout',
                                                        pageModels: model.pageModels,
                                                        baseOptions: model.baseOptions,
                                                        sourceOptions: model.sourceOptions,
                                                        userOptions: model.userOptions,
                                                        stringsModels: model.stringsModels,
                                                        styles: model.styles,
                                                        mvcId: mvcId])
    model.lm = m; model.lv = v; model.lc = c
    model.dm.layoutView = v
//    v.layoutFrame.show()

    // create the panel used to edit the flow styles
    //mvcId = 'StyleStack' + System.currentTimeMillis()
    mvcId = 'Styles'
    (m, v, c) = createMVCGroup( 'StylesPanel', mvcId, [ tabGroup: view.tabGroup,
                                                        tabName: 'Flow Styles',
                                                        styles: model.styles,
                                                        fd: fd,
                                                        mvcId: mvcId])
    model.sm = m; model.sv = v; model.sc = c
    model.dm.stylesView = v
    model.dm.stylesModel = m

    view.tabGroup.setSelectedIndex( 0)
//    v.styleFrame.show()

    //  create the dialog used to control 'outputing'
    //  and tie the dialog data to the flow model data
    mvcId = 'OutpuDialog'
    ( m, v, c) = createMVCGroup( 'OutputDialog', mvcId, [ mvcId: mvcId,
                                                          flowEvents: model.flowEvents,
                                                          nodes: model.nodes,
                                                          strings: model.stringsModels[ 'base'],
                                                          options: model.baseOptions,
                                                          pageModel: model.pageModels[ 'base'],
                                                          counts: model.counts,
                                                          styles: model.styles.activeStyles])
    model.om = m; model.ov = v; model.oc = c
//    model.om.flowEvents = model.dm.flowEvents
//    model.om.nodes = model.dm.nodes
//    model.om.styles = model.styles

    //  create the dialog used editing the flow event table
    mvcId = 'TableEdit'
    ( m, v, c) = createMVCGroup( 'TableEdit', mvcId, [ mvcId: mvcId,
                                                       table: model.dv.flowTable,
                                                       flowEvents: model.flowEvents])
    model.tm = m; model.tv = v; model.tc = c
    model.dm.tableEditController = c
    model.dm.tableEditModel = m

    panel(constraints: SOUTH, layout: new MigLayout( '', '[] push [] push []', '')) {
        button( 'Quit', constraints: 'cell 0 0', action: quitAction)

        panel( constraints: 'cell 1 0') {
            button( 'Save', id: 'flowSaveBtn',
                    action: saveAction,
                    enabled: bind { model.dm.hasModifiedContents },
                  ) //actionPerformed: controller.saveFile)
            button( 'Save As', id: 'flowSaveAsBtn',
                    action: saveAsAction,
                    enabled: bind { model.dm.hasModifiedContents },
                  ) //actionPerformed: controller.saveAsFile)
        }

        panel( constraints: 'cell 2 0') {
            button( 'Preview',
                    enabled: bind { model.dm.hasEventContents },
                    action: buildPreviewAction)
            button( 'Impress',
                    enabled: bind { model.dm.hasEventContents },
                    action: buildImpressAction)
            button( text: 'PowerPoint',
                    enabled: bind( source: model.dm, sourceProperty: 'hasEventContents'),
                    action: buildPowerpointAction)
        }
    }

    //  create the dialog used editing the flow event table
    mvcId = 'JfxPreview'
    ( m, v, c) = createMVCGroup( 'JfxPreview', mvcId, [ mvcId: mvcId,
                                                        flowEvents: model.flowEvents,
                                                        nodes: model.nodes,
                                                        strings: model.stringsModels[ 'base'],
                                                        options: model.baseOptions,
                                                        pageModel: model.pageModels[ 'base'],
                                                        counts: model.counts,
                                                        styles: model.styles.activeStyles])
    model.pm = m; model.pv = v; model.pc = c

}
