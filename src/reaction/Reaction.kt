package reaction

import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.dealer.Processors
import block.customizableCrafter.tile.LATile
import element.Element
import element.Elements
import element.Phase
import utility.Bits
import kotlin.math.max
import kotlin.math.min

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

    var maxRate = 0.0f

    /**反应物位图表示*/
    val bits by lazy{
        Bits().also{
            for(e: Element in ingredients.keys){
                it.or(e.id)
            }
        }
    }



}