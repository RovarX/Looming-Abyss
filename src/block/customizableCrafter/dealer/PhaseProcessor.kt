package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATile
import block.customizableCrafter.tile.LATiles
import element.Elements

class PhaseProcessor: Processor() {

    override fun process(tiles: LATiles) {
        super.process(tiles)
        for(tile in tiles.array) {
            checkPhaseChange(tile)
        }
    }

    fun checkPhaseChange(tile: LATile){
        if(tile.acted || tile.es.element==Elements.vacuum){
            return
        }
        tile.es.apply {
            if(element.phaseUpTemp[phase]<temperature){
                phase ++
                tile.acted = true
            }
            if(element.phaseDownTemp[phase]>temperature){
                phase --
                tile.acted = true
            }
        }
    }
}