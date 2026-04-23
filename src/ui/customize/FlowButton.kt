package ui.customize

import arc.scene.ui.Button

class FlowButton(button: Button): FlowDialog("button") {

    init{
        shouldPack=false
        cont.clear()
        buttons.clearChildren()
        cont.add(button).pad(6f).growX()
    }
}