package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles
import element.Phase

object Processors {

    val phase= PhaseProcessor()
    val flow = FlowProcessor()
    val push = PushProcessor()
    val react = ReactProcessor()

    val normProcessors : Array<out Processor> = arrayOf(
        react,
        phase,
        flow
    )

    fun normUpdate(tiles: LATiles){
        normProcessors.forEach {
            it.process(tiles)
        }
    }
}