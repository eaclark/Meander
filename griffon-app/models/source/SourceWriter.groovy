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

//import org.odftoolkit.odfdom.doc.OdfTextDocument
import org.w3c.dom.Element
import meander.FlowEvent
import org.apache.tools.ant.types.FileList.FileName
import org.odftoolkit.simple.TextDocument
import org.odftoolkit.odfdom.pkg.OdfNamespace
import org.odftoolkit.simple.style.StyleTypeDefinitions
import org.odftoolkit.simple.style.Font
import org.odftoolkit.odfdom.type.Color
import org.odftoolkit.simple.style.Border
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType

/**
 * handles the specifics associated with OpenOffice Writer source files
 */
class SourceWriter extends SourceFile {

    SourceWriter() {
        super()
    }

    SourceWriter( filename) {
        super( filename)
    }

    SourceWriter( filename, cnt) {
        super( filename, cnt)
    }

    def loadDocument( filename) {
//        document = OdfTextDocument.loadDocument( filename)
        document = TextDocument.loadDocument( filename)
    }

    def createDocument() {
        def locTab, tabWidth, font

        document = TextDocument.newTextDocument()

        // now build the target table
        locTab = document.addTable( 2, 5)       // the default is 2 rows and 5 columns, but lets force it
                                                // creating just 1 row seems to screw up the cell borders

        // set with widths as a percentage
        tabWidth = locTab.width
        locTab.getColumnByIndex(0).width = 0.12 * tabWidth
        locTab.getColumnByIndex(1).width = 0.12 * tabWidth
        locTab.getColumnByIndex(2).width = 0.45 * tabWidth
        locTab.getColumnByIndex(3).width = 0.12 * tabWidth
        locTab.getColumnByIndex(4).width = 0.19 * tabWidth

        // set some table defaults
        font = new Font('Times New Roman', StyleTypeDefinitions.FontStyle.REGULAR, 8, Color.BLACK)
        (0 .. 1).each { r ->
            (0 .. 4).each { c ->
                locTab.getCellByPosition( c, r).font = font
            }
        }
    }

    def openDocument( filename) {
        document = TextDocument.loadDocument( filename)
    }

    def saveDocument() {
        document.save( fileName)
    }

    def closeDocument() {
        document.close()
    }

    def cellScan( cell ) {
        def str = new StringBuffer()
        def spaceCount
        // if the table cell only has one child element then the ODT getDisplayText
        // routine works.  But, if it has more, then we have to parse each child
        // separately.
        //
        // this is a kludge until getDisplayText is updated to handle lists of elements
//        println 'cell.displayText = ' + cell.displayText + ' with node legth = ' + cell.mCellElement.childNodes.length
//        println 'cell = ' + cell.mCellElement
        if (cell.mCellElement.childNodes.length == 1) str = cell.displayText
        else {
            def strList = []
//            println 'cell childNodes= ' + cell.mCellElement.childNodes
            cell.mCellElement.childNodes.each { n ->
//                println 'n = ' + n
                n.childNodes.each { c ->
//                println 'c = ' + c + ' with localName = ' + c.localName
                    switch (c.localName) {
                    case 'soft-page-break':
                        // ignore these
                        break
                    case 'line-break':
                        str << '\n'
                        strList << str
                        break
                    case 's':
                        try {
                            spaceCount = Integer.parseInt( ((Element) c).getAttributeNS( OdfNamespace.TEXT.getUri(), 'c'))
                        } catch(Exception e) {
                            spaceCount = 1
                        }
                        str << ' ' * spaceCount
                        strList << str
                        break
                    default: strList << c.nodeValue
                    }
                }
            }
            str = strList.join( '\n')
        }
        return str
    }

    // get the specified Writer table
    def getTable( document, tableIdx ) {
        table = document.tableList[ tableIdx + startingTableIndex]
    }

    // remove all the rows
    def emptyTable( ) {
        println 'starting rowCount = ' + table.rowCount
        table.removeRowsByIndex( 0, table.rowCount)
        println 'empty table size = ' + table.rowList.size
//        def row = table.getRowList()[0]
//        row.getCellByIndex( 0 ).stringValue = ''
//        row.getCellByIndex( 1 ).stringValue = ''
//        row.getCellByIndex( 2 ).stringValue = ''
//        row.getCellByIndex( 3 ).stringValue = ''
//        row.getCellByIndex( 4 ).stringValue = ''
    }

    // get the rows in the Writer table as a list
    def getTableRows() {
        table.rowList
    }

    // get the rows in the Writer table as a list
    def getTableRow( index) {
        table.rowList[ index ]
    }

    // add or delete rows so the table is of size cnt
    def sizeTable( cnt) {
        def currSize = table.rowList.size
        if ( currSize > cnt ) table.removeRowsByIndex( cnt, currSize - cnt)
        if ( currSize < cnt) (1 .. cnt-currSize).each { table.appendRow() }
    }

    // add a row to the Word table
    def addTableRow() {
        table.appendRow()
    }

    // get the text in the specified cell
    def getCell( row, index ) {
//        println 'read cell paragraphs = ' + row.getCellByIndex( index + startingCellIndex).getParagraphContainerElement().childNodes.each { println it }
        row.getCellByIndex( index + startingCellIndex)
    }

    // set the text in the specified cell
    def setCell( row, index, data ) {
        def dList = data.split( '\n')
        def dLen = dList.length
        def cell = row.getCellByIndex( index + startingCellIndex )
        def cLen = cell.getParagraphContainerElement().childNodes.length

        // make sure the number of paragraphs in the cell matches the number of lines in the new data
        if ( cLen > dLen ) ( cLen-1 .. dLen ).each { cell.removeParagraph( cell.getParagraphByIndex( it, false)) }
        if ( dLen > cLen ) ( 0 .. dLen-cLen).each { cell.addParagraph(' ') }

        // now set the text
        dList.eachWithIndex { s, i ->
            def p = cell.getParagraphByIndex( i, false)
            p.setTextContent( s)
        }
    }
}
