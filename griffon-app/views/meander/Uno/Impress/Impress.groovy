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

import com.sun.star.drawing.XShape
import com.sun.star.uno.XComponentContext
import com.sun.star.frame.XComponentLoader
import com.sun.star.comp.helper.Bootstrap
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.bridge.UnoUrlResolver
import com.sun.star.bridge.XUnoUrlResolver
import com.sun.star.beans.XPropertySet
import com.sun.star.uno.UnoRuntime
import com.sun.star.lang.XMultiServiceFactory

/**
 * Proxy object representing the Impress application
 */
class Impress {

    /* Variables for the ODF file */
    def xOfficeFactory
    XComponentContext xComponentContext
    XComponentContext xOfficeComponentContext
    XComponentLoader xComponentLoader
    XUnoUrlResolver urlResolver
    XPropertySet xProperySet
    def oDesktop
    def oDefaultContext
    def linguProps
    def dispatcher

    def doc
    def xPagesSupplier
    def xDrawPages
    def xDrawPage
    def xShape
    def xPageProps
    int nPageWidth, nPageHeight, nShape
    int nodeWidth
    def nodeXPos = []

    int xpos = 0, ypos = 0, currYpos = 0, maxYpos

    Impress() {
        xComponentContext = Bootstrap.createInitialComponentContext(null)

        // create a connector, so that it can contact the office
        urlResolver = UnoUrlResolver.create(xComponentContext)

        try {
            def initialObject = urlResolver.resolve( "uno:socket,host=127.0.0.1,port=8100;urp;StarOffice.ServiceManager")

            xOfficeFactory = (XMultiComponentFactory) UnoRuntime.queryInterface( XMultiComponentFactory.class, initialObject)

            // retrieve the component context as property (it is not yet exported from the office)
            // Query for the XPropertySet interface.
            xProperySet = (XPropertySet) UnoRuntime.queryInterface( XPropertySet.class, xOfficeFactory)

            // Get the default context from the office server.
            oDefaultContext = xProperySet.getPropertyValue("DefaultContext")

            // Query for the interface XComponentContext.
            xOfficeComponentContext = (XComponentContext) UnoRuntime.queryInterface( XComponentContext.class, oDefaultContext)

            // now create the desktop service
            // NOTE: use the office component context here!
            oDesktop = xOfficeFactory.createInstanceWithContext( "com.sun.star.frame.Desktop", xOfficeComponentContext)

            // Get a reference to the desktop interface that can load files
            xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface( XComponentLoader.class, oDesktop)

            // now turn off automatic spell checking to speed things up
            // should make this a configuration option
            def mxFactor = (XMultiServiceFactory) UnoRuntime.queryInterface( XMultiServiceFactory.class, initialObject )
            linguProps = mxFactor.createInstance( 'com.sun.star.linguistic2.LinguProperties')

        } catch( Exception exception ) {
            System.err.println( exception )
        }

    }

    public create( Map map) {
        map.xComponentLoader = xComponentLoader
        map.xContext = xOfficeComponentContext
        map.xLinguProps = linguProps
        doc = new UnoDocument( map)
        return doc
    }
}
