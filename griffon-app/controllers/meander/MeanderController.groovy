package meander

import javax.swing.JFileChooser
import source.SourceWriter
import source.SourceWord
import javax.swing.tree.DefaultMutableTreeNode

class MeanderController {
    // these will be injected by Griffon
    def model
    def view

    def openFile = {
        def openResult = view.fd.showOpenDialog()
        if( JFileChooser.APPROVE_OPTION == openResult ) {
        }
    }

    def quitApp = {
/*
 * it appears that the COM objects have been release by the time we get here
 * and that we can't close the application at this time
 *
 * this means that any Word application has to be close earlier on - immediately after use
 *
        // check if the source document application needs to be closed
        if ( app.models.Data.baseDocument) {
            Scriptom.inApartment {

                app.models.Data.baseDocument.application = new ActiveXObject( "Word.Application")
                println 'trying to quit'
                app.models.Data.baseDocument.application.quit(0)
            }
        }
*/
        app.shutdown()
    }

    def buildPreview = {
        model.pc.showPreview()
        model.pc.actions.genJfxPreview()
    }

    def buildImpress = {
        model.oc.showImpress()
        model.oc.actions.genImpress()
    }

    def buildPowerpoint = {
        model.oc.showPowerpoint()
        model.oc.actions.genPowerpoint()
    }

    def saveFile = {
        def doc
        def fileChoice

        // look to see if baseDocument is already set
        // If so, then use it.  If no, then treat like Save As
        if ( model.dm.baseDocument) {
            doc = model.dm.baseDocument
            doc.rows.clear()
            doc.rows.addAll( model.flowEvents)
            doc.rowCnt = model.flowEvents.count
        } else {
            fileChoice = view.fd.showOpenDialog()
            if( fileChoice == view.fd.APPROVE_OPTION) {
                def fname = view.fd.selectedFile.absolutePath

                def m = fname =~ /[^\.]*$/    // grab the file extension
                switch ( m[0]) {
                case 'odt':
                case 'doc':
                case 'docx':
                    switch( m[0]) {
                    case 'odt':
                        doc = new SourceWriter()
                        break
                    case 'doc':
                    case 'docx':
                        doc = new SourceWord()
                    }
                }

                doc.fileName = fname
                doc.rows.addAll( model.flowEvents)
                doc.rowCnt = doc.rows.count
                model.dm.baseDocument = doc
            } else return  // if no baseDocument and they user didn't pick a file name, bail out
        }
        doOutside {
            doc.save()
            doLater {
                // clear the old events
                model.dm.hasEventContents = true
                model.dm.hasModifiedContents = false

                model.dv.sourceTree.model.root.removeAllChildren()
                model.dv.sourceTree.model.root.add( new DefaultMutableTreeNode( doc.fileName))
                model.dv.sourceTree.model.root.userObject = 'Included Source File'
                model.dv.sourceTree.model.reload()
            }
        }
    }

    def saveAsFile = {
        def doc
        def fileChoice = view.fd.showOpenDialog()
        if( fileChoice == view.fd.APPROVE_OPTION) {
            def fname = view.fd.selectedFile.absolutePath

            def m = fname =~ /[^\.]*$/    // grab the file extension
            switch ( m[0]) {
            case 'odt':
            case 'doc':
            case 'docx':
                switch( m[0]) {
                case 'odt':
                    doc = new SourceWriter()
                    break
                case 'doc':
                case 'docx':
                    doc = new SourceWord()
                }
            }

            doc.fileName = fname
            doc.rows.addAll( model.flowEvents)
            doc.rowCnt = doc.rows.count

            doOutside {
                doc.save()
                doLater {
                    // clear the old events
                    model.dm.baseDocument = doc
                    model.dm.hasEventContents = true
                    model.dm.hasModifiedContents = false

                    model.dv.sourceTree.model.root.removeAllChildren()
                    model.dv.sourceTree.model.root.add( new DefaultMutableTreeNode( fname))
                    model.dv.sourceTree.model.root.userObject = 'Included Source File'
                    model.dv.sourceTree.model.reload()
                }
            }
        }
    }

//    void mvcGroupInit(Map args) {
    //      this method is called after model and view are injected
//        def mvcId = 'OutpuDialog'
//        def ( m, v, c) = createMVCGroup( 'OutputDialog', mvcId, [ mvcId: mvcId])
//        model.om = m; model.ov = v; model.oc = c
//    }

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
    def action = { evt = null ->
    }
    */
}
