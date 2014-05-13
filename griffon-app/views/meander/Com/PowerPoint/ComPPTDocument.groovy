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
package meander.Com.PowerPoint

import meander.Style

import static meander.PageType.*

import org.codehaus.groovy.scriptom.tlb.office2007.MsoOrientation
import org.codehaus.groovy.scriptom.tlb.office2007.powerpoint.PpSlideSizeType
import org.codehaus.groovy.scriptom.tlb.office2007.powerpoint.PpViewType

/**
 * PowerPoint document
 */
class ComPPTDocument {
    def events
    def fileName = ''
    def options
    def nodes
    def pageModel
    def strings
    def styles
    def counts
    def linguProps

    def app
    def doc
    def context
    def masters
    def master
    def page

    ComPPTDocument( named=[:], app) {
        this.app = app

        named.each { key, value -> this[ key] = value }

        // instantiate a PowerPoint document
        doc = app.Presentations.Add()

        switch( pageModel.size) {
        case Default:
            // set the working page size to the active one
            switch( doc.PageSetup.SlideSize) {
            case PpSlideSizeType.ppSlideSizeLetterPaper:
                pageSize = LETTER
                break
            case PpSlideSizeType.ppSlideSizeLedgerPaper:
                pageSize = LEDGER
                break
            case PpSlideSizeType.ppSlideSizeA3Paper:
                pageSize = A3
                break
            case PpSlideSizeType.ppSlideSizeA4Paper:
                pageSize = A4
                break
            case PpSlideSizeType.ppSlideSizeB4ISOPaper:
                pageSize = B4
                break
            case PpSlideSizeType.ppSlideSizeB5ISOPaper:
                pageSize = B5
                break
            case PpSlideSizeType.ppSlideSizeOnScreen:
                pageSize = Default
                break
            }
            break
        case LETTER:
            doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeLetterPaper
            break
        case LEDGER:
            doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeLedgerPaper
            break
        case A3:
            doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeA3Paper
            break
        case A4:
            doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeA4Paper
            break
        case B4:
            doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeB4ISOPaper
            break
        case B5:
            doc.PageSetup.SlideSize = PpSlideSizeType.ppSlideSizeB5ISOPaper
            break
        }

        if ( pageModel.portrait) {
            doc.PageSetup.SlideOrientation = MsoOrientation.msoOrientationVertical
        }

        //page = pres.Slides.Add( Index: 1, Layout: PpSlideLayout.ppLayoutBlank)
        strings.counts[ 'PC'] = 1
        strings.counts[ 'FPC'] = 1
        page = new ComPPTPage( doc: doc,
                               options: options,
                               nodes: nodes,
                               pageModel: pageModel,
                               strings: strings,
                               styles: styles,
                               -1)

        app.ActiveWindow.ViewType = PpViewType.ppViewSlide
        page.setupPage()

/*        println 'page size = ' + pres.PageSetup.SlideSize
        println 'slwidth = ' + slwidth
        println 'slheight = ' + slheight
        if ( slwidth > slheight) {
            def l = ((pageSize.y * pageSize.p) - slwidth) / 2 as int
            def s = ((pageSize.x * pageSize.p) - slheight) / 2 as int
            println 'long border = ' + l
            println 'short border = ' + s
        } else {
            def s = ((pageSize.y * pageSize.p) - slwidth) / 2 as int
            def l = ((pageSize.x * pageSize.p) - slheight) / 2 as int
            println 'long border = ' + l
            println 'short border = ' + s
        }*/
    }

    public walkEvents() {
        def cmd, cmdTags
        def src, srcTags
        def data, dataTags
        def dest, destTags
        def annotation
//        def objs
        Style style
        String styleName
        def graphics, shapes
        def currY
        def newpage
        boolean tempAutoSpell

        // make sure the automatic spelling option is off, but remember what it was so we can restore it
//        if ( linguProps != null) {
//            use (UnoCategory) {
//                tempAutoSpell = linguProps.uno(XPropertySet)['IsSpellAuto']
//                linguProps.uno(XPropertySet)['IsSpellAuto'] = false
//            }
//        }

        events.each { event ->
            def srcs, dests

            def obj = []
 //           cmd = event.command
 //           cmdTags = event.commandTags
 //           src = event.source
 //           srcTags = event.sourceTags
 //           data = event.data
 //           dest = event.destination
 //           destTags = event.destinationTags
 //           annotation = event.annotation

            switch ( event.command) {
            case 'action':
                counts[ 'AC'] += 1
                counts[ 'GC'] += 1
                break
            case 'comment':
                counts[ 'CC'] += 1
                counts[ 'GC'] += 1
                break
            case 'exchange':
                counts[ 'EC'] += 1
                counts[ 'GC'] += 1
                break
            case 'message':
                counts[ 'MC'] += 1
                counts[ 'GC'] += 1
                break
            case 'subproc':
                counts[ 'SC'] += 1
                counts[ 'GC'] += 1
                break
            case 'bearerpath':
                counts[ 'BC'] += 1
                counts[ 'GC'] += 1
                break
            case 'messagechain':
                // any necessary incrementing of the message and global counters will be handled
                // during the building of the chain
                break
            case 'bearerchain':
                counts[ 'BCC'] += 1
                counts[ 'GC'] += 1
                break
            case 'pagebreak':
            case 'break':
                strings.counts[ 'PC'] += 1
                strings.counts[ 'FPC'] += 1

                // wrap up with the current page
                page.finalizePage( false)

                // and start a new one
                newpage = new ComPPTPage( doc: doc,
                                          options: options,
                                          nodes: nodes,
                                          pageModel: pageModel,
                                          strings: strings,
                                          styles: styles,
                                        )


                page = newpage
                app.Activewindow.View.GotoSlide( strings.counts[ 'PC'])
                page.setupPage()
                return
            case 'style':
                // any new style added in the source should have been initialized in the
                // style stack earlier in the processing.  Here we just need to update the
                // style attributes (after double checking that it is a valid style name)
                style = styles[ event.source]
                if( style) {
                    style.update( event.data, styles)
                }
                return
            default:
                // ignore anything else
                return
            }

            // look to see if a specific style was requested in the command tags
            // if not, use the one associated with the command
            styleName = event.commandTags[ 'style']
            style = styles[ styleName ?: event.command]
            if( style == null) {
                // hmmm, they must have requested a style that hasn't been defined
                // default to the one associated with the command
                style = styles[ event.command ]
                // log it
                println "Style '${event.commandTags[ 'style']}' was requested for command '${event.command}', but it hasn't been defined."
            }
            //style = (event.commandTags.style) ? styles[event.commandTags.style] : styles[ event.command]
            //style = styles[ (event.commandTags[ 'style'] ?: event.command) ]

 //           srcs = event.source?.split( ',')
 //           dests = event.destination?.split( ',')
 //           if ( (srcs?.size() > 1) || (dests?.size() > 1)) {
            if ( event.command == 'messagechain' || event.command == 'bearerchain') {
//                obj = new UnoImpressChain( event: event,
//                                           style: style,
//                                           options: options,
//                                           counts: counts,
//                                           leftM: page.leftM,
//                                           rightM: page.pageWidth - page.rightM,
//                                         )
            } else {
                obj = new ComPPTEvent( event: event,
                                       style: style,
                                       options: options,
                                       counts: counts,
                                       leftM: page.leftM,
                                       rightM: page.pageWidth - page.rightM,
                                     )

            }
            if (obj) {
                graphics = page.addEvent( obj)

                // this may have added more shapes than will fit on the page, if so, graphics will
                // hold a list of shapes that extend below the bottom of the valid drawing area
                if ( page.currYpos > page.maxYpos) {
                    // initialize the next page
                    strings.counts[ 'PC'] += 1
                    strings.counts[ 'FPC'] += 1
                    newpage = new ComPPTPage( doc: doc,
                                              options: options,
                                              nodes: nodes,
                                              pageModel: pageModel,
                                              strings: strings,
                                              styles: styles,
                                            )

                    // copy the excess graphics to the new page
                    page.copyShapes( graphics)
                    app.Activewindow.View.GotoSlide( strings.counts[ 'PC'])
                    newpage.setupPage()
                    shapes = newpage.paste()
                    newpage.repositionShapes( shapes, page.vbuffer)

                    // go back and delete the copied graphics from the first page
                    page.deleteShapes( graphics)
                    // wrap up with the current page
                    page.finalizePage( false)

                    // continue with the new page
                    page = newpage
                }
            }
        }
        page.finalizePage( true)

/*        (0..29).each {
            strings.counts[ 'PC'] += 1
            newpage = new ComPPTPage( doc: doc,
                                      options: options,
                                      pageModel: pageModel,
                                      strings: strings,
                                      styles: styles,
                                    )
            app.Activewindow.View.GotoSlide( strings.counts[ 'PC'])
            newpage.setupPage()
            newpage.finalizePage( it)
        }*/

        // now move on to the attachment(s) phase
        if ( options.postfix || options.notes) {
            String post, note
            def obj = []

            strings.counts[ 'PC'] += 1
            strings.counts[ 'FPC'] += 1
            newpage = new ComPPTPage( doc: doc,
                                      options: options,
                      //        no nodes are passed in which will stop the node boxes from being drawn
                                      pageModel: pageModel,
                                      strings: strings,
                                      styles: styles,
                                    )

            app.Activewindow.View.GotoSlide( strings.counts[ 'PC'])
            newpage.setupPage()
            page = newpage

            events.each { event ->
                switch ( event.command) {
                case 'action':
                case 'annotation':
                case 'comment':
                case 'exchange':
                case 'message':
                case 'subproc':
                    obj = new ComPPTPostNote( event: event,
                                              style: styles[ 'annotate'],
                                              options: options,
                                              leftM: page.leftM,
                                              rightM: page.pageWidth - page.rightM,
                                            )
                    if (obj) {
                        graphics = page.addNote( obj)
                        if ( page.currYpos > page.maxYpos) {
                            // initialize the next page
                            strings.counts[ 'PC'] += 1
                            strings.counts[ 'FPC'] += 1
                            newpage = new ComPPTPage( doc: doc,
                                                      options: options,
                                                      pageModel: pageModel,
                                                      strings: strings,
                                                      styles: styles,
                                                    )

                            page.copyShapes( graphics)
                            app.Activewindow.View.GotoSlide( strings.counts[ 'PC'])
                            newpage.setupPage()
                            shapes = newpage.paste()
                            newpage.repositionShapes( shapes, 0)

                            // go back and delete the copied graphics from the first page
                            page.deleteShapes( graphics)
                            // wrap up with the current page
                            page.finalizePage( false)

//                            newpage.addNote( obj)
                            page = newpage
                        }
                    }
                    break
                default:
                    return
                }
            }
            page.finalizePage( true)

        }
    }
}
