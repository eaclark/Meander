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
 * enumeration for the different Fill types
 */
public enum FillType {
  NOFILL( 'No Fill'),
  ONECOLOR( 'One Color'),
  TWOCOLOR( 'Two Colors')

  private String display

  FillType( String value) {
    display = value
  }

  String toString() { return display }
}