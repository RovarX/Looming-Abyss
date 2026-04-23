package block.customizableCrafter.tile

import block.customizableCrafter.assist.ElementArea
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
        array.forEach { it.acted = false }
        Processors.normUpdate(this)
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

    /**return a new element area that can be used*/
    fun createArea():ElementArea{
        if(freeAreas.isEmpty()){
            return ElementArea(this,curAreaID).also {
                areas.add(it)
                curAreaID ++
            }
        }
        else{
            return freeAreas.removeFirst().also {
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