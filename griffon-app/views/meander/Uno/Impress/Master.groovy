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

import meander.Uno.UnoCategory
import com.sun.star.view.PaperOrientation

/**
 * Proxy object for an Impress Master slide
 */
class Master {
  def pageModel
  def mastersSupplier

  def page

  /* Variables for the ODF file */
//  XPropertySet xProperySet

  Master( named = [:], index) {
    named.each { key, value -> this[ key] = value }
    def masters = mastersSupplier.getMasterPages()
    page = masters.getByIndex( index)
    setup()
  }

  def setup() {
    int w, h // orientation, width, height
    def orientation = pageModel.portrait ? PaperOrientation.PORTRAIT : PaperOrientation.LANDSCAPE

    use (UnoCategory) {


      // too bad I don't know how to get the page size info out of OpenOffice
      // have to calculate it by hand
//      w = xDrawPage.uno(XPropertySet)["Width"]
//      h = xDrawPage.uno(XPropertySet)["Height"]
      w = (int)(pageModel.size.y * pageModel.size.p * 100 / 2.834646f)
      h = (int)(pageModel.size.x * pageModel.size.p * 100 / 2.834646f)
      if ( pageModel.portrait) {
        // if using portrait mode, flip width and height around
        (w, h) = [h, w]
      }

      // could grab the existing margins from the masterpage...
      // probably only need to do that when using templates.
//      def mtop += xDrawPage.uno(XPropertySet)["BorderTop"]
//      def mright += xDrawPage.uno(XPropertySet)["BorderRight"]
//      def mbottom += xDrawPage.uno(XPropertySet)["BorderBottom"]
//      def mleft += xDrawPage.uno(XPropertySet)["BorderLeft"]

      int printerBuffer = 0i
      pageModel.top += printerBuffer
      pageModel.right += printerBuffer
      pageModel.bottom += printerBuffer
      pageModel.left += printerBuffer
      page.multiPropertySet = [ Orientation: orientation,
                                Width: w,
                                Height: h,
                                BorderTop: pageModel.top,
                                BorderRight: pageModel.right,
                                BorderBottom: pageModel.bottom,
                                BorderLeft: pageModel.left,
                              ]
    }
  }
}
