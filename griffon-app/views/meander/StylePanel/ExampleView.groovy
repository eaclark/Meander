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
package meander.StylePanel

import static meander.PlacementType.*
import meander.Jfx.JfxEvent

/**
 * defines the view members/methods for triggering the example event redraw
 */


// this function checks that the example event information is complete enough
// (and consistent enough) to draw.  The event information will be incomplete
// during the time the selected style is being updated in the StyleView.
auditExampleInfo = { example, style ->
    def isGood = true


    switch( style.placement) {
    case SRCJUST:
    case DESTJUST:
    case OVERLAP:
    case SPAN:
        // verify that the event has a valid source and destination node - either being empty
        // just doesn't make sense for SRCJUST, DESTJUST, OVERLAP, or SPAN
        if ( example.source == '' || example.source == ' ' || example.source == null) isGood = false
        if ( example.destination == '' || example.destination == ' ' || example.destination == null) isGood = false
        break
    case MIDDLE:
        break

    }

    return isGood
}

drawExample = {
    // prep the nodes
    def nodePos = []
    ( 0 .. model.currNodeCount-1).each { idx ->
        def wid = model.exampleWidth/model.currNodeCount
        nodePos << (int)(wid * idx + wid/2)
    }

    // prep the style
    def style = (styleSelectGroup.selection.actionCommand == model.currExample) ? model.currStyle : app.models.Meander.styles.editStyles[ model.currExample ]

    // prep the event info
    model.currExampleEvent.command = model.currExample
    if( exSrcSrcNode.selectedItem) model.currExampleEvent.source = exSrcSrcNode.selectedItem.trim()
    if( exSrcText.text) model.currExampleEvent.data = exSrcText.text
    if( exSrcDestNode.selectedItem) model.currExampleEvent.destination = exSrcDestNode.selectedItem.trim()

    // make a place to draw on
    def examplePane = examplegb.with { group() }

    // now create an event and draw it
    def exampleEvt = new JfxEvent( event: model.currExampleEvent,
                                   convertUnits: 1i,
                                   style: style,
                                   options: model.options,
                                   counts: model.counts,
                                   leftM: 0,
                                   rightM: view.examplegp.width,
                                   builder: examplegb
                                 )

    if( auditExampleInfo( model.currExampleEvent, style)) {
        def exampleObj = exampleEvt.draw( examplePane, null, model.nodes, nodePos, 50)

        examplegb.defer {
            examplegb.currentScene = view.examplegp.scene

            // clear the existing scene
            view.examplegp.scene.root.children.clear()

            // now draw the background nodes and lines
            exampleBackground( examplegb, view, model)
    //
    //        // finally do the example
    //        def style = (styleSelectGroup.selection.actionCommand == model.currExample) ? model.currStyle : app.models.Meander.styles.editStyles[ model.currExample ]
    //        exampleMessage( examplegb, view, model, style)
            view.examplegp.scene.root.children.add exampleObj[0]
        }
    }
}

// and draw the example
drawExample()
