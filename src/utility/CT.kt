package utility

import arc.Core
import arc.graphics.g2d.TextureRegion

/**an object contains some constant for this mod*/
object CT {
    val modName = "looming-abyss"

    /**direction array*/
    val dir = arrayOf(
        intArrayOf(0,1),
        intArrayOf(1,0),
        intArrayOf(0,-1),
        intArrayOf(-1,0)
    )

    /**The more convenient way to get a texture region*/
    fun getRegion(s:String): TextureRegion{
        return Core.atlas.find("$modName-$s")
    }
}