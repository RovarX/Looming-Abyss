package ui.customize

import arc.scene.ui.Image
import arc.scene.ui.TextButton
import arc.scene.ui.TextField
import arc.scene.ui.layout.Scl
import block.customizableCrafter.assist.ElementState
import element.Elements
import mindustry.gen.Icon
import mindustry.ui.Styles
import ui.ui

class DebugPanel() : FlowDialog("@DebugPanel") {
    val elementIdField: TextField
    val massField: TextField
    val heatField: TextField
    val temperatureField: TextField
    
    val applyButton :TextButton

    var applying = false

    val es = ElementState()

    init{
        add("Element")
        elementIdField = field("", { _ -> })
            .width(180f)
            .get()
        row()

        add("Mass")
        massField = field("", { _ -> })
            .width(180f)
            .get()
        row()

        add("Heat")
        heatField = field("", { _ -> })
            .width(180f)
            .get()
        row()

        add("Temp")
        temperatureField = field("", { _ -> })
            .width(180f)
            .get()
        row()
        
        applyButton = TextButton("@apply",Styles.clearTogglet).also{
            it.add(Image(Icon.play)).size(Icon.play.imageSize()/Scl.scl(1f))
            it.cells.reverse()
            it.clicked({
                applying=!applying
            })
        }

        buttons.add(applyButton)
        buttons.button("@reset",this@DebugPanel::resetValues)
    }

    /**called when inner view want an es*/
    fun applyInputValues(){
        toElement(elementIdField.text)
        es.mass = massField.text.trim().toDoubleOrNull()?:-1.0
        es.heat = heatField.text.trim().toDoubleOrNull()?:-1.0
        es.temperature = temperatureField.text.trim().toDoubleOrNull()?:-1.0
        es.autoFill()
        ui.customize.data.es.copyFrom(es)
    }

    fun resetValues(){
        elementIdField.text = ""
        massField.text = "-1"
        heatField.text = "-1"
        temperatureField.text = "-1"

    }

    fun toElement(s: String){
        es.element = Elements.vacuum
        val id = s.trim().toIntOrNull()
        if(id!=null&&id>=0&&id<Elements.all.size){
            es.element= Elements.all[id]
            return
        }
        else{
            for(e in Elements.all){
                if(e.name.equals(s.trim())){
                    es.element=e
                    break
                }
            }
        }
    }
}