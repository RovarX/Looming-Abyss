package statuseffect;

import mindustry.type.StatusEffect;

public class LAStatusEffects {
    /**用于打开自定义ui时静止 */
    public static StatusEffect glaciated;

    public static void load() {
        glaciated = new StatusEffect("glaciated"){
            {
                this.speedMultiplier = 0.0f;
                this.disarm = true;
            }
        };
    }   
}