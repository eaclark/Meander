application {
    title = 'Meander'
    startupGroups = ['Meander']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "JfxPreview"
    'JfxPreview' {
        model      = 'meander.JfxPreviewModel'
        view       = 'meander.JfxPreviewView'
        controller = 'meander.JfxPreviewController'
    }

    // MVC Group for "TableEdit"
    'TableEdit' {
        model = 'meander.TableEditModel'
        controller = 'meander.TableEditController'
        view = 'meander.TableEditView'
    }

    // MVC Group for "OutputDialog"
    'OutputDialog' {
        model = 'meander.OutputDialogModel'
        controller = 'meander.OutputDialogController'
        view = 'meander.OutputDialogView'
    }

    // MVC Group for "StylesPanel"
    'StylesPanel' {
        model = 'meander.StylesPanelModel'
        controller = 'meander.StylesPanelController'
        view = 'meander.StylesPanelView'
    }

    // MVC Group for "LayoutPanel"
    'LayoutPanel' {
        model = 'meander.LayoutPanelModel'
        controller = 'meander.LayoutPanelController'
        view = 'meander.LayoutPanelView'
    }

    // MVC Group for "ModelPanel"
    'DataPanel' {
        model = 'meander.DataPanelModel'
        controller = 'meander.DataPanelController'
        view = 'meander.DataPanelView'
    }

    // MVC Group for "Meander"
    'Meander' {
        model = 'meander.MeanderModel'
        controller = 'meander.MeanderController'
        view = 'meander.MeanderView'
    }

}
