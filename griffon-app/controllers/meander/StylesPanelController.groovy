package meander

import griffon.transform.Threading

class StylesPanelController {
    // these will be injected by Griffon
    def model
    def view

    // void mvcGroupInit(Map args) {
    //    // this method is called after model and view are injected
    // }

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
    def action = { evt = null ->
    }
    */

    @Threading(Threading.Policy.SKIP)
    def changeStyle = { name ->
        // first, cache the style that is being edited
        // (if it is still there - it may have been cleared during a style reset)
        def cm = app.models.Meander.styles.editStyles[ model.currStyle.name]
        if( cm) model.currStyle.copyTo( cm )
        // now pull in the requested style
        model.currNode = name
        app.models.Meander.styles.copyEditStyle( name, model.currStyle)
    }

    @Threading(Threading.Policy.SKIP)
    def exportStyles = {
        def fileChoice = view.fd.showOpenDialog()
        if( fileChoice == view.fd.APPROVE_OPTION) {
            model.styles.exportStyles( view.fd.selectedFile.absolutePath)
        }
    }

//    def onShutdownStart = { app ->
//        println "${app.config.application.title} is shutting down"
//    }

    def onStartupEnd = { app ->

        // build the style name lists
        model.coreSL.addAll( app.models.Meander.styles.baseStyleList.sort() )
        model.userSL.addAll( app.models.Meander.styles.activeStyleList - model.coreSL )

        // initialize the style buttons
        model.currNode = 'message'
        view.initAllStyleButtons( model.coreSL, model.userSL)

        // and set up the current style and example node count
        app.models.Meander.styles.editStyles[ 'message'].copyTo( model.currStyle)
//        model.currNodeCount = 5
        model.currExample = 'message'

    }

    def onReadyStart = { app ->
        //    println "${app.config.application.title} is entering the Ready phase"
        // now that the styles are in place we can build the example panel

        // first setup the data model
        model.nodeX1 = model.exampleWidth / model.currNodeCount
        model.nodeX2 = model.nodeX1 / 2

        view.build StylePanel.ExampleBackground
        view.build StylePanel.ExampleMessage
        view.build StylePanel.ExampleView
        view.updateExampleSource()
    }

//    def onReadyEnd = { app ->
//        println view.fillgb.cl1.runtime.shape.bounds.width
//        println "${app.config.application.title} is at ready end"
//    }
}
