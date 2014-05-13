package meander

import groovy.beans.Bindable

class JfxPreviewModel {
   // @Bindable String propName
    // the current flow events
    @Bindable List flowEvents

    def pageModel
    def counts
    def baseDocument
    def options
    def strings

    def tableEditController
    def tableEditModel

    def stylesView
    def stylesModel
    def styles

    def jfxObj
    def jfxDoc

    @Bindable int pageIdx = -1   // index of the currently displayed slide

    // list of currently visible flow nodes
    @Bindable List nodes

    // show rulers or not
    @Bindable boolean rulers = false

    // type of printer whitespace buffer
    @Bindable String buffer = 'Impress'
}