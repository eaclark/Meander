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

import org.codehaus.groovy.scriptom.ActiveXObject
import org.codehaus.groovy.scriptom.Scriptom
import meander.FlowEvent

/**
 * handles the specifics associated with MS Office Word source files
 */
class SourceWord extends SourceFile {

    SourceWord() {
        super()
        // need to initialize starting indices - the other constructors call init() which will do this
        startingTableIndex = 1
        startingRowIndex = 1
        startingCellIndex = 1
    }

    SourceWord( name) {
        super( name)
    }

    SourceWord( name, cnt) {
        super( name, cnt)
    }

    /*
     * the next couple of methods initialize a Scriptom Apartment before calling back to the
     * base class routine
     *
     * they complete ALL the work associated with this function within the scope of this routine
     */

    // opens the assigned filename and reads its data into the model
    def init() {
        startingTableIndex = 1
        startingRowIndex = 1
        startingCellIndex = 1
        Scriptom.inApartment {
            application = new ActiveXObject( "Word.Application")
            super.init()
            application.quit(0)
        }
    }

    // opens the assigned filename and saves the given data to it
    def save() {
        Scriptom.inApartment {
            application = new ActiveXObject( "Word.Application")
            super.save()
            application.quit(0)
        }
    }





    /*
     * the following methods assume that a Scriptom Apartment has already been initialized
     */

    def loadDocument( filename) {
        document = application.Documents.Open( new File( filename).canonicalPath, // filename
                                               Scriptom.MISSING,                  // ComfirmConversions
                                               true,                              // ReadOnly
                                               Scriptom.MISSING,                  // AddToRecentFiles
                                               Scriptom.MISSING,                  // PasswordDocument
                                               Scriptom.MISSING,                  // PasswordTemplate
                                               Scriptom.MISSING,                  // Revert
                                               Scriptom.MISSING,                  // WritePasswordDocument
                                               Scriptom.MISSING,                  // WritePasswordTemplate
                                               Scriptom.MISSING,                  // Format
                                               Scriptom.MISSING,                  // Encoding
                                               false)                             // Visible
                                                                                  // OpenAndRepair
                                                                                  // DocumentDirection
                                                                                  // NoEncodingDialog
                                                                                  // XMLTransform
    }

    def createDocument() {
        def lspace, locTab

        document = application.Documents.Add()

        // now build the target table
        document.Tables.Add( document.Range(), 1, 5)
        locTab = document.Tables(1)
        locTab.Borders.InsideLineStyle = 1
        locTab.Borders.OutsideLineStyle = 1

        locTab.PreferredWidthType = 2 // wdPreferredWidthPercent
        locTab.PreferredWidth = 100
        locTab.Columns.PreferredWidthType = 2 // wdPreferredWidthPercent
        locTab.Columns(1).PreferredWidth = 12
        locTab.Columns(2).PreferredWidth = 12
        locTab.Columns(3).PreferredWidth = 45
        locTab.Columns(4).PreferredWidth = 12
        locTab.Columns(5).PreferredWidth = 19

        //locTab.Columns(1).PreferredWidth = (int) (72 * 0.8)
        //locTab.Columns(2).PreferredWidth = (int) (72 * 0.9)
        //locTab.Columns(3).PreferredWidth = (int) (72 * 3)
        //locTab.Columns(4).PreferredWidth = (int) (72 * 0.9)
        //locTab.Columns(5).PreferredWidth = (int) (72 * 1.3)

        // set some table defaults
        lspace = application.LinesToPoints( 1)
        locTab.Cell( 1, 1).Range.Font.Size = 10
        locTab.Cell( 1, 1).Range.Font.Name = 'Times New Roman'
        //locTab.Cell( 1, 1).Range.ParagraphFormat.SpaceBefore = 3
        locTab.Cell( 1, 1).Range.ParagraphFormat.SpaceAfter = 0
        locTab.Cell( 1, 1).Range.ParagraphFormat.LineSpacing = lspace
        locTab.Cell( 1, 2).Range.Font.Size = 10
        locTab.Cell( 1, 2).Range.Font.Name = 'Times New Roman'
        //locTab.Cell( 1, 2).Range.ParagraphFormat.SpaceBefore = 3
        locTab.Cell( 1, 2).Range.ParagraphFormat.SpaceAfter = 0
        locTab.Cell( 1, 2).Range.ParagraphFormat.LineSpacing = lspace
        locTab.Cell( 1, 3).Range.Font.Size = 10
        locTab.Cell( 1, 3).Range.Font.Name = 'Times New Roman'
        //locTab.Cell( 1, 3).Range.ParagraphFormat.SpaceBefore = 3
        locTab.Cell( 1, 3).Range.ParagraphFormat.SpaceAfter = 0
        locTab.Cell( 1, 3).Range.ParagraphFormat.LineSpacing = lspace
        locTab.Cell( 1, 4).Range.Font.Size = 10
        locTab.Cell( 1, 4).Range.Font.Name = 'Times New Roman'
        //locTab.Cell( 1, 4).Range.ParagraphFormat.SpaceBefore = 3
        locTab.Cell( 1, 4).Range.ParagraphFormat.SpaceAfter = 0
        locTab.Cell( 1, 4).Range.ParagraphFormat.LineSpacing = lspace
        locTab.Cell( 1, 5).Range.Font.Size = 10
        locTab.Cell( 1, 5).Range.Font.Name = 'Times New Roman'
        //locTab.Cell( 1, 5).Range.ParagraphFormat.SpaceBefore = 3
        locTab.Cell( 1, 5).Range.ParagraphFormat.SpaceAfter = 0
        locTab.Cell( 1, 5).Range.ParagraphFormat.LineSpacing = lspace
    }

    def openDocument( filename) {
        document = application.Documents.Open( new File( filename).canonicalPath)
    }

    def saveDocument() {
        document.SaveAs( fileName)
    }

    def closeDocument() {
        document.close()
    }

    def quitApplication( value) {
        application.quit( value)
    }


    def cellScan( cell ) {
        // trim any leading or trailing white spaces
        // then convert all line break style characters into a vertical tab (\013)
        // but, have to look for runs of characters to convert into one vertical tab
        def matcher = (cell.trim() =~ /[\a\n\v\f\r]+/)      /* \a=\007  \n=\012  \v=\013  \f=\014  \r=\015 */
        def str = matcher.replaceAll( '\n')

        return str
    }

    // get the specified Word locTab
    def getTable( document, tableIdx ) {
        table = document.Tables( tableIdx )
    }

    // remove all the rows
    def emptyTable() {
        table.Rows().each { row -> row.Delete() }
    }

    // add or delete rows so the table is of size cnt
    def sizeTable( cnt) {
        def currSize = table.Rows().count
        if ( currSize > cnt ) (cnt+1 .. currSize).each { table.Rows(it).Delete() }
        if ( currSize < cnt) (1 .. cnt-currSize).each { table.Rows().Add() }
    }

    // get the rows in the Word table as a list
    def getTableRows() {
        table.Rows()
    }

    // get the rows in the Writer table as a list
    def getTableRow( index) {
        table.Rows( index)
    }

    // add a row to the Word table
    def addTableRow() {
        table.Rows.add()
    }

    // get the text in the specified cell
    def getCell( row, index ) {
        row.Cells( index ).Range.text
    }

    // set the text in the specified cell
    def setCell( row, index, data ) {
        row.Cells( index ).Range.text = data
    }
}
