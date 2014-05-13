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

import javax.swing.JLabel
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import meander.Vlabel
import javax.swing.border.EtchedBorder
import javax.swing.JScrollPane
import javax.swing.SwingConstants
import meander.PagePlacement
import meander.WatermarkPlacement
import meander.PageStrings
import meander.Options

/**
 * GUI panel for displaying the various page strings (e.g. the title and footer strings)
 */
//border = new TitledBorder( 'Page Strings')
//panel( border: border, layout: new MigLayout( 'wrap 1', 'grow')) {

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    // now do the page strings
    panel(title: 'Page Strings', id: 'stringsTab', layout: new MigLayout('flowY'), constraints: 'grow') {
        // controlling radio buttons for source vs user override
        panel( constraints: 'align center, spanx, growy') {
            buttonGroup( id: 'strsOpt').with {
                add radioButton( text: 'Source',
                                 id: 'strsSrcOpt',
                                 selected: true,
                                 actionCommand: 'source',
                                 actionPerformed: {
                                     // copy the items in 'base' to 'user' and link from 'source' to 'base'
                                     if ( model.stringsModels[ 'base'].mirror == 'user') {
                                         PageStrings.bools.each {
                                             model.stringsModels[ 'user']."$it" = model.stringsModels[ 'base']."$it"
                                             model.stringsModels[ 'user']."${it}Str" = model.stringsModels[ 'base']."${it}Str"
                                             model.stringsModels[ 'user']."${it}Pos" = model.stringsModels[ 'base']."${it}Pos"
                                             model.stringsModels[ 'user']."${it}Style" = model.stringsModels[ 'base']."${it}Style"
                                         }
                                         PageStrings.bools.each {
                                             model.stringsModels[ 'base']."$it" = model.stringsModels[ 'source']."$it"
                                             model.stringsModels[ 'base']."${it}Str" = model.stringsModels[ 'source']."${it}Str"
                                             model.stringsModels[ 'base']."${it}Pos" = model.stringsModels[ 'source']."${it}Pos"
                                             model.stringsModels[ 'base']."${it}Style" = model.stringsModels[ 'source']."${it}Style"
                                         }
                                         model.stringsModels[ 'base'].mirror = 'source'
                                         model.stringsModels[ 'source'].mirror = model.stringsModels[ 'base']
                                         model.stringsModels[ 'user'].mirror = null
                                     }
                                 }
                )
                add radioButton( text: 'Override',
                                 id: 'strsOvrOpt',
                                 actionCommand: 'override',
                                 actionPerformed: {
                                     // copy the items in 'base' to 'user' and link from 'source' to 'base'
                                     if ( model.stringsModels[ 'base'].mirror == 'source') {
                                         PageStrings.bools.each {
                                             model.stringsModels[ 'base']."$it" = model.stringsModels[ 'user']."$it"
                                             model.stringsModels[ 'base']."${it}Str" = model.stringsModels[ 'user']."${it}Str"
                                             model.stringsModels[ 'base']."${it}Pos" = model.stringsModels[ 'user']."${it}Pos"
                                             model.stringsModels[ 'base']."${it}Style" = model.stringsModels[ 'user']."${it}Style"
                                         }
                                         model.stringsModels[ 'base'].mirror = 'user'
                                         model.stringsModels[ 'source'].mirror = null
                                         model.stringsModels[ 'user'].mirror = model.stringsModels[ 'base']
                                     }
                        }
                )
            }
        }

        // Title string
        panel( constraints: 'align center, growy', layout: new MigLayout()) {
            checkBox( text: 'Title',
                      id: 'titleOpt',
                      selected: bind( source: model.stringsModels[ 'base'], sourceProperty: 'title', mutual: true),
                      enabled: bind { strsOvrOpt.selected },
                      constraints: 'cell 0 0, align left')
            panel( constraints: 'cell 0 1, align left') {
                label( text: 'Placement',
                       enabled: bind { strsOvrOpt.selected },
        //                horizontalAlignment: JLabel.RIGHT,
                     )
                comboBox( items: PagePlacement.values().toList(),
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( source: model.stringsModels['base'], sourceProperty: 'titlePos', mutual: true),
        //                  constraints: 'cell 1 1, align left'
                        )
            }
            panel( constraints: 'cell 1 1, align right') {
                label( text: 'Style',
                       enabled: bind { strsOvrOpt.selected },
        //               horizontalAlignment: JLabel.RIGHT,
                     )
                comboBox( items: model.styles.activeStyles.collect{ e -> e.value.name},
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'titleStyle', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'cell 3 1, align right'
                        )
            }
            scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        constraints: 'cell 0 2, spanx') {
                textArea( columns: 35,
                          rows: 3,
                          id: 'titleText',
                          enabled: bind { strsOvrOpt.selected },
                          text: bind( source: model.stringsModels['base'], sourceProperty: 'titleStr', mutual:true)
                        )
            }
        }

        separator( constraints: 'grow')

        // Footer string
        panel( constraints: 'align center, growy', layout: new MigLayout()) {
            checkBox( text: 'Footer',
                      id: 'footerOpt',
                      selected: bind( source: model.stringsModels[ 'base'], sourceProperty: 'footer', mutual: true),
                      enabled: bind { strsOvrOpt.selected },
                      constraints: 'cell 0 0, align left')
            panel( constraints: 'cell 0 1, align left') {
                label( text: 'Placement',
                       enabled: bind { strsOvrOpt.selected },
        //                horizontalAlignment: JLabel.RIGHT,
                     )
                comboBox( items: PagePlacement.values().toList(),
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'footerPos', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'cell 1 1, align left'
                        )
            }
            panel( constraints: 'cell 1 1, align right') {
                label( text: 'Style',
                       enabled: bind { strsOvrOpt.selected },
        //               horizontalAlignment: JLabel.RIGHT,
                     )
                comboBox( items: model.styles.activeStyles.collect{ e -> e.value.name},
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'footerStyle', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'cell 3 1, align right'
                        )
            }
            scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        constraints: 'cell 0 2, spanx') {
                textArea( columns: 35,
                          rows: 3,
                          id: 'footerText',
                          enabled: bind { strsOvrOpt.selected },
                          text: bind( 'footerStr', source: model.stringsModels['base'], mutual:true)
                        )
            }
        }

        separator( constraints: 'grow')

        // Author string
        panel( constraints: 'align center, growy, wrap', layout: new MigLayout()) {
            checkBox( text: 'Author(s)',
                      id: 'authorOpt',
                      selected: bind( source: model.stringsModels[ 'base'], sourceProperty: 'author', mutual: true),
                      enabled: bind { strsOvrOpt.selected },
                      constraints: 'cell 0 0, align left')
            panel( constraints: 'cell 0 1, align left') {
                label( text: 'Style', enabled: bind { strsOvrOpt.selected })
                comboBox( items: model.styles.activeStyles.collect{ e -> e.value.name},
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'authorStyle', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'align left'
                        )
            }
            panel( constraints: 'cell 1 1, align right') {
                label( text: 'Place', enabled: bind { strsOvrOpt.selected })
                comboBox( items: PagePlacement.values().toList(),
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'authorPos', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'align left'
                        )
            }
            scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        constraints: 'cell 0 2, spanx') {
                textArea( columns: 35,
                          rows: 3,
                          id: 'authorText',
                          enabled: bind { strsOvrOpt.selected },
                          text: bind( 'authorStr', source: model.stringsModels['base'], mutual:true)
                        )
            }
        }

        separator( orientation: SwingConstants.VERTICAL, constraints: 'spany, grow, wrap')

        // Date string
        panel( constraints: 'align center, growy', layout: new MigLayout()) {
            checkBox( text: 'Date',
                      id: 'dateOpt',
                      selected: bind( source: model.stringsModels[ 'base'], sourceProperty: 'date', mutual: true),
                      enabled: bind { strsOvrOpt.selected },
                      constraints: 'align left, spanx 2')
            panel( constraints: 'cell 0 1, align left') {
                label( text: 'Style', enabled: bind { strsOvrOpt.selected })
                comboBox( items: model.styles.activeStyles.collect{ e -> e.value.name},
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'dateStyle', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'align left'
                        )
            }
            panel( constraints: 'cell 1 1, align right') {
                label( text: 'Place', enabled: bind { strsOvrOpt.selected })
                comboBox( items: PagePlacement.values().toList(),
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( source: model.stringsModels['base'], sourceProperty: 'datePos', mutual: true),
                          constraints: 'align left'
                        )
            }
            scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        constraints: 'cell 0 2, spanx') {
                textArea( columns: 35,
                          rows: 3,
                          id: 'dateText',
                          enabled: bind { strsOvrOpt.selected },
                          text: bind( source: model.stringsModels['base'], sourceProperty: 'dateStr', mutual:true)
                        )
            }
        }

        separator( constraints: 'grow, spanx')

        // Page number string
        panel( constraints: 'align center, growy', layout: new MigLayout( 'wrap 2')) {
            checkBox( text: 'Page Number',
                      id: 'pagenumOpt',
                      selected: bind( source: model.stringsModels[ 'base'], sourceProperty: 'pagenum', mutual: true),
                      enabled: bind { strsOvrOpt.selected },
                      constraints: 'align left, spanx 2')
            panel( constraints: 'cell 0 1, align left') {
                label( text: 'Style', enabled: bind { strsOvrOpt.selected })
                comboBox( items: model.styles.activeStyles.collect{ e -> e.value.name},
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'pagenumStyle', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'align left'
                        )
            }
            panel( constraints: 'cell 1 1, align right') {
                label( text: 'Place', enabled: bind { strsOvrOpt.selected })
                comboBox( items: PagePlacement.values().toList(),
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( source: model.stringsModels['base'], sourceProperty: 'pagenumPos', mutual: true),
        //                  constraints: 'align left'
                        )
            }
            scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        constraints: 'cell 0 2, spanx') {
                textArea( columns: 35,
                          rows: 3,
                          id: 'pagenumText',
                          enabled: bind { strsOvrOpt.selected },
                          text: bind( source: model.stringsModels['base'], sourceProperty: 'pagenumStr', mutual:true)
                        )
            }
        }

        separator( constraints: 'grow, spanx')

        // Watermark string
        panel( constraints: 'align center, growy', layout: new MigLayout( 'wrap 2')) {
            checkBox( text: 'Watermark',
                      id: 'watermarkOpt',
                      selected: bind( source: model.stringsModels[ 'base'], sourceProperty: 'watermark', mutual: true),
                      enabled: bind { strsOvrOpt.selected },
                      constraints: 'align left, spanx 2')
            panel( constraints: 'cell 0 1, align left') {
                label( text: 'Style', enabled: bind { strsOvrOpt.selected })
                comboBox( items: model.styles.activeStyles.collect{ e -> e.value.name},
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( 'watermarkStyle', source: model.stringsModels['base'], mutual: true),
        //                  constraints: 'align left'
                        )
            }
            panel( constraints: 'cell 1 1, align right') {
                label( text: 'Place', enabled: bind { strsOvrOpt.selected })
                comboBox( items: WatermarkPlacement.values().toList(),
                          enabled: bind { strsOvrOpt.selected },
                          selectedItem: bind( source: model.stringsModels['base'], sourceProperty: 'watermarkPos', mutual: true),
        //                  constraints: 'align left'
                        )
            }
            scrollPane( horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        constraints: 'cell 0 2, spanx') {
                textArea( columns: 35,
                          rows: 3,
                          id: 'watermarkText',
                          enabled: bind { strsOvrOpt.selected },
                          text: bind( source: model.stringsModels['base'], sourceProperty: 'watermarkStr', mutual:true)
                        )
            }
        }
    }
}



/*  noparent {
    //  http://stackoverflow.com/questions/620929/rotate-a-swing-jlabel
    l1.metaClass.getSize = { ->
      oldSize = super.getSize();
      return new Dimension( size.height, size.width)
    }
    l1.metaClass.getHeight = { -> return getSize().height }
    l1.metaClass.getWidth = { -> return getSize().width }
    l1.metaClass.paintComponent = { g ->
      Graphics2D gr = (Graphics2D) g.create();

      gr.translate(0, getSize().getHeight());
      gr.transform(AffineTransform.getQuadrantRotateInstance(-1))
//      needsRotate = true;
      super.paintComponent(gr);
//      needsRotate = false;
    }
  }  */
