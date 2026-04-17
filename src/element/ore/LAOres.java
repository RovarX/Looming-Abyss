package element.ore;

import element.LAItems;
import mindustry.world.blocks.environment.OreBlock;

public class LAOres {
    public static OreBlock oreIron;
    public static OreBlock oreAluminum;
    public static OreBlock oreQuartzSand;
    public static void load(){
        oreIron = new OreBlock("ore-iron", LAItems.iron);
        oreAluminum = new OreBlock("ore-aluminum", LAItems.aluminum);
        oreQuartzSand = new OreBlock("ore-quart-sand", LAItems.quartzSand);
    }
}
