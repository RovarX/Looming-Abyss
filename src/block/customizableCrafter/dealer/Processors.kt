package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles

object Processors {
    val flow = FlowProcessor()

    val normProcessors : Array<out Processor> = arrayOf(
        flow
    )

    fun normUpdate(tiles: LATiles){
        normProcessors.forEach {
            it.process(tiles)
        }
    }
}