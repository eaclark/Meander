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

import java.awt.Color
import groovy.beans.Bindable

/**
 * the information concerning how to display text
 */
@Bindable
class TextStyle implements Serializable {
    String face
    int size
    boolean bold
    boolean italic
    Color color

    def copyTo( TextStyle target) {
        target.face = face
        target.size = size
        target.bold = bold
        target.italic = italic
        target.color = color
    }

    def toMap() {
        return [
          'face' : face,
          'size' : size,
          'bold' : bold,
          'italic' : italic,
          'color' : Integer.toHexString( color.RGB)
        ]
    }

    String toXML( xb) {
        xb.text() {
            xb.face "$face"
            xb.size "$size"
            xb.bold "$bold"
            xb.italic "$italic"
            xb.color "0x${Integer.toHexString( color.RGB & 0x00ffffff)}"
        }
    }
}
