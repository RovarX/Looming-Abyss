package world;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

/** 控制中心方块，用于管理和控制地图上的所有热交换 */
public class ControlCenter extends Block{
	public ControlCenter(String name) {
		super(name);
		this.buildVisibility = BuildVisibility.editorOnly;
		this.replaceable = false;
		this.destructible = false;
		this.update = true;
	}

	public class ControlCenterBuild extends Building {
        

		@Override
		public void draw() {
		}

		public void updateTile() {

		}
	}
}
