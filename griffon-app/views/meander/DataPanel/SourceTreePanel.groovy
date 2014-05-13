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
package meander.DataPanel

import net.miginfocom.swing.MigLayout
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.JScrollPane
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import java.awt.Insets
import javax.swing.border.TitledBorder
import javax.swing.border.Border
import java.awt.Graphics
import java.awt.Component
import java.awt.Rectangle
import java.awt.Font
import java.awt.Color
import javax.swing.JComponent
import java.awt.FontMetrics
import sun.swing.SwingUtilities2
import java.awt.Point
import javax.swing.border.EtchedBorder

/**
 * GUI panel for displaying currently included source files
 */

def class MyTitledBorder extends TitledBorder {

    // Space between the border and the component's edge
    static protected final int EDGE_SPACING = 0

    // Space between the border and text
    static protected final int TEXT_SPACING = 0

    private Point textLoc = new Point()

    public MyTitledBorder( String title) {
          super( title)
    }

    public MyTitledBorder( Border border) {
        super( border)
    }

    public MyTitledBorder( Border border, String title) {
        super( border, title)
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Border border = getBorder()
        if (getTitle() == null || getTitle().equals("")) {
            if (border != null) {
                border.paintBorder(c, g, x, y, width, height)
            }
            return
        }

        Rectangle grooveRect = new Rectangle( (int) x + EDGE_SPACING,
                                              (int) y + EDGE_SPACING,
                                              (int) width - (EDGE_SPACING * 2),
                                              (int) height - (EDGE_SPACING * 2))
        Font font = g.getFont()
        Color color = g.getColor()

        g.setFont(getFont(c))

        JComponent jc = (c instanceof JComponent) ? (JComponent)c : null

        FontMetrics fm = SwingUtilities2.getFontMetrics(jc, g)

        int fontHeight = fm.getHeight()
        int descent = fm.getDescent()
        int ascent = fm.getAscent()
        int diff
        int stringWidth = SwingUtilities2.stringWidth( jc, fm, getTitle())
        Insets insets

        if (border != null) {
            insets = border.getBorderInsets(c)
        } else {
            insets = new Insets(0, 0, 0, 0)
        }

        int titlePos = getTitlePosition()

        switch (titlePos) {
        case ABOVE_TOP:
            diff = ascent + descent + (Math.max(EDGE_SPACING, TEXT_SPACING*2) - EDGE_SPACING)
            grooveRect.y += diff
            grooveRect.height -= diff
            textLoc.y = grooveRect.y - (descent + TEXT_SPACING)
            break
        case TOP:
        case DEFAULT_POSITION:
            diff = Math.max(0, ((ascent/2) + TEXT_SPACING) - EDGE_SPACING)
            grooveRect.y += diff;
            grooveRect.height -= diff;
            textLoc.y = (grooveRect.y - descent) + (insets.top + ascent + descent)/2
            break
        case BELOW_TOP:
            textLoc.y = grooveRect.y + insets.top + ascent + TEXT_SPACING
            break
        case ABOVE_BOTTOM:
            textLoc.y = (grooveRect.y + grooveRect.height) - (insets.bottom + descent + TEXT_SPACING)
            break
        case BOTTOM:
            grooveRect.height -= (int) fontHeight/2
            textLoc.y = ((grooveRect.y + grooveRect.height) - descent) + ((ascent + descent) - insets.bottom)/2
            break
        case BELOW_BOTTOM:
            grooveRect.height -= fontHeight
            textLoc.y = grooveRect.y + grooveRect.height + ascent + TEXT_SPACING
            break
        }

        int justification = getTitleJustification()

        if ( isLeftToRight(c)) {
            if ( justification==LEADING || justification==DEFAULT_JUSTIFICATION) {
                justification = LEFT
            }
            else if(justification==TRAILING) {
                justification = RIGHT
            }
        }
        else {
            if(justification==LEADING || justification==DEFAULT_JUSTIFICATION) {
                justification = RIGHT
            }
            else if(justification==TRAILING) {
                justification = LEFT
            }
        }

        switch ( justification) {
        case LEFT:
            textLoc.x = grooveRect.x + TEXT_INSET_H + insets.left
            break
        case RIGHT:
            textLoc.x = (grooveRect.x + grooveRect.width) - (stringWidth + TEXT_INSET_H + insets.right)
            break
        case CENTER:
            textLoc.x = grooveRect.x + ((grooveRect.width - stringWidth) / 2)
            break
        }

        // If title is positioned in middle of border AND its fontsize
        // is greater than the border's thickness, we'll need to paint
        // the border in sections to leave space for the component's background
        // to show through the title.
        //
        if (border != null) {
            if ((( titlePos == TOP || titlePos == DEFAULT_POSITION) &&
                 (grooveRect.y > textLoc.y - ascent)) ||
                (titlePos == BOTTOM && (grooveRect.y + grooveRect.height < textLoc.y + descent))
               ) {
                Rectangle clipRect = new Rectangle()

                // save original clip
                Rectangle saveClip = g.getClipBounds()

                // paint strip left of text
                clipRect.setBounds(saveClip)
                if ( computeIntersection( clipRect,
                                          x,
                                          y,
                                          (int) textLoc.x-1-x,
                                          height)) {
                    g.setClip(clipRect)
                    border.paintBorder(c, g, (int) grooveRect.x, (int) grooveRect.y, (int) grooveRect.width, (int) grooveRect.height)
                }

                // paint strip right of text
                clipRect.setBounds(saveClip)

                if ( computeIntersection( clipRect,
                                          (int) textLoc.x+stringWidth+1,
                                          y,
                                          (int) x+width-(textLoc.x+stringWidth+1),
                                          height)) {
                    g.setClip(clipRect)
                    border.paintBorder( c, g, (int) grooveRect.x, (int) grooveRect.y, (int) grooveRect.width, (int) grooveRect.height)
                }

                if (titlePos == TOP || titlePos == DEFAULT_POSITION) {
                    // paint strip below text
                    clipRect.setBounds(saveClip)

                    if ( computeIntersection( clipRect,
                                              (int) textLoc.x-1,
                                              (int) textLoc.y+descent,
                                              stringWidth+2,
                                              (int) y+height-textLoc.y-descent)) {
                        g.setClip(clipRect)
                        border.paintBorder(c, g, (int) grooveRect.x, (int) grooveRect.y, (int) grooveRect.width, (int) grooveRect.height)
                    }

                } else { // titlePos == BOTTOM
                  // paint strip above text
                    clipRect.setBounds(saveClip)

                    if ( computeIntersection( clipRect,
                                              (int) textLoc.x-1,
                                              y,
                                              stringWidth+2,
                                              (int) textLoc.y - ascent - y)) {
                        g.setClip(clipRect)
                        border.paintBorder(c, g, (int) grooveRect.x, (int) grooveRect.y, (int) grooveRect.width, (int) grooveRect.height)
                    }
                }

                // restore clip
                g.setClip(saveClip)

            } else {
                border.paintBorder( c, g, (int) grooveRect.x, (int) grooveRect.y, (int) grooveRect.width, (int) grooveRect.height)
            }
        }

        g.setColor(getTitleColor())

        SwingUtilities2.drawString(jc, g, getTitle(), (int) textLoc.x, (int) textLoc.y)

        g.setFont(font)
        g.setColor(color)
    }
}

def makeScriptAction = { id, name -> }

panel( id: 'tp',
       border: titledBorder( border: new EtchedBorder(), titlePosition: TitledBorder.TOP, 'Included Source File(s)'),
//       border: new MyTitledBorder( 'Included Source File(s)'),
       constraints: 'cell 2 0 2 1, grow, sg, bottom',
       layout: new MigLayout( )
     ) {
    scrollPane( //id: 'tp',
//                maximumSize: [height: 75],
                constraints: 'spanx, wmax 500, wmin 500, align center',
                verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
        def renderer = new DefaultTreeCellRenderer()
        def top = new DefaultMutableTreeNode('No Included Source File')
        renderer.setOpenIcon(null)
        renderer.setClosedIcon(null);
        renderer.setLeafIcon(null)
        tree( id: 'sourceTree',
              model: new DefaultTreeModel(top),
              rootVisible: false,
              visibleRowCount: 4,
              cellRenderer: renderer)
    }
}
