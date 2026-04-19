package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles

abstract class Processor {

    /**the tiles to process*/
    lateinit var tiles: LATiles

    /**process the tiles*/
    open fun process(tiles:LATiles){
        this.tiles=tiles
    }

}