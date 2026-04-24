package element


object Elements {

    /**The placeholder element*/
    val vacuum = Element("vacuum",0)

    var H2O = vacuum
    var Fe = vacuum
    var Al = vacuum
    var Al_alloy = vacuum

    val all = arrayOf(vacuum,H2O)

    fun load(){

        H2O = Element("H2O",1).apply {
            heatCapacity = 4.18
            minMass = 0.001
            flowability = arrayOf(0.0,0.0,0.8,1.0)
        }

        Fe = Element("Fe",2).apply {
            heatCapacity = 0.45
            minMass = 0.01
            flowability = arrayOf(0.0,0.5,0.7,1.0)
        }

        Al = Element("Al",3).apply {
            heatCapacity = 0.9
            minMass = 0.01
            flowability = arrayOf(0.0,0.5,0.7,1.0)
        }

        Al_alloy = Element("Al_alloy",4).apply {
            heatCapacity = 0.8
            minMass = 0.01
            flowability = arrayOf(0.0,0.5,0.7,1.0)
        }

    }
}