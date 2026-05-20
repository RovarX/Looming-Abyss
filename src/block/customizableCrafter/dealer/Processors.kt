package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles
import element.Phase

object Processors {

    val build = BuildProcessor()
    val phase= PhaseProcessor()
    val flow = FlowProcessor()
    val push = PushProcessor()
    val react = ReactProcessor()
    val heat = HeatConductionProcessor()

    val normProcessors : Array<out Processor> = arrayOf(
        react,
        phase,
        flow,
        heat
    )

    fun normUpdate(tiles: LATiles){
        normProcessors.forEach {
            it.process(tiles)
        }
    }
}