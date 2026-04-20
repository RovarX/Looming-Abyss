package block.customizableCrafter.base

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import mindustry.world.blocks.environment.Floor
import utility.CT

class CrafterFloor(name: String) : Floor(name) {

    lateinit var floorRegion: TextureRegion

    override fun load() {
        super.load()
        this.floorRegion = CT.getRegion(name)
    }

    fun drawFloor(x: Float, y: Float, zoom: Float) {
        Draw.rect(floorRegion, x, y, floorRegion.width * zoom, floorRegion.height * zoom)
    }
}