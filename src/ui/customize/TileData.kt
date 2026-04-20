package ui.customize

import arc.scene.ui.Label
import block.customizableCrafter.assist.ElementState
import element.Element
import java.util.Locale

class TileData : FlowDialog("@TileData") {

    private val infoLabel: Label
    private val snapshot = ElementState()

    private var lastTileIndex = -1
    private var lastTileX = -1
    private var lastTileY = -1
    private var hasSnapshot = false

    init {
        cont.clear()
        cont.defaults().left().pad(4f)

        cont.marginTop(36f)

        infoLabel = Label("")
        infoLabel.setWrap(true)
        cont.add(infoLabel).width(280f).left().top()

        buttons.clearChildren()
        updateText()
    }

    override fun act(delta: Float) {
        super.act(delta)
        pullFromInnerView()
    }

    private fun pullFromInnerView() {
        val view = dialog.view
        val tiles = view.tiles ?: return
        val tile = tiles.getTile(view.isMouseIn())

        if (tile != null) {
            lastTileIndex = tile.y * tiles.totalWidth + tile.x
            lastTileX = tile.x
            lastTileY = tile.y
            snapshot.copyFrom(tile.es)
            hasSnapshot = true
        }

        updateText()
    }

    private fun updateText() {
        if (!hasSnapshot) {
            infoLabel.setText("等待鼠标指向 InnerView 中的 tile...")
            return
        }

        infoLabel.setText(buildString {
            append("tile index: ").append(lastTileIndex).append('\n')
            append("tile pos: (").append(lastTileX).append(", ").append(lastTileY).append(")\n")
            append("element: ").append(snapshot.element.name).append(" (#").append(snapshot.element.id).append(")\n")
            append("mass: ").append(formatValue(snapshot.mass)).append('\n')
            append("heat: ").append(formatValue(snapshot.heat)).append('\n')
            append("temperature: ").append(formatValue(snapshot.temperature)).append('\n')
            append("phase: ").append(formatPhase(snapshot.phase))
        })
    }

    // Handles Phase.vacuum(-1) and any unexpected phase value safely.
    private fun formatPhase(phase: Int): String {
        return Element.phaseNames[phase+1]
    }

    private fun formatValue(value: Double): String {
        if (value < 0.0) return "N/A"
        return String.format(Locale.PRC, "%.4f", value)
    }

}