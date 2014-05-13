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
package source

import meander.FlowEvent

/**
 * Created by IntelliJ IDEA.
 * User: eac
 * Date: 3/2/11
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
class SourceFile {
    def fileName
    def strings = [:]
    def rows = []
    def options = []
    def styleStrs = []
    def rowCnt
    def table
    def startingTableIndex = 0
    def startingRowIndex = 0
    def startingCellIndex = 0
    def document
    def application

    protected def actionCmds = [ 'message', 'action', 'comment', 'annotate', 'bearerpath', 'exchange', 'summary', 'subproc']
    protected def stringCmds = [ 'title', 'footer', 'date', 'author', 'watermark' ]

    SourceFile() {
    }

    SourceFile( name) {
        fileName = name
        rowCnt = 1
        init()
    }

    SourceFile( name, cnt) {
        fileName = name
        rowCnt = cnt
        init()
    }

    def init() {
        boolean preData = true
        List newNodes
        loadDocument( fileName)
        getTable( document, startingTableIndex)
        getTableRows().each { row ->
            String cmdstr = cellScan( getCell( row, startingCellIndex))
            String srcstr = cellScan( getCell( row, startingCellIndex+1))
            String datastr = cellScan( getCell( row, startingCellIndex+2))
            String deststr = cellScan( getCell( row, startingCellIndex+3))

            String cmd = textScan( cmdstr)?.toLowerCase()

            // grab any option requests that the user specified before any real flow events
            // we'll get them in place in the GUI and for immediate use
            if ( (cmd == 'options') && preData) {
                parseOptions( datastr)
            }

            // now extract any user specified style info
            if ( cmd == 'style') styleStrs << [ 'name' : textScan( srcstr), 'data' : datastr, 'preData' : preData ]

            if ( actionCmds.contains( cmd) ) { preData = false }

            if ( stringCmds.contains( cmd) && preData ) { strings[ cmd ] = textScan( datastr) }

            rows << new FlowEvent( count:           rowCnt++,
                                   commandStr:      cmdstr,
                                   command:         cmd,
                                   commandTags:     tagScan( cmdstr),
                                   sourceStr:       srcstr,
                                   source:          textScan( srcstr),
                                   sourceTags:      tagScan( srcstr),
                                   data:            datastr,
                                   destinationStr:  deststr,
                                   destination:     textScan( deststr),
                                   destinationTags: tagScan( deststr),
                                   annotation:      cellScan( getCell( row, startingCellIndex+4))
                                 )
        }

        closeDocument()
    }

    def save() {
        def options = []

        // if the table exists
        def file = new File( fileName)
        if (!file.exists()) {
            createDocument()
        } else openDocument( fileName)
        getTable( document, startingTableIndex)
        sizeTable( rows.size())//emptyTable()

/*
        if (strings.title) options << 'title'
        if (strings.footer) options << 'footer'
        if (strings.author) options << 'author'
        if (strings.date) options << 'date'
        if (strings.watermark) options << 'watermark'

        offset = 1

        if (strings.title) {
          setCell( offset, 1, 'title')
          setCell( offset, 3, strings.titleStr)
          addTableRow()
          offset += 1
        }

        if (options.size > 0) {
          setCell( offset, 1, 'options')
          setCell( offset, 3, options.join( ', '))
          addTableRow()
          offset += 1
        }

        if (strings.date) {
          setCell( offset, 1, 'date')
          setCell( offset, 3, strings.dateStr)
          addTableRow()
          offset += 1
        }

        if (strings.author) {
          setCell( offset, 1, 'author')
          setCell( offset, 3, strings.authorStr)
          addTableRow()
          offset += 1
        }

        if (strings.footer) {
          setCell( offset, 1, 'footer')
          setCell( offset, 3, strings.footerStr)
          addTableRow()
          offset += 1
        }


        if (strings.watermark) {
          setCell( offset, 1, 'watermark')
          setCell( offset, 3, strings.watermarkStr)
          addTableRow()
          offset += 1
        }

        // buffer row
        addTableRow()
        offset += 1

        nodes.each { node ->
          setCell( offset, 1, 'component')
          setCell( offset, 2, node)
          addTableRow()
          offset += 1
        }

        // buffer row
        addTableRow()
        offset += 1
*/
        rows.eachWithIndex { row, idx ->
            def newRow
            newRow = getTableRow( idx + startingRowIndex)
            setCell( newRow, 0 + startingCellIndex, row.commandStr)
            setCell( newRow, 1 + startingCellIndex, row.sourceStr)
            setCell( newRow, 2 + startingCellIndex, row.data)
            setCell( newRow, 3 + startingCellIndex, row.destinationStr)
            setCell( newRow, 4 + startingCellIndex, row.annotation)
        }
        saveDocument()
        closeDocument()
    }

    // abstract method handled by the subclass
    def loadDocument( filename) {}

    // abstract method handled by the subclass
    def openDocument( filename) {}

    // abstract method handled by the subclass
    def createDocument() {}

    // abstract method handled by the subclass
    def writeDocument() {}

    // abstract method handled by the subclass
    def saveDocument() {}

    // abstract method handled by the subclass
    def closeDocument() {}

    // abstract method handled by the subclass
    def quitApplication( value) {}

    // abstract method handled by the subclass
    def cellScan( cell ) {}

    // abstract method handled by the subclass
    def getTable( document, tableIdx ) {}

    // abstract method handled by the subclass
    def emptyTable() {}

    // abstract method handled by the subclass
    def sizeTable( cnt) {}

    // abstract method handled by the subclass
    def getTableRows() {}

    // abstract method handled by the subclass
    def getTableRow( index ) {}

    // abstract method handled by the subclass
    def addTableRow() {}

    // abstract method handled by the subclass
    def getCell( row, index ) {}

    // abstract method handled by the subclass
    def setCell( row, index, data ) {}

    def parseOptions( String optStr) {
        def opts = optStr.split(',')
        opts.each { opt ->
            def norm = opt.toLowerCase().trim()
            switch ( norm) {
            case 'animate':
            case 'author':
            case 'date':
            case 'footer':
            case 'landscape':
            case 'notes':
            case 'pagenum':
            case 'portrait':
            case 'postfix':
            case 'terse':
            case 'title':
            case 'trim':
            case 'watermark':
                options << norm
                break
            default:
                println 'Unknown option:  ' + opt
            }
        }
    }

    static textScan( String str) {
        // grab anything before an optional ':'
        // but if the input is null, return an empty string
        str ? str.split(':')[0].trim() : ''
    }

    static tagScan( str) {
        // look for an optional set of tags after a ':'
        // the tags will be ';' separated and are in either a
        // 'tag=value' format or just 'tag'

        // return a (possibly empty) map of the tags/values
        def tag, value
        def tags = str.split(':')
        def tagmap = [:]
        if (tags.size() > 1) {
            tags[1].split(';').inject( tagmap) { map, tagline ->
                (tag, value) = tagline.split('=').collect { it }
                map << [ (tag.trim()) : value.trim()]
            }
        }
        return tagmap
    }

}
