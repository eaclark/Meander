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
 * data structure for information about a flow event
 */
class FlowEvent {
    int count
    String commandStr = ''
    String command = ''
    def commandTags = [:]
    String sourceStr = ''
    String source = ''
    def sourceTags = [:]
    String data = ''
    String destinationStr = ''
    String destination = ''
    def destinationTags = [:]
    String annotation = '\n'    // by putting a default newline character here, the
                                // event table editor makes the row taller

    String label = ''
}
