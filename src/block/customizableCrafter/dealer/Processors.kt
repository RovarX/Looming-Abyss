package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles

object Processors {

    val flow = FlowProcessor()
    val push = PushProcessor()
    val react = ReactProcessor()

    val normProcessors : Array<out Processor> = arrayOf(
        react,
        flow
    )

    fun normUpdate(tiles: LATiles){
        normProcessors.forEach {
            it.process(tiles)
        }
    }
}