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
package meander.LayoutPanel

/**
 * GUI panel for displaying the current output page size and orientation.  It will
 * also display the current margin settings
 */

rect( f: 'lightgray',
      width: bind { gp.width - 1 },
      height: bind { gp.height - 1 }
    )
rect( id: 'examplePage',
      f: 'white',
      bc: 'black',
      width: 100, height: 100
    )

// now define some closures to call to recalculate the
// margin coordinates that change when the slider moves
// (this could be done in the 'bind' call, but it saves
// having to repeat a few lines of code and keeps
// related logic close together.)
def left_x_clos = { pts ->
    if ( pts.isInteger()) {
        examplePage.x + (examplePage.width * pts.toInteger() / model.pageModels['base'].xpts) as int
    } else 0
}
def right_x_clos = { pts ->
    if ( pts.isInteger()) {
        (examplePage.x + examplePage.width - (examplePage.width * pts.toInteger() / model.pageModels['base'].xpts)) as int
    } else 0
}
def top_y_clos = { pts ->
    if ( pts.isInteger()) {
        (examplePage.y + (examplePage.height * pts.toInteger() / model.pageModels['base'].ypts)) as int
    } else 0
}
def bottom_y_clos = { pts ->
    if ( pts.isInteger()) {
        (examplePage.y + examplePage.height - (examplePage.height * pts.toInteger() / model.pageModels['base'].ypts)) as int
    } else 0
}

// top margin line
line( id: 'topMargin',
      x1: bind( group: pbg,
                source: examplePage,
                sourceProperty: 'x',
                converter: { it }),
      x2: bind( group: pbg,
                source: examplePage,
                sourceProperty: 'x',
                converter: { it + examplePage.width }),
      y1: bind( group: pbg,
                source: tmpf,
                sourceProperty: 'text',
                converter: top_y_clos),
      y2: bind( group: pbg,
                source: tmpf,
                sourceProperty: 'text',
                converter: top_y_clos),
      bc: 'blue'
    ) {
        basicStroke( dash: [2f,2f] as float[])
      }

// right margin line
line( id: 'rightMargin',
      y1: bind( group: pbg,
                source: examplePage,
                sourceProperty: 'y',
                converter: { it }),
      y2: bind( group: pbg,
                source: examplePage,
                sourceProperty: 'y',
                converter: { it + examplePage.height }),
      x1: bind( group: pbg,
                source: rmpf,
                sourceProperty: 'text',
                converter: right_x_clos),
      x2: bind( group: pbg,
                source: rmpf,
                sourceProperty: 'text',
                converter: right_x_clos),
      bc: 'blue'
    ) {
        basicStroke( dash: [2f,2f] as float[])
      }

// bottom margin line
line( id: 'bottomMargin',
      x1: bind( source: examplePage,
                sourceProperty: 'x',
                group: pbg,
                converter: { it }),
      x2: bind( source: examplePage,
                sourceProperty: 'x',
                group: pbg,
                converter: { it + examplePage.width }),
      y1: bind( source: bmpf,
                sourceProperty: 'text',
                group: pbg,
                converter: bottom_y_clos),
      y2: bind( source: bmpf,
                sourceProperty: 'text',
                group: pbg,
                converter: bottom_y_clos),
      bc: 'blue'
    ) {
        basicStroke( dash: [2f,2f] as float[])
      }
line( id: 'leftMargin',
      y1: bind( source: examplePage,
                sourceProperty: 'y',
                group: pbg,
                converter: { it }),
      y2: bind( source: examplePage,
                sourceProperty: 'y',
                group: pbg,
                converter: { it + examplePage.height }),
      x1: bind( source: lmpf,
                sourceProperty: 'text',
                group: pbg,
                converter: left_x_clos),
      x2: bind( source: lmpf,
                sourceProperty: 'text',
                group: pbg,
                converter: left_x_clos),
      bc: 'blue'
    ) {
        basicStroke( dash: [2f,2f] as float[])
      }


// define the real closure that gets called to redraw things
// when the user changes the page layout
sizeExamplePage = { ->
    def longside, shortside

    if ( model.pageModels['base'].portrait) {
        longside = gp.height - 10
        shortside = longside * model.pageModels['base'].size.x / model.pageModels['base'].size.y
        examplePage.x = (gp.width - shortside)/2 as int
        examplePage.y = (gp.height - longside)/2 as int
        examplePage.width = shortside as int
        examplePage.height = longside as int
    } else {
        longside = gp.width - 10
        shortside = longside * model.pageModels['base'].size.x / model.pageModels['base'].size.y
        examplePage.x = (gp.width - longside)/2 as int
        examplePage.y = (gp.height - shortside)/2 as int
        examplePage.width = longside as int
        examplePage.height = shortside as int
    }
    // gotta update the margin line bindings
    pbg.update()
    // now update all the slider label bindings
    sbg.update()
}

// now size the example page
sizeExamplePage()