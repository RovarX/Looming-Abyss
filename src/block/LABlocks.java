package block;

import block.costomizableCrafter.base.LABase;
import block.costomizableCrafter.base.CrafterFloor;
import block.furnace.FurnaceEdge;
import block.furnace.FurnaceFloor;
import block.furnace.FurnacePivot;

public class LABlocks {
    public static FurnacePivot furnacePivot;
    public static FurnaceFloor furnaceFloor;
    public static FurnaceEdge furnaceWall;

    public static LABase LABase;
    public static CrafterFloor crafterFloor;

    public static void load() {

        crafterFloor = new CrafterFloor("crafter-floor") {
            {
                this.size = 1;
            }
        };
        furnacePivot = new FurnacePivot("furnace-pivot") {
            {
                rotate = true;
                this.size = 1;
            }
        };
        furnaceFloor = new FurnaceFloor("furnace-floor") {
            {
                this.size = 1;
            }
        };
        furnaceWall = new FurnaceEdge("furnace-wall") {
            {
                this.size = 1;
            }

        };
        LABase = new LABase("customizable-crafter") {
            {
                this.size = 3;
                this.innerSize = 8;
            }
        };
        
    }
}
