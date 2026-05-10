package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATiles
import reaction.Reactions

class ReactProcessor : Processor() {


    override fun process(tiles: LATiles){
        super.process(tiles)
        checkTiles()
    }

    fun checkTiles(){
        for(tile in tiles.array){
            if(!tile.canReact()||tile.acted){
                continue
            }
            for(reaction in Reactions.allByBase[tile.es.element.id]){
                if(reaction.requirement.onTile(tile)){
                    reaction.doReact.reactOnTile(tile)
                    tile.acted = true
                    break
                }
            }
        }
    }
}