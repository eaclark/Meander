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

import ca.odell.glazedlists.BasicEventList
import static meander.AlignmentType.*
import static meander.GraphicType.*
import static meander.GradientType.*
import static meander.LineType.*
import static meander.LinePatternType.*
import static meander.FillType.*
import static meander.LabelType.*
import static meander.AnimationType.*
import static meander.PlacementType.*
import groovy.beans.Bindable

import java.awt.Color

class StylesPanelModel {
    // these get initialized by the controller
    def styles
    @Bindable Style currStyle = new Style( name: 'default',
                                           text: new TextStyle( face: 'Times New Roman',
                                                                size: 12,
                                                                bold: false,
                                                                italic: false,
                                                                color: new Color( 0, 0, 0)),
                                           graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                                      line: SOLID,
                                                                      pattern: SINGLE,
                                                                      thickness: 0.75,
                                                                      color: new Color( 0, 0, 0)),
                                           border: new BorderStyle( line: NOLINE,
                                                                    pattern: SINGLE,
                                                                    thickness: 0.75,
                                                                    color: new Color( 255, 255, 255)),
                                           fill: new FillStyle( type: NOFILL,
                                                                foreColor: new Color( 255, 255, 255),
                                                                backColor: new Color( 255, 255, 255),
                                                                gradient: NOGRADIENT,
                                                                gradientScale: 50),
                                           label: new LabelStyle( labelType: NOLABEL,
                                                                  labelFormat: ''),
                                           timestamp: new LabelStyle( labelType: NOLABEL,
                                                                      labelFormat: ''),
                                           animate: FROMSOURCE,
                                           alignment: LEFT,
                                           placement: SRCJUST,
                                           wrap: false,
                                           trimLen: 0,
                                           terseLen: 0,
                                           linesAbove: 0)
    @Bindable def coreSL = []
    @Bindable def userSL = [] as Set     // only allow one instance for each user defined style name

    def gradientVarients = new BasicEventList( GradientVariant.values().toList())

    @Bindable int currNodeCount = 5
    String currNode = 'message'
    def nodes = [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ]

    def counts = [ 'GC': 1,       // global
                   'AC': 1,       // action
                   'CC': 1,       // comment
                   'EC': 1,       // exchange
                   'MC': 1,       // message
                   'SC': 1,       // subproc
                   'BC': 1,       // bearer path
                   'BCC': 1,      // bearer chain
                   'PC': 1,       // page
                   'FPC': 1,      // flow page
                 ]

    def options = [ trim: false,
                    trimLen: 0,
                    terse: false,
                    terseLen: 0
                  ]

    int nodeW = 50
    int nodeH = 20

    int exampleWidth = 800
    int exampleHeight = 300

    int nodeY = 20
    int nodeX1
    int nodeX2

    def examplesInfo = [ 'message' :
                         [ 'src' : 'Node2',
                           'srcList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ],
                           'str' : '''This is an example, multi-line Message.
If you don't like this example, please fill in the message you would like in
the above text box.

You can also change the source and destinations.''',
                           'dest' : 'Node4',
                           'destList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ]
                         ],
                         'action' :
                         [ 'src' : 'Node3',
                           'srcList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5', ' ' ],
                           'str' : '''This is an example Action string that has a long first line
and then continues on the second.
It has a blank destination, so it should have
a display width of one node.''',
                           'dest' : ' ',
                           'destList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5', ' ', '1', '2', '3', '4', '5' ]
                         ],
                         'comment' :
                         [ 'src' : 'Node3',
                           'srcList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5', ' ' ],
                           'str' : '''This is an example Comment string that has a long first line
and then continues on the second.
It has a 'destination' of '2' so it should
have a display width of two nodes.''',
                           'dest' : '2',
                           'destList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5', ' ', '1', '2', '3', '4', '5' ]
                         ],
                         'exchange' :
                         [ 'src' : 'Node1',
                           'srcList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ],
                           'str' : '''This is an example exchange string that should be a double
arrow between two nodes.''',
                           'dest' : 'Node3',
                           'destList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ]
                         ],
                         'bearerpath' :
                         [ 'src' : 'Node1',
                           'srcList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ],
                           'str' : '''This is an example bearer that should be between two or more nodes.''',
                           'dest' : 'Node3',
                           'destList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5', 'Node3, Node5' ]
                         ],
                         'subproc' :
                         [ 'src' : 'Node2',
                            'srcList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ],
                            'str' : '''This is an example subproc that
extends across multiple
lines.''',
                            'dest' : 'Node4',
                            'destList' : [ 'Node1', 'Node2', 'Node3', 'Node4', 'Node5' ]
                    ],
                      ]

    def currExample = 'message'
    def currExampleEvent = [ command: 'message',
                             source: 'Node2',
                             data: '''This is an example, multi-line Message.
If you don't like this example, please fill in the message of your dreams in
the above text box.

You can also change the source and destinations.''',
                             destination : 'Node4',
                           ]

    boolean bindingsSet = false

    @Bindable exampleOutOfDate = false
}
