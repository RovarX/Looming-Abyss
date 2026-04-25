package block.customizableCrafter.assist

import arc.graphics.g2d.Draw
import element.Element
import element.Elements
import element.Phase

/**pack class*/
class ElementState {
    var element: Element = Elements.vacuum
    var mass: Double = -1.0
    var heat: Double = -1.0
    var temperature: Double = -1.0
    var phase = Phase.vacuum
    var area : ElementArea? = null
    var isAreaEdge = false
    var changed = false

    var isStoredInTileData = false
    /**Base on [element], two of [mass],[heat],[temperature], fill the last one*/
    fun autoFill(){
        if(element===Elements.vacuum){
            toNull()
            return
        }

        if(mass<0.0){
            mass = heat * temperature /element.heatCapacity
        }
        else if(heat<0.0){
            heat = mass * element.heatCapacity * temperature
        }
        else if(temperature < 0.0){
            temperature = heat / mass / element.heatCapacity
        }

        //TODO:Phase自动计算
        phase = Phase.liquid
    }

    fun copyFrom(es: ElementState) {
        element = es.element
        mass = es.mass
        heat = es.heat
        temperature = es.temperature
        phase = es.phase
    }

    fun toNull(){
        element = Elements.vacuum
        mass = -1.0
        heat = -1.0
        temperature = -1.0
        phase = Phase.vacuum
    }

    fun addMass(delta: Double){
        addMH(delta,0.0)
    }

    fun addHeat(delta: Double){
        addMH(0.0,delta)
    }

    fun addMH(deltaM: Double, deltaH: Double){
        mass += deltaM
        heat += deltaH
        refreshTemp()
        changed = true
    }
    fun refreshTemp(){
        if(element===Elements.vacuum || mass<=0.0){
            temperature = -1.0
        }
        else{
            temperature = heat / mass / element.heatCapacity
        }
    }

    fun drawElement(x:Float,y:Float,zoom:Float){
        if(element===Elements.vacuum) return
        val region = element.drawRegion[Phase.liquid]
        Draw.rect(region,x,y,region.width*zoom,region.height*zoom)
    }
}