package block.costomizableCrafter.base

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import mindustry.world.blocks.environment.Floor

open class CrafterFloor(name: String) : Floor(name) {
    private var floorRegion: TextureRegion? = null

    override fun load() {
        super.load()
        this.floorRegion = Core.atlas.find(this.name)
    }

    fun drawFloor(x: Float, y: Float, zoom: Float) {
        val region = this.floorRegion ?: return
        Draw.rect(region, x, y, region.width * zoom, region.height * zoom)
    }
}