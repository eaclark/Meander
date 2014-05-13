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
package meander.Com.PowerPoint

import com.sun.star.awt.Point
import com.sun.star.awt.Size
import org.codehaus.groovy.scriptom.SafeArray

/**
 * the shapes on a PowerPoint slide corresponding the the post flow depiction of the
 * text in a Flow Event and/or the text of any annotation associated with the Flow Event
 */
class ComPPTPostNote {
    def event
    def style
    def options
    def leftM
    def rightM

    public draw( page, currY) {
        Point ip
        int adj = 2i

        int labelWidth = 50i   // 0.7 in  // 0.5 in
        int evtWidth = 0i
        int noteWidth = 0
        def lRect, rect1, rect2
        def label, text1, text2
        def gh
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

        // insert the backing borders
        lRect = ComPPTUtils.insertBorder( page, new Point( leftM, currY), new Size( labelWidth, 50i), style)

        label = ComText.drawPositioned( event.label,
                                        page,
                                        style,
                                        new Point( leftM, currY),
                                        labelWidth
                                      )
        gh = label.Height

        if ( evtWidth > 0) {
            rect1 = ComPPTUtils.insertBorder( page,
                                              new Point( leftM + labelWidth, currY),
                                              new Size( evtWidth, 50i),
                                              style)
            text1 = ComText.drawPositioned( event.data,
                                            page,
                                            style,
                                            new Point( leftM + labelWidth, currY),
                                            evtWidth
                                          )
            if ( gh < text1.Height) gh = text1.Height
        }
        if ( noteWidth > 0) {
            rect2 = ComPPTUtils.insertBorder( page,
                                              new Point( leftM  + labelWidth + evtWidth, currY),
                                              new Size( noteWidth, 50i),
                                              style)
            text2 = ComText.drawPositioned( event.annotation,
                                            page,
                                            style,
                                            new Point( leftM + labelWidth + evtWidth, currY),
                                            noteWidth
                                          )
            if ( gh < text2.Height) gh = text2.Height
        }

        // start gathering the shape names
        def nameList = [ lRect.name, label.name]

        // make the all the borders the same height
        lRect.Height = gh
        if( rect1) {
            rect1.Height = gh
            nameList << rect1.name
            nameList << text1.name
        }
        if( rect2) {
            rect2.Height = gh
            nameList << rect2.name
            nameList << text2.name
        }

        def nameArray = new SafeArray( (String[])nameList)
        group = page.Shapes.Range( nameArray).Group()

        return group
    }
}
