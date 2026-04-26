package block.customizableCrafter.tile

import block.customizableCrafter.assist.ElementArea
import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.dealer.Processors
import utility.CT

class LATiles(
    val width: Int,
    val height: Int
) {
    /**the width with boarder*/
    val totalWidth = width+2
    /**the height with boarder*/
    val totalHeight = height+2
    
    val array = Array<LATile>(totalWidth*totalHeight){ i->
        val x = i%totalWidth
        val y = i/totalWidth
        LATile(x,y,this).also{
            if(x==0||y==0||x==totalWidth-1||y==totalHeight-1){
                it.isEdge=true
                it.isShown=false
            }
        }
    }

    /**the element areas*/
    val areas = HashSet<ElementArea>()

    /**free element areas*/
    val freeAreas = ArrayDeque<ElementArea>()

    /**current max area id -1*/
    var curAreaID = 0

    fun update(){
        array.forEach {
            it.acted = false
            it.es.changed = false
        }
        Processors.normUpdate(this)
    }

    /**apply an element state to a tile*/
    fun applyESTo(index:Int,es: ElementState){
        val tile = getTile(index) ?: return

        tile.es.copyFrom(es)

        setTileToArea(tile)
    }

    fun setTileToArea(tile: LATile){
        for(nearTile in getNearTiles(tile)){
            if(nearTile==null){
                continue
            }
            val nearArea = nearTile.es.area
            if(nearTile.es.element === tile.es.element && nearArea != null){
                nearArea.addTile(tile)
                return
            }
        }

        createArea().also {
            it.element = tile.es.element
            it.addTile(tile)
        }
    }

    fun getTile(tile:LATile,d:Int):LATile?{
        return getTile(tile.x+CT.dir[d][0],tile.y+CT.dir[d][1])
    }

    fun getTile(x:Int,y:Int):LATile?{
        if(x<0||x>=totalWidth||y<0||y>=totalHeight){
            return null
        }
        return array[y*totalWidth+x]
    }

    fun getTile(index: Int,d:Int, placeHolder:Any):LATile?{
        val tile = getTile(index)
        if(tile==null){
            return null
        }
        return getTile(tile,d)
    }

    /**return the tile at index*/
    fun getTile(index:Int):LATile?{
        if(index<0||index>=array.size){
            return null
        }
        return array[index]
    }

    /**return the near tiles*/
    fun getNearTiles(tile:LATile):Array<LATile?>{
        return Array<LATile?>(4){i->
            getTile(tile,i)
        }
    }

    fun getNearTiles(index:Int):Array<LATile?>{
        return Array<LATile?>(4){i->
            getTile(index,i,0)
        }
    }

    /**return a new element area that can be used*/
    fun createArea():ElementArea{
        return if(freeAreas.isEmpty()){
            ElementArea(this,curAreaID).also {
                areas.add(it)
                curAreaID ++
            }
        } else{
            freeAreas.removeFirst().also {
                areas.add(it)
            }
        }
    }

    /**delete an area*/
    fun deleteArea(area:ElementArea){
        area.clear()
        areas.remove(area)
        freeAreas.addLast(area)
    }
}