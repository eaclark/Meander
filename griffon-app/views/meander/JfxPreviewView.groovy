package meander

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import groovyx.javafx.SceneGraphBuilder
import groovyx.javafx.GroovyFX
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import net.miginfocom.swing.MigLayout

import java.awt.Dimension

application( title: 'Meander Preview',
             id: 'jfxPreview',
             preferredSize: [model.pageModel.xpts + 50, model.pageModel.ypts + 100],
             pack: true,
             //location: [50,50],
             locationByPlatform: true,
             iconImage: imageIcon('/meander-icon-48x48.png').image,
             iconImages: [ imageIcon('/meander-icon-48x48.png').image,
                           imageIcon('/meander-icon-32x32.png').image,
                           imageIcon('/meander-icon-16x16.png').image]) {

    previewgb = new SceneGraphBuilder()

    previewSetup = { gbuilder ->
        gbuilder.with {
            def x, y
            if( model.pageModel.portrait) {
                x = model.pageModel.xpts
                y = model.pageModel.ypts
            } else {
                x = model.pageModel.ypts
                y = model.pageModel.xpts
            }
            psp = stackPane( id: 'pvStack', minWidth: x, minHeight: y)


//            background = currentScene.root


            bp = pane( id: 'background')
            //println gbuilder.background
            psp.children.add bp
            psp.setMargin( background, new Insets(0,0,0,0))

            fp = pane( id: 'foreground')
            //currentScene.root.children.add r
            pvStack.children.add fp
            psp.setMargin( foreground, new Insets(0,0,0,0))
            currentScene.root.children.add psp
        }
    }

    panel( id: 'jfxPreviewPane', layout: new MigLayout('fill')) {
        panel ( id: 'jfxPreviewButtons', constraints: 'growx, wrap', layout: new MigLayout( 'ins 0', '[] push [] push []', '')) {
            button( id: 'jfxPrvwRules',
                    constraints: 'cell 0 0',
                    text: 'Rulers?',
                    actionPerformed: controller.actions.toggleJfxRulers
                  )
            panel( constraints: 'cell 1 0, growx') {
                button( id: 'jfxPrvwPrevious',
                        text: 'Previous',
                        enabled: bind( source: model, sourceProperty: 'pageIdx',
                                converter: { idx ->
                                    if( model.jfxDoc?.pages?.size() > 0 ) return (idx > 0)
                                    else return false
                                }
                        ),
                        actionPerformed: {
                            model.pageIdx--
                            model.jfxDoc.setCurrentPage( model.jfxDoc.pages[ model.pageIdx])
                            model.jfxDoc.page.display()
                        }
                      )
                button( id: 'jfxPrvwNext',
                        text: 'Next',
                        enabled: bind( source: model, sourceProperty: 'pageIdx',
                                       converter: { idx ->
                                           if( model.jfxDoc?.pages?.size() > 0 ) return !(idx == model.jfxDoc.pages.size()-1)
                                           else return false
                                       }
                                     ),
                        actionPerformed: {
                            model.pageIdx++
                            model.jfxDoc.setCurrentPage( model.jfxDoc.pages[ model.pageIdx])
                            model.jfxDoc.page.display()
                        }
                      )
            }
            panel( constraints: 'cell 2 0') {
                label( text: '<html>Printer<br>buffers:</html>', constraints: '')
                comboBox( id: 'jfxPrvwBuff',
                          constraints: '',
                          items: [ 'No Buffers', 'Impress', 'PowerPoint', ],
                          selectedItem: 'Impress',
                          actionPerformed: {
                              if ( jfxPrvwBuff.selectedItem != model.buffer) {
                                  model.buffer = jfxPrvwBuff.selectedItem
                                  controller.actions.genJfxPreview()
                              }
                          }
                        )
            }
        }
        // add content here
        widget( id: 'previewgp',
                constraints: 'align center',
                preferredSize: [ model.pageModel.xpts + 10, model.pageModel.ypts + 10],
                new JFXPanel()
        )
    }

    noparent() {
        Platform.runLater {
            previewgp.scene = new Scene( new Group())
            // prep the scene
            previewgb.currentScene = view.previewgp.scene
            previewSetup( previewgb)
        }
//        jfxPreview.preferredSize = [model.pageModel.xpts + 10, model.pageModel.ypts + 10]

        bind( source: model.pageModel, sourceProperty: 'size',
              target: previewgp, targetProperty: 'preferredSize',
              converter: { [ model.pageModel.xpts, model.pageModel.ypts] }
        )
        bind( source: model.pageModel, sourceProperty: 'portrait',
              target: previewgp, targetProperty: 'preferredSize',
              converter: { [ model.pageModel.xpts, model.pageModel.ypts] }
        )
        bind( source: previewgp, sourceProperty: 'preferredSize',
              target: jfxPreview, targetProperty: 'preferredSize',
              converter: {
                  def fi = jfxPreview.insets
                  int bh = jfxPreviewButtons.preferredSize.height
                  int ph = previewgp.preferredSize.height
                  int pw = previewgp.preferredSize.width

                  // probably should get the real gapping between panel components
                  int vgap = 7i
                  int hgap = 7i

                  int newW, newH

                  newW = pw + fi.left + fi.right + 2i*hgap
                  newH = bh + ph + fi.top + fi.bottom + 4i*vgap

                  jfxPreview.size = [ newW, newH ]

                  controller.actions.genJfxPreview()

                  [ newW, newH ]
              }
        )
    }
}
