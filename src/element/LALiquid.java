package element;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class LALiquid extends Liquid {
    /** 所属元素 */
    public Element element;

    public LALiquid(String name, Color color, Element element) {
        super(name, color);
        this.element = element;
        this.element.liquidType = this;
    }

}
