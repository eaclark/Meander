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

class OutputDialogModel {
    // @Bindable String propName

    def flowEvents
    def nodes
    def styles
    def strings
    def options
    def pageModel
    def counts

    def unoObj         // for handling the UNO API
    def unoDoc         // for handling the UNO API

    def comObj         // for handling the COM API
    def comDoc         // for handling the COM API

    def jfxObj         // for handling the COM API
    def jfxDoc         // for handling the COM API

}