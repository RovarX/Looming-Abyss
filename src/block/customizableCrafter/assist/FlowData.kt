package block.customizableCrafter.assist

class FlowData {
    /**is able to flow to other tile*/
    var canFlowing: Boolean = true
    /**is able to be flowed to*/
    var canFlowed: Boolean = true
    /**is flowing to other tile*/
    var isFlowing: Boolean = false
    /**flow to where*/
    var flowingTo: BooleanArray = BooleanArray(4)
    /**flow to how many tiles*/
    var flowingCount: Int = 0
    /**is flowed to by other tile*/
    var isFlowed: Boolean = false
    /**where*/
    var flowedFrom: BooleanArray = BooleanArray(4)
    var flowedCount: Int = 0
    var massDelta: Double = 0.0
    var heatDelta: Double = 0.0

    fun reset(type: Int) {
        canFlowing = true
        canFlowed = true
        isFlowing = false
        flowingTo = BooleanArray(4)
        flowingCount = 0
        isFlowed = false
        flowedFrom = BooleanArray(4)
        flowedCount = 0
        massDelta = 0.0
        heatDelta = 0.0
    }

    fun reset(){
        reset(0)
    }
}