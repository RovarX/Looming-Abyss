package block.costomizableCrafter.reaction

import block.costomizableCrafter.assist.ElementArea
import block.costomizableCrafter.tile.LATile

class Requirement {

    var canReactPred: TilePred?=null
    var canReactOnPred: TilePred?=null
    var canAbsorbPred: AreaPred?=null
    val canAbsorbOnPred: AreaPred?=null

    /** 判断在该格能否进行反应，包含了对周边格的检测  */
    fun canReact(tile:LATile):Boolean{
        return canReactPred.let { it == null || it.test(tile) }
    }

    /**判断该格物质是否满足反应条件*/
    fun canReactOn(tile:LATile):Boolean{
        return canReactOnPred.let { it == null || it.test(tile) }
    }

    /** 判断在该区域能否进行吸收，包含了对周边格的检测  */
    fun canAbsorb(area:ElementArea):Boolean{
        return canAbsorbPred.let { it == null || it.test(area) }
    }

    /** 判断该区域是否满足反应条件*/
    fun canAbsorbOn(area:ElementArea):Boolean{
        return canAbsorbOnPred.let { it == null || it.test(area) }
    }

    fun interface TilePred{
        fun test(tile: LATile):Boolean
    }
    fun interface AreaPred{
        fun test(area: ElementArea):Boolean
    }
}