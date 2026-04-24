package block.customizableCrafter.assist

import block.customizableCrafter.tile.LATile
import block.customizableCrafter.tile.LATiles
import element.Elements

class ElementArea(
    val tiles: LATiles,
    val id:Int
) {

    val areaTiles = HashSet<LATile>()

    val nearTiles = HashSet<LATile>()

    var element = Elements.vacuum

    /**clean its data*/
    fun clear(){
        areaTiles.clear()
        nearTiles.clear()
        element = Elements.vacuum
    }

    fun addTile(tile : LATile){
        areaTiles.add(tile)
        nearTiles.remove(tile)
        tile.es.area = this
        var isEdge = false
        for(nearTile in tile.getNearTiles()){
            
            if(nearTile==null){
                continue
            }
            if(nearTile.es.area === this){

                //if is edge, check whether it's still edge
                if(nearTile.es.isAreaEdge){
                    var stillEdge = false
                    for(nearNearTile in nearTile.getNearTiles()){
                        if(nearNearTile==null){
                            continue
                        }
                        if(nearTile.es.area!==this){
                            stillEdge = true
                            break
                        }
                    }
                    nearTile.es.isAreaEdge=stillEdge
                }
            }

            //if near tile has same element, combine them
            else if(nearTile.es.element === element ){

            }

            else{
                isEdge=true
                nearTiles.add(nearTile)
            }
        }
        tile.es.isAreaEdge=isEdge
    }

    fun removeTile(tile:LATile){
        areaTiles.remove(tile)
        tile.es.isAreaEdge = false
        tile.es.area = null

        var cnt=0
        for(nearTile in tile.getNearTiles()){
            if(nearTile==null){
                continue
            }
            if(nearTile.es.area===this){
                cnt++
                nearTile.es.isAreaEdge=true
                nearTiles.add(tile)
            }

            if(nearTile.es.element !== element){
                var stillNear = false
                for(nearNearTile in nearTile.getNearTiles()){
                    if(nearNearTile==null){
                        continue
                    }
                    if(nearNearTile.es.area===this){
                        stillNear = true
                        break
                    }
                }
                if(!stillNear){
                    nearTiles.remove(nearTile)
                }
            }
        }
        if(cnt>=2){
            breakArea()
        }
    }

    /**build from a tile automatically*/
    fun autoBuildArea(tile: LATile){
        areaTiles.add(tile)
        tile.es.area = this
        for(nearTile in tile.getNearTiles()){
            if(nearTile==null||nearTile.es.area===this){
                continue
            }
            if(nearTile.es.element === element){
                autoBuildArea(nearTile)
            }
            else{
                nearTiles.add(nearTile)
            }
        }
    }

    fun breakArea(){

    }
}