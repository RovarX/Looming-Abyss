package block.costomizableCrafter.reaction

import block.costomizableCrafter.assist.ElementArea
import block.costomizableCrafter.tile.LATile
import element.Element
import element.Phase
import kotlin.math.min

open class Reaction {
    /**同底物的反应编号,代表优先级，数字越小优先级越高  */
    var serialID: Int = -1
    var priority: Int = 0
    var baseElement: Element? = null
    var ingredients= HashMap<Element, Float>()
    var mainProduct: Element? = null
    var products= HashMap<Element, Float>()

    /**是否单一产物  */
    var isSingleProduct: Boolean = true
    var requirement: Requirement = Requirement()
    var react: React = React()
    var bits: Long = 0L

    /**反应物位图表示  */
    fun toBits() {
        for (e in ingredients.keys) {
            this.bits = this.bits or (1L shl e.serialID)
        }
    }


    fun normCanReact(tile: LATile): Boolean {
        if(tile.element!== baseElement){
            return false
        }

        var tempBits = 0L

        tempBits = 1L shl tile.element!!.serialID

        for(nearTile in tile.getNearTiles()){
            nearTile.element.let{
                if(it!==null){
                    tempBits = tempBits or (1L shl it.serialID)
                }
            }
        }

        if(tempBits and this.bits != this.bits){
            return false
        }

        tempBits=0L

        if (requirement.canReactOn(tile)) {
            tempBits = 1L shl tile.element!!.serialID
        }

        for (nearTile in tile.getNearTiles()) {
            if (nearTile.isEdge || nearTile.element === baseElement) {
                continue
            }
            if (requirement.canReactOn(nearTile)) {
                tempBits = tempBits or (1L shl nearTile.element!!.serialID)
            }
        }

        return tempBits and this.bits == this.bits
    }

    fun normCanAbsorb(area: ElementArea): Boolean {
        if(area.element!== baseElement){
            return false
        }

        var tempBits = 0L

        tempBits = 1L shl area.element!!.serialID

        for (tile in area.areaTiles) {
            tile.element.let {
                if (it !== null) {
                    tempBits = tempBits or (1L shl it.serialID)
                }
            }
        }

        if(tempBits and this.bits != this.bits){
            return false
        }

        tempBits=0L

        if(requirement.canAbsorbOn(area)){
            tempBits = tempBits or (1L  shl area.element!!.serialID)
        }

        for (nearTile in area.nearTiles) {
            if (requirement.canReact(nearTile)) {
                tempBits = tempBits or (1L shl nearTile.element!!.serialID)
            }
        }

        return tempBits == this.bits
    }


    //TODO:优化生成物的质量热量如何分配
    fun normalTileReact(tile: LATile) {

    }

    fun normalAreaReact(area: ElementArea) {

    }
}