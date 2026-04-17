package block.furnace;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.struct.EnumSet;
import arc.struct.ObjectSet;
import block.furnace.FurnaceEdge.FurnaceEdgeBuild;
import block.furnace.FurnaceFloor.FurnaceFloorBuild;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.world.meta.BlockFlag;

public class FurnacePivot extends FurnaceEdge {

    public TextureRegion pivotBase;
    public TextureRegion pivotTop;

    public FurnacePivot(String name) {
        super(name);
        this.rotate = true;
        this.flags = EnumSet.<BlockFlag>of(BlockFlag.factory);
        this.sync = true;
        this.category = Category.crafting;
    }

    @Override
    public void load() {
        super.load();
        pivotTop = Core.atlas.find(this.name + "-top");
    }
    
    public class FurnacePivotBuild extends FurnaceEdgeBuild {
        /** 是否需要更新熔炉 */
        public boolean updateFurnace = true;
        /** 熔炉合法 */
        public boolean FurnaceValid = false;
        /** 熔炉大小 */
        public int furnaceSize = 0;
        /** 熔炉边界方块 */
        public ObjectSet<FurnaceEdgeBuild> edges = new ObjectSet<>();
        /** 熔炉地板方块 */
        public ObjectSet<FurnaceFloorBuild> floors = new ObjectSet<>();
        /** 直连地板 */
        public FurnaceFloorBuild directLinkedFloor = null;

        @Override
        public void updateTile() {
            haveToUpdate();
            if (updateFurnace || !FurnaceValid) {
                if (this.block != null && this.tile != null) {
                    directLinkedFloor = this.front() instanceof FurnaceFloorBuild
                            ? (FurnaceFloorBuild) this.front()
                            : null;
                    findFloors();
                    findEdges();
                    setValid();
                } else {
                    this.directLinkedFloor = null;
                }
            }
        }

        public void haveToUpdate() {
            if(this.directLinkedFloor == null){
                    updateFurnace = true;
            }
            for (FurnaceEdgeBuild edge : edges) {
                if (edge.tile.build != (Building) edge) {
                    updateFurnace = true;
                }
            }
            for (FurnaceFloorBuild floor : floors) {
                if (floor.tile.build != (Building) floor) {
                    updateFurnace = true;
                }
            }
        }
        
        public void findFloors() {
            floors.clear();
            furnaceSize = 0;
            if (directLinkedFloor != null) {
                floors.add(directLinkedFloor);
                findNearFloors(directLinkedFloor);
            }
        }

        public void findNearFloors(Building floor) {

            for (int dir = 0; dir < 4; dir++) {
                FurnaceFloorBuild thisFloor = floor.nearby(dir) instanceof FurnaceFloorBuild
                        ? (FurnaceFloorBuild) floor.nearby(dir)
                        : null;
                if (thisFloor != null && !floors.contains(thisFloor)) {
                    floors.add(thisFloor);
                    furnaceSize++;
                    findNearFloors(thisFloor);
                }
            }
        }

        public void findEdges() {
            edges.clear();
            boolean findNotValid = false;
            for (Building floor : floors) {
                for (int dir = 0; dir < 4; dir++) {
                    if ((floor.nearby(dir) instanceof FurnaceEdgeBuild) && floor.nearby(dir) != null) {
                        edges.add((FurnaceEdgeBuild) floor.nearby(dir));
                    } else if (!(floor.nearby(dir) instanceof FurnaceFloorBuild)) {
                        findNotValid = true;
                    }
                }
            }
            FurnaceValid = !findNotValid;
            updateFurnace = findNotValid;
        }

        public void setValid() {
            for (FurnaceFloorBuild floor : floors) {
                floor.isFurnaceValid = FurnaceValid;
            }
            for (FurnaceEdgeBuild edge : edges) {
                edge.isFurnaceValid = FurnaceValid;
            }
        }

        @Override
        public void draw() {
            
            super.draw();
            Draw.rect(FurnacePivot.this.pivotTop, this.x, this.y, this.rotdeg());

            float pastz = Draw.z();
            
            if (floors == null || floors.isEmpty() || !FurnaceValid) {
                return;
            }
            Draw.z(Layer.blockOver);
            Lines.stroke(1.0F, Color.yellow);
            for (Building floor : floors) {
                if (floor == null || floor.block == null)
                    continue;
                float half = (float) (floor.block.size * 8) / 2.0F;
                Lines.rect(floor.x - half, floor.y - half, half * 2.0F, half * 2.0F);
            }
            for(Building edge : edges){
                if (edge == null || edge.block == null)
                    continue;
                float half = (float) (edge.block.size * 8) / 2.0F;
                Lines.rect(edge.x - half, edge.y - half, half * 2.0F, half * 2.0F);
            }
            Draw.z(pastz);
        }
    }
}
