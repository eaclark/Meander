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

import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Scene
import net.miginfocom.swing.MigLayout
import javax.swing.JTabbedPane
import javax.swing.border.EtchedBorder
import javax.swing.JOptionPane
import griffon.builder.gfx.*
import griffon.builder.gfx.swing.*
import groovyx.javafx.SceneGraphBuilder
import javafx.embed.swing.JFXPanel

c = colorChooser()
c.setPreviewPanel( panel())
cp = optionPane( message: c,
                 messageType: JOptionPane.QUESTION_MESSAGE,
                 optionType: JOptionPane.OK_CANCEL_OPTION)
cd = cp.createDialog( null, 'Choose a color')

//fillgb = new GfxBuilder()
fillgb = new SceneGraphBuilder()
examplegb = new SceneGraphBuilder()

// define drawExample early
// the real drawExample will be built later
drawExample = {}

def addStyleButtons( type, names) {
    names.each { name ->
        styleSelectGroup.add type.add( radioButton( text: name,
                                                    actionCommand: name,
                                                    selected: (name == model.currNode),
                                                    actionPerformed: {
                                                        // first, save any changes to the example (i.e., src, dst, or text)
                                                        if( model.examplesInfo[ model.currExample]) view.saveExampleSource()
                                                        // disable the source, destination, and text choices for the example
                                                        // they will be enabled appropriately when the new source is updated
                                                        exSrcSrcNode.enabled = false
                                                        exSrcText.enabled = false
                                                        exSrcDestNode.enabled = false

                                                        styleEditLbl.text = name
//                                                        styleSaveBtn.text = "Save $name"
//                                                        styleUndoBtn.text = "Reset $name"

                                                        controller.changeStyle( name)
                                                        doOutside {
                                                            def example = model.styles.editStyles[name].example
                                                            if( model.examplesInfo[ example]) {
                                                                model.currExample = example
                                                                doLater {
                                                                    updateFill()
                                                                    view.updateExampleSource()
                                                                    exSrcSrcNode.enabled = true
                                                                    exSrcText.enabled = true
                                                                    exSrcDestNode.enabled = true
                                                                    drawExample()
                                                                }
                                                            } else doLater {
                                                                updateFill()
                                                            }
                                                        }
                                                    }
                                                  )
                                     )
    }
}

def initStyleButtons( type, names) {
    type.removeAll()
    addStyleButtons( type, names)
    type.revalidate()
}

def initAllStyleButtons( coreNames, userNames) {
    initStyleButtons( coreStyles, coreNames)
    initStyleButtons( userStyles, userNames)
}

def addUserStyleButtons( names=model.userSL) {
    initStyleButtons( userStyles, names)
}

def clearUserStyleButtons( names) {
    initStyleButtons( userStyles, model.userSL)
}
// note: tabGroup and tabName are passed in from the MVC creation on MeanderView.groovy
tabbedPane( tabGroup, selectedIndex: tabGroup.tabCount) {
//frame( title: 'Style Frame', id: 'styleFrame', size: [400,400]) {
    panel( title: tabName,
           id: 'stylesTabs',
           constraints: 'grow',
           layout: new MigLayout('fill')) {

//        bindGroup( id: 'exampleBindings', bound: false )

        buttonGroup( id: 'styleSelectGroup')
        panel( id: 'styleSelect', border: new EtchedBorder(),
               constraints: 'cell 0 0, spany 4, growy, growx 150', layout: new MigLayout( 'wrap')) {
            label( 'Core defined styles')
            panel( id: 'coreStyles', constraints: 'growx', layout: new MigLayout( 'wrap 3', '[] 40 []'))
            separator( constraints: 'growx',)
            label( 'User defined styles')
            panel( id: 'userStyles', constraints: 'growx', layout: new MigLayout( 'wrap 3'))
        }

        panel( id: 'exampleSource', border: new EtchedBorder(),
                constraints: 'cell 1 3, growx, spanx', layout: new MigLayout('fill')) {
            build StylePanel.ExampleSource
        }

        tabbedPane( id: 'styleEdit',
                    border: new EtchedBorder(),
                    tabPlacement: JTabbedPane.TOP,
                    constraints: 'cell 1 0, spany 3, grow') {

            // now add some Style editing tabs starting with font selection
            panel( title: 'Text',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) { build StylePanel.TextView }

            // label treatment
            panel( title: 'Labels',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) { build StylePanel.LabelsView }

            // fill selection
            panel( title: 'Fill',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) {
                widget( id: 'fillgp',
                        constraints: 'cell 2 0, spany, center, w 120!, h 120!',
                        size: [ 120, 120],
//                        new GfxCanvas()
                        new JFXPanel()
                      )
                build StylePanel.FillView
            }

            // border selection
            panel( title: 'Border',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) { build StylePanel.BorderView }

            // placement and alignment selection
            panel( title: '<html>Alignment &<br>Placement</html>',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) { build StylePanel.PlaceAlignView }

            // graphic selection
            panel( title: 'Graphic',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) { build StylePanel.GraphicView }

            // animation selection
            panel( title: 'Animation',
                   constraints: 'growx',
                   layout: new MigLayout( '', 'grow')) { build StylePanel.AnimationView }
        }

        panel( border: new EtchedBorder(), constraints: 'cell 2 0, growx', layout: new MigLayout( 'fill, wrap')) {
            label( id: 'styleEditLbl', text: "Style:", constraints: 'align center')
            label( id: 'styleEditLbl', text: model.currNode, constraints: 'align center')
//            button( id: 'styleSaveBtn', text: "Save ${model.currNode}", constraints: 'align center, w 140!',
            button( id: 'styleSaveBtn', text: "Save", constraints: 'align center',
                    actionPerformed: {
    //                    model.currStyle.copyTo( app.models.Meander.styles.activeStyles[ styleSelectGroup.selection.actionCommand])
                        model.currStyle.copyTo( app.models.Meander.styles.activeStyles[ model.currNode])
                    })
            button( id: 'styleUndoBtn', text: "Undo", constraints: 'align center',
                    actionPerformed: {
                        def srcStyle = app.models.Meander.styles.activeStyles[ styleSelectGroup.selection.actionCommand]
                        srcStyle.copyTo( model.currStyle)
                        srcStyle.copyTo( app.models.Meander.styles.editStyles[ styleSelectGroup.selection.actionCommand])
                    })
        }
        panel( border: new EtchedBorder(), constraints: 'cell 2 1, grow', layout: new MigLayout( 'fill, wrap')) {
            label( 'All Styles', constraints: 'align center')

            button( 'Revert', constraints: 'align center',
                    actionPerformed: {
                        // revert the all styles to their default value
                        model.styles.revertStyles()
                        // 'change' to the same style - this is needed because
                        // this style's options may have been reset.
                        model.styles.copyEditStyle( styleSelectGroup.selection.actionCommand, model.currStyle)
                    }
            )

            button( 'Deep Reset', constraints: 'align center',
                    actionPerformed: {
                        // this will remove any styles added by the user so we need
                        // to move the style selection to a style that will stick around

                        // get the currently selected style
                        def currStyle = styleSelectGroup.selection.actionCommand

                        // reset the all styles to the base initial values
                        model.styles.deepResetStyles()
                        model.userSL.clear()
                        clearUserStyleButtons()

                        // if the style that was previously selected isn't around any more
                        // let's select 'message'
                        if( !model.coreSL.contains( currStyle)) {
                            def btn = styleSelectGroup.elements.find( { it.text == 'message' })
                            if( btn) {
                                // gotta get the button visually selected
                                btn.selected = true
                                // gotta trigger that button's action
                                btn.doClick()
                            }
                        } else {
                            // 'change' to the same style - this is needed because
                            // this style's options may have been reset.
                            model.styles.copyEditStyle( styleSelectGroup.selection.actionCommand, model.currStyle)
                        }
                    }
            )

            button ( id: 'exportStylesBtn', text: 'Export', constraints: 'align center',
                     actionPerformed: controller.exportStyles
//                     actionPerformed: {
//                         def fileChoice = fd.showOpenDialog()
//                         if( fileChoice == fd.APPROVE_OPTION) {
//                             model.styles.exportStyles( view.fd.selectedFile.absolutePath)
//                         }
//                     }
            )

            button ( id: 'importStylesBtn', text: 'Import', constraints: 'align center',
                     actionPerformed: {
                         def oldbutton = fd.approveButtonText
                         fd.approveButtonText = 'Import'
                         def fileChoice = fd.showOpenDialog()
                         fd.approveButtonText = oldbutton
                         if( fileChoice == fd.APPROVE_OPTION) {
                             model.styles.importStyles( view.fd.selectedFile.absolutePath, true)
                         }

                         // check to see if any styles were imported that are not part of
                         // the base set of styles - if so, need to look at adding buttons
                         // to the user style button panel
                         model.userSL = model.styles.defaultStyles.collect { it.key}.sort() - model.coreSL
                         addUserStyleButtons()
                     }
            )
        }

        panel( id: 'styleExample', border: new EtchedBorder(),
               constraints: 'cell 0 4, growx, spanx', layout: new MigLayout('fill')) {
            widget( id: 'examplegp',
                    constraints: 'center, w 792!, h 300!',
                    size: [ model.exampleWidth, model.exampleHeight],
//                    new GfxCanvas()
                    new JFXPanel()
                  )
        }

//        bind( source: tabGroup, sourceProperty: selectedIndex,
//              target: examplegb
//              converter: { println 'I got here\nend ' + fillgb.cl1.calculateShape().bounds.width } )
    }
}

// now set up a binding to funnel the example repaint trigger through one state point
// the binding is initially turned off to allow things to be setup
// the controller will turn it one once everything is up and running
noparent() {
    Platform.runLater { examplegp.scene = new Scene( new Group())}
    bind( source: model, 'exampleOutOfDate',
          converter: { if (it) { drawExample(); examplegp.repaint(); model.exampleOutOfDate = false } },
          bind: false)
}
