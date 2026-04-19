package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles

class FlowProcessor:Processor() {

    override fun process(tiles: LATiles) {
        super.process(tiles)

        cleanFlowData()
    }

    fun cleanFlowData(){

    }
}