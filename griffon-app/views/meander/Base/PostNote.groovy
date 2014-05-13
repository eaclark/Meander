package meander.Base

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/19/13
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
class PostNote {
    def event
    def convertUnits
    def style
    def options
    def leftM
    def rightM

    def labelWidth
    def bh       // base height used for null lines


    PostNote( named=[:]) {
        named.each { key, value -> this[ key] = value }
        bh = (int)(style.text.size + 4i) * convertUnits
        labelWidth = (int)(30i * convertUnits)
    }

    public draw( page, pageSupplier, currY) {
        def ip
        int evtWidth = 0i
        int noteWidth = 0
        def lRect, rect1, rect2
        def label, text1, text2
        def gh = bh      // really need to look at sizing null lines better
        def gl = []
        def group


        if ( options.postfix && options.notes) {
            // they're both there
            evtWidth = (int) (rightM - leftM - labelWidth)/2
            noteWidth = evtWidth
        } else if (options.postfix) {
            evtWidth = (int) (rightM - leftM - labelWidth)
        } else if (options.notes) {
            noteWidth = (int) (rightM - leftM - labelWidth)
        }

        // build the label portion
        // insert the backing border
        lRect = insertBorder( page,
                              createPoint( leftM, currY),
                              createSize( labelWidth, bh),
                              style
                            )
        gl << lRect
        // and text if present
        if( event.label) {
            label = drawPositionedText( event.label,
                                        page,
                                        style,
                                        createPoint( leftM, currY),
                                        labelWidth
                                      )
            gl << label
            gh = label.Height
        }

        // if events are requested
        if ( evtWidth > 0) {
            // backing border
            rect1 = insertBorder( page,
                                  createPoint( leftM + labelWidth, currY),
                                  createSize( evtWidth, bh),
                                  style
                                )
            gl << rect1
            // any text
            if ( event.data) {
                text1 = drawPositionedText( event.data,
                                            page,
                                            style,
                                            createPoint( leftM + labelWidth, currY),
                                            evtWidth
                                          )
                gl << text1
                if ( gh < text1.Height) gh = text1.Height
            }
        }

        // if notes are requested
        if ( noteWidth > 0) {
            // backing border
            rect2 = insertBorder( page,
                                  createPoint( leftM + labelWidth + evtWidth, currY),
                                  createSize( noteWidth, bh),
                                  style
                                )
            gl << rect2
            // any notes text
            if ( event.annotation) {
                text2 = drawPositionedText( event.annotation,
                                            page,
                                            style,
                                            createPoint( leftM + labelWidth + evtWidth, currY),
                                            noteWidth
                                          )
                gl << text2
                if ( gh < text2.Height) gh = text2.Height
            }
        }

        // make the all the borders the same height
        lRect.Size = createSize( labelWidth, gh)
        rect1?.Size = createSize( evtWidth, gh)
        rect2?.Size = createSize( noteWidth, gh)

        group = buildGroup( page, gl )

        currY += group.Height

        return group
    }

    def insertBorder( pane, point, size, style ) { }

    def drawPositionedText( str, pane, style, ip, wid) { }

    def buildGroup( pane, shapeList) { }

    Object createPoint( x, y) { }

    Object createSize( w, h) { }
}
