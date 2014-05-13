package meander

import meander.Uno.Impress.Impress
import meander.Com.PowerPoint.PowerPoint
import org.codehaus.groovy.scriptom.Scriptom
import griffon.transform.Threading

class OutputDialogController {
    // these will be injected by Griffon
    def model
    def view
    def evtList

    def actions = [
        'genImpress': { evt = null ->
            def d = new Date()
            model.counts.each { it.value = 0 }
            model.strings.counts.each { it.value = 0 }
            model.strings.counts[ 'today'] = d.dateString
            model.strings.counts[ 'now'] = d.timeString
            model.strings.counts[ 'raw'] = d
            model.unoObj = new Impress()
            model.unoDoc = model.unoObj.create( events: model.flowEvents,
                                                nodes: model.nodes,
                                                styles: model.styles,
                                                strings: model.strings,
                                                options: model.options,
                                                counts: model.counts,
                                                pageModel: model.pageModel)
            model.unoDoc.walkEvents()
        },

        'cancelImpress': { evt = null ->
            def file = Dialogs.showSaveDirectoryDialog("New Project", null, app.appFrames[0])
            if (file) {
                model.filePath = file.absolutePath
                if (!model.name) { model.name = file.name }
            }
        },

        'genPowerpoint': { evt = null ->
            def d = new Date()
            model.counts.each { it.value = 0 }
            model.strings.counts.each { it.value = 0 }
            model.strings.counts[ 'today'] = d.dateString
            model.strings.counts[ 'now'] = d.timeString
            model.strings.counts[ 'raw'] = d
            Scriptom.inApartment {
                model.comObj = new PowerPoint()
                model.comDoc = model.comObj.create( events: model.flowEvents,
                                                    nodes: model.nodes,
                                                    styles: model.styles,
                                                    strings: model.strings,
                                                    options: model.options,
                                                    counts: model.counts,
                                                    pageModel: model.pageModel)
                model.comDoc.walkEvents()
            }
        },

        'cancelPowerpoint': { evt = null ->
            def file = Dialogs.showSaveDirectoryDialog("New Project", null, app.appFrames[0])
            if (file) {
                model.filePath = file.absolutePath
                if (!model.name) { model.name = file.name }
            }
        }
    ]


//  void mvcGroupInit(Map args) {
    // this method is called after model and view are injected
    //actions.createImpress()
//  }

  // void mvcGroupDestroy() {
  //    // this method is called when the group is destroyed
  // }

    @Threading(Threading.Policy.SKIP)
    void showImpress() {
        showDialog( 'impressDialog')
    }

    @Threading(Threading.Policy.SKIP)
    void showPowerpoint() {
        showDialog( 'powerpointDialog')
    }

    private showDialog(dialogName, pack = true) {
        def dialog = view."$dialogName"
        if(pack) dialog.pack()
        int x = app.windowManager.windows[0].x + (app.windowManager.windows[0].width - dialog.width) / 2
        int y = app.windowManager.windows[0].y + (app.windowManager.windows[0].height - dialog.height) / 2
        dialog.setLocation(x, y)
        dialog.show()
    }
}
