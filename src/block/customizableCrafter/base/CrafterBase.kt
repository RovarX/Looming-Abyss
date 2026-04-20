package block.customizableCrafter.base

import arc.math.geom.Vec2
import arc.scene.ui.layout.Table
import arc.util.Time
import block.customizableCrafter.tile.LATiles
import mindustry.gen.Icon
import mindustry.type.Category
import mindustry.ui.Styles
import mindustry.world.blocks.production.GenericCrafter
import ui.uis

class CrafterBase(
    name:String?
): GenericCrafter(name) {

    var innerSize =0

    init {
        this.rotate = true
        this.update = true
        this.configurable = true
        //this.saveConfig = true;
        this.category = Category.crafting
    }

    override fun load() {
        super.load()
    }

    inner class CrafterBaseBuild : GenericCrafterBuild() {

        var innerSize: Int = this@CrafterBase.innerSize

        var innerTiles: LATiles = LATiles(innerSize, innerSize)


        var showInner: Boolean = false

        var isChanging: Boolean = true

        // 基于游戏时间增量的累积计时器，约每 60 tick ≈ 1 秒触发
        private var innerUpdateCounter = 0f


        override fun updateTile() {
            innerUpdateCounter += Time.delta
            if (innerUpdateCounter >= 60f) {
                innerUpdateCounter -= 60f
                innerTiles.update()
            }

        }

        override fun buildConfiguration(table: Table) {
            table.button(Icon.info, Styles.cleari, 40f, Runnable {
                uis.customize.show(innerTiles)
            })
        }

        override fun draw() {
            super.draw()
            if (this.showInner) {
                if (isChanging) {
                    isChanging = false
                }
            } else if (isChanging) {
                isChanging = false
            }
        }

        /**定位旋转后建筑位置  */
        fun rotatePos(x: Int, y: Int, size: Int, delta: Int): Vec2 {
            val res = Vec2()
            when (delta % 4) {
                0 -> {
                    res.x = x.toFloat()
                    res.y = y.toFloat()
                }

                1 -> {
                    res.x = (this.innerSize - y - size).toFloat()
                    res.y = x.toFloat()
                }

                2 -> {
                    res.x = (this.innerSize - x - size).toFloat()
                    res.y = (this.innerSize - y - size).toFloat()
                }

                3 -> {
                    res.x = y.toFloat()
                    res.y = (this.innerSize - x - size).toFloat()
                }
            }
            return res
        }
    }
}