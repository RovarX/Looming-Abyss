package block.costomizableCrafter.reaction

import block.costomizableCrafter.assist.ElementArea
import block.costomizableCrafter.tile.LATile

class React {

    var tileReact: TileReact? = null
    var areaReact: AreaReact? = null

    /** 对格子进行反应  */
    fun reactOn(tile: LATile) {
        if (tileReact != null) {
            tileReact!!.react(tile)
        }
    }

    /** 对区域进行反应  */
    fun reactOn(area: ElementArea) {
        if (areaReact != null) {
            areaReact!!.react(area)
        }
    }

    fun interface TileReact {
        fun react(tile: LATile)
    }

    fun interface AreaReact {
        fun react(area: ElementArea)
    }
}