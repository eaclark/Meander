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
package meander.DataPanel

import net.miginfocom.swing.MigLayout
import javax.swing.tree.DefaultTreeCellRenderer
//import org.odftoolkit.odfdom.doc.OdfDocument
//import org.odftoolkit.odfdom.doc.OdfTextDocument
//import org.odftoolkit.odfdom.doc.table.OdfTable
//import org.odftoolkit.odfdom.dom.OdfNamespaceNames
import org.w3c.dom.Element
import source.SourceWriter
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.border.EtchedBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.CompoundBorder
import source.SourceWord
import org.codehaus.groovy.scriptom.Scriptom
import meander.FlowEvent

/**
 * GUI panel for browsing for source files
 */

panel( border: titledBorder( border: new EtchedBorder(), 'Source File'),
       layout: new MigLayout(),
       constraints: 'cell 0 0 2 1, grow, sg'
     ) {

    button( text: 'Browse',
            id: 'sourceBrowseButton',
            actionPerformed: {
                def oldbutton = fd.approveButtonText
                fd.approveButtonText = 'Select'
                fileChoice = fd.showOpenDialog()
                fd.approveButtonText = oldbutton
                if( fileChoice == fd.APPROVE_OPTION) {
                    sourceFile.text = fd.selectedFile.absolutePath
                    sourceReadButton.requestFocusInWindow()
                }
            })
    doLater { sourceBrowseButton.requestFocusInWindow() }

    textField( columns: 30,
               id: 'sourceFile',
               constraints: 'spanx 2, wrap',
               actionPerformed: {
                   if (sourceFile.text.trim() != '') sourceReadButton.requestFocusInWindow()
               }
             )

    button( text: 'Read',
            enabled: bind( 'text', source: sourceFile, converter: { it.trim() != ''}),
            id: 'sourceReadButton',
            actionPerformed: {
                def m = sourceFile.text=~/[^\.]*$/    // grab the file extension
                switch ( m[0]) {
                case 'odt':
                case 'doc':
                case 'docx':
                    doOutside {
                        switch( m[0]) {
                        case 'odt':
                            model.baseDocument = new SourceWriter( sourceFile.text)
                            break
                        case 'doc':
                        case 'docx':
                            model.baseDocument = new SourceWord( sourceFile.text)
                        }
                        doLater {
                            // clear the old events
                            model.flowEvents.clear()
                            model.flowEvents.addAll( model.baseDocument.rows)
                            model.hasEventContents = true
                            model.hasModifiedContents = false

                            model.updateStrings( model.baseDocument.strings, 'source')

                            // reset the options
                            model.initOptions()
                            model.updateOptions( model.baseDocument.options)

                            // reset the nodes
                            model.nodes.clear()
                            model.updateNodes()
                            flowTableModel.fireTableDataChanged()
                            updateNodes()

                            // get any new styles
                            def nsList = model.baseDocument.styleStrs.collect {it.name}.sort() - model.stylesModel.coreSL
                            model.styles.applyStyleInfo( model.baseDocument.styleStrs)
                            model.stylesModel.userSL.addAll( nsList )
                            model.stylesView.addUserStyleButtons()
                        }
                    }
                    sourceTree.model.root.removeAllChildren()
                    sourceTree.model.root.add( new DefaultMutableTreeNode( sourceFile.text))
                    sourceTree.model.root.userObject = 'Included Source File'
                    sourceTree.model.reload()
                    break
                }
            })

    button( text: 'Append',
            enabled: bind( 'text', source: sourceFile, converter: { it.trim() != ''}),
            id: 'sourceAppendButton',
            actionPerformed: {
                def inputDocument
                def m = sourceFile.text=~/[^\.]*$/
                switch ( m[0]) {
                case 'odt':
                case 'doc':
                    doOutside {
                        switch( m[0]) {
                        case 'odt':
                            inputDocument = new SourceWriter( sourceFile.text, model.flowEvents.size() + 1)
                            break
                        case 'doc':
                            inputDocument = new SourceWord( sourceFile.text, model.flowEvents.size() + 1)
                        }
                        doLater {
                            model.flowEvents.addAll( inputDocument.rows)
                            model.hasEventContents = true
                            model.hasModifiedContents = true
                            model.updateNodes()
                            flowTableModel.fireTableDataChanged()
                            updateNodes()
                        }
                    }
                    sourceTree.model.root.add( new DefaultMutableTreeNode( sourceFile.text))
                    if (sourceTree.model.root.getChildCount() == 1) {
                        sourceTree.model.root.userObject = 'Included Source File'
                    }
                    else {
                        sourceTree.model.root.userObject = 'Included Source Files'
                    }
                    sourceTree.model.root.userObject = 'Included Source Files'
                    sourceTree.model.reload()
                }
            })

    button( text: 'Import',
            enabled: bind( 'text', source: sourceFile, converter: { it.trim() != ''}),
            id: 'sourceImportButton',
            actionPerformed: {
                def inputDocument
                def m = sourceFile.text=~/[^\.]*$/
                switch ( m[0]) {
                case 'odt':
                case 'doc':
                    doOutside {
                        switch( m[0]) {
                        case 'odt':
                            inputDocument = new SourceWriter( sourceFile.text, model.flowEvents.size() + 1)
                            break
                        case 'doc':
                            inputDocument = new SourceWord( sourceFile.text, model.flowEvents.size() + 1)
                        }
                        def newRow = []
                        newRow << new FlowEvent( count: model.flowEvents.size() + 1,
                                                 commandStr: 'import',
                                                 command: 'import',
                                                 commandTags: [:],
                                                 sourceStr: '',
                                                 source: '',
                                                 sourceTags:  [:],
                                                 data: sourceFile.text,
                                                 destinationStr: '',
                                                 destination: '',
                                                 destinationTags: '',
                                                 annotation: ''
                                               )
                        doLater {
                            model.flowEvents.addAll( newRow)
                            model.hasEventContents = true
                            model.hasModifiedContents = true
                            model.updateNodes()
                            flowTableModel.fireTableDataChanged()
                            updateNodes()
                        }
                    }
                    sourceTree.model.root.add( new DefaultMutableTreeNode( 'importing -> ' + sourceFile.text))
                    if (sourceTree.model.root.getChildCount() == 1) {
                        sourceTree.model.root.userObject = 'Included Source File'
                    }
                    else {
                        sourceTree.model.root.userObject = 'Included Source Files'
                    }
                    sourceTree.model.reload()
                }
            })

    button( text: 'Clear',
            id: 'sourceClearButton',
            actionPerformed: {
                model.flowEvents.clear()
                (0..19).each { num ->
                    model.flowEvents.add( new FlowEvent( count: num+1,
                                                         commandStr: '',
                                                         command: '',
                                                         commandTags: [:],
                                                         sourceStr: '',
                                                         source: '',
                                                         sourceTags:  [:],
                                                         data: '',
                                                         destinationStr: '',
                                                         destination: '',
                                                         destinationTags: '',
                                                         annotation: '\n'))   // by putting a newline character here, the
                }                                                             // event table editor makes the row taller
                model.hasEventContents = false
                model.hasModifiedContents = false

                model.updateStrings( model.stringsModels.user.getPageStrings(), 'user')

                // reset the options
                model.initOptions()

                // clear the base document
                model.baseDocument = null

                model.nodes.clear()
                model.updateNodes()
                flowTableModel.fireTableDataChanged()
                updateNodes()
                sourceTree.model.root.removeAllChildren()
                sourceTree.model.root.userObject = 'No Included Source Files'
                sourceTree.model.reload()
            })

}
