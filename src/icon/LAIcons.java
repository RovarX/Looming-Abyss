package icon;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;

public class LAIcons {
    public static TextureRegionDrawable selectIcon;

    public static void load() {
        selectIcon = new TextureRegionDrawable(Core.atlas.find("select-icon"));
    }
    
}
