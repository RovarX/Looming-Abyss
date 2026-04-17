package element;

import arc.Core;
import arc.graphics.g2d.TextureRegion;

public class Element {
    public String name;

    public int serialID = -1;
    /**熔点 */
    public float meltPoint;
    /**凝固点 */
    public float freezePoint;
    /**沸点 */
    public float boilPoint;
    /**液化点 */
    public float condensePoint;
    /**可能状态数，-1：空，0：固体，1：液体，2：气体 */
    public int possibleStates;

    public LAItem solidType;
    public LALiquid liquidType;
    public LAGas gasType;

    /** 热导率 */
    public double[] heatConductivity;
    /** 比热容 */
    public double heatCapacity;
    /** 名称 */
    public double[] flowability;

    public double[] minMass;

    public String[] phaseNames;

    public boolean flowable=true;

    public TextureRegion[] iconRegions = new TextureRegion[]{null, null, null};
    public TextureRegion[] drawRegions = new TextureRegion[]{null,null,null};

    public static int maxStates = 3;
    public static String[] stateNames = new String[] { "solid", "liquid", "gas" };
    public static String[] shownStateNames = new String[3];


    public Element(String name) {
        this.name = name;
    }

    public void reload() {
        this.solidType = null;
        this.liquidType = null;
        this.gasType = null;
        this.loadStateName();
    }


    public void loadStateName() {
        for (int i = 0; i < maxStates; i++) {
            shownStateNames[i] = Core.bundle.get("element." + stateNames[i]);
        }
    }

    /**? */
    public TextureRegion getRegion(int phase) {
        if(iconRegions[phase] != null) {
            return iconRegions[phase];
        }
        switch (phase) {
            case 0:
                if (solidType != null) {
                    TextureRegion region = Core.atlas.find(solidType.name + "_icon");
                    TextureRegion r2=Core.atlas.find(solidType.name+"_draw");
                    drawRegions[0]=r2;
                    iconRegions[0] = region;
                    return region;
                }
            case 1:
                if (liquidType != null) {
                    TextureRegion region = Core.atlas.find(liquidType.name + "_icon");
                    TextureRegion r2=Core.atlas.find(liquidType.name+"_draw");
                    drawRegions[1]=r2;
                    iconRegions[1] = region;
                    return region;
                }
            case 2:
                if (gasType != null) {
                    TextureRegion region = Core.atlas.find(gasType.name + "_icon");
                    TextureRegion r2=Core.atlas.find(gasType.name+"_draw");
                    drawRegions[2]=r2;
                    iconRegions[2] = region;
                    return region;
                }
        }
        //返回error图片
        return Core.atlas.find(name);
    }
}
