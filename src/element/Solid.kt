package element

import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import mindustry.type.Item
import utility.CT

class Solid(
    name: String,
    color: Color,
    override val element: Element,
) : Item(name,color), 
    PhaseType {

    override val phaseName: String = name
    override val drawRegion: TextureRegion = CT.getRegion("$name-drawRegion")

    init{
       element.phaseTypes[Phase.solid] = this
    }

}