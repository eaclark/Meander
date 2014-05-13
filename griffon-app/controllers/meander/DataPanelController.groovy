package meander

//import ca.odell.glazedlists.BasicEventList

class DataPanelController {
    // these will be injected by Griffon
    def model
    def view

    //void mvcGroupInit(Map args) {
    //    this method is called after model and view are injected
    //}

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
    def action = { evt = null ->
    }
    */

/*    List getNodes( List el) {
        def nl = []
        el.each { evt ->
            def n = evt.source
            n?.split(',').each { str ->
                println str
                if ( !nl.contains( str.trim())) nl << str.trim()
            }
            n = evt.destination
            n?.split(',').each { str ->
                println str
                if ( !str.isBigInteger() ) { //if ( !(('1' .. '9') + ("10' .. '19')).contains(n) ) {
                    if ( !nl.contains( str.trim())) nl << str.trim()
                }
            }
        }
        return nl
    } */

/*    def invokeTableEdit = { } */

/*  def onReadyStart = { app ->
        // got the add row clue from http://javaconfessions.com/2010/02/add-rows-to-groovy-swingbuilder.html
        def columnModel = view.flowTable.columnModel
        def rows = view.flowTableModel.rowsModel.value    // getRowsModel().getValue()
        (0..22).each { num ->
            rows.add( [ cnt: num+1, cmd: '', src: '', data: '', dest: '', annt: ''])
        }
        view.flowTableModel.rowsModel.value = rows        // getRowsModel().setValue( rows )
        view.flowTableModel.fireTableDataChanged()
    } */
}
