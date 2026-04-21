package element

import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import mindustry.type.Liquid
import utility.CT

class Liquid(
    name: String,
    color: Color,
    override val element: Element,
) : Liquid(name,color),
    PhaseType {

    override val phaseName: String = name
    override val drawRegion: TextureRegion = CT.getRegion(name +"_region")

    init{
        element.phaseTypes[Phase.liquid] = this
    }

}