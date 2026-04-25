package ui.customize

import arc.scene.ui.Label
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import block.customizableCrafter.assist.ElementState
import element.Element
import utility.CT

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
                FieldDescriptor("tile index") { formatNumber(tileIndex.toDouble()) },
                FieldDescriptor("tile pos") { "(${formatNumber(tileX.toDouble())}, ${formatNumber(tileY.toDouble())})" },
                FieldDescriptor("isEdge") { tileIsEdge.toString() },
                FieldDescriptor("isShown") { tileIsShown.toString() },
                FieldDescriptor("acted") { tileActed.toString() },
                FieldDescriptor("floor") { floorName },
            )
        ),
        PageDescriptor(
            "ElementState",
            listOf(
                FieldDescriptor("element") { "${esSnapshot.element.name} (#${formatNumber(esSnapshot.element.id.toDouble())})" },
                FieldDescriptor("phase") { "${formatPhase(esSnapshot.phase)} (${formatNumber(esSnapshot.phase.toDouble())})" },
                FieldDescriptor("mass") { formatValue(esSnapshot.mass, "g") },
                FieldDescriptor("heat") { formatValue(esSnapshot.heat) },
                FieldDescriptor("temperature") { formatValue(esSnapshot.temperature, "T") }
            )
        ),
        PageDescriptor(
            "FlowData",
            listOf(
                FieldDescriptor("canFlowing") { flowCanFlowing.toString() },
                FieldDescriptor("canFlowed") { flowCanFlowed.toString() },
                FieldDescriptor("isFlowing") { flowIsFlowing.toString() },
                FieldDescriptor("flowingTo") { formatDirs(flowTo) },
                FieldDescriptor("flowingCount") { formatNumber(flowFlowingCount.toDouble()) },
                FieldDescriptor("isFlowed") { flowIsFlowed.toString() },
                FieldDescriptor("flowedFrom") { formatDirs(flowFrom) },
                FieldDescriptor("flowedCount") { formatNumber(flowFlowedCount.toDouble()) },
                FieldDescriptor("massDelta") { formatValue(flowMassDelta, "g") },
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
    private var tileTag = -1
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
            if (hasTile) {
                hasTile = false
                tileTag = -1
                updateText()
            }
            return
        }
        val tile = tiles.getTile(view.mouseOnTile)

        if (tile == null) {
            if (hasTile) {
                hasTile = false
                tileTag = -1
                updateText()
            }
            return
        }

        val currentTag = tile.y * tiles.totalWidth + tile.x
        val switchedTile = currentTag != tileTag
        val es = tile.es

        if (!switchedTile && es.isStoredInTileData && !es.changed) return

        tileIndex = currentTag
        tileTag = currentTag
        tileX = tile.x
        tileY = tile.y
        tileIsEdge = tile.isEdge
        tileIsShown = tile.isShown
        tileActed = tile.acted
        floorName = tile.floor.name

        esSnapshot.copyFrom(es)

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

        es.isStoredInTileData = true
        es.changed = false

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

    private fun formatValue(value: Double, end: String = ""): String {
        if (value < 0.0) return "N/A"
        return CT.format(value, 6, end)
    }

    private fun formatNumber(value: Double): String {
        return CT.format(value, 6, "")
    }
}