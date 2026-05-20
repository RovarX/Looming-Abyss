package statusEffect

import mindustry.type.StatusEffect

object LASE {
    lateinit var whenEdit: StatusEffect

    fun load(){
        whenEdit = StatusEffect("when-edit").apply {
            speedMultiplier = 0.0f
            disarm = true
        }
    }
}