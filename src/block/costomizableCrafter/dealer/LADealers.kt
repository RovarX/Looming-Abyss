package block.costomizableCrafter.dealer

import block.costomizableCrafter.tile.LATiles

object LADealers {

    val flowDealer = FlowDealer()
    val heatDealer = HeatDealer()
    val interactDealer = InteractDealer()
    val reactDealer = ReactDealer()

    val commonDealers :Array<Dealer> = arrayOf(flowDealer, heatDealer, interactDealer, reactDealer)


    fun dealWith(tiles: LATiles){
        for(dealer in commonDealers){
            dealer.update(tiles)
        }
    }
}