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

import meander.Base.PostNote
import meander.Uno.*

/**
 * routine for drawing the post flow event text and/or annotation
 */
class UnoImpressPostNote extends PostNote  {

    def pagesSupplier

    UnoImpressPostNote ( paramMap) {
        super( paramMap)
    }

    def insertBorder( drawPane, point, size, style ) {
        def rect
        rect = UnoUtils.insertBorder( pagesSupplier, drawPane, point, size, style)

        return rect
    }

    def drawPositionedText( str, drawPane, style, ip, wid) {
        def text
        text = UnoText.drawPositioned( str,
                                       drawPane,
                                       pagesSupplier,
                                       style,
                                       ip,
                                       wid
                                     )
        return text
    }

    def buildGroup( drawPane, shapeList) {
        def group

        group = UnoUtils.buildGroup( pagesSupplier, drawPane, shapeList )
        return group
    }

    Object createPoint( x, y) {
        UnoUtils.createPoint( (int) x, (int) y)
    }

    Object createSize( w, h) {
        UnoUtils.createSize( (int) w, (int) h)
    }
}
