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

/**
 * enumeration of the different ways to associate a label with a flow event
 * it also applies to time stamps
 */
public enum LabelType {
    NOLABEL( 'No Label'),
    LEFTSIDE( 'Left Side'),
    PREPEND( 'Prepend'),
    EMBEDDED( 'Embedded'),
    RIGHTSIDE( 'Right Side')

    private String display

    LabelType( String value) {
       display = value
    }

    String toString() { return display }

    public static LabelType fromString( String s) {
        println 'got here with ' + s
    }
}