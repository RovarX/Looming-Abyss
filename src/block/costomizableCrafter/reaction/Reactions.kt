package block.costomizableCrafter.reaction

import block.costomizableCrafter.reaction.React.AreaReact
import block.costomizableCrafter.reaction.React.TileReact
import block.costomizableCrafter.reaction.Requirement.AreaPred
import block.costomizableCrafter.reaction.Requirement.TilePred
import block.costomizableCrafter.tile.LATile
import element.Element
import element.Elements
import element.Phase

object Reactions {

    /**所有反应，按底物分类  */
    var all: List<List<Reaction>> = listOf()

    /**所有反应，按产物分类  */
    var allPd: List<List<Reaction>> = listOf()

    /**铝+铁->铝合金 */
    var re_1: Reaction? = null

    @JvmStatic
    fun load() {
        re_1 = object : Reaction() {
            init {
                serialID = 1
                baseElement = Elements.Al
                ingredients = object : HashMap<Element, Float>() {
                    init {
                        put(Elements.Al, 1.0f)
                        put(Elements.Fe, 0.5f)
                    }
                }
                mainProduct = Elements.Al_Alloy
                products = object : HashMap<Element, Float>() {
                    init {
                        put(Elements.Al_Alloy, 1.3f)
                    }
                }
                requirement.canReactOnPred = TilePred label@{ tile: LATile ->
                    if (tile.isEdge) {
                        return@label false
                    }
                    if (tile.element === Elements.Al) {
                        val t = tile.temperature
                        if (t == null) return@label false
                        return@label t >= 660.0f && t <= 700.0f
                    }
                    if (tile.element === Elements.Fe) {
                        return@label tile.phase == Phase.liquid
                    }
                    return@label false
                }

                requirement.canReactPred = TilePred(this::normCanReact)
                requirement.canAbsorbPred = AreaPred(this::normCanAbsorb)
                react.tileReact = TileReact(this::normalTileReact)
                react.areaReact = AreaReact(this::normalAreaReact)
                toBits()
            }
        }

        all = listOf(
            listOf(),
            listOf(),
            listOfNotNull(re_1),
            listOf(),
            listOf(),
        )

        allPd = listOf(
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOfNotNull(re_1),
        )
    }
}