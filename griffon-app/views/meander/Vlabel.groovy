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
package meander

import javax.swing.JLabel
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.Graphics
import java.awt.image.BufferedImage

/**
 *  Vertical label - not finished, not debugged, not being used right now
 */


// http://www.thatsjava.com/java-essentials/76852/
// http://stackoverflow.com/questions/620929/rotate-a-swing-jlabel
// http://www.coderanch.com/t/346914/GUI/java/JTableHeader

class Vlabel extends JLabel {

  private boolean needsRotate

  Vlabel( String text) { super( text) }

  Dimension getSize() {
//    if (!needsRotate) {
//        return super.getSize();
//      }

    Dimension sSize = super.getSize()
    new Dimension( (int) sSize.height, (int) sSize.width)
  }
/*  int getHeight() {
    getSize().height
  }
  int getWidth() {
    getSize().width
  }*/
  void paintComponent( Graphics g) {
    int width = getSize().width, height = getSize().height
    BufferedImage img = new BufferedImage( height, width, BufferedImage.TYPE_INT_ARGB)
    Graphics2D gr = img.createGraphics();
    AffineTransform trans = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, (double)width)
    gr.translate(0, height)
    gr.setTransform( trans)
    //gr.translate( 0, -width)
    super.paintComponent( gr)
    g.drawImage( img, 0, 0, null)
//    Graphics2D gr = (Graphics2D) g.create()
//    gr.translate(0, getSize().getHeight());
//    gr.transform(AffineTransform.getQuadrantRotateInstance(-1))
//    needsRotate = true
//    super.paintComponent(gr)
//    needsRotate = false
  }
}
