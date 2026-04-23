package reaction

import block.customizableCrafter.tile.LATile
import element.Element
import element.Elements
import java.util.BitSet
import kotlin.math.max

class Reaction {

    /**总编号*/
    var id = -1

    /**依据基元素编号*/
    var idByBase = -1

    /** 原料 以及 比例 */
    val ingredients = mutableMapOf<Element, Float>()

    /** 基底元素*/
    var base = Elements.vacuum

    /** 产品 以及 比例*/
    val products = mutableMapOf<Element, Float>()

    /** 是否单一产物*/
    val isSingleProduct: Boolean
        get() = products.size == 1

    /**反应需求*/
    val requirement = Requirement()

    /**进行反应*/
    val doReact = DoReact()

    /**反应物位图表示*/
    val bits by lazy{
        var b =0
        ingredients.forEach {
            b= max(b, it.key.id)
        }
        val bitSet = BitSet(b)
        ingredients.forEach {
            bitSet.set(it.key.id)
        }
        bitSet
    }



    fun basicOnTile(tile:LATile):Boolean{
        if(tile.es.element!== base){
            return false
        }

        val tempBits = BitSet(bits.size())

        tempBits.set(tile.es.element.id)

        for(nearTile in tile.getNearTiles()){
            if(nearTile == null || nearTile.canReact()){
                continue
            }
            tempBits.set(nearTile.es.element.id)
        }

        if(!tempBits.and(bits).equals(bits)){
            return false
        }

        tempBits.clear()

        if (requirement.checkTile(tile)) {
            tempBits.set(tile.es.element.id)
        }

        for (nearTile in tile.getNearTiles()) {
            if(nearTile == null || nearTile.canReact() || nearTile.es.element === base){
                continue
            }
            if (requirement.checkTile(nearTile)) {
                tempBits.set(nearTile.es.element.id)
            }
        }

        return tempBits.and(bits).equals(bits)
    }
}