package loomingabyss;

import arc.Core;
import arc.Events;
import arc.util.Time;
import block.LABlocks;
import block.costomizableCrafter.reaction.Reactions;
import element.Elements;
import element.LAItems;
import element.LALiquids;
import element.ore.LAOres;
import icon.LAIcons;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;
import planet.LAPlanet;
import statuseffect.LAStatusEffects;

public class LoomingAbyss extends Mod {


    public LoomingAbyss() {


        // listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            // show dialog upon startup
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("frog");
                dialog.cont.add("behold").row();
                dialog.cont.image(Core.atlas.find("example-java-mod-frog")).pad(20f).row();
                dialog.cont.button("I see", dialog::hide).size(100f, 50f);
                dialog.show();
            });
        });
    }

    @Override
    public void loadContent() {
        LAIcons.load();
        LAPlanet.load();
        LAStatusEffects.load();
        Elements.load();
        LAItems.load();
        LALiquids.load();
        LAOres.load();
        LABlocks.load();
        Reactions.load();
    }

}
