package element

import arc.graphics.g2d.TextureRegion
import utility.CT

/**标记作为元素一种相*/
interface PhaseType {
    /**所属元素*/
    val element: Element
    /**形态名*/
    val phaseName: String
    /**在自定工厂里绘制的贴图*/
    val drawRegion : TextureRegion
}