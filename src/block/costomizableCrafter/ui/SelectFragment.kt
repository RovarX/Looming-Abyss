package block.costomizableCrafter.ui

import arc.Core
import arc.Events
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ButtonGroup
import arc.scene.ui.ImageButton
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import arc.struct.Seq
import block.costomizableCrafter.component.CrafterComponent
import mindustry.Vars
import mindustry.game.EventType
import mindustry.type.Category
import mindustry.ui.Styles
import mindustry.world.Block

open class SelectFragment : Table() {
    private var currentCategory: Category = Category.distribution
    val category: Category
        get() = currentCategory
    var selectedBlock: Block? = null
        private set

    var onSelected: ((Block?) -> Unit)? = null

    val categories = mutableSetOf<Category>()

    private val rowWidth = 4
    private val blocks = Seq<Block>()
    private var blockTable: Table? = null
    private var blockPane: ScrollPane? = null

    init {
        Events.on(EventType.UnlockEvent::class.java) { event ->
            if (event.content is Block && shouldShow(event.content as Block)) {
                rebuildBlocks()
            }
        }

        buildUi()
        rebuildBlocks()
    }

    fun rebuildBlocks() {
        val table = blockTable ?: return
        val pane = blockPane ?: return
        val scrollY = pane.scrollY

        collectBlocks()

        table.clearChildren()
        table.top().margin(5f)

        val group = ButtonGroup<ImageButton>()
        group.setMinCheckCount(0)

        var index = 0
        for (block in blocks) {
            if (index++ % rowWidth == 0) {
                table.row()
            }

            val button = table.button(TextureRegionDrawable(block.uiIcon), Styles.selecti) {
                selectedBlock = if (selectedBlock === block) null else block
                onSelected?.invoke(selectedBlock)
            }.size(46f).group(group).name("basic-block-${block.name}").get()

            button.resizeImage(Vars.iconMed)
            button.update { button.isChecked = selectedBlock === block }
        }

        if (blocks.isEmpty()) {
            table.add("@none.found").padLeft(36f).padTop(10f)
        }

        // Keep scroll position stable while refreshing the same single-category list.
        pane.setScrollYForce(scrollY)
        Core.app.post {
            pane.setScrollYForce(scrollY)
            pane.act(0f)
            pane.layout()
        }
    }

    fun setCategory(category: Category) {
        if (currentCategory != category) {
            currentCategory = category
            rebuildBlocks()
        }
    }

    private fun buildUi() {
        clearChildren()
        margin(4f)

        blockPane = pane(Styles.smallPane) { table -> blockTable = table }.grow().height(194f).get()
        row()
    }

    private fun collectBlocks() {
        blocks.clear()
        blocks.addAll(
            Vars.content.blocks().select {
                categories.contains(it.category) && shouldShow(it)
            }.sort { b1, b2 ->
                java.lang.Boolean.compare(!b1.isPlaceable, !b2.isPlaceable)
            }
        )

        // Drop stale selection when the filtered list no longer contains that block.
        if (selectedBlock != null && !blocks.contains(selectedBlock)) {
            selectedBlock = null
            onSelected?.invoke(null)
        }
    }

    fun shouldShow(block: Block): Boolean {
        return  block.unlockedNowHost() &&
                block is CrafterComponent &&
                block.placeablePlayer &&
                block.environmentBuildable() &&
                block.supportsEnv(Vars.state.rules.env)
    }
}