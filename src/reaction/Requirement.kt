package reaction

import block.customizableCrafter.tile.LATile

class Requirement {

    /**是否能在该格发生*/
    var onTile : (LATile) -> Boolean = { false }

    /**该格元素是否满足条件*/
    var checkTile : (LATile) -> Boolean = { false }


}