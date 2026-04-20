package ui.customize

import arc.Core
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq

class WindowManager(val windowLayer: WidgetGroup) {

    val floatingWindows = Seq<FlowDialog>()

    val shownWindows: Seq<FlowDialog>
        get() = floatingWindows.select { it.isShownInDialog }

    var activeWindow: FlowDialog? = null

    fun topWindowOrNull(): FlowDialog? {
        return if (floatingWindows.isEmpty) null else floatingWindows.maxBy { it.zIndex }
    }

    fun openWindow(dialog: FlowDialog, center: Boolean = true) {
        if (!floatingWindows.contains(dialog)) {
            floatingWindows.add(dialog)
        }

        if (dialog.parent !== windowLayer) {
            windowLayer.addChild(dialog)
        }

        if (dialog.shouldPack) {
            dialog.pack()
        }

        if (center) {
            centerWindow(dialog)
        }

    }

    fun openWindow(dialog: FlowDialog, x: Float, y: Float) {
        dialog.setInitialPosition(x, y)
        openWindow(dialog, center = false)
    }

    fun closeWindow(dialog: FlowDialog) {
        dialog.remove()
        floatingWindows.remove(dialog)
        if (activeWindow === dialog) {
            activeWindow = topWindowOrNull()
        }
    }

    fun closeTopWindow() {
        val top = activeWindow ?: topWindowOrNull()
        top?.remove()
        if (top != null) {
            floatingWindows.remove(top)
            activeWindow = topWindowOrNull()
        }
    }

    fun closeAllWindows() {
        floatingWindows.each { it.remove() }
        floatingWindows.clear()
        activeWindow = null
    }

    fun bringToFront(dialog: FlowDialog) {
        activeWindow = dialog
        dialog.toFront()
    }

    fun centerWindow(dialog: FlowDialog) {
        dialog.setInitialPosition(
            (effectiveLayerWidth() - dialog.width) / 2f,
            (effectiveLayerHeight() - dialog.height) / 2f
        )
    }


    fun effectiveLayerWidth(): Float {
        return if (windowLayer.width > 0f) windowLayer.width else Core.scene.width
    }

    fun effectiveLayerHeight(): Float {
        return if (windowLayer.height > 0f) windowLayer.height else Core.scene.height
    }


}
