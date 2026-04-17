package block.craftor;

import block.Linkable;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.gen.Building;

public class PhaseTower extends GenericCrafter implements Linkable {
    public PhaseTower(String name) {
        super(name);
    }

    @Override
    public Building LinkTo() {
        return null;
    }

    public class PhaseTowerBuild extends GenericCrafterBuild {

    }
}
