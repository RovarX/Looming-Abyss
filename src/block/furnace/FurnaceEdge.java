package block.furnace;

import arc.struct.EnumSet;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;

public class FurnaceEdge extends Block {

    public FurnaceEdge(String name) {
        super(name);
        this.update = true;
        this.flags = EnumSet.<BlockFlag>of(BlockFlag.factory);
        this.category = Category.crafting;
    }
    public class FurnaceEdgeBuild extends Building {
        public boolean isFurnaceValid = false;
        
    }
}
