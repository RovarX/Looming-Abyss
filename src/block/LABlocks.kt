package block

import block.customizableCrafter.base.CrafterBase

object LABlocks {

    lateinit var crafterBase: CrafterBase

    fun load(){
        crafterBase = CrafterBase("crafter-base").apply{
            size =3
            innerSize = 8
        }
    }

}