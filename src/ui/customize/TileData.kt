package ui.customize

import arc.scene.ui.Label
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import block.customizableCrafter.assist.ElementState
import element.Element
import element.Phase
import java.util.Locale

class TileData : FlowDialog("@TileData") {

    private data class FieldDescriptor(
        val label: String,
        val value: TileData.() -> String
    )

    private data class PageDescriptor(
        val name: String,
        val fields: List<FieldDescriptor>
    )

    private val pages = listOf(
        PageDescriptor(
            "Tile",
            listOf(
                FieldDescriptor("tile index") { tileIndex.toString() },
                FieldDescriptor("tile pos") { "($tileX, $tileY)" },
                FieldDescriptor("isEdge") { tileIsEdge.toString() },
                FieldDescriptor("isShown") { tileIsShown.toString() },
                FieldDescriptor("acted") { tileActed.toString() },
                FieldDescriptor("floor") { floorName },
            )
        ),
        PageDescriptor(
            "ElementState",
            listOf(
                FieldDescriptor("element") { "${esSnapshot.element.name} (#${esSnapshot.element.id})" },
                FieldDescriptor("phase") { "${formatPhase(esSnapshot.phase)} (${esSnapshot.phase})" },
                FieldDescriptor("mass") { formatValue(esSnapshot.mass) },
                FieldDescriptor("heat") { formatValue(esSnapshot.heat) },
                FieldDescriptor("temperature") { formatValue(esSnapshot.temperature) }
            )
        ),
        PageDescriptor(
            "FlowData",
            listOf(
                FieldDescriptor("canFlowing") { flowCanFlowing.toString() },
                FieldDescriptor("canFlowed") { flowCanFlowed.toString() },
                FieldDescriptor("isFlowing") { flowIsFlowing.toString() },
                FieldDescriptor("flowingTo") { formatDirs(flowTo) },
                FieldDescriptor("flowingCount") { flowFlowingCount.toString() },
                FieldDescriptor("isFlowed") { flowIsFlowed.toString() },
                FieldDescriptor("flowedFrom") { formatDirs(flowFrom) },
                FieldDescriptor("flowedCount") { flowFlowedCount.toString() },
                FieldDescriptor("massDelta") { formatValue(flowMassDelta) },
                FieldDescriptor("heatDelta") { formatValue(flowHeatDelta) }
            )
        )
    )

    private val pageLabel: Label
    private val infoLabel: Label
    private val infoPane: ScrollPane
    private val esSnapshot = ElementState()

    private var page = 0

    private var hasTile = false
    private var tileIndex = -1
    private var tileX = -1
    private var tileY = -1
    private var tileIsEdge = false
    private var tileIsShown = false
    private var tileActed = false
    private var floorName = "unknown"

    private var flowCanFlowing = false
    private var flowCanFlowed = false
    private var flowIsFlowing = false
    private var flowIsFlowed = false
    private var flowFlowingCount = 0
    private var flowFlowedCount = 0
    private var flowMassDelta = 0.0
    private var flowHeatDelta = 0.0
    private val flowTo = BooleanArray(4)
    private val flowFrom = BooleanArray(4)

    init {
        cont.clear()
        cont.defaults().left().pad(4f)

        pageLabel = Label("")
        cont.add(pageLabel).left().top().row()

        infoLabel = Label("")
        infoLabel.setWrap(true)
        val infoTable = Table().apply {
            add(infoLabel).width(320f).left().top().growY()
        }
        infoPane = ScrollPane(infoTable)
        infoPane.setScrollingDisabled(true, false)
        cont.add(infoPane).width(340f).height(260f).growY().left().top()

        buttons.clearChildren()
        buttons.defaults().size(56f, 40f).pad(2f)
        buttons.button("<") { turnPage(-1) }
        buttons.add("").growX()
        buttons.button(">") { turnPage(1) }

        updateText()
    }

    override fun act(delta: Float) {
        super.act(delta)
        pullFromInnerView()
    }

    //TODO: optimize by only pulling when mouse position changes, or at a reasonable interval.
    private fun pullFromInnerView() {
        val view = dialog.view
        val tiles = view.tiles ?: run {
            hasTile = false
            updateText()
            return
        }
        val tile = tiles.getTile(view.mouseOnTile)

        if (tile == null) {
            hasTile = false
            updateText()
            return
        }

        tileIndex = tile.y * tiles.totalWidth + tile.x
        tileX = tile.x
        tileY = tile.y
        tileIsEdge = tile.isEdge
        tileIsShown = tile.isShown
        tileActed = tile.acted
        floorName = tile.floor.name

        esSnapshot.copyFrom(tile.es)

        val fd = tile.flowData
        flowCanFlowing = fd.canFlowing
        flowCanFlowed = fd.canFlowed
        flowIsFlowing = fd.isFlowing
        flowIsFlowed = fd.isFlowed
        flowFlowingCount = fd.flowingCount
        flowFlowedCount = fd.flowedCount
        flowMassDelta = fd.massDelta
        flowHeatDelta = fd.heatDelta
        for (i in 0..3) {
            flowTo[i] = fd.flowingTo[i]
            flowFrom[i] = fd.flowedFrom[i]
        }

        hasTile = true
        updateText()
    }

    private fun turnPage(delta: Int) {
        page = (page + delta + pages.size) % pages.size
        updateText()
    }

    private fun renderPageText(pageDescriptor: PageDescriptor): String {
        return buildString {
            pageDescriptor.fields.forEachIndexed { index, field ->
                if (index > 0) append('\n')
                append(field.label).append(": ").append(field.value(this@TileData))
            }
        }
    }

    private fun updateText() {
        val currentPage = pages[page]
        pageLabel.setText("${page + 1}/${pages.size}  ${currentPage.name}")

        if (!hasTile) {
            infoLabel.setText("等待鼠标指向 InnerView 中的 tile...\n\n使用下方 < 和 > 按钮翻页。")
            return
        }

        infoLabel.setText(renderPageText(currentPage))
        infoLabel.invalidateHierarchy()
        infoPane.invalidateHierarchy()
    }

    private fun formatPhase(phase: Int): String {
        if (phase < 0 || phase >= Element.phaseNames.size) return "unknown($phase)"
        return Element.phaseNames[phase]
    }

    private fun formatDirs(flags: BooleanArray): String {
        val names = arrayOf("d0", "d1", "d2", "d3")
        return buildString {
            append('[')
            for (i in flags.indices) {
                if (i > 0) append(", ")
                append(names[i]).append('=').append(flags[i])
            }
            append(']')
        }
    }

    private fun formatValue(value: Double): String {
        if (value < 0.0) return "N/A"
        return String.format(Locale.PRC, "%.4f", value)
    }

}