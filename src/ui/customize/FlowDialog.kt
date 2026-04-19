package ui.customize

import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.event.Touchable
import arc.scene.ui.Dialog
import arc.scene.ui.Dialog.DialogStyle

open class FlowDialog(title: String = "") : Dialog(title) {
    private var behaviorInstalled = false
    private var movedCallback: ((Dialog) -> Unit)? = null

    val data = Data()

    init {
        setFillParent(false)
        setModal(false)
        val baseStyle = style
        setStyle(DialogStyle().apply {
            background = baseStyle.background
            titleFont = baseStyle.titleFont
            titleFontColor = baseStyle.titleFontColor
            stageBackground = null
        })
        titleTable.touchable = Touchable.enabled
        attachBehavior()
    }

    /**
     * Installs a title-bar drag listener. `onDrag` receives [fromStageX, fromStageY, toStageX, toStageY].
     */
    fun attachBehavior(
        onFocus: () -> Unit = {},
        onMoved: (Dialog) -> Unit = {}
    ) {
        if (behaviorInstalled) return
        behaviorInstalled = true
        movedCallback = onMoved

        titleTable.addListener(object : InputListener() {
            var dragPointer = -1
            var dragging = false
            var dragOffsetX = 0f
            var dragOffsetY = 0f

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                if (button != KeyCode.mouseLeft) return false

                dragPointer = pointer

                dragOffsetX = event.stageX - this@FlowDialog.data.x
                dragOffsetY = event.stageY - this@FlowDialog.data.y
                dragging = true
                onFocus()
                this@FlowDialog.toFront()
                return true
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (!dragging || pointer != dragPointer) return

                data.x = event.stageX - dragOffsetX
                data.y = event.stageY - dragOffsetY
                data.state = State.Moved
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {
                if (pointer != dragPointer) return
                dragging = false
                dragPointer = -1
            }
        })
    }

    override fun act(delta: Float) {
        super.act(delta)
        setPosition(data.x, data.y)
    }

    fun setInitialPosition(x: Float, y: Float) {
        data.x = x
        data.y = y
    }

    class Data{

        var x: Float = 0f
        var y: Float = 0f
        var width: Float = 0f
        var height: Float = 0f
        var state: State = State.Shown
        var isShown: Boolean = true

    }

    enum class State{
        Moved, Resized, Closed,Shown
    }
}