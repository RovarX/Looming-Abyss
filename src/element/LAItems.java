package element;

import arc.graphics.Color;
import java.lang.reflect.Field;

public class LAItems {
    public static LAItem iron;
    public static LAItem gold;
    public static LAItem aluminum;
    public static LAItem steel;
    public static LAItem quartzSand;
    public static LAItem zincum;
    public static LAItem log;

    public static void load() {
        iron = new LAItem("iron", Color.valueOf("954c3c"), null) {
            {
                
            }
        };
        gold = new LAItem("gold", Color.valueOf("DBD40B"), null) {
            {
                
            }
        };
        aluminum = new LAItem("aluminum", Color.valueOf("C3C3C3"), null) {
            {
                
            }
        };
        steel = new LAItem("steel", Color.valueOf("888888"), null) {
            {
                
            }
        };
        quartzSand = new LAItem("quartz-sand", Color.valueOf("EDE6D1"), null) {
            {
                
            }
        };
        zincum = new LAItem("zincum", Color.valueOf("7FB3D5"), null) {
            {
                
            }
        };
        log = new LAItem("log", Color.valueOf("6F4E37"), null) {
            {
                
            }
        };
    }
    
}
