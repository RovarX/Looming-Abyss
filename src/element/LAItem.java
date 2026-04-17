package element;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.util.Nullable;
import mindustry.type.Item;

public class LAItem extends Item {
    /** 所属元素 */
    public Element element;

    public LAItem(String name, Color color, Element element) {
        super(name, color);
        this.element = element;
    }
}
