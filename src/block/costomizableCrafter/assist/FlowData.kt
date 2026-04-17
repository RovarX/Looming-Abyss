package block.costomizableCrafter.assist

class FlowData {
    var canFlowing: Boolean = true
    var canFlowed: Boolean = true
    var isFlowing: Boolean = false
    var flowingTo: BooleanArray = BooleanArray(4)
    var flowingCount: Int = 0
    var isFlowed: Boolean = false
    var flowedFrom: BooleanArray = BooleanArray(4)
    var flowedCount: Int = 0
    var massDelta: Double = 0.0
    var heatDelta: Double = 0.0

    fun reset() {
        canFlowing = true
        canFlowed = true
        isFlowing = false
        flowingTo = BooleanArray(4)
        flowingCount = 0
        isFlowed = false
        flowedFrom = BooleanArray(4)
        flowedCount = 0
    }
}

