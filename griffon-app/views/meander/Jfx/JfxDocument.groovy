package meander.Jfx

import javafx.scene.shape.StrokeLineCap
import meander.Base.Document

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/3/13
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
class JfxDocument extends Document {

    def builder
    def pages
    def model
    def clipboard

    JfxDocument( named=[:]) {
        super( named)
    }

    public setupDocument() {
        try {
//            if ( fileName) {
//                doc = xComponentLoader.loadComponentFromURL( fileName, "_blank", 0, null)
//            } else {
//                //          def pv = new PropertyValue[1]
//                //          pv[0] = new PropertyValue( Name: 'PaperFormat', Value: PaperFormat.LETTER)
//                //          doc = xComponentLoader.loadComponentFromURL( "private:factory/simpress", "_blank", 0, pv)
//
//                doc = xComponentLoader.loadComponentFromURL( "private:factory/simpress", "_blank", 0, null)
//            }

            def orientation = pageModel.portrait
            def format = pageModel.size

        } catch( Exception exception ) {
            System.err.println( exception )
        }
    }

    public setupFirstPage() {
        // initialize the list of pages
        pages = []

        strings.counts[ 'PC'] = 1
        strings.counts[ 'FPC'] = 1
        page = new JfxPage( doc: doc,
                            options: options,
                            builder: builder,
                            displayPane: builder.with { background }, //currentScene.root },
                            nodes: nodes,
                            pageModel: pageModel,
                            strings: strings,
                            styles: styles,
                            rulers: model.rulers,
                            buffer: model.buffer,
                            0)
        pages << page
        model.pageIdx = 0i
        return page
    }

    public addNewPage( boolean doNodes) {
        def newPage
        newPage = new JfxPage( doc: doc,
                               options: options,
                               builder: builder,
                               displayPane: builder.with { background }, //currentScene.root },
                               nodes: doNodes ? nodes : null,
                               pageModel: pageModel,
                               strings: strings,
                               styles: styles,
                               rulers: model.rulers,
                               buffer: model.buffer,
                               0)
        pages << newPage

//        model.pageIdx++
        return newPage
    }

    public setCurrentPage( nextpage) {
        model.pageIdx = pages.indexOf( nextpage)
        page = nextpage
    }

    public paintRulers() {

        def drawPane = builder.with{ group() }

        // look to see if the user wants rulers displayed
        if( model.rulers ) {
            def i, hashCount, x, y, scale
            if( pageModel.size.d == 'in') {
                // dealing with imperial units - work in 1/8th inches
                i = [ 20, 3, 6, 3, 10, 3, 6, 3 ]
                hashCount = 8
                scale = 1  // the page dimensioning is already in inches
            } else {
                // dealing with metric units - work in 1/2 cms
                i = [ 15, 7 ]
                hashCount = 2
                scale = 10  // the page dimensioning is in mm - want to go to cm
            }

            if( pageModel.portrait) {
                x = (int)( hashCount * pageModel.size.x / scale)
                y = (int)( hashCount * pageModel.size.y / scale)
            } else
            {
                x = (int)( hashCount * pageModel.size.y / scale)
                y = (int)( hashCount * pageModel.size.x / scale)
            }

            def l = JfxUtils.createShape( builder,
                                          'Line',
                                          [ startX: 0,
                                            startY: 0,
                                            endX: 0,
                                            endY: pageModel.ypts,
                                            style: styles[ 'node'].border,
                                            strokeLineCap: StrokeLineCap.BUTT,
                                            strokeWidth: styles[ 'node'].border.thickness,
                                          ],
                                          null)

            JfxUtils.insertShape( l, builder, drawPane)
            ( 0 .. y).each {
                l = JfxUtils.createShape( builder,
                                          'Line',
                                          [ startX: 0,
                                            startY: it * scale * pageModel.size.p/hashCount,
                                            endX: i[it.mod( hashCount)],
                                            endY: it * scale * pageModel.size.p/hashCount,
                                            style: styles[ 'node'].border,
                                            strokeLineCap: StrokeLineCap.BUTT,
                                            strokeWidth: styles[ 'node'].border.thickness,
                                          ],
                                          null)

                JfxUtils.insertShape( l, builder, drawPane)
            }

            l = JfxUtils.createShape( builder,
                                      'Line',
                                      [ startX: 0,
                                        startY: 0,
                                        endX: pageModel.xpts,
                                        endY: 0,
                                        style: styles[ 'node'].border,
                                        strokeLineCap: StrokeLineCap.BUTT,
                                        strokeWidth: styles[ 'node'].border.thickness,
                                      ],
                                      null)
            JfxUtils.insertShape( l, builder, drawPane)
            ( 0 .. x).each {
                l = JfxUtils.createShape( builder,
                                          'Line',
                                          [ startX: it * scale * pageModel.size.p/hashCount,
                                            startY: 0,
                                            endX: it * scale * pageModel.size.p/hashCount,
                                            endY: i[it.mod( hashCount)],
                                            style: styles[ 'node'].border,
                                            strokeLineCap: StrokeLineCap.BUTT,
                                            strokeWidth: styles[ 'node'].border.thickness,
                                          ],
                                          null)

                JfxUtils.insertShape( l, builder, drawPane)
            }

            builder.defer {
                builder.foreground.children.clear()
                builder.foreground.children.add drawPane
            }

        } else {
            builder.defer {
                builder.foreground.children.clear()
            }
        }

    }

    public copyShapes( shapes) {
        // ask the current page to do it
        clipboard = []
        shapes.each { s ->
//            println 'copy shape ' + s
//            s.children?.each { c1 ->
//                println 'child1 ' + c1 + ' has position ' + c1.X + ', ' + c1.Y
//                c1.children?. each { c2 ->
//                    println 'child1 ' + c2 + ' has position ' + c2.X + ', ' + c2.Y
//                }
//            }
            clipboard << [ shape: s, X: s.X, Y: s.Y ]
        }
    }

    public pasteShapes() {
        // ask the last page to do it
        pages[-1].paste( clipboard)
        return clipboard.collect { it.shape }
    }

    public createEventObject( map ) {
        def obj
        map.builder = builder
        obj = new JfxEvent( map)
        return obj
    }

    public createPostNoteObject( map ) {
        def obj
        map.builder = builder
        obj = new JfxPostNote( map)
        return obj
    }

}
