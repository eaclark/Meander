package meander

import griffon.transform.Threading
import javafx.embed.swing.JFXPanel
import meander.Jfx.JfxPreview

class JfxPreviewController {
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
        Remember that actions will be called outside of the UI thread
        by default. You can change this setting of course.
        Please read chapter 9 of the Griffon Guide to know more.
       
    def action = { evt = null ->
    }
    */

    def actions = [
            'genJfxPreview': { evt = null ->

                // clean out the results of a previous preview run
                view.previewgb.defer {
                    view.previewgb.background.children.clear() //previewgp.scene.root.children.clear()
                }

                def d = new Date()
                model.counts.each { it.value = 0 }
                model.strings.counts.each { it.value = 0 }
                model.strings.counts[ 'today'] = d.dateString
                model.strings.counts[ 'now'] = d.timeString
                model.strings.counts[ 'raw'] = d
                model.jfxObj = new JfxPreview()
                model.jfxDoc = model.jfxObj.create( events: model.flowEvents,
                                                    nodes: model.nodes,
                                                    styles: model.styles,
                                                    strings: model.strings,
                                                    options: model.options,
                                                    counts: model.counts,
                                                    pageModel: model.pageModel,
                                                    model: model,
                                                    builder: view.previewgb)
                model.jfxDoc.paintRulers()
                model.jfxDoc.walkEvents()
            },

            'toggleJfxRulers': { evt = null ->
                model.rulers = !model.rulers
                model.jfxDoc.paintRulers()
            },

            // placeholder until I look into actually canceling
            'cancelJfxPreview': { evt = null ->
                def file = Dialogs.showSaveDirectoryDialog("New Project", null, app.appFrames[0])
                if (file) {
                    model.filePath = file.absolutePath
                    if (!model.name) { model.name = file.name }
                }
            },
            ]

    @Threading(Threading.Policy.SKIP)
    void showPreview() {
        view.jfxPreview.show()
    }
}
