package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles
import element.Elements
import element.Phase
import kotlin.math.abs
import kotlin.math.min

class FlowProcessor:Processor() {

    override fun process(tiles: LATiles) {
        super.process(tiles)
        cleanFlowData()
        checkFlow()
        countFlow()
        updateResult()
    }

    fun cleanFlowData(){
        for(tile in tiles.array){
            tile.flowData.reset()
        }
    }

    fun checkFlow(){

        for(tile in tiles.array){
            if(tile.es.element=== Elements.vacuum||tile.acted){
                continue
            }
            for(idx in 0..3){
                (tiles.getTile(tile, idx) ?: continue).also{
                    if((it.es.element !== tile.es.element && it.es.element !== Elements.vacuum)  || it.isEdge){
                        continue
                    }
                }.flowData.apply{
                    flowedCount++
                    flowedFrom[(idx+2)%4]=true
                    isFlowed=true
                }

                tile.apply{
                    acted=true
                }.flowData.apply{
                    flowingCount++
                    flowingTo[idx]=true
                    isFlowing=true
                }
            }
        }

        //空位特判
        for (tile in tiles.array) {
            if (!tile.flowData.isFlowed || tile.es.phase != Phase.vacuum) {
                continue
            }
            var total = 0.0
            var d = 0
            val fromTiles = tiles.getNearTiles(tile)
            while (d < 4) {
                if(fromTiles[d]==null){
                    d++
                    continue
                }
                if (tile.flowData.flowedFrom[d]) {
                    fromTiles[d]!!.let{
                        total += it.es.element.flowability[it.es.phase]
                        it.flowData.flowingCount--
                        it.flowData.flowingTo[(d + 2) % 4] = false
                        it.flowData.isFlowing = it.flowData.flowingCount > 0
                    }
                }
                d++
            }
            val ran = Math.random() * total
            var lim = 0.0
            d = 0
            while (d < 4) {
                if (tile.flowData.flowedFrom[d]) {
                    lim += fromTiles[d]!!.es.let { it.element.flowability[it.phase] }
                    if (ran <= lim) {
                        break
                    }
                }
                d++
            }
            d = min(d, 3)
            tile.flowData.apply{
                isFlowed = true
                flowedFrom = booleanArrayOf(false, false, false, false)
                flowedFrom[d] = true
            }

            tile.es.element = fromTiles[d]!!.es.element

            fromTiles[d]!!.flowData.apply{
                isFlowing=true
                flowingTo[(d + 2) % 4] = true
            }
        }
    }


    fun countFlow(){
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
                massDeltaSum+=abs(nearTiles[idx]!!.es.mass-tile.es.mass)
            }

            if(massDeltaSum <= 0.0 || tile.es.mass <= 0.0){
                continue
            }

            for(idx in 0..3){
                if(!tile.flowData.flowingTo[idx]){
                    continue
                }
                val md=tile.es.let{it.mass/massDeltaSum*abs(nearTiles[idx]!!.es.mass  -it.mass)*it.element.flowability[it.phase]}
                if(md <= 0.0 || md.isNaN() || md.isInfinite()){
                    continue
                }
                val hd = tile.es.heat*md/tile.es.mass
                if(hd.isNaN() || hd.isInfinite()){
                    continue
                }
                nearTiles[idx]!!.flowData.apply {
                    massDelta+= md
                    heatDelta+= hd
                }
                tile.flowData.apply{
                    massDelta-= md
                    heatDelta-= hd
                }
            }
        }
    }

    fun updateResult(){
        for(tile in tiles.array){
            if(!(tile.flowData.isFlowing || tile.flowData.isFlowed)){
                continue
            }
            tile.es.addMH(tile.flowData.massDelta, tile.flowData.heatDelta)
        }
    }
}