/*
 * Copyright 2011 Ed Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package meander.Uno.Impress

import meander.Uno.*
import static meander.GraphicType.*
import static meander.LinePatternType.*
import com.sun.star.awt.Point
import com.sun.star.awt.Size
import com.sun.star.beans.XPropertySet

/**
 * routines for drawing an Impress shape representing a Flow Event graphic
 */
class UnoGraphic {
    static int arrowUnit = 100i
    
    static public draw( event, page, pagesSupplier, style, nodeInfo, xPosInfo, ypos, compList) {
        def gr, gr1, gr2, gr3, lw
        int xpos1, xpos2
        def graphic = style.graphic.graphic
        def linetype = style.graphic.line
        def pattern = style.graphic.pattern
        int thickness = style.graphic.thickness * 25.4      // thickness is in inches, convert to mm
        def color = style.graphic.color.RGB

        xpos1 = xPosInfo.x1
        xpos2 = xPosInfo.x2

        // shift everything down to account for the arrowhead thickness
        ypos += arrowUnit

        switch( graphic) {
        case ARROW:
            return drawArrow( page, pagesSupplier, xpos1, xpos2, ypos, pattern, thickness, color, linetype)
            break
        case DOUBLEARROW:
            return drawDoubleArrow( page, pagesSupplier, xpos1, xpos2, ypos, pattern, thickness, color, linetype)
            break
        case DOTTED:
            return drawDotted( page, pagesSupplier, xpos1, xpos2, ypos, pattern, thickness, color, linetype, compList)
            break
        case NOGRAPHIC:
        default:
            return null
        }
    }
    
    static private drawArrow( page, pagesSupplier, xpos1, xpos2, ypos, pattern, thickness, color, linetype) {
        def gr, gr1, gr2, gr3
        switch( pattern) {
        case SINGLE:
            // shorten the line so it just touches the arrowhead
            // otherwise a thick line (specified by the user) might show
            // underneath the tip of the arrow
            Size sz
            if ( xpos2 > xpos1) {
                sz = new Size( xpos2 - (arrowUnit * 2i) - xpos1, 0i)
            } else {
                // in this case, the size will be negative - causing the line to be drawn to the left
                // so to shorten it, we have to *add* the arrowhead width
                sz = new Size( xpos2 + (arrowUnit * 2i) - xpos1, 0i)
            }
            gr1 = UnoUtils.insertLine( pagesSupplier,
                                      page,
                                      new Point( xpos1, ypos),
                                      sz,
                                      color,
                                      thickness,
                                      linetype
                                    )
            break
        case THICKBETWEENTHIN:
            if ( xpos2 > xpos1) {
                // arrow on the right
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - (thickness*3i)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos - (thickness*3i)) ],
                                                [ new Point( xpos1,                    ypos - (int)(thickness/2i)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos - (int)(thickness/2i)) ],
                                                [ new Point( xpos1,                    ypos + (int)(thickness/2i)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos + (int)(thickness/2i)) ],
                                                [ new Point( xpos1,                    ypos + (thickness*3i)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos + (thickness*3i)) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )

                } else {
                 // arrow on the left
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - (thickness*3i)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos - (thickness*3i)) ],
                                                [ new Point( xpos1,                    ypos - (int)(thickness/2i)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos - (int)(thickness/2i)) ],
                                                [ new Point( xpos1,                    ypos + (int)(thickness/2i)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos + (int)(thickness/2i)) ],
                                                [ new Point( xpos1,                    ypos + (thickness*3i)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos + (thickness*3i)) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )
            }
            break
        case THICKTHIN:
            if ( xpos2 > xpos1) {
                // arrow on the right
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - thickness),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos - thickness) ],
                                                [ new Point( xpos1,                    ypos),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos) ],
                                                [ new Point( xpos1,                    ypos + (thickness*2i)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos + (thickness*2i)) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )

            } else {
                // arrow on the left
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - thickness),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos - thickness) ],
                                                [ new Point( xpos1,                    ypos),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos) ],
                                                [ new Point( xpos1,                    ypos + (thickness*2i)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos + (thickness*2i)) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )
            }
            break
        case THINTHICK:
            if ( xpos2 > xpos1) {
                // arrow on the right
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - (thickness*2i)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos - (thickness*2i)) ],
                                                [ new Point( xpos1,                    ypos),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos) ],
                                                [ new Point( xpos1,                    ypos + thickness),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos + thickness) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )

            } else {
                // arrow on the left
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - (thickness*2i)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos - (thickness*2i)) ],
                                                [ new Point( xpos1,                    ypos),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos) ],
                                                [ new Point( xpos1,                    ypos + thickness),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos + thickness) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )
            }
            break
        case THINTHIN:
            if ( xpos2 > xpos1) {
                // arrow on the right
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - (int)(1*thickness)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos - (int)(1*thickness)) ],
                                                [ new Point( xpos1,                    ypos + (int)(1*thickness)),
                                                  new Point( xpos2 - (arrowUnit * 2i), ypos + (int)(1*thickness)) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )

            } else {
                // arrow on the left
                gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                               page,
                                               [[ new Point( xpos1,                    ypos - (int)(2*thickness)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos - (int)(2*thickness)) ],
                                                [ new Point( xpos1,                    ypos + (int)(2*thickness)),
                                                  new Point( xpos2 + (arrowUnit * 2i), ypos + (int)(2*thickness)) ]
                                               ] as Point[][],
                                               color,
                                               thickness
                                             )
            }
            break
        }

        // now draw the arrowhead
        if ( xpos2 > xpos1) {
            // arrow on the right
            gr2 = UnoUtils.insertPolygon( pagesSupplier,
                                          page,
                                          [[ new Point( xpos2, ypos),
                                             new Point( xpos2 - (arrowUnit * 2i), ypos - arrowUnit),
                                             new Point( xpos2 - (arrowUnit * 2i), ypos + arrowUnit) ]
                                          ] as Point[][],
                                          color,
                                          thickness
                                        )

        } else {
            gr2 = UnoUtils.insertPolygon( pagesSupplier,
                                          page,
                                          [[ new Point( xpos2, ypos),
                                             new Point( xpos2 + (arrowUnit * 2i), ypos - arrowUnit),
                                             new Point( xpos2 + (arrowUnit * 2i), ypos + arrowUnit) ]
                                          ] as Point[][],
                                          color,
                                          thickness
                                        )
        }

        // group the pieces into one graphics
        gr = UnoUtils.buildGroup( pagesSupplier, page, [ gr1, gr2] )
        return gr
    }

    static private drawDoubleArrow( page, pagesSupplier, xpos1, xpos2, ypos, pattern, thickness, color, linetype) {
        def gr, gr1, gr2, gr3

        // since there's an arrow at each end, the direction doesn't matter
        // normalize the x1 and x2 so x1 < x2
        if (xpos1 > xpos2) (xpos1, xpos2) = [ xpos2, xpos1]

        switch( pattern) {
        case SINGLE:
            Size sz = new Size( xpos2 - xpos1 - 300i, 0i)
            gr1 = UnoUtils.insertLine( pagesSupplier,
                                      page,
                                      new Point( xpos1 + (arrowUnit * 2i), ypos),
                                      sz,
                                      color,
                                      thickness,
                                      linetype
                                    )
            break
        case THICKBETWEENTHIN:
            gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1 + (arrowUnit * 2i), ypos - (thickness*3i)),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos - (thickness*3i)) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos - (int)(thickness/2i)),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos - (int)(thickness/2i)) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos + (int)(thickness/2i)),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos + (int)(thickness/2i)) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos + (thickness*3i)),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos + (thickness*3i)) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        case THICKTHIN:
            gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1 + (arrowUnit * 2i), ypos - thickness),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos - thickness) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos + (thickness*2i)),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos + (thickness*2i)) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        case THINTHICK:
            gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1 + (arrowUnit * 2i), ypos - (thickness*2i)),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos - (thickness*2i)) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), ypos + thickness),
                                              new Point( xpos2 - (arrowUnit * 2i), ypos + thickness) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        case THINTHIN:
            gr1 = UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1 + (arrowUnit * 2i), (int)(ypos - thickness/2)),
                                              new Point( xpos2 - (arrowUnit * 2i), (int)(ypos - thickness/2)) ],
                                            [ new Point( xpos1 + (arrowUnit * 2i), (int)(ypos + thickness/2)),
                                              new Point( xpos2 - (arrowUnit * 2i), (int)(ypos + thickness/2)) ]
                                           ] as Point[][],
                                           color,
                                           (int)(thickness/2)
                                         )
            break
        }

        // now draw both arrowheads
        //           tweaking the xpos1 and xpos2 points of the arrow because the
        //           polygon is using 'round' joints instead of 'miter' (can't choose
        //           'miter' in the API yet).  this pulls the arrowhead back a bit
        def ah_adj
        if ( xpos1 < xpos2) ah_adj = -50i else ah_adj = 50i
        gr2 = UnoUtils.insertPolygon( pagesSupplier,
                                      page,
                                      [[ new Point( xpos2 + ah_adj,           ypos),
                                         new Point( xpos2 - (arrowUnit * 2i), ypos - arrowUnit),
                                         new Point( xpos2 - (arrowUnit * 2i), ypos + arrowUnit) ]
                                      ] as Point[][],
                                      color,
                                      thickness
                                    )
        gr3 = UnoUtils.insertPolygon( pagesSupplier,
                                      page,
                                      [[ new Point( xpos1 - ah_adj,           ypos),
                                         new Point( xpos1 + (arrowUnit * 2i), ypos - arrowUnit),
                                         new Point( xpos1 + (arrowUnit * 2i), ypos + arrowUnit) ]
                                      ] as Point[][],
                                      color,
                                      thickness
                                    )

        // and group the graphic pieces together
        gr = UnoUtils.buildGroup( pagesSupplier, page, [ gr1, gr2, gr3] )
        return gr
    }

    static private drawDotted( page, pagesSupplier, xpos1, xpos2, ypos, pattern, thickness, color, linetype, compList) {
        def gr = []
        switch( pattern) {
        case SINGLE:
            Size sz = new Size( xpos2 - xpos1, 0i)
            gr << UnoUtils.insertLine( pagesSupplier,
                                       page,
                                       new Point( xpos1, ypos),
                                       sz,
                                       color,
                                       thickness,
                                       linetype
                                     )
            break
        case THICKBETWEENTHIN:
            gr << UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1, ypos - (thickness*3i)),
                                              new Point( xpos2, ypos - (thickness*3i)) ],
                                            [ new Point( xpos1, ypos - (int)(thickness/2i)),
                                              new Point( xpos2, ypos - (int)(thickness/2i)) ],
                                            [ new Point( xpos1, ypos + (int)(thickness/2i)),
                                              new Point( xpos2, ypos + (int)(thickness/2i)) ],
                                            [ new Point( xpos1, ypos + (thickness*3i)),
                                              new Point( xpos2, ypos + (thickness*3i)) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        case THICKTHIN:
            gr << UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1, ypos - thickness),
                                              new Point( xpos2, ypos - thickness) ],
                                            [ new Point( xpos1, ypos),
                                              new Point( xpos2, ypos) ],
                                            [ new Point( xpos1, ypos + (thickness*2i)),
                                              new Point( xpos2, ypos + (thickness*2i)) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        case THINTHICK:
            gr << UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1, ypos - (thickness*2i)),
                                              new Point( xpos2, ypos - (thickness*2i)) ],
                                            [ new Point( xpos1, ypos),
                                              new Point( xpos2, ypos) ],
                                            [ new Point( xpos1, ypos + thickness),
                                              new Point( xpos2, ypos + thickness) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        case THINTHIN:
            gr << UnoUtils.insertPolyLine( pagesSupplier,
                                           page,
                                           [[ new Point( xpos1, ypos - (int)(2*thickness)),
                                              new Point( xpos2, ypos - (int)(2*thickness)) ],
                                            [ new Point( xpos1, ypos + (int)(2*thickness)),
                                              new Point( xpos2, ypos + (int)(2*thickness)) ]
                                           ] as Point[][],
                                           color,
                                           thickness
                                         )
            break
        }

        // add the dots, one per node
        compList.each { x ->
            Size sz = new Size( 400i, 200i)
            gr << UnoUtils.insertEllipse( pagesSupplier,
                                          page,
                                          new Point( x - 200i, ypos - 100i),
                                          sz,
                                          color
                                        )
        }
        gr = UnoUtils.buildGroup( pagesSupplier, page, gr )
        return gr
    }
}
