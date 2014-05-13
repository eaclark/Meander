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

/**
 * data associated with the label in a flow event
 */
@Bindable
class LabelStyle implements Serializable {
    LabelType labelType
    String labelFormat

    def copyTo( LabelStyle target) {
        target.labelType = labelType
        target.labelFormat = labelFormat
    }

    def toMap() {
        return [
            'labelType' : labelType,
            'labelFormat' : labelFormat
        ]
    }

    def toXML( xb, type) {
        xb."$type"() {
            xb.labelType "${labelType.name()}"
            xb.labelFormat labelFormat
        }
    }
}
