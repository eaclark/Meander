package meander

import griffon.transform.Threading
import java.awt.Frame

class TableEditController {
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
    def addrow = { currRow, numRows, before ->
        def rows = []
        ( 1 .. numRows).each {
            rows << new FlowEvent()
        }
        model.flowEvents.addAll( currRow+1, rows)
        // renumber rows to account for the additions
        ( currRow .. model.flowEvents.size()-1).each { model.flowEvents[it].count = it+1 }
        model.table.model.fireTableDataChanged()
        if ( before) model.table.selectionModel.setSelectionInterval( currRow + numRows + 1, currRow + numRows + 1)
        else model.table.selectionModel.setSelectionInterval( currRow, currRow)
    }

    @Threading(Threading.Policy.SKIP)
    def deleteCurrentRow = { ->
        def currRow = model.selectedRow
        model.flowEvents.remove( currRow)
        // if a row before the last row was deleted, then some renumbering is needed
        if ( currRow < model.flowEvents.size()) {
            ( currRow .. model.flowEvents.size()-1).each {
                model.flowEvents[it].count = it+1
            }
        } else {
            currRow = currRow - 1  // should be the last row
        }
        model.table.model.fireTableDataChanged()
        model.table.selectionModel.setSelectionInterval( currRow, currRow)
    }

    @Threading(Threading.Policy.SKIP)
    void showTableEdit() {
        showDialog( 'tableEditDialog', false)
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    private showDialog( dialogName, pack = true) {
        def dialog = view."$dialogName"
        if( pack) dialog.pack()
        // if the dialog has been moved, leave it at the last location
        if( dialog.getLocation().x == 0 && dialog.getLocation().y == 0) {
            int x = app.windowManager.windows[0].x + (app.windowManager.windows[0].width - dialog.width) / 2
            int y = app.windowManager.windows[0].y + (app.windowManager.windows[0].height - dialog.height) / 2
            dialog.setLocation(x, y)
        }
        if( !dialog.isVisible()) dialog.visible = true
        dialog.toFront()
    }
}
