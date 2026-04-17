package block.costomizableCrafter.tile

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Draw.draw
import block.LABlocks
import block.costomizableCrafter.assist.ElementArea
import block.costomizableCrafter.assist.FlowData
import block.costomizableCrafter.base.CrafterFloor
import block.costomizableCrafter.component.CrafterComponent
import element.Element
import element.Phase

class LATile(val x:Int,val y:Int,val tiles: LATiles){


    val floor: CrafterFloor = LABlocks.crafterFloor
    var component: CrafterComponent? = null
    var elementArea: ElementArea?=null
    var element: Element?=null

    var temperature : Float?=null
    var heat : Double=0.0
    var mass : Double=0.0

    var isEdge = false
    var isShown = true
    var hasComponent = false
    var acted = false

    var flowable = true

    var phase = Phase.vacuum

    val flowData = FlowData()

    fun setState(type:Int){
        when(type){
            0 -> {
            }
            1 -> {
                isEdge=true
                isShown=false
            }
        }
    }

    fun drawTile(x: Float,y: Float,zoom: Float){
        floor.drawFloor(x,y,zoom)
        element?.let{
            Draw.rect(it.getRegion(phase), x, y, 8f*zoom, 8f*zoom)
        }
    }

    fun applyFrom(es: ES){
        element = es.element
        mass = es.mass
        heat = es.heat
        refreshTemperature()
    }

    fun refreshTemperature(){
        if(mass==0.0||element==null){
            temperature = null
        }
        else{
            temperature = element?.heatCapacity?.let { (heat/mass/it - 273.15).toFloat() }
        }
    }

    fun addMass(delta: Double){
        mass+=delta
        clampMass()
        refreshTemperature()
    }

    fun addHeat(delta: Double){
        heat+=delta
        clampHeat()
        refreshTemperature()
    }

    fun addMH(deltaM: Double, deltaH: Double){
        mass+=deltaM
        heat+=deltaH
        clampMass()
        clampHeat()
        refreshTemperature()
    }

    fun getNearTile(d:Int): LATile? {
        return tiles.getInnerTile(this, d)
    }

    fun getNearTiles(): List<LATile>{
        return tiles.getNearTiles(this)
    }

    fun canFlow(): Boolean{
        return element!==null && phase>=Phase.liquid && !isEdge && flowable
    }

    fun clampMass(){
        element?.minMass[phase]?.let {
            if(mass<it){
                cleanElement()
            }
        }
    }

    fun clampHeat(){
        if(heat<0.0){
            heat=0.0
            refreshTemperature()
        }
    }

    fun cleanElement(){
        element = null
        mass = 0.0
        heat = 0.0
        temperature=null
        phase = Phase.vacuum
    }

    /**封装一个格子的元素状态*/
    class ES{
        var element: Element?=null
        var mass: Double=0.0
        var heat: Double=0.0
        var temperature: Float?=null

        fun autoFill(){
            if(heat==0.0){
                heat=element?.heatCapacity?.let { mass*it*(temperature!!+273.15) } ?: 0.0
            }
        }
    }
}