package element;

import arc.graphics.Color;

public class LALiquids {
    public static LALiquid water;
    public static LALiquid fe_liquid;
    public static LALiquid al_liquid;
    public static LALiquid al_alloy_liquid;

    public static void load() {
        water = new LALiquid("water", Color.valueOf("596ab8"), Elements.H2O) {
            {
                
            }
        };

        fe_liquid = new LALiquid("fe_liquid", Color.valueOf("b7410e"), Elements.Fe) {
            {

            }
        };

        al_liquid = new LALiquid("al_liquid", Color.valueOf("d9d9d9"), Elements.Al) {
            {

            }
        };

        al_alloy_liquid = new LALiquid("al_alloy_liquid", Color.valueOf("c0c0c0"), Elements.Al_Alloy) {
            {

            }
        };
    }
}
