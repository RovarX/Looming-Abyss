package block

import block.customizableCrafter.base.CrafterBase
import block.customizableCrafter.base.CrafterFloor

object LABlocks {

    lateinit var crafterBase: CrafterBase
    lateinit var crafterFloor: CrafterFloor
    
    fun load(){
        crafterBase = CrafterBase("crafter-base").apply{
            size =3
            innerSize = 8
        }
        crafterFloor = CrafterFloor("crafter-floor").apply {
            size = 1
        }
    }

}