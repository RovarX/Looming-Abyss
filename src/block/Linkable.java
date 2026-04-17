package block;

import arc.struct.ObjectSet;
import arc.util.Nullable;
import mindustry.gen.Building;

public interface Linkable {
    public ObjectSet<Building> linkFrom = new ObjectSet<>();
    @Nullable
    public Building LinkTo();
}
