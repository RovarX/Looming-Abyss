package element

import arc.graphics.Color

object Liquids {

    val vacuum =
        Liquid("vacuum",
            Color.valueOf("000000"),
            Elements.vacuum)

    var water = vacuum

    fun load(){
        water =
            Liquid("water",
                Color.valueOf("3f76e4"),
                Elements.H2O)
    }

}