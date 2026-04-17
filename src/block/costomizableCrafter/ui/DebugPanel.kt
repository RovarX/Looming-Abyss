package block.costomizableCrafter.ui

import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import element.Element
import element.Elements

class DebugPanel : Table() {

    val elementIdField: TextField
    val massField: TextField
    val temperatureField: TextField
    var applied = false
    
    var curmass = 0.0
    var curtemp = 0.0f
    var curElement: Element? = null


    init {
        defaults().pad(4f).left()

        add("Element ID")
        elementIdField = field("", { _ -> })
            .width(180f)
            .get()
        elementIdField.filter = TextField.TextFieldFilter.digitsOnly
        row()

        add("Mass")
        massField = field("", { _ -> })
            .width(180f)
            .get()
        row()

        add("Temperature")
        temperatureField = field("", { _ -> })
            .width(180f)
            .get()
        row()

        button({ if (applied) "@unapply" else "@apply" }) {
            applyInputValues()
            applied = !applied
        }.colspan(2).left()
    }

    fun applyInputValues() {
        toElement(elementIdField.text)
        curmass = massField.text.trim().toDoubleOrNull() ?: return
        curtemp = temperatureField.text.trim().toFloatOrNull() ?: return
    }

    fun toElement(s: String){
        curElement = null
        val id = s.trim().toIntOrNull() 
        if(id!=null&&id>=0&&id<Elements.all.size){
            curElement= Elements.all[id]
            return
        }
        else{
            for(e in Elements.all){
                if(e.name.equals(s.trim())){
                    curElement=e
                    break
                }
            }
        }
    }
}
