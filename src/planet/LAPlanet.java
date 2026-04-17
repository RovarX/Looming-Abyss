package planet;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;

public class LAPlanet {
    public static Planet asharok;

    public static void load() {
        asharok = new Planet("asharok", Planets.sun, 1.0f, 2) {{

            this.generator = new AsharokPlanetGenerator();

            this.meshLoader = () -> new HexMesh(this, 5);
            this.cloudMeshLoader = () -> new MultiMesh(new GenericMesh[] {
                new HexSkyMesh(this, 2, 0.15F, 0.14F, 5, Color.valueOf("b86b3a").a(0.75F), 2, 0.42F, 1.0F,0.43F),
                new HexSkyMesh(this, 3, 0.6F, 0.15F, 5, Color.valueOf("9b3b2a").a(0.75F), 2, 0.42F, 1.2F, 0.45F)
            });

            this.alwaysUnlocked = true;
            this.landCloudColor = Color.valueOf("9e4a2f");
            this.atmosphereColor = Color.valueOf("8b2f1f");
            this.iconColor = Color.valueOf("b65a3a");
            this.atmosphereRadIn = 0.02F;
            this.atmosphereRadOut = 0.3F;
            this.tidalLock = true;
            this.orbitSpacing = 2.0F;
            this.totalRadius += 2.6F;
            this.defaultEnv = 17;
            this.startSector = 10;
            this.defaultCore = Blocks.coreBastion;

            this.clearSectorOnLose = true;
            this.launchCapacityMultiplier = 0.25F;
            this.enemyBuildSpeedMultiplier = 0.5F;
            this.allowLaunchToNumbered = false;

            this.ruleSetter = (r) -> {
                r.waveTeam = Team.blue;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.lighting = false;
                r.coreDestroyClear = true;
                r.onlyDepositCore = true;
            };


            this.campaignRuleDefaults.fog = true;
            this.campaignRuleDefaults.showSpawns = true;
            this.unlockedOnLand.add(Blocks.coreBastion);
        }};
    }
}
