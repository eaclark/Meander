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

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.awt.Color
import static meander.AlignmentType.*
import static meander.GraphicType.*
import static meander.GradientType.*
import static meander.GradientVariant.*
import static meander.LineType.*
import static meander.LinePatternType.*
import static meander.FillType.*
import static meander.LabelType.*
import static meander.AnimationType.*
import static meander.PlacementType.*
import javax.swing.JOptionPane

/**
 * These are the default styles
 *
 * They can be overridden in the source files
 */
class StyleStack {
    def editStyles = new ObservableMap()
    def defaultStyles = new ObservableMap()
    def activeStyles = new ObservableMap()
    def baseStyles = new ObservableMap( [
            'default' : new Style( name: 'default',
                                   text: new TextStyle( face: 'Times New Roman',
                                                        size: 12,
                                                        bold: false,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: NOFILL,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: LEFT,
                                   placement: SRCJUST,
                                   wrap: false,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0),
            'message' : new Style( name: 'message',
                                   text: new TextStyle( face: 'Times New Roman',
                                                        size: 8,
                                                        bold: false,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: ARROW,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: PREPEND,
                                                          labelFormat: '(M<MC>)'),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: LEFT,
                                   placement: SRCJUST,
                                   wrap: false,
                                   trimLen: 50,
                                   terseLen: 1,
                                   linesAbove: 1,
                                   example: 'message'),
         'bearerpath' : new Style( name: 'bearerpath',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 8,
                                                        bold: false,
                                                        italic: false,
                                                        color: new Color( 255, 0, 0)),
                                   graphic: new GraphicStyle( graphic: DOTTED,
                                                              line: DASH,
                                                              pattern: SINGLE,
                                                              thickness: 2.25,
                                                              color: new Color( 255, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: PREPEND,
                                                          labelFormat: 'BP-<BC>:  '),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMTOP,
                                   alignment: AlignmentType.CENTER,
                                   placement: MIDDLE,
                                   wrap: false,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0,
                                   example: 'bearerpath'),
           'exchange' : new Style( name: 'exchange',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 8,
                                                        bold: false,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: DOUBLEARROW,
                                                              line: SOLID,
                                                              pattern: THINTHIN,
                                                              thickness: 3,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: AlignmentType.CENTER,
                                   placement: MIDDLE,
                                   wrap: false,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0,
                                   example: 'exchange'),
            'comment' : new Style( name: 'comment',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 8,
                                                        bold: false,
                                                        italic: true,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: EMBEDDED,
                                                          labelFormat: 'C<CC>: '),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: APPEAR,
                                   alignment: AlignmentType.CENTER,
                                   placement: MIDDLE,
                                   wrap: true,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0,
                                   example: 'comment'),
            'summary' : new Style( name: 'summary',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 8,
                                                        bold: false,
                                                        italic: true,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: SOLID,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 0, 0, 0)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: APPEAR,
                                   alignment: AlignmentType.CENTER,
                                   placement: SPAN,
                                   wrap: true,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0,
                                   example: 'summary'),
            'subproc' : new Style( name: 'subproc',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 8,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 200, 200, 200)),
                                   border: new BorderStyle( line: SOLID,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 0, 0, 0)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 200, 200, 200), //( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: APPEAR,
                                   alignment: AlignmentType.CENTER,
                                   placement: OVERLAP,
                                   wrap: true,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0,
                                   example: 'subproc'),
             'action' : new Style( name: 'action',
                                   text: new TextStyle( face: 'Times New Roman',
                                                        size: 8,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: PREPEND,
                                                          labelFormat: 'A<AC>: '),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: APPEAR,
                                   alignment: LEFT,
                                   placement: MIDDLE,
                                   wrap: true,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0,
                                   example: 'action'),
           'annotate' : new Style( name: 'annotate',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 8,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: SOLID,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 0, 0, 0)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: LEFT,
                                   placement: SPAN,
                                   wrap: true,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0),
               'node' : new Style( name: 'node',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 10,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: SOLID,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 0, 0, 0)),
                                   fill: new FillStyle( type: ONECOLOR,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: NOANIM,
                                   alignment: AlignmentType.CENTER,
                                   placement: SPAN,
                                   wrap: false,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0),
             'header' : new Style( name: 'header',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 10,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: NOFILL,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: AlignmentType.CENTER,
                                   placement: SPAN,
                                   wrap: false,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0),
             'footer' : new Style( name: 'footer',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 10,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 0, 0, 0)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: NOFILL,
                                                        foreColor: new Color( 255, 255, 255),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: AlignmentType.CENTER,
                                   placement: SPAN,
                                   wrap: false,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0),
          'watermark' : new Style( name: 'watermark',
                                   text: new TextStyle( face: 'Arial',
                                                        size: 10,
                                                        bold: true,
                                                        italic: false,
                                                        color: new Color( 128, 128, 128)),
                                   graphic: new GraphicStyle( graphic: NOGRAPHIC,
                                                              line: SOLID,
                                                              pattern: SINGLE,
                                                              thickness: 0.75,
                                                              color: new Color( 0, 0, 0)),
                                   border: new BorderStyle( line: NOLINE,
                                                            pattern: SINGLE,
                                                            thickness: 0.75,
                                                            color: new Color( 255, 255, 255)),
                                   fill: new FillStyle( type: NOFILL,
                                                        foreColor: new Color( 128, 128, 128),
                                                        backColor: new Color( 255, 255, 255),
                                                        gradient: NOGRADIENT,
                                                        variant: NOVARIANT,
                                                        gradientScale: 50),
                                   label: new LabelStyle( labelType: NOLABEL,
                                                          labelFormat: ''),
                                   timestamp: new LabelStyle( labelType: NOLABEL,
                                                              labelFormat: ''),
                                   animate: FROMSOURCE,
                                   alignment: AlignmentType.CENTER,
                                   placement: SPAN,
                                   wrap: true,
                                   trimLen: 0,
                                   terseLen: 0,
                                   linesAbove: 0),
                      ])

    StyleStack() {
        Thread.currentThread().setContextClassLoader( getClass().getClassLoader())
        deepResetStyles()
    }

//    //  the following section of code is derived from http://thread.gmane.org/gmane.comp.lang.groovy.user/44897
//    private void xml(StringBuilder doc, int indent, String key, Style style){
//        doc << "  "*indent
//        doc << "<style id=\"${key}\">\n"
//        println '\n\n\n\n\n'
//        style.toMap().each {k,v -> println 'k = ' + k + ' v = ' + v;xml( doc,indent+1,k, v) }
//        doc << "  "*indent
//        doc << "</style>\n"
//    }
//    private void xml(StringBuilder doc, int indent, String key, List values){
//        doc << "  "*indent
//        doc << "<${key}s>\n"
//        values.each { xml(doc,indent+1,key,it) }
//        doc << "  "*indent
//        doc << "</${key}s>\n"
//    }
//    private void xml(StringBuilder doc, int indent, String key, String value){
//        doc << "  "*indent
//        def newStr = value.replaceAll( '<', '&lt;')
//        newStr = newStr.replaceAll( '>','&gt;')
////        doc << "<${key}>${XmlUtil.escapeXml( value)}</${key}>\n"
//        doc << "<${key}>$newStr</${key}>\n"
//    }
//    private void xml(StringBuilder doc, int indent, String key, value){
//        doc << "  "*indent
//        doc << "<${key}>$value</${key}>\n"
//    }
//    private void xml(StringBuilder doc, int indent, String key, Map values){
//        doc << "  "*indent
//        doc << "<${key}>\n"
//        println 'key = ' + key
//        println 'values = ' + values
//        values.each {k,v-> xml(doc,indent+1,k,v) }
//        doc << "  "*indent
//        doc << "</${key}>\n"
//    }

//    def toXML(Map m) {
//        def ret = new StringBuilder()
//        xml(ret,0,"styles",m)
//        return ret.toString()
//    }

    def toXML( Map m) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder( writer)
        xml.styles() {
            m.each() { k, style -> style.toXML( xml) }
        }
        return writer.toString()
    }

    def exportStyles( String fname) {
        def out = new File( fname)
//        def writer = new FileWriter( out)
        if( out?.exists()){
            def actionDialog = JOptionPane.showConfirmDialog(null, "Replace existing file?")
            // if yes, then replace, otherwise forget it
            if( actionDialog == JOptionPane.YES_OPTION){
                try {
                    out.write( toXML( activeStyles) )
                } catch (IOException e) {
                    println 'Unable to write ' + fname
                }
            }
        } else {
            try {
                out.write( toXML( activeStyles) )
            } catch (IOException e) {
                println 'Unable to write ' + fname
            }
        }
    }

    def importStyles( String fname, preData=false) {
        def inFile = new File( fname)
        def styleInfo, styleXML
        if( ! inFile?.exists()){
            JOptionPane.showMessageDialog(null, "File with name: $fname does not exist");
        } else {
            try {
                def styleStrs = []
                styleXML = inFile.text
                styleInfo = new XmlSlurper().parseText( styleXML)
                styleInfo.style.each { style ->
                    def styleMap = [:]
                    def subStrs = []
                    def sName = style.name

                    style.children().each { sa ->
                        def aName = sa.name()
                        sa.children().each { value ->
                            def vName = value.name()
                            subStrs << "${aName}.${vName}=" + value
                        }
                    }

                    styleMap.name = sName
                    styleMap.preData = preData
                    styleMap.data = subStrs.join( ';')

                    styleStrs << styleMap
                }

                applyStyleInfo( styleStrs)
            } catch (IOException e) {
                println 'Unable to read ' + fname
            }
        }
    }

    def applyStyleInfo( styleStrs) {
        styleStrs.each { str ->
            // if the requested style is not in the current set of styles, add it
            if (!activeStyles[str.name]) addStyle( str.name, str.data)
            // if it is already there, then update the style only if preData is true
            // otherwise, the update will happen when we execute the flow
            else if (str.preData) updateStyle( str.name, str.data)
        }
    }


    def addStyle( name, String styleStr) {
        // separate the style definition request
        def styleMap = [:]
        styleStr.split(';').each {
            if( it.split('=').length < 2) styleMap[it.split('=')[0].trim()] = null   // attribute is there, but no value
            else  styleMap[it.split('=')[0].trim()] = it.split('=')[1]?.trim()
        }

        defaultStyles."$name" = new Style()
        // look for a 'basis' request in the styleStr and copy from it - otherwise copy from 'default'
        def src = (styleMap."basis") ?: 'default'
        activeStyles."$src".copyTo( defaultStyles."$name")
        // update the name of the just copied style
        defaultStyles."$name".name = name
        // now apply the rest of the updates - if there were any
        if ( styleStr != '') defaultStyles."$name".update( styleMap)

        // update the activeStyles and editStyles copies to be the same
        activeStyles."$name" = new Style()
        defaultStyles."$name".copyTo( activeStyles."$name")
        editStyles."$name" = new Style()
        defaultStyles."$name".copyTo( editStyles."$name")
    }

    def updateStyle( name, styleStr) {
        activeStyles."$name".update( styleStr, activeStyles)
        editStyles."$name".update( styleStr, editStyles)
    }


    def deepcopy(orig) {
        def bos, oos, bin, ois
        bos = new ByteArrayOutputStream()
        oos = new ObjectOutputStream(bos)
        oos.writeObject(orig); oos.flush()
        bin = new ByteArrayInputStream(bos.toByteArray())
        ois = new ContextAwareObjectInputStream( bin)
        return ois.readObject()
    }

    def deepResetStyles() {
        // new set of defaults
        defaultStyles.clear()
        // copy the base styles over
        baseStyles.each { defaultStyles[ it.key] = deepcopy( it.value) as Style }
        // reset the active styles
        activeStyles.clear()
        baseStyles.each { activeStyles[ it.key] = deepcopy( it.value) as Style }
        // reset the edit styles
        editStyles.clear()
        baseStyles.each { editStyles[ it.key] = deepcopy( it.value) as Style }
    }

    def revertStyles() {
        // reset the active styles
        activeStyles.clear()
        defaultStyles.each { activeStyles[ it.key] = deepcopy( it.value) as Style }
        // reset the edit styles
        editStyles.clear()
        defaultStyles.each { editStyles[ it.key] = deepcopy( it.value) as Style }
    }

    def copyActiveStyles() {
        def hm = new HashMap()
        activeStyles.each { hm[ it.key] = deepcopy( it.value) as Style }
        return hm
    }

    def copyActiveStyle( name, target) { activeStyles[ name].copyTo( target) }
    def copyEditStyle( name, target) { editStyles[ name].copyTo( target) }

    def getBaseStyleList() { baseStyles.collect( { it.key }) }
    def getDefaultStyleList() { defaultStyles.collect( { it.key }) }
    def getActiveStyleList() { activeStyles.collect( { it.key }) }

    class ContextAwareObjectInputStream extends ObjectInputStream {
        ContextAwareObjectInputStream( InputStream ins) { super( ins) }
        protected Class resolveClass( ObjectStreamClass osc) {
            return Thread.currentThread().contextClassLoader.loadClass( osc.name)
        }
    }

}
