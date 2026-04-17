package block.costomizableCrafter.dealer

import block.costomizableCrafter.tile.LATiles
import element.Phase
import kotlin.math.abs
import kotlin.math.min


class FlowDealer() : Dealer() {

    override fun update(tiles: LATiles) {
        super.update(tiles)
        cleanFlowData()
        checkFlow()
        countFlow()
        updateResult()
    }

    fun cleanFlowData() {
        val tiles = ftiles ?: return

        for (tile in tiles.array) {
            tile.flowData.reset()
        }
    }

    fun checkFlow(){
        val tiles = ftiles ?: return

        for(tile in tiles.array){
            if(!tile.canFlow()||tile.acted){
                continue
            }
            for(idx in 0..3){
                val nearTile=(tiles.getInnerTile(tile, idx) ?: continue).also{
                    if(!it.canFlow()||!(it.phase== Phase.vacuum&&it.element===tile.element)){
                        continue
                    }
                }.apply{
                    this.flowData.flowedCount++
                    this.flowData.flowedFrom[(idx+2)%4]=true
                    this.flowData.isFlowed=true
                }
                tile.acted=true
                tile.flowData.flowingCount++
                tile.flowData.flowingTo[idx]=true
                tile.flowData.isFlowing=true
            }
        }

        //空位特判
        for (tile in tiles.array) {
            if (!tile.flowData.isFlowed || tile.phase != Phase.vacuum) {
                continue
            }
            var total = 0.0
            var d = 0
            val fromTiles = tiles.getNearTiles(tile)
            d = 0
            while (d < 4) {
                if (tile.flowData.flowedFrom[d]) {
                    total += fromTiles[d].element?.let { it.flowability[fromTiles[d].phase] } ?: 0.0
                    fromTiles[d].flowData.flowingCount--
                    fromTiles[d].flowData.flowingTo[(d + 2) % 4] = false
                    fromTiles[d].flowData.isFlowing = fromTiles[d].flowData.flowingCount > 0
                }
                d++
            }
            val ran = Math.random() * total
            var lim = 0.0
            d = 0
            while (d < 4) {
                if (tile.flowData.flowedFrom[d]) {
                    lim += fromTiles[d].element?.let { it.flowability[fromTiles[d].phase] } ?: 0.0
                    if (ran <= lim) {
                        break
                    }
                }
                d++
            }
            d = min(d, 3)
            tile.flowData.isFlowed = true
            tile.flowData.flowedFrom = booleanArrayOf(false, false, false, false)
            tile.flowData.flowedFrom[d] = true
            fromTiles[d].flowData.isFlowing = true
            fromTiles[d].flowData.flowingTo[(d + 2) % 4] = true
        }
    }


    fun countFlow(){
        val tiles= ftiles ?: return
        for(tile in tiles.array){
            if(!tile.flowData.isFlowing){
                continue
            }
            val nearTiles=tiles.getNearTiles(tile)
            var massDeltaSum=0.0

            for(idx in 0..3){
                if(!tile.flowData.flowingTo[idx]){
                    continue
                }
                massDeltaSum+=abs(nearTiles[idx].mass-tile.mass)
            }

            for(idx in 0..3){
                if(!tile.flowData.flowingTo[idx]){
                    continue
                }
                val md=tile.element?.let { tile.mass/massDeltaSum * abs(nearTiles[idx].mass-tile.mass) *it.flowability[tile.phase] } ?: 0.0
                nearTiles[idx].flowData.massDelta+= md
                nearTiles[idx].flowData.heatDelta+= tile.heat*md/tile.mass
                tile.flowData.massDelta-= md
                tile.flowData.heatDelta-= tile.heat*md/tile.mass
            }
        }
    }

    fun updateResult(){
        val tiles= ftiles ?: return
        for(tile in tiles.array){
            if(!(tile.flowData.isFlowing&&tile.flowData.isFlowed)){
                continue
            }
            tile.addMH(tile.flowData.massDelta, tile.flowData.heatDelta)
        }
    }
}