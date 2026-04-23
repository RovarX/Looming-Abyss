package ui.customize

import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.WidgetGroup
import block.costomizableCrafter.ui.InnerView
import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.tile.LATiles
import mindustry.Vars
import mindustry.core.GameState
import mindustry.gen.Icon
import mindustry.ui.Styles
import mindustry.ui.dialogs.BaseDialog

class CustomizeDialog : BaseDialog("@dialog") {

    val windowLayer = WidgetGroup()
    val windowManager = WindowManager(windowLayer)
    private val testWindow = createTestWindow()

    val view : InnerView
    val debugPanel = DebugPanel()
    val tileData = TileData()

    val allWindows : List<FlowDialog>

    val shownWindows: List<FlowDialog>
        get() = allWindows.filter { it.isShownInDialog }

    /**the data shared by uis*/
    val data = ShareData()

    val switchButton: FlowButton

    val functionalButtons = FunctionalButtons()

    
    init {
        clearChildren()
        setFillParent(true)
        shouldPause = true
        titleTable.clear()
        titleTable.add("@customize")

        buildLayers()


        shown {
            setup()
            shownWindows.forEach{
                windowManager.openWindow(it)
            }

        }

        hidden {
            windowManager.closeAllWindows()
        }

        view = InnerView()

        switchButton = FlowButton(TextButton("@switchState",Styles.clearTogglet).also{
            it.add(Image(Icon.play)).size(Icon.play.imageSize()/Scl.scl(1f))
            it.cells.reverse()
            it.clicked(this@CustomizeDialog::switchState)
        })

        functionalButtons.add(switchButton)

        addCloseListener()

        setInitialPositions()

        allWindows = listOf(view, debugPanel, tileData, functionalButtons)
    }

    fun setup(){
    }

    fun switchState(){
        if (Vars.state.isPaused) {
            Vars.state.set(GameState.State.playing)
        } else {
            Vars.state.set(GameState.State.paused)
        }
    }

    fun buildLayers() {
        cont.clear()
        cont.defaults().grow()
        windowLayer.setFillParent(false)
        windowLayer.touchable = Touchable.childrenOnly
        addChild(windowLayer)
        windowLayer.toFront()
    }

    override fun act(delta: Float) {
        super.act(delta)
        // Full overlay over this dialog (dialog itself fills the stage).
        windowLayer.setBounds(0f, 0f, width, height)
        windowLayer.toFront()
    }

    private fun createTestWindow(): FlowDialog {
        return FlowDialog("Test Window").apply {
            cont.clear()
            cont.defaults().pad(8f).left()

            cont.add("This is a test window.").row()
            cont.add("Drag the title bar to move it.").row()

            buttons.clearChildren()
            buttons.button("Close") { hide() }
        }
    }

    fun show(tiles: LATiles) {
        view.setViewOf(tiles)
        super.show()

    }

    fun setInitialPositions(){
        debugPanel.setInitialPosition(0f,0f)
        tileData.setInitialPosition(0f,140f)
        view.setInitialPosition(100f,100f)
    }

    class ShareData{
        val es = ElementState()
    }
}
