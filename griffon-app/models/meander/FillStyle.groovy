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
 * Specifies the fill style - used with the border object
 */
@Bindable
class FillStyle implements Serializable {
    FillType type
    Color foreColor
    Color backColor
    GradientType gradient
    int gradientScale
    GradientVariant variant

    def copyTo( FillStyle target) {
        target.type = type
        target.foreColor = foreColor
        target.backColor = backColor
        target.gradient = gradient
        target.gradientScale = gradientScale
        target.variant = variant
    }

    def toMap() {
        return [
            'fillType' : type,
            'foreColor' : Integer.toHexString( foreColor.RGB),
            'backColor' : Integer.toHexString( backColor.RGB),
            'gradientType' : gradient,
            'gradientScale' : gradientScale,
            'variant' : variant
        ]
    }

    def toXML( xb) {
        xb.fill() {
            xb.type "${type.name()}"
            xb.foreColor "0x${Integer.toHexString( foreColor.RGB & 0x00ffffff)}"
            xb.backColor "0x${Integer.toHexString( backColor.RGB & 0x00ffffff)}"
            xb.gradient "${gradient.name()}"
            xb.gradientScale "$gradientScale"
            xb.variant "${variant.name()}"
        }
    }
}
