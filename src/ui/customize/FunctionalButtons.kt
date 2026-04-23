package ui.customize

import arc.scene.ui.Button

class FunctionalButtons : FlowDialog("buttons") {

    init {
        clearChildren()
        add(titleTable)
        row()
        buttons.clearChildren()
        buttons.defaults().size(160f,64f)
        add(buttons).colspan(3).growX()
    }

    fun addButton(button: Button){
        buttons.add(button)
    }
}