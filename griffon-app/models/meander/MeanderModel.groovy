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

import groovy.beans.Bindable
import static meander.PageType.*
import static meander.PagePlacement.*
import static meander.WatermarkPlacement.*
import ca.odell.glazedlists.BasicEventList
import source.SourceFile

class MeanderModel {
    // @Bindable String propName

    StyleStack styles = new StyleStack()

    // the 'base' model is the active one that gets 'bound' to
    // the 'user' model is data 'overridden' through the GUI
    // the 'source' model is the data coming from the source input
    def pageModels = [ 'base' : new PageModel( size: LETTER, portrait: false),
                       'user' : new PageModel( size: LETTER, portrait: false),
                       'source' : new PageModel( size: LETTER, portrait: false)
                     ]
    def baseOptions = new Options()
    def userOptions = new Options()
    def sourceOptions = new Options()

    def stringsModels = [ 'base' : new PageStrings(),
                          'user' : new PageStrings(),
                          'source' : new PageStrings()
                        ]

    def counts = [ 'MC'  : 0,      // message count
                   'AC'  : 0,      // action count
                   'CC'  : 0,      // comment count
                   'EC'  : 0,      // exchange count
                   'BC'  : 0,      // bearer count
                   'BCC' : 0,      // bearerchain count
                   'SC'  : 0,      // subproc count
                   'GC'  : 0,      // global count
                 ]

    List srcLines = new BasicEventList()
    List flowEvents = new BasicEventList()
    List nodes = new BasicEventList()

    // @Bindable String propName
    def dm            // Data panel model
    def dv            // Data panel view
    def dc            // Data panel controller

    def lm            // Layout panel model
    def lv            // Layout panel view
    def lc            // Layout panel controller

    def sm            // Styles panel model
    def sv            // Styles panel view
    def sc            // Styles panel controller

    def om            // Output dialog model
    def ov            // Output dialog view
    def oc            // Output dialog controller

    def tm            // TableEdit dialog model
    def tv            // TableEdit dialog view
    def tc            // TableEdit dialog controller

    def pm            // Preview frame model
    def pv            // Preview frame view
    def pc            // Preview frame controller

    MeanderModel() {
        (0..19).each { num -> flowEvents.add(new FlowEvent( count: num + 1)) }

        pageModels[ 'base'].mirror = 'source'
        baseOptions.mirror = 'source'
        sourceOptions.mirror = baseOptions
        // leave the userOptions.mirror null
        stringsModels[ 'base'].mirror = 'source'
        //stringsModels[ 'user'].mirror = null
        //stringsModels[ 'source'].mirror = stringsModels[ 'base']

    }
}


@Bindable
class PageModel {
    String mirror
    PageType size = LETTER
    Boolean portrait = false
    int top = 0
    int right = 0
    int bottom = 0
    int left = 0


//  Integer getXpts () {
//      if ( portrait) size.x() * size.p() as Integer
//      else size.y() * size.p() as Integer
//  }
    Integer getXpts() { (size.p() * ( portrait ? size.x() : size.y())) as Integer}

    Integer getYpts () {
        if ( portrait) size.y() * size.p() as Integer
        else size.x() * size.p() as Integer
    }

    def getHlen () {
        if ( portrait) size.x() as Float
            else size.y() as Float
    }

    def getVlen () {
        if ( portrait) size.y() as Float
        else size.x() as Float
    }

    def mirror( PageModel pm) {
        if ( mirror == pm) return // already mirroring
        // unlink current far model
        mirror.mirror = null

        // now link up bi-directionally
        mirror = pm
        pm.mirror = this

    }

    def leftShift( PageModel source) {
        this.size = source.size
        this.portrait = source.portrait
        this.top = source.top
        this.right = source.right
        this.bottom = source.bottom
        this.left = source.left
    }
}

@Bindable
class PageStrings {
    String mirror


    def counts = [ 'PC' : 0,      // page count
                   'FPC' : 0,     // flow page count
                   'today' : '',  // will be initialized to the current date at the beginning of the run
                   'now' : '',    // will be initialized to the current time at the beginning of the run
                   'raw' : null
                 ]


    def res = [ /(.*)<(MC)>(.*)/,
                /(.*)<(CC)>(.*)/,
                /(.*)<(AC)>(.*)/]

    // list of the strings to be placed
    // watermark is not included because it is not in the header nor footer
    def strings = ['author', 'date', 'footer', 'pagenum', 'title']

    boolean author = false
    String authorStr = ''
    PagePlacement authorPos = BOTTOMLEFT
    String authorStyle = 'footer'

    boolean date = false
    String dateStr = '<MMMM dd, yyyy>\n<hh:mm:ss a>'
    PagePlacement datePos = BOTTOMRIGHT
    String dateStyle = 'footer'

    boolean footer = false
    String footerStr = ''
    PagePlacement footerPos = BOTTOMCENTER
    String footerStyle = 'footer'

    boolean pagenum = false
    String pagenumStr = '<PC>'
    PagePlacement pagenumPos = TOPRIGHT
    String pagenumStyle = 'header'

    boolean title = false
    String titleStr = ''
    PagePlacement titlePos = TOPCENTER
    String titleStyle = 'header'

    boolean watermark = false
    String watermarkStr = ''
    WatermarkPlacement watermarkPos = DIAGONALUP
    String watermarkStyle = 'watermark'

    static getBools() {
        return [ 'author', 'date', 'footer', 'pagenum', 'title', 'watermark']
    }

    def getPageStrings() {
        return [ 'author' : authorStr, 'date' : dateStr, 'footer' : footerStr,
                 'pagenum' : pagenumStr, 'title' : titleStr, 'watermark' : watermarkStr]
    }
}

@Bindable
class Options {
    def mirror
    boolean animate = false
    boolean author = false
    boolean notes = false
    boolean postfix = false
    boolean terse = false
    int terseLen = 0
    boolean trim = false
    int trimLen = 0

    static getBools() {
        return [ 'animate', 'author', 'notes', 'postfix', 'terse', 'trim']
    }
}