package block.costomizableCrafter.ui

import arc.scene.ui.Button
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import block.costomizableCrafter.tile.LATiles
import mindustry.Vars
import mindustry.core.GameState.State
import mindustry.gen.Icon
import mindustry.ui.dialogs.BaseDialog
import statuseffect.LAStatusEffects

class CustomizeDialog() : BaseDialog("customize") {

    var tiles: LATiles? = null
    val view = CUI.innerView
    val debugPanel = DebugPanel()
    val selectFragment = SelectFragment()
    var viewCell: Cell<InnerView>? = null
    var switchButton: Button? = null

    init{
        clearChildren()
        shouldPause=true
        addCloseListener()
        shown(this::setup)
        hidden(this::whenHide)

        val leftTools = Table().apply {
            top().left()
            defaults().left().padBottom(6f)
            add(debugPanel)
                .left()
                .top()
                .pad(8f)
                .name("debugPanel")
            row()
        }

        val rightTools = Table().apply {
            top().left()
            defaults().left().padBottom(6f)
            add(selectFragment)
                .left()
                .top()
                .pad(8f)
                .name("selectFragment")
            row()
        }

        add(leftTools)
            .left()
            .top()
            .width(260f)
            .fillY()
            .name("leftTools")

        viewCell = add(view)
            .size(view.totalWidth, view.totalHeight)
            .center()
            .name("view")

        add(rightTools)
            .left()
            .top()
            .width(260f)
            .fillY()
            .name("rightTools")
        row()

        add(buttons).growX().colspan(3).name("buttons")
    }

    fun setup(){
        Vars.player.unit().apply(LAStatusEffects.glaciated, 1.0E8f)
        buttons.clearChildren()
        buttons.defaults().size(160f,64f)
        buttons.button("@back", Icon.left, this::hide).name("back")

        val switchText = if (Vars.state.isPaused) "@toPlay" else "@toPause"
        val switchIcon = if (Vars.state.isPaused) Icon.play else Icon.pause
        switchButton = buttons.button(switchText, switchIcon, this::switchPauseState).name("switch").get()


    }

    fun show(tiles: LATiles){
        this.tiles=tiles
        view.setViewOf(tiles)
        // Keep the actor at setArea() size and avoid stretch from parent layout.
        viewCell?.size(view.totalWidth, view.totalHeight)
        super.show()
    }

    fun whenHide(){
        Vars.player.unit().unapply(LAStatusEffects.glaciated)
    }

    fun switchPauseState(){
        if (Vars.state.isPaused) {
            Vars.state.set(State.playing)
        } else {
            Vars.state.set(State.paused)
        }
    }
}