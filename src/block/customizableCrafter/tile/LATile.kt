package block.customizableCrafter.tile

import block.LABlocks
import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.assist.FlowData
import element.Elements

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

    /**remove element in this tile*/
    fun removeElement(){
        es.area?.removeTile(this)
        es.toNull()
    }

    /**是否能流入液体*/
    fun canFlowIn():Boolean{
        return !isEdge
    }

    fun canFlowOut():Boolean{
        return !isEdge && es.element !== Elements.vacuum
    }

    fun canReact():Boolean{
        return !isEdge && es.element !== Elements.vacuum
    }
    fun getNearTiles(): Array<LATile?> {
        return tiles.getNearTiles(this)
    }
}