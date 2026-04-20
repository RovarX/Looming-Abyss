package element


object Elements {

    /**The placeholder element*/
    val vacuum = Element("vacuum",0)

    var H2O = vacuum

    val all = arrayOf(vacuum,H2O)

    fun load(){

        H2O = Element("H2O",1).apply {
            heatCapacity = 4.18
            minMass = 0.001
            flowability = arrayOf(0.0,0.8,1.0)
        }


    }
}