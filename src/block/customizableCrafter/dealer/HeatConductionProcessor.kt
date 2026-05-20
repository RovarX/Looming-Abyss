package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATile
import block.customizableCrafter.tile.LATiles
import element.Elements

class HeatConductionProcessor: Processor() {

    private var heatTemp = DoubleArray(0)
    private var bufferWidth = 0
    private var bufferHeight = 0

    override fun process(tiles: LATiles) {
        super.process(tiles)
        withNearTile()
    }

    fun withNearTile(){
        ensureBuffer()
        heatTemp.fill(0.0)
        for(tile in tiles.array){
            if(tile.es.element==Elements.vacuum){
                continue
            }
            val rightTile = tiles.getTile(tile,1)
            val upTile = tiles.getTile(tile,0)
            if(rightTile!=null&&rightTile.es.element!=Elements.vacuum){
                val heatDelta = calHeatDelta(tile,rightTile)
                heatTemp[indexOf(tile.x, tile.y)] -= heatDelta
                heatTemp[indexOf(tile.x + 1, tile.y)] += heatDelta
            }
            if(upTile!=null&&upTile.es.element!=Elements.vacuum){
                val heatDelta = calHeatDelta(tile,upTile)
                heatTemp[indexOf(tile.x, tile.y)] -= heatDelta
                heatTemp[indexOf(tile.x, tile.y + 1)] += heatDelta
            }
        }
        for(tile in tiles.array){
            tile.es.addHeat(heatTemp[indexOf(tile.x, tile.y)])
        }
    }

    private fun ensureBuffer() {
        if (bufferWidth != tiles.totalWidth || bufferHeight != tiles.totalHeight || heatTemp.size != tiles.totalWidth * tiles.totalHeight) {
            bufferWidth = tiles.totalWidth
            bufferHeight = tiles.totalHeight
            heatTemp = DoubleArray(bufferWidth * bufferHeight)
        }
    }

    private fun indexOf(x: Int, y: Int): Int {
        return y * bufferWidth + x
    }

    fun calHeatDelta(tileA : LATile,tileB : LATile):Double{
        val condA =tileA.es.let{
            it.element.heatConductivity[it.phase]
        }
        val condB =tileB.es.let{
            it.element.heatConductivity[it.phase]
        }
        val tempD = tileA.es.temperature-tileB.es.temperature
        return condA * condB * tempD / (condA + condB)
    }
}