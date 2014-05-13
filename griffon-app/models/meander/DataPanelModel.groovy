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

class DataPanelModel {
    @Bindable templateList = ['one', 'two', 'three']

    // the current flow events
    @Bindable List flowEvents
    @Bindable boolean hasEventContents = false
    @Bindable boolean hasModifiedContents = false

    def validNodeCommands = [ 'node', 'network', 'message', 'bearerpath', 'exchange', 'comment', 'subproc', 'action']
    def validCommands = [ 'action', 'annotate', 'bearerpath', 'comment', 'exchange', 'import', 'message', 'network', 'node', 'style', 'subproc', 'summary']
    def aliasCommands = [ 'component' : 'node',
                          'voicepath' : 'bearerpath']

    def pageModels
    def counts
    def baseDocument
    def baseOptions
    def sourceOptions
    def userOptions
    def stringsModels

    def layoutView
    def tableEditController
    def tableEditModel

    def stylesView
    def stylesModel
    def styles

    // list of currently visible flow nodes
    @Bindable List nodes

    void updateNodes() {
        nodes.clear()
        flowEvents.each { evt ->
            def s
            if (evt.command in validNodeCommands) {
                def n = evt.source
                n?.split(',').each { str ->
                    s = str.trim()
                    if ( s && !nodes.contains( s)) nodes << s
                }
                n = evt.destination
                n?.split(',').each { str ->
                    if ( !str.isBigInteger() ) { //if ( !(('1' .. '9') + ("10' .. '19')).contains(n) ) {
                        s = str.trim()
                        if ( s && !nodes.contains( s)) nodes << s
                    }
                }
            }
        }
    }

    void updateStrings( strs, type) {
        strs.each { key, value ->
            stringsModels[ type]."${key}Str" = value
            if ( layoutView.strsSrcOpt.selected) {
                stringsModels[ 'base']."${key}Str" = value
                stringsModels[ 'user']."${key}Str" = value
            }
        }
    }

    void initOptions() {
        Options.bools.each {
            sourceOptions."$it" = false
            if ( layoutView.optSource.selected ) {
                baseOptions."$it" = false
                userOptions."$it" = false
            }
        }
        PageStrings.bools.each {
            stringsModels['source']."$it" = false
            if ( layoutView.optSource.selected ) {
                stringsModels['base']."$it" = false
                stringsModels['user']."$it" = false
            }
        }
    }

    void updateOptions( opts) {
        opts.each { opt ->
            switch (opt) {
            case 'animate':
                sourceOptions.animate = true
                if ( layoutView.optSource.selected ) {
                    baseOptions.animate = true
                    userOptions.animate = true
                }
                break
            case 'author':
                stringsModels[ 'source'].author = true
                if ( layoutView.strsSrcOpt.selected ) {
                    stringsModels['base'].author = true
                    stringsModels['user'].author = true
                }
                break
            case 'date':
                stringsModels[ 'source'].date = true
                if ( layoutView.strsSrcOpt.selected ) {
                    stringsModels[ 'base'].date = true
                    stringsModels[ 'user'].date = true
                }
                break
            case 'footer':
                stringsModels[ 'source'].footer = true
                if ( layoutView.strsSrcOpt.selected ) {
                    stringsModels[ 'base'].footer = true
                    stringsModels[ 'user'].footer = true
                }
                break
            case 'landscape':
                pageModels[ 'source'].landscape = true
                if ( layoutView.optSource.selected ) {
                    pageModels[ 'base'].landscape = true
                    pageModels[ 'user'].landscape = true
                }
                break
            case 'notes':
                sourceOptions.notes = true
                if ( layoutView.optSource.selected ) {
                    baseOptions.notes = true
                    userOptions.notes = true
                }
                break
            case 'pagenum':
                stringsModels[ 'source'].pagenum = true
                if ( layoutView.strsSrcOpt.selected ) {
                    stringsModels[ 'base'].pagenum = true
                    stringsModels[ 'user'].pagenum = true
                }
                break
            case 'portrait':
                pageModels['source'].protrait = true
                if ( layoutView.optSource.selected ) {
                    pageModels['base'].portrait = true
                    pageModels['user'].portrait = true
                }
                break
            case 'postfix':
                sourceOptions.postfix = true
                if ( layoutView.optSource.selected ) {
                    baseOptions.postfix = true
                    userOptions.postfix = true
                }
                break
            case 'terse':
                sourceOptions.terse = true
                if ( layoutView.optSource.selected ) {
                    baseOptions.terse = true
                    userOptions.terse = true
                }
                break
            case 'title':
                stringsModels[ 'source'].title = true
                if ( layoutView.strsSrcOpt.selected ) {
                    stringsModels[ 'base'].title = true
                    stringsModels[ 'user'].title = true
                }
                break
            case 'trim':
                sourceOptions.trim = true
                if ( layoutView.optSource.selected ) {
                    baseOptions.trim = true
                    userOptions.trim = true
                }
                break
            case 'watermark':
                stringsModels[ 'source'].watermark = true
                if ( layoutView.strsSrcOpt.selected ) {
                    stringsModels[ 'base'].watermark = true
                    stringsModels[ 'user'].watermark = true
                }
                break
            }
        }
    }

    def columnNames = [ '', 'Command', 'Source', 'Data', 'Destination', 'Annotation']

}