package meander

/**
 * Created with IntelliJ IDEA.
 * User: eac
 * Date: 2/21/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public enum GradientVariant {
    NOVARIANT( 'No Variant'),
    TOPLEFT( 'Top Left'),
    TOPRIGHT( 'Top Right'),
    BOTTOMLEFT( 'Bottom Left'),
    BOTTOMRIGHT( 'Bottom Right'),
    CENTER( 'Center'),
    AXISIN( 'Inward'),
    AXISOUT( 'Outward'),
    TOPDOWN( 'Top Down'),
    BOTTOMUP( 'Bottom Up'),
    LEFTRIGHT( 'From Left'),
    RIGHTLEFT( 'From Right')


    private String display

    GradientVariant( String value) {
        display = value
    }

    String toString() { return display }
}