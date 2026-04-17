package block.costomizableCrafter.dealer

import block.costomizableCrafter.tile.LATiles

open class Dealer() {

    var ftiles: LATiles?=null

    open fun update(tiles: LATiles){
        this.ftiles = tiles
    }

}