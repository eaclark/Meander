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

import net.miginfocom.swing.MigLayout
import javax.swing.border.TitledBorder
import groovy.swing.SwingBuilder
import griffon.builder.gfx.*
import griffon.builder.gfx.swing.*

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
//frame( title: 'Style Frame', id: 'layoutFrame', size: [400,400]) {

    swing = new SwingBuilder()
 //   gb = new GfxBuilder()

    // set up a bind group fro the page dimensions
    bindGroup( id: 'pbg')
    // and a bind group for the sliders/margins
    bindGroup( id: 'sbg')

    // set up a dummy closure
    // the real one is defined in ExamplePage.groovy after the references are valid
    sizeExamplePage = { ->
    }

    panel(title: tabName, id: 'layoutTab', layout: new MigLayout('', 'grow', '[min!] [] [min!]')) {
//    panel( id: 'layoutTab', layout: new MigLayout('', 'grow', '[min!] [] [min!]')) {
        // set up a couple of Swing variables to track the vertical and horizontal
        // dimensions of the example page - this is to shorten some longer code lines
        vres = model.pageModels['base'].ypts
        hres = model.pageModels['base'].xpts

        // set up a common action to call when the page size or orientation changes
        initSliders = {
            // vertical direction
            vres = model.pageModels['base'].ypts
            tmslider.maximum = vres
            bmslider.maximum = vres
            // horizontal direction
            hres = model.pageModels['base'].xpts
            rmslider.maximum = hres
            lmslider.maximum = hres
            sizeExamplePage()
        }
    
        // start laying out the page panels
        border = new TitledBorder( 'Page Orientation & Parameters')
        panel( constraints: 'cell 0 0, growx', border: border, layout: new MigLayout( 'fill', 'grow', '')) {
            build LayoutPanel.PageInfo
        }

        // build the example page
        border = new TitledBorder( 'Page Margins')
        panel( constraints: 'cell 1 0', border: border, layout: new MigLayout('fill')) {
            gp = widget( size: [ 400, 400],
                         constraints: 'cell 1 1, w 400!, h 400!',
                         new GfxCanvas()
                       )

            // Construct the margin panels
            build LayoutPanel.Margins

            // build the page graphics
            //
            // NOTE - this will redefine the sizeExamplePage closure to something useful
            gp.node = group {
                build LayoutPanel.ExamplePage
            }
        }
    }

}

build LayoutPanel.FlowStrings