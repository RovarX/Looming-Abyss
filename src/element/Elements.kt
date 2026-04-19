package element

import element.Elements.vacuum


object Elements {

    /**The placeholder element*/
    val vacuum = Element("vacuum",0)

    var H2O : Element = vacuum

    fun load(){

        H2O = Element("H2O",1).apply {
            heatCapacity = 4.18
            minMass = 0.001
        }


    }
}