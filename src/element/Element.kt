package element

import arc.graphics.g2d.TextureRegion
import utility.CT

class Element(
    /**元素名称*/
    val name:String,
    /**元素索引序号*/
    val id:Int
){
    /**升相变温度,单位K*/
    var phaseUpTemp : Array<Double> = emptyArray()
    /**降相变温度,单位K*/
    var phaseDownTemp : Array<Double> = emptyArray()
    /**各相对应的形态*/
    var phaseTypes: Array<PhaseType?> = arrayOfNulls(maxPhases)
    /**比热容*/
    var heatCapacity: Double = 0.0
    /**最小允许质量*/
    var minMass: Double = 0.0

    var flowability : Array<Double> = emptyArray()

    val drawRegion by lazy {Array<TextureRegion>(maxPhases){i-> CT.getRegion("$name-$i")}}

    companion object{
        /**最大形态数*/
        val maxPhases = 4
        /**各形态名称*/
        val phaseNames = arrayOf("@vacuum","@solid","@liquid","@gas")
    }

}