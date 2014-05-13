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
package meander.Base

import meander.Style

/**
 * Proxy object representing an Impress presentation
 */
class Document {
    def events
    def fileName = ''
    def options
    def nodes
    def pageModel
    def strings
    def styles
    def counts

    def doc
    def page

    Document( named=[:]) {
        named.each { key, value -> this[ key] = value }

        setupDocument()

        setupFirstPage()
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
        def graphics
        def newpage

        // make any initial tweaks to the document
        initialDocumentHandling()

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

            // handle various non drawing event items like updating counters
            // and style attributes
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

                // and start a new one - with nodes
                newpage = addNewPage( true)
                setCurrentPage( newpage)
                page = newpage
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

 //           srcs = event.source?.split( ',')
 //           dests = event.destination?.split( ',')
 //           if ( (srcs?.size() > 1) || (dests?.size() > 1)) {
            if ( event.command == 'messagechain' || event.command == 'bearerchain') {
                obj = createChainObject( event: event,
                                         convertUnits: page.convertUnits,
                                         style: style,
                                         options: options,
                                         counts: counts,
                                         leftM: page.leftM,
                                         rightM: page.pageWidth - page.rightM,
                                       )
            } else {
                obj = createEventObject( event: event,
                                         convertUnits: page.convertUnits,
                                         style: style,
                                         options: options,
                                         counts: counts,
                                         leftM: page.leftM,
                                         rightM: page.pageWidth - page.rightM,
                                       )
            }
            if (obj) {
                def shapes
                graphics = page.addEvent( obj)

                if ( page.currYpos > page.maxYpos) {
                    // initialize the next page
                    strings.counts[ 'PC'] += 1
                    strings.counts[ 'FPC'] += 1
                    newpage = addNewPage( true)    // 'true' says to put nodes at the top of the page

                    // wrap up with the current page
                    page.finalizePage( false)

                    // but need to have a placeholder for the last page
                    // in case there is a thread that needs to handle the
                    // display in the background - I'm looking at you JavaFX
                    def oldpage = page

                    // copy the excess graphics to the clipboard
                    copyShapes( graphics)

                    // continue with the new page
//                    page = newpage
                    // update the subclass document to use the new page
                    setCurrentPage( newpage)

                    // and paste the excess graphics to the new (now current) page
                    shapes = pasteShapes()
                    page.repositionShapes( shapes, page.vbuffer)

                    // go back and delete the copied graphics from the previous page
                    graphics.each { gr ->
                        oldpage.delete( gr)
                    }

                    // it's a wrap on the previous page, so print it
                    oldpage.display()
                }
            }
        }
        page.finalizePage( true)
        // it's a wrap on the last page, so print it
        page.display()

        // now move on to the attachment(s) phase
        if ( options.postfix || options.notes) {
            String post, note
            def obj = []
            def graphic

            strings.counts[ 'PC'] += 1
            strings.counts[ 'FPC'] += 1
            newpage = addNewPage( false)  // false means no nodes on this page
            setCurrentPage( newpage)
            page = newpage

            events.each { event ->
                switch ( event.command) {
                case 'action':
                case 'annotation':
                case 'comment':
                case 'exchange':
                case 'message':
                case 'subproc':
                    obj = createPostNoteObject( event: event,
                                                convertUnits: page.convertUnits,
                                                style: styles[ 'annotate'],
                                                options: options,
                                                leftM: page.leftM,
                                                rightM: page.pageWidth - page.rightM
                                              )
                    if (obj) {
                        graphic = page.addNote( obj)
                        if ( page.currYpos > page.maxYpos) {
                            // wrap up with the current page
                            page.delete( graphic)
                            page.finalizePage( false)
                            // but need to have a placeholder for the last page
                            // in case there is a thread that needs to handle the
                            // display in the background - I'm looking at you JavaFX
                            def oldpage = page

                            // initialize the next page
                            strings.counts[ 'PC'] += 1
                            strings.counts[ 'FPC'] += 1
                            newpage = addNewPage( false)  // no nodes on this page
                            setCurrentPage( newpage)
                            newpage.addNote( obj)

                            // it's a wrap on the previous page, so print it
                            oldpage.display()
                        }
                    }
                    break
                default:
                    return
                }
            }
            page.finalizePage( true)
            // it's a wrap on the last page, so print it
            page.display()

        }

        // do any post document tweaks
        finalDocumentHandling()
    }

    public setupDocument() { }

    public setupFirstPage() { }

    public initialDocumentHandling() { }

    public finalDocumentHandling() { }

    public addNewPage( boolean doNodes) { }

    public setCurrentPage( page ) { }

    public createChainObject( map ) { }

    public createEventObject( map ) { }

    public createPostNoteObject( map ) { }

    // copies to a clipboard
    public copyShapes( shapes) { }

    // pastes from a clipboard
    public pasteShapes() { }
}
