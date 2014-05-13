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
 * data concerning the graphic within a flow event
 */
@Bindable
class GraphicStyle implements Serializable {
    LineType line
    GraphicType graphic
    LinePatternType pattern
    float thickness
    Color color

    def copyTo( GraphicStyle target) {
        target.line = line
        target.graphic = graphic
        target.pattern = pattern
        target.thickness = thickness
        target.color = color
    }

    def toMap() {
        return [
          'lineType' : line,
          'graphicType' : graphic,
          'linePattern' : pattern,
          'thickness' : thickness,
          'color' : Integer.toHexString( color.RGB)
        ]
    }

    def toXML( xb) {
        xb.graphic() {
            xb.line "${line.name()}"
            xb.graphic "${graphic.name()}"
            xb.pattern "${pattern.name()}"
            xb.thickness "$thickness"
            xb.color "0x${Integer.toHexString( color.RGB & 0x00ffffff)}"
        }
    }

//    def setThickness( String s) {
//        thickness = Float.parseFloat( s)
//    }
//
//    def setColor( String s){
//        if ( s.isInteger()) color = Color.decode( s)
//        else color = new Color( s)
//    }
}
