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
 * enumerations of the different ways to position a flow event
 */
public enum PlacementType {
  SRCJUST( 'Source Justify'),
  DESTJUST( 'Destination Justify'),
  MIDDLE( 'Middle'),
  OVERLAP( 'Overlap'),
  SPAN( 'Span')

  private String display

  PlacementType( String value) {
    display = value
  }

  String toString() { return display }
}