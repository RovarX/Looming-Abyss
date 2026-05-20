package ui.customize

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.input.KeyCode
import arc.scene.Element
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.Image
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.Scl
// import mindustry.ui.Styles // not used
import utility.CT

/**
 * CompSelect - an image selection FlowDialog.
 *
 * Features:
 * - Black semi-transparent background for the dialog (uses FlowDialog styling).
 * - Title bar on top (inherited).
 * - Below title bar is the image display area which is a ScrollPane holding a grid table.
 * - Mouse entering the display area gives it scroll focus, so mouse wheel scrolls it.
 * - Images are square and arranged 6 per row.
 * - Click an image -> it becomes selected (single selection). Selected image draws a yellow border.
 * - UI width can be adjusted via `uiWidth` and calling `rebuild()`.
 *
 * Usage example:
 * val dlg = CompSelect(listOf("floor-1","floor-2", ...)) // region names that CT.getRegion can resolve
 * dlg.onSelect = { idx, name -> println("selected $idx -> $name") }
 * dlg.uiWidth = 640f
 * dlg.show()
 */
class CompSelect(
    regionNames: List<String> = emptyList()
) : FlowDialog("Select") {

    /** number of images per row */
    private val perRow = 6

    /** gap between tiles */
    private val gap = 8f * Scl.scl()

    /** padding inside the content area */
    private val contentPad = 8f * Scl.scl()

    /** current UI width; can be changed, then call rebuild() */
    var uiWidth: Float = 600f
        set(value) {
            field = value
        }

    /** height for the scroll area; can be changed as needed */
    var contentHeight: Float = 360f

    /** list of region names (used to fetch TextureRegions) */
    private var regions = regionNames.toMutableList()

    /** currently selected index (-1 none) */
    var selectedIndex: Int = -1
        private set

    /** selection callback: index and name */
    var onSelect: ((Int, String) -> Unit)? = null

    private val contentTable = Table()
    private val scrollPane: ScrollPane

    init {
        // clear default children and build custom layout
        clearChildren()
        // add title bar (FlowDialog's titleTable) then content
        add(titleTable).row()

        // configure content area
        contentTable.defaults().pad(gap / 2f)
        contentTable.left().top()

        // PlacementFragment-style scroll handling:
        // keep the pane scrollable while hovered, and release scroll focus once the mouse leaves.
        scrollPane = ScrollPane(contentTable).apply {
            setScrollingDisabled(true, false) // disable horizontal, enable vertical
            setFadeScrollBars(false)
            exited {
                if (hasScroll()) {
                    Core.scene.setScrollFocus(null)
                }
            }

            update {
                if (!hasScroll()) return@update

                val hover: Element? = Core.scene.getHoverElement()
                val inside = hover != null && hover.isDescendantOf(this)
                if (inside) {
                    Core.scene.setScrollFocus(this)
                }else {
                    Core.scene.setScrollFocus(null)
                }
            }
        }

        // put the scrollpane into the dialog content
        cont.clear()
        cont.defaults().left().pad(4f)
        cont.add(scrollPane).width(uiWidth).height(contentHeight).row()

        // populate initial regions
        rebuild()
    }

    /**
     * Replace the images (region names) and rebuild the grid.
     */
    fun setRegions(names: List<String>) {
        regions.clear()
        regions.addAll(names)
        selectedIndex = -1
        rebuild()
    }

    /**
     * Rebuilds the grid according to current uiWidth and regions list.
     * Call this after changing uiWidth or regions.
     */
    fun rebuild() {
        contentTable.clearChildren()

        // compute tile size: make them square, fit 6 columns inside uiWidth with gaps and padding.
        val totalGap = gap * (perRow - 1)
        val effectiveWidth = uiWidth - (contentPad * 2)
        val tileSize = (effectiveWidth - totalGap) / perRow

        var col = 0
        for ((idx, name) in regions.withIndex()) {
            val region: TextureRegion? = try {
                CT.getRegion(name)
            } catch (_: Throwable) {
                null
            }

            val tile = SelectableImage(region, idx) { clickedIdx ->
                // single selection model
                selectedIndex = clickedIdx
                onSelect?.invoke(clickedIdx, regions[clickedIdx])
                // refresh visuals on all children
                refreshSelectionVisuals()
            }

            contentTable.add(tile).size(tileSize).pad(gap / 2f)
            col++
            if (col >= perRow) {
                contentTable.row()
                col = 0
            }
        }

        // If last row not full, ensure row end
        if (col != 0) contentTable.row()
    }

    private fun refreshSelectionVisuals() {
        // iterate children (they are SelectableImage wrapped as Actor)
        contentTable.children.each { actor ->
            if (actor is SelectableImage) {
                actor.isSelected = actor.index == selectedIndex
            }
        }
    }

    /**
     * Returns the currently selected region name, or null if none.
     */
    fun selectedName(): String? = if (selectedIndex in regions.indices) regions[selectedIndex] else null

    /**
     * Small Image subclass that can draw an outer yellow border when selected.
     */
    private inner class SelectableImage(
        region: TextureRegion?,
        val index: Int,
        clickHandler: (Int) -> Unit
    ) : Image(region) {

        var isSelected: Boolean = false
            set(value) {
                field = value
                // request redraw
                invalidate()
            }

        init {
            touchable = arc.scene.event.Touchable.enabled
            addListener(object : InputListener() {
                override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                    return button == KeyCode.mouseLeft
                }

                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {
                    if (button == KeyCode.mouseLeft) clickHandler(index)
                }
            })
            // ensure image scales to fill the cell square
            scaleBy(0f) // no-op but keeps default behavior
        }

        override fun draw() {
            // draw the image first
            super.draw()
            if (isSelected) {
                // draw an outer yellow border slightly outside the image bounds
                val pad = 3f * Scl.scl()
                val x0 = x - pad
                val y0 = y - pad
                val w = width + pad * 2f
                val h = height + pad * 2f

                Draw.color(Color.yellow)
                Lines.stroke(3f * Scl.scl())
                Lines.rect(x0, y0, w, h)
                Lines.stroke(1f)
                Draw.color()
            }
        }
    }
}