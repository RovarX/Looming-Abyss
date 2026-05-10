package reaction

import block.customizableCrafter.tile.LATile
import element.Elements
import element.Phase

object Reactions {

    lateinit var allByBase: List<List<Reaction>>
    lateinit var allByProduct : List<List<Reaction>>

    /**2Al + Fe -> 3Al_alloy*/
    lateinit var re_1: Reaction


    fun load(){
        re_1 = Reaction().apply{
            val self = this
            id = 1
            idByBase = 1
            base= Elements.Al
            maxRate = 100.0f
            ingredients.apply{
                put(Elements.Al,2.0f)
                put(Elements.Fe,1.0f)
            }
            products.apply{
                put(Elements.Al_alloy,3.0f)
            }
            requirement.apply{
                checkTile = L@{ tile: LATile ->
                    tile.es.element.also{
                        if(!ingredients.containsKey(tile.es.element)){
                            return@L false
                        }
                    }
                    return@L tile.es.phase == Phase.liquid
                }

                onTile = { tile: LATile ->
                    Normal.onTile(self, tile)
                }
            }
            doReact.apply {
                reactOnTile = { tile: LATile ->
                    Normal.reactOn(self, tile)
                }
            }
        }

        allByBase = listOf(
            listOf(),
            listOf(),
            listOfNotNull(re_1),
            listOf(),
            listOf(),
        )

        allByProduct = listOf(
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOfNotNull(re_1),
        )
    }

}