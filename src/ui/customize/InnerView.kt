package block.costomizableCrafter.ui

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.graphics.g2d.ScissorStack
import arc.input.KeyCode
import arc.math.Mathf
import arc.math.geom.Rect
import arc.scene.Element
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.event.Touchable
import arc.util.Time
import block.customizableCrafter.tile.LATiles
import mindustry.input.Binding
import ui.customize.FlowDialog
import kotlin.math.floor
import kotlin.math.min


class InnerView() : FlowDialog("@view"){

    var tiles: LATiles? = null

    /**边框粗细  */
    var edgeThickness: Float = 5f

    /**缩放比例  */
    var zoom: Float = 1f

    /**X轴偏移  */
    var offsetX: Float = 0f

    /**Y轴偏移  */
    var offsetY: Float = 0f

    /**原始X轴偏移  */
    var originalX: Float = 0f

    /**原始Y轴偏移  */
    var originalY: Float = 0f

    var mousex: Float = 0f

    var mousey: Float = 0f

    var mouseOnTile = -1
    var applyOnTile = false
    
    init{

        shouldPack = false
        this.touchable = Touchable.enabled
        resize()
        initArea()

        this.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Element?) {
                Core.scene.setScrollFocus(this@InnerView)
            }

            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                this@InnerView.mousex = x
                this@InnerView.mousey = y
                return true
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
                this@InnerView.mousex = x
                this@InnerView.mousey = y

                Core.scene.setScrollFocus(this@InnerView)

                val debugPanel = this@InnerView.dialog.debugPanel
                if (debugPanel.applying) {
                    applyOnTile = true
                }

                return true
            }
        })
    }

    override fun draw() {
        super.draw()
        drawEdge()
        drawInner()
    }

    /**绘制边框  */
    fun drawEdge() {
        Draw.color(Color.orange)
        Lines.stroke(edgeThickness)
        val inset = edgeThickness / 2f
        Lines.rect(x + inset, y + inset, width - edgeThickness, height - edgeThickness)
        Draw.reset()
    }

    fun setViewOf(tiles: LATiles){
        this.tiles = tiles
        resize()
        refresh()
    }

    fun drawInner() {
        val tiles = this.tiles ?: return
        val clipX = x + edgeThickness
        val clipY = y + edgeThickness
        val clipW = width - edgeThickness * 2f
        val clipH = height - edgeThickness * 2f

        val local = Rect(clipX, clipY, clipW, clipH)
        val scissor = Rect()
        Core.scene.calculateScissors(local, scissor)
        if (ScissorStack.push(scissor)) {
            for (tile in tiles.array) {
                if(!tile.isShown){
                    continue
                }
                val tileX: Float = x + originalX + offsetX + (tile.x * 32f * zoom)
                val tileY: Float = y + originalY + offsetY + (tile.y * 32f * zoom)
                tile.drawTile(tileX, tileY, zoom)
            }
            ScissorStack.pop()
        }
    }

    /**自动设置区域大小  */
    fun resize() {
        val edgeLength = min(0.75f * Core.scene.height, 0.66f * Core.scene.width)
        this.setSize(edgeLength, edgeLength)
    }

    /**初始化位置  */
    fun initArea() {
        refreshOrigin()
        centralize()
    }

    /**刷新原点  */
    fun refreshOrigin() {
        val tiles = this.tiles ?: return
        val tileSize = 32f * zoom
        val halfSize = tileSize / 2f
        this.originalX = (this.width - tiles.width * tileSize) / 2f + halfSize
        this.originalY = (this.height - tiles.height * tileSize) / 2f + halfSize
    }

    fun centralize() {
        this.offsetX = 0f
        this.offsetY = 0f
    }

    fun clampZoom() {
        this.zoom = Mathf.clamp(this.zoom, 0.3f, 5f)
    }

    fun clampOffset() {
        val minX = -1f * (this.width - this.originalX) + 10f
        val maxX = -1f * minX
        val minY = -1f * (this.height - this.originalY) + 10f
        val maxY = -1f * minY
        this.offsetX = Mathf.clamp(this.offsetX, minX, maxX)
        this.offsetY = Mathf.clamp(this.offsetY, minY, maxY)
    }




    override fun act(delta: Float) {
        super.act(delta)

        getMouseIn()

        if (Core.scene.keyboardFocus == null || !Core.scene.hasField() && !Core.input.keyDown(KeyCode.controlLeft)) {
            val ax = Core.input.axis(Binding.moveX)
            val ay = Core.input.axis(Binding.moveY)
            this.offsetX += ax * 15.0f * Time.delta / this.zoom
            this.offsetY += ay * 15.0f * Time.delta / this.zoom
            this.clampOffset()
        }

        if (Core.scene.getScrollFocus() === this) {
            this.zoom += Core.input.axis(Binding.zoom) / 10.0f * this.zoom
            this.clampZoom()
            this.refreshOrigin()
            this.clampOffset()
        }
        
        if(applyOnTile&&tiles!=null){
            val debugPanel = this.dialog.debugPanel
            debugPanel.applyInputValues()


            if (mouseOnTile >= 0 && mouseOnTile < tiles!!.array.size) {
                tiles?.applyESTo(mouseOnTile, debugPanel.es)
            }

            // Keep wheel zoom available right after applying onto a tile.
            Core.scene.setScrollFocus(this)
            applyOnTile = false
        }

    }

    /**根据tiles刷新视图*/
    fun refresh(){
        refreshOrigin()
        centralize()
    }

    /**更新mouseOnTile*/
    fun getMouseIn(){

        mouseOnTile =-1
        
        val tiles = this.tiles ?: return

        // touchDown / mouseMoved give local coordinates in this table.
        val localX = this.mousex
        val localY = this.mousey

        // Ignore border frame area.
        if (localX < edgeThickness || localX > width - edgeThickness || localY < edgeThickness || localY > height - edgeThickness) {
            return
        }

        val tileSize = 32f * zoom
        if (tileSize <= 0f) return

        val gridLeft = originalX + offsetX - tileSize / 2f
        val gridBottom = originalY + offsetY - tileSize / 2f

        val tx = floor(((localX - gridLeft) / tileSize).toDouble()).toInt()
        val ty = floor(((localY - gridBottom) / tileSize).toDouble()).toInt()

        val widthWithBorder = tiles.width + 2
        val heightWithBorder = tiles.height + 2

        if (tx !in 0 until widthWithBorder || ty !in 0 until heightWithBorder) {
            return
        }

        mouseOnTile = ty * widthWithBorder + tx
    }
}