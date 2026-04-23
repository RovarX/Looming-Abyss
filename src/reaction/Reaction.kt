package reaction

import element.Element
import element.Elements

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
}