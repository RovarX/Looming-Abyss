package block.customizableCrafter.tile

import block.customizableCrafter.assist.ElementState

class LATile(
    val x:Int,
    val y:Int,
    val tiles:LATiles
) {

    val es = ElementState()

    var isEdge = false
    var isShown = true

    fun drawTile(x:Float,y:Float,zoom:Float){

    }
}