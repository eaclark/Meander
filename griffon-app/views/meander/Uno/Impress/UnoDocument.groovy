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
package meander.Uno.Impress

import com.sun.star.beans.PropertyValue
//import com.sun.star.beans.XPropertySet
import com.sun.star.drawing.XMasterPagesSupplier
import com.sun.star.drawing.XDrawPagesSupplier
import com.sun.star.view.PaperFormat
import com.sun.star.view.PaperOrientation
import com.sun.star.view.XPrintable
import meander.Uno.UnoCategory
import meander.PageType
import com.sun.star.frame.XModel
import com.sun.star.uno.UnoRuntime
import com.sun.star.drawing.XDrawView

import com.sun.star.beans.XPropertySet

import meander.Base.Document

import meander.Uno.UnoUtils

/**
 * Proxy object representing an Impress presentation
 */
class UnoDocument extends Document {

    /* Variables for the ODF file */
//  XPropertySet xProperySet

    def master
    def masters

    def xPagesSupplier
    def xMastersSupplier
    def xView
    def xContext
    def xComponentLoader
    def xLinguProps

    def tempAutoSpell   // a placeholder for saving a document setting

    UnoDocument( named=[:]) {
        super( named)
    }

    public setupDocument() {
        try {
            if ( fileName) {
                doc = xComponentLoader.loadComponentFromURL( fileName, "_blank", 0, null)
            } else {
                //          def pv = new PropertyValue[1]
                //          pv[0] = new PropertyValue( Name: 'PaperFormat', Value: PaperFormat.LETTER)
                //          doc = xComponentLoader.loadComponentFromURL( "private:factory/simpress", "_blank", 0, pv)

                doc = xComponentLoader.loadComponentFromURL( "private:factory/simpress", "_blank", 0, null)
            }

            //println doc.getCurrentController().getFrame().getContainerWindow().visible = true
            //xView = (XDrawView) UnoRuntime.queryInterface (XDrawView.class, doc.getCurrentController())
            //xView.getFrame().getContainerWindow().visible = true
            //def frame = UnoRuntime.queryInterface (XFrame.class, xView.getFrame())

            def pv = new PropertyValue[3]
            def orientation = pageModel.portrait ? PaperOrientation.PORTRAIT : PaperOrientation.LANDSCAPE
            def format
            switch (pageModel.size) {
                case PageType.LETTER: format = PaperFormat.LETTER; break
                case PageType.LEDGER: format = PaperFormat.LEGAL; break
                case PageType.A3: format = PaperFormat.A3; break
                case PageType.A4: format = PaperFormat.A4; break
                case PageType.B4: format = PaperFormat.B4; break
                case PageType.B5: format = PaperFormat.B5; break
            }

            // set the page format and orientation
            pv[0] = new PropertyValue( Name: 'PaperFormat', Value: format)
            pv[1] = new PropertyValue( Name: 'PaperOrientation', Value: orientation)
            use (UnoCategory) {
                def xPrint = doc.uno(XPrintable)
                xPrint.printer = pv

                // grab the master page set while we're using Uno
                xMastersSupplier = doc.uno( XMasterPagesSupplier)
                //        masters = xMastersSupplier.getMasterPages()
                xPagesSupplier = doc.uno( XDrawPagesSupplier)
            }

            pv[2] = new PropertyValue( Name: 'Hidden', Value: false)

            masters = xMastersSupplier.getMasterPages()

        } catch( Exception exception ) {
            System.err.println( exception )
        }
    }

    public setupFirstPage() {

        // setup access to the first master page
        master = masters.getByIndex( 0)  //new Master( mastersSupplier: xMastersSupplier, pageModel: pageModel, 0)
        use (UnoCategory) {
            master.multiPropertySet = [ Orientation: pageModel.portrait ? PaperOrientation.PORTRAIT : PaperOrientation.LANDSCAPE,
                                        Width: (pageModel.xpts * 100 / 2.834646f) as int, // "11in",
                                        Height: (pageModel.ypts * 100 / 2.834646f) as int, // (8.5 * 2540) as int, // "8.5in",
                                        BorderTop: 1000 as int,
                                        BorderRight: 1000 as int,
                                        BorderBottom: 1000 as int,
                                        BorderLeft: 1000 as int,
                                      ]        // setup access to the automatically created first page
        }
        strings.counts[ 'PC'] = 1
        strings.counts[ 'FPC'] = 1
        page = new UnoPage( pagesSupplier: xPagesSupplier,

                            context: xContext,
                            doc: doc,
                            options: options,
                            nodes: nodes,
                            pageModel: pageModel,
                            strings: strings,
                            styles: styles,
                            0)
    }

    public initialDocumentHandling() {
        // make sure the automatic spelling option is off, but remember what it was so we can restore it
        if ( xLinguProps != null) {
            use (UnoCategory) {
                tempAutoSpell = xLinguProps.uno(XPropertySet)['IsSpellAuto']
                xLinguProps.uno(XPropertySet)['IsSpellAuto'] = false
            }
        }
    }

    public finalDocumentHandling() {
        // reset the automatic spelling option back to what it was
        if ( xLinguProps != null) use (UnoCategory) { xLinguProps.uno(XPropertySet)['IsSpellAuto'] = tempAutoSpell }
    }

    public addNewPage( boolean doNodes) {
        def page
        page = new UnoPage( pagesSupplier: xPagesSupplier,
                            context: xContext,
                            doc: doc,
                            options: options,
                            nodes: doNodes ? nodes : null,
                            pageModel: pageModel,
                            strings: strings,
                            styles: styles,
                          )

        return page
    }

    public setCurrentPage( nextpage) {
        XModel aModel
        aModel = (XModel) UnoRuntime.queryInterface(XModel.class, doc)
        xView = (XDrawView) UnoRuntime.queryInterface (XDrawView.class, aModel.getCurrentController())
        // the page to set is actually the page property of nextpage
        xView.setCurrentPage( nextpage.drawPane)

        page = nextpage
    }

    public createEventObject( map ) {
        def obj
        map.pagesSupplier = xPagesSupplier
        obj = new UnoImpressEvent( map)
        return obj
    }

    public createChainObject( map ) {
        def obj
        map.pagesSupplier = xPagesSupplier
        obj = new UnoImpressChain( map)
        return obj
    }

    public createPostNoteObject( map ) {
        def obj
        map.pagesSupplier = xPagesSupplier
        obj = new UnoImpressPostNote( map)
        return obj
    }

    public copyShapes( shapes) {
        // ask the current page to do it
        page.copyShapes( shapes)
    }

    public pasteShapes() {
        // pastes from the clipboard
        // ask the current page to do it
        def shapes
        shapes = page.paste()
        return shapes
    }
}
