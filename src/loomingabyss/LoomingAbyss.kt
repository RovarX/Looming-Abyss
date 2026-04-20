package loomingabyss

import arc.Events
import arc.func.Cons
import arc.util.Time
import block.LABlocks
import element.Elements
import element.Liquids
import mindustry.game.EventType
import mindustry.mod.Mod
import mindustry.ui.dialogs.BaseDialog
import utility.CT

class LoomingAbyss : Mod() {

    init {


        Events.on(
            EventType.ClientLoadEvent::class.java,
            Cons { e: EventType.ClientLoadEvent? ->
                // show dialog upon startup
                Time.runTask(10f, Runnable {
                    val dialog = BaseDialog("frog")
                    dialog.cont.add("behold").row()
                    dialog.cont.image(CT.getRegion("floor-1")).pad(20f).row()
                    dialog.cont.button("I see", Runnable { dialog.hide() }).size(100f, 50f)
                    dialog.show()
                })
            })
    }

    override fun loadContent() {
        Elements.load()
        Liquids.load()
        LABlocks.load()
    }
}