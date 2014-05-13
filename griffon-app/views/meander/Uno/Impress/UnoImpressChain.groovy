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
package meander.Uno.Impress

import java.awt.Point

/**
 * To be done -- routine for drawing the Impress shapes representing a chain of events or bearer paths
 */
class UnoImpressChain {
    def event
    def style
    def options
    def counts
    def leftM
    def rightM

    boolean dirLR = true
    def srcs
    def dests
    def tempEvent
    def tempOptions
    def grObjs = []

    public draw( page, pagesSupplier, nodes, nodeXPos, currY) {
        Point ip
        int adj = 75i
        def gPosInfo = [:]
        def tPosInfo = [:]
        def nodeInfo = [:]
        def strs
        def rect
        def label, text1, text2
        def graphic, gh
        def group1, group2
        def trim, tl
        def terselen

        srcs = event.source.split( ',')
        dests = event.destination.split( ',')

        // setup the temporary event and options
        event.each { k, v -> tempEvent.k = v }
        options.each { k, v -> tempOptions.k = v }

        switch (event.command) {
        case 'messagechain':
            break
        case 'bearerchain':
            break
        }
    }
}
