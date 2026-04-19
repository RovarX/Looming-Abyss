package element;


import arc.graphics.Color;

public class Elements {

    public static Element[] all;
    public static Element vacuum;
    public static Element H2O;

    public static Element Al;
    public static Element Fe;

    public static Element Al_Alloy;

    public static int elementCount = 5;
    public static void load() {

        vacuum = new Element("vacuum"){{
                serialID=0;
                meltPoint = -273.15f;
                freezePoint = -273.15f;
                boilPoint = -273.15f;
                condensePoint = -273.15f;
            }
        };
        
        H2O = new Element("H2O") {
            {
                serialID = 1;
                possibleStates = 3;
                meltPoint = 0.0f;
                freezePoint = -0.1f;
                boilPoint = 100.0f;
                condensePoint = 99.9f;
                phaseNames = new String[] { "ice", "water", "steam" };
                flowability = new double[] { 0, 0.8, 0.9 };
                heatCapacity = 4000.0;
                heatConductivity = new double[] { 2.2, 0.6, 0.025 };
                minMass = new double[] { 0.00000001, 0.000000001, 0.000000001 };
                reload();
            }
        };

        Al = new Element("Al") {
            {
                serialID = 2;
                meltPoint = 660.32f;
                freezePoint = 660.22f;
                boilPoint = 2519.0f;
                condensePoint = 2518.0f;
                phaseNames = new String[] { "al", "al_liquid", "al_gas" };
                flowability = new double[] { 0, 0.8, 0.9 };
                heatCapacity = 4000.0;
                heatConductivity = new double[] { 2.2, 0.6, 0.025 };
                minMass = new double[] { 0.00000001, 0.000000001, 0.000000001 };
            }
        };

        Fe = new Element("Fe") {
            {
                serialID = 3;
                phaseNames = new String[] { "fe", "fe_liquid", "fe_gas" };
                meltPoint = 1538.0f;
                freezePoint = 1537.0f;
                boilPoint = 2862.0f;
                condensePoint = 2861.0f;
                flowability = new double[] { 0, 0.8, 0.9 };
                heatCapacity = 450.0;
                heatConductivity = new double[] { 80.4, 30.0, 0.1 };
                minMass = new double[] { 0.00000001, 0.000000001, 0.000000001 };
            }
        };

        Al_Alloy = new Element("Al_Alloy") {
            {
                serialID = 4;
                phaseNames = new String[] { "al_alloy", "al_alloy_liquid", "al_alloy_gas" };
                meltPoint = 500.0f;
                freezePoint = 499.0f;
                boilPoint = 2000.0f;
                condensePoint = 1999.0f;
                flowability = new double[] { 0, 0.8, 0.9};
                heatCapacity = 3800.0;
                heatConductivity = new double[] { 3.5, 1.0, 0.05 };
                minMass = new double[] { 0.00000001, 0.000000001, 0.000000001 };
            }
        };
        
        all = new Element[] {vacuum, H2O, Al, Fe, Al_Alloy};
    }
}
