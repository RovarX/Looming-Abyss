package element;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import mindustry.type.Liquid;

public class LAGas extends Liquid{

    public Element element;


    public LAGas(String name, Color color, Element element) {
        super(name, color);
        this.element = element;
    }
}
