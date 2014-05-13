package meander.Jfx

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/3/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
class JfxPreview {
    def doc

    JfxPreview() {

    }

    public create( map) {
        doc = new JfxDocument( map)
        return doc
    }
}
