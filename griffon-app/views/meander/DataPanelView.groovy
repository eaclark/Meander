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

import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.UIManager
import java.awt.Component
import javax.swing.table.TableCellRenderer
import javax.swing.JTextArea
import javax.swing.border.EmptyBorder
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusAdapter

import java.awt.event.KeyListener
import javax.swing.table.TableCellEditor
import javax.swing.AbstractCellEditor
import javax.swing.ListSelectionModel
import com.sun.star.awt.Size
import javax.swing.ScrollPaneConstants
import javax.swing.event.ListSelectionListener
import javax.swing.border.EtchedBorder
import source.SourceFile

/**
   * Multiline Table Cell Renderer.
   *
   *   http://blog.botunge.dk/post/2009/10/09/JTable-multiline-cell-renderer.aspx
   */
public class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {
    private java.util.List<List<Integer>> rowColHeight = new ArrayList<List<Integer>>()

    public MultiLineTableCellRenderer() {
        setLineWrap(true)
        setWrapStyleWord(true)
        setOpaque(true)
    }

    public Component getTableCellRendererComponent( JTable table,
                                                    Object value,
                                                    boolean isSelected,
                                                    boolean hasFocus,
                                                    int row,
                                                    int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground())
            setBackground(table.getSelectionBackground())
        } else {
            setForeground(table.getForeground())
            setBackground(table.getBackground())
        }
        setFont(table.getFont());
        if (hasFocus) {
            setBorder(UIManager.getBorder('Table.focusCellHighlightBorder'))
            if (table.isCellEditable(row, column)) {
                setForeground(UIManager.getColor('Table.focusCellForeground'))
                setBackground(UIManager.getColor('Table.focusCellBackground'))
            }
        } else {
            setBorder(new EmptyBorder(1, 2, 1, 2))
        }
        if (value != null) {
            setText(value.toString())
        } else {
            setText('')
        }
        adjustRowHeight(table, row, column)
        return this
    }

    /**
    * Calculate the new preferred height for a given row, and sets the height on the table.
    */
    private void adjustRowHeight(JTable table, int row, int column) {
        //The trick to get this to work properly is to set the width of the column to the
        //textarea. The reason for this is that getPreferredSize(), without a width tries
        //to place all the text in one line. By setting the size with the with of the column,
        //getPreferredSize() returns the proper height which the row should have in
        //order to make room for the text.
        int cWidth = table.getTableHeader().getColumnModel().getColumn(column).getWidth()
        setSize(new Dimension(cWidth, 1000))
        int prefH = getPreferredSize().height
        while (rowColHeight.size() <= row) {
            rowColHeight.add(new ArrayList<Integer>(column))
        }
        java.util.List<Integer> colHeights = rowColHeight.get(row)
        while (colHeights.size() <= column) {
            colHeights.add(0)
        }
        colHeights.set(column, prefH)
        int maxH = prefH
        for (Integer colHeight : colHeights) {
            if (colHeight > maxH) {
                maxH = colHeight
            }
        }
        if (table.getRowHeight(row) != maxH) {
            table.setRowHeight(row, maxH)
        }
    }
}

public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {
    private java.util.List<List<Integer>> rowColHeight = new ArrayList<List<Integer>>()
    private JTextArea textArea
    private JScrollPane scrollPane
    private JTable table
    public eventList
    public model
    public view

    public TextAreaCellEditor() {
        textArea = new JTextArea()
        textArea.setLineWrap( false)
        textArea.setWrapStyleWord( false)
        textArea.setBorder( new EmptyBorder(1, 2, 1, 2))
        scrollPane = new JScrollPane(textArea)
        scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
        scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
        scrollPane.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent fe) {
              textArea.requestFocusInWindow()
              textArea.selectAll()
            }
        })
        textArea.addKeyListener(this)
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textArea.setText(value.toString())
 //       println textArea.getRows()
 //       if ( textArea.getRows() < 2) {
 //           textArea.setRows( 2)
 //           textArea.revalidate()
 //       }
 //       println 'new ' + textArea.getRows()
        if (this.table == null) this.table = table
        return scrollPane
    }

    public Object getCellEditorValue() { return textArea.text }

    public void keyTyped(KeyEvent e) { }

    public void keyPressed(KeyEvent ke) {
        // println( table.getEditingRow() + "," + table.getEditingColumn())
        // println( 'key = ' + ke.getKeyCode() + ' tab = ' + KeyEvent.VK_TAB)
        // tab key will transfer focus to next cell
        int column = table.getEditingColumn()
        int row = table.getEditingRow()

        if ( ke.getKeyCode() == KeyEvent.VK_TAB && !ke.isShiftDown()) {
            ke.consume()
            if ((column + 1)  >= table.getColumnCount()) {
                // if column is last column, check if there is a next row
                if ((row + 1) >= table.getRowCount()) row = 0
                else row++
                column = 1
            } else column++
            if (row > -1 && column > -1) {
                table.changeSelection(row, column, false, false)
                table.editCellAt( row, column)
                table.transferFocus()
            }
            textArea.selectAll()
        }

        if ( ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            if ( textArea.getCaretPosition() >= textArea.text.size() ) {
                ke.consume()
                if ((column + 1)  >= table.getColumnCount()) {
                    // if column is last column, check if there is a next row
                    if ((row + 1) >= table.getRowCount()) row = 0
                    else row++
                    column = 1
                } else column++
                if (row > -1 && column > -1) {
                    table.changeSelection(row, column, false, false)
                    table.editCellAt( row, column)
                    table.transferFocus()
                }
                textArea.moveCaretPosition( 0)
                textArea.select( 0, 0)
            }
        }

        if ( ke.getKeyCode() == KeyEvent.VK_TAB && ke.isShiftDown()) {
            ke.consume()
            if ( column  == 1) {
                // if column is first column, check if this is the first row
                if (row == 0) row = table.getRowCount() - 1
                else row--
                column = table.getColumnCount() - 1
            } else column--
            if (row > -1 && column > -1) {
                table.changeSelection(row, column, false, false)
                table.editCellAt( row, column)
                table.transferFocus()
            }
            textArea.selectAll()
        }

        if ( ke.getKeyCode() == KeyEvent.VK_LEFT) {
            if ( textArea.getCaretPosition() <= 0 ) {
                ke.consume()
                if ( column  == 1) {
                    // if column is first column, check if this is the first row
                    if (row == 0) row = table.getRowCount() - 1
                    else row--
                    column = table.getColumnCount() - 1
                } else column--
                if (row > -1 && column > -1) {
                    table.changeSelection(row, column, false, false)
                    table.editCellAt( row, column)
                    table.transferFocus()
                }
            }
        }

        if ( ke.getKeyCode() == KeyEvent.VK_ENTER && !ke.isShiftDown()) {
            def cp = textArea.getCaretPosition()
            def nt
            int ph
            int nh
            int rc
            int rh

            ke.consume()

            // insert the new line at the right spot - first check if at the end
            if (cp == textArea.text.length()) nt = textArea.text + '\n'
            // then check if at the beginning
            else if ( cp == 0) nt = '\n' + textArea.text
            // otherwise put the new line in the middle where the cursor is
            else nt = textArea.text[ 0 .. cp-1] + '\n' + textArea.text[ cp .. -1]
            textArea.text = nt

      //      table.model.fireTableDataChanged()

            // now, reposition the cursor
            textArea.setCaretPosition( cp+1)

        }
    }

    public void keyReleased(KeyEvent e) { }

    public boolean stopCellEditing() {
        int column = table.getEditingColumn()
        int row = table.getEditingRow()
        def str = textArea.text

        // get the previous value for comparison
        def oldStr = table.model.getValueAt( row, column)

        // save the new value
        table.model.setValueAt( str, row, column)

        // now let's propagate this back to the flow event list
        switch (column) {
        case 1:
            eventList[row].commandStr = str
            eventList[row].command = SourceFile.textScan( str)
            eventList[row].commandTags = SourceFile.tagScan( str)
            break
        case 2:
            eventList[row].sourceStr = str
            eventList[row].source = SourceFile.textScan( str)
            eventList[row].sourceTags = SourceFile.tagScan( str)
            break
        case 3:
            eventList[row].data = str
            break
        case 4:
            eventList[row].destinationStr = str
            eventList[row].destination = SourceFile.textScan( str)
            eventList[row].destinationTags = SourceFile.tagScan( str)
            break
        case 5:
            eventList[row].annotation = str
            break
        }

        // look to see if there is any valid command and if so, set the content flag
        // and look if the node list changed
//        if ( eventList.any{ model.validCommands.contains( it.command) }) {
        if ( model.validCommands.contains( eventList[row].command) ) {
            model.updateNodes()
            view.updateNodes()
            model.hasEventContents = true
        }

        // and if the old value and new value differ, then allow the flow to be saved
        if ( str != oldStr) {
            model.hasModifiedContents = true
            model.hasModifiedContents = true
        }

        return true
    }
}

updateNodes = {
    nodeDisp.clear()
    nodeDisp.repaint()
    model.nodes.eachWithIndex { node, i ->
        nl = node.split( '\\r|\\n')
        if (nl.size() > 1) {
            n = new StringBuffer( '<html><center>')
            (0 .. nl.size()-2).each {
                n += nl[it] + '<br>'
            }
            n += nl[-1] + '</html>'
            nodeDisp.add( button( n), 'sg')
        } else nodeDisp.add( button( node), 'sg')
    }
}


tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
    panel(title: tabName, id: 'sourceTab', layout: new MigLayout()) {
//tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
//    panel( id: 'sourceTab', layout: new MigLayout()) {

        build DataPanel.SourcePanel

        build DataPanel.SourceTreePanel

//        label( 'Nodes: ', constraints: 'cell 0 1')
        toolBar( id: 'nodeDisp',
                 rollover: true,
                 floatable: false,
                 margin: [0,0,0,0],
                 layout: new MigLayout('fillx', 'center'),
                 border: titledBorder( border: new EtchedBorder(), 'Nodes'),
//                 border: new EtchedBorder(),
                 constraints: 'cell 0 1 4 1, growx, hmin 75')

        panel( id: 'ftable', constraints: 'cell 0 2 4 1, grow', layout: new MigLayout()) {
            def renderer = new MultiLineTableCellRenderer()
            def editor = new TextAreaCellEditor()
            editor.eventList = model.flowEvents
            editor.model = model
            editor.view = this
            scrollPane( constraints: 'spanx, growx',
                        minimumSize: [ width: 1100],
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
                table( id: 'flowTable',
                       selectionMode : ListSelectionModel.SINGLE_SELECTION,
                       rowSelectionAllowed: false,
                       columnSelectionAllowed: false,
//                       autoResizeMode: false,
//                       reorderingAllowed: false,
//                       resizingAllowed: false,
                       mouseClicked: { e -> model.tableEditController.showTableEdit() },
                       showHorizontalLines: true,
                       showVerticalLines: true) {
                    current.selectionModel.addListSelectionListener( { e ->
                        model.tableEditModel.selectedRow = flowTable.selectedRow } as ListSelectionListener)
                    tableModel( id: 'flowTableModel', list: model.flowEvents) {
                        propertyColumn( propertyName: 'count',
                                        editable: false,
                                        maxWidth: 40)
                        propertyColumn( header: 'Command',
                                        propertyName: 'commandStr',
                                        cellRenderer: renderer,
                                        cellEditor: editor,
                                        minWidth: 75)
                        propertyColumn( header: 'Source',
                                        propertyName: 'sourceStr',
                                        cellRenderer: renderer,
                                        cellEditor: editor,
                                        minWidth: 100)
                        propertyColumn( header: 'Data',
                                        propertyName: 'data',
                                        cellRenderer: renderer,
                                        cellEditor: editor,
                                        minWidth: 450)
                        propertyColumn( header: 'Destination',
                                        propertyName: 'destinationStr',
                                        cellRenderer: renderer,
                                        cellEditor: editor,
                                        minWidth: 100)
                        propertyColumn( header: 'Annotation',
                                        propertyName: 'annotation',
                                        cellRenderer: renderer,
                                        cellEditor: editor,
                                        minWidth: 250)
                    }
                }
            }
        }
    }
}
  