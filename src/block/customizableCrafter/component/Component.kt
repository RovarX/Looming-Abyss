package block.customizableCrafter.component

import block.customizableCrafter.tile.LATiles
import mindustry.gen.Building
import mindustry.world.Block


open class Component(name: String): Block(name) {



    open class ComponentBuild: Building() {

        var tx = 0
        var ty = 0
        var rotate = 0
        lateinit var tiles : LATiles

        fun latile() = if(::tiles.isInitialized) tiles.getTile(tx, ty) else null

        override fun update(){
            if(::tiles.isInitialized){
                latile()?.componentBuild = this
            }
            updatec()
        }

        open fun updatec(){

        }
    }
}