package block.customizableCrafter.tile

import block.LABlocks
import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.assist.FlowData

class LATile(
    val x:Int,
    val y:Int,
    val tiles:LATiles
) {

    val es = ElementState()

    var isEdge = false
    var isShown = true
    var acted = false

    var floor = LABlocks.crafterFloor

    val flowData = FlowData()

    fun drawTile(x:Float,y:Float,zoom:Float){
        floor.drawFloor(x,y,zoom)
        es.drawElement(x,y,zoom)
    }

    fun canFlow():Boolean{
        return !isEdge && !acted
    }
}