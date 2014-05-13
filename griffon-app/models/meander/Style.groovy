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

import groovy.beans.Bindable
import org.codehaus.groovy.runtime.StackTraceUtils

import java.awt.Color

/**
 * information that defines a style
 */
@Bindable
class Style implements Serializable {

    String name
    TextStyle text = new TextStyle()
    GraphicStyle graphic = new GraphicStyle()
    BorderStyle border = new BorderStyle()
    FillStyle fill = new FillStyle()
    LabelStyle label = new LabelStyle()
    LabelStyle timestamp = new LabelStyle()
    AnimationType animate
    AlignmentType alignment
    PlacementType placement
    boolean wrap
    int trimLen
    int terseLen
    int linesAbove

    def fillCache = [:]
    String example = ''

    def copyTo( Style target) {
        target.name = name
        text.copyTo( target.text)
        graphic.copyTo( target.graphic)
        border.copyTo( target.border)
        fill.copyTo( target.fill)
        label.copyTo( target.label)
        timestamp.copyTo( target.timestamp)
        target.animate = animate
        target.alignment = alignment
        target.placement = placement
        target.wrap = wrap
        target.trimLen = trimLen
        target.terseLen = terseLen
        target.linesAbove = linesAbove
        target.example = example
    }

    def update( String updateStr, srcStyles) {
        def styleMap = [:]

        // only do work if style string is not empty
        if( updateStr != '') {
            // separate the style definition requests
            updateStr.split(';').each { styleMap[it.split('=')[0].trim()] = it.split('=')[1].trim()}

            // look for a basis request
            if( styleMap.basis) {
       //         println 'trying to update ' + this.name + ' with basis = ' + styleMap.basis
                srcStyles[ styleMap.basis].copyTo( this)
            }

            update( styleMap)
        }
    }

    def update( Map styleMap) {
        def props, lastp, firstp
        // walk through each style update string and apply it
        styleMap.each { styleProp, val ->
            if (styleProp != 'basis') {
                props = styleProp.split('\\.')
                try {
                    if (props.size() > 1) {
                        lastp = props[-1]

                        // have to map string representations to the correct values
                        switch( lastp) {
                        case 'foreColor':
                        case 'backColor':
                        case 'color':
                            if( val.isInteger()) {
                                val = Color.decode( val)
                            }
                            else {
                                // try parsing the color name
                                val = Color."$val"
                            }
                            break
                        case 'thickness':
                            val = Float.parseFloat( val)
                            break
                        case 'gradientScale':
                        case 'size':
                            val = Integer.parseInt( val)
                            break
                        }


                        firstp = props[0..-2]
                        firstp.inject( this, { obj, p -> obj."$p"})."$lastp" = val
                        //update( firstp.inject( this, { obj, p -> obj."$p"}), lastp, val)
                    } else this."$styleProp" = val

                    // now clear the fill cache
                    fillCache = [:]
                } catch (e) {
                    println "Invalid style string '$styleProp' for $name with $e"
                    println 'Style map = ' + styleMap
     //               StackTraceUtils.sanitize(e.printStackTrace())
                }
            }
        }
    }

    // morph the object to a map
    def toMap() {
        return [
            'name' : name,
            'text' : text.toMap(),
            'graphic' : graphic.toMap(),
            'border' : border.toMap(),
            'fill' : fill.toMap(),
            'label' : label.toMap(),
            'timestamp' : timestamp.toMap(),
            'animation' : animate,
            'alignment' : alignment,
            'placement' : placement,
            'wrap' : wrap,
            'trimLen' : trimLen,
            'terseLen' : terseLen,
            'linesAbove' : linesAbove,
            'example' : example
        ]
    }

    String toXML( xb) {
        xb.style( id: name ) {
            xb.name "$name"
            text.toXML( xb)
            graphic.toXML( xb)
            border.toXML( xb)
            fill.toXML( xb)
            label.toXML( xb, 'label')
            timestamp.toXML( xb, 'timestamp')
            xb.animate "${animate.name()}"
            xb.alignment "${alignment.name()}"
            xb.placement "${placement.name()}"
            xb.wrap "$wrap"
            xb.trimLen "$trimLen"
            xb.terseLen "$terseLen"
            xb.linesAbove "$linesAbove"
            xb.example "$example"
        }
    }

    def fromXML( xmlStr) {
        xb.style( id: name ) {
            xb.name "$name"
            text.toXML( xb)
            graphic.toXML( xb)
            border.toXML( xb)
            fill.toXML( xb)
            label.toXML( xb, 'label')
            timestamp.toXML( xb, 'timestamp')
            xb.animate "${animate.name()}"
            xb.alignment "${alignment.name()}"
            xb.placement "${placement.name()}"
            xb.wrap "$wrap"
            xb.trimLen "$trimLen"
            xb.terseLen "$terseLen"
            xb.linesAbove "$linesAbove"
            xb.example "$example"
        }
    }
}
