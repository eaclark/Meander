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
 * determines the animation used for the event in the output file
 */
@Bindable
public enum AnimationType {
  NOANIM( 'No Animation'),
  FROMSOURCE( 'From Source'),
  FROMTOP( 'From Top'),
  FROMBOTTOM( 'From Bottom'),
  APPEAR( 'Appear')

  private String display

  AnimationType( String value) {
    display = value
  }

  String toString() { return display }
}