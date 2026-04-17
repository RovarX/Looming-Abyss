package block.costomizableCrafter.base

import arc.math.geom.Vec2
import arc.scene.ui.layout.Table
import arc.util.Time
import block.costomizableCrafter.tile.LATiles
import block.costomizableCrafter.ui.CUI
import mindustry.gen.Icon
import mindustry.type.Category
import mindustry.ui.Styles
import mindustry.world.blocks.production.GenericCrafter


open class LABase(name: String?) : GenericCrafter(name) {

    @JvmField
    var innerSize: Int = 0

    init {
        this.rotate = true
        this.update = true
        this.configurable = true
        //this.saveConfig = true;
        this.category = Category.crafting
    }

    override fun load() {
        super.load()
    }

    inner class CrafterBaseBuild : GenericCrafterBuild() {

        var innerSize: Int = this@LABase.innerSize

        var innerTiles: LATiles = LATiles(innerSize, innerSize)


        var showInner: Boolean = false

        var isChanging: Boolean = true

        // 基于游戏时间增量的累积计时器，约每 60 tick ≈ 1 秒触发
        private var innerUpdateCounter = 0f


        override fun updateTile() {
            innerUpdateCounter += Time.delta
            if (innerUpdateCounter >= 60f) {
                innerUpdateCounter -= 60f
                innerTiles.update()
            }

        }

        public override fun buildConfiguration(table: Table) {
            table.button(Icon.info, Styles.cleari, 40f, Runnable {
                CUI.customizeDialog.show(innerTiles)
            })
        }

        override fun draw() {
            super.draw()
            if (this.showInner) {
                if (isChanging) {
                    isChanging = false
                }
            } else if (isChanging) {
                isChanging = false
            }
        }



        /**转出内部数据为json，以供蓝图使用  */ /*@Override
        public Object config() {
            ObjectMap<String, Object> data = new ObjectMap<>();
            data.put("rotation", rotation);

            Seq<ObjectMap<String, Object>> components = new Seq<>();
            for (ComponentBuild cmp : componentBuilds) {
                ObjectMap<String, Object> m = new ObjectMap<>();
                m.put("x", cmp.x);
                m.put("y", cmp.y);
                m.put("type", cmp.name);
                components.add(m);
            }
            data.put("components", components);

            return JsonIO.json.toJson(data);
        }

        / **转换json为内部数据 */
        /*public void applyInnerConfig(String json) {
            if (json == null || json.isEmpty())
                return;
            try {
                int rotationDelta = 0;

                Object obj = JsonIO.json.fromJson(ObjectMap.class, json);
                if (!(obj instanceof ObjectMap))
                    return;
                ObjectMap data = (ObjectMap) obj;

                Object rotationObj = data.get("rotation");
                if (rotationObj instanceof Number) {
                    rotationDelta = ((Number) rotationObj).intValue() - this.rotation;
                }

                Object cmpObject = data.get("components");
                if (cmpObject instanceof Seq) {
                    Seq cmpSeq = (Seq) cmpObject;
                    for (Object o : cmpSeq) {
                        if (!(o instanceof ObjectMap)) {
                            continue;
                        }
                        ObjectMap cmpData = (ObjectMap) o;
                        int cmpx = 0;
                        int cmpy = 0;
                        Component cmpType = Components.all.get((String) cmpData.get("type"));
                        Object xObj = cmpData.get("x");
                        Object yObj = cmpData.get("y");
                        if (xObj instanceof Number) {
                            cmpx = ((Number) xObj).intValue();
                        }
                        if (yObj instanceof Number) {
                            cmpy = ((Number) yObj).intValue();
                        }
                        Vec2 Pos = rotatePos(cmpx, cmpy, cmpType.size, rotationDelta);
                        buildComponent(Pos, cmpType);
                    }
                }
            } catch (Throwable ignored) {

            }

        }

        @Override
        public void configure(Object value){
            try{
                if(value == null) return;
                if(value instanceof String){
                    applyInnerConfig((String)value);
                }else if(value instanceof ObjectMap){
                    // 将结构化对象转换为json再复用现有解析逻辑
                    String json = JsonIO.json.toJson(value);
                    applyInnerConfig(json);
                }
            }catch(Throwable ignored){
            }
        }
*/
        /**定位旋转后建筑位置  */
        fun rotatePos(x: Int, y: Int, size: Int, delta: Int): Vec2 {
            val res = Vec2()
            when (delta % 4) {
                0 -> {
                    res.x = x.toFloat()
                    res.y = y.toFloat()
                }

                1 -> {
                    res.x = (this.innerSize - y - size).toFloat()
                    res.y = x.toFloat()
                }

                2 -> {
                    res.x = (this.innerSize - x - size).toFloat()
                    res.y = (this.innerSize - y - size).toFloat()
                }

                3 -> {
                    res.x = y.toFloat()
                    res.y = (this.innerSize - x - size).toFloat()
                }
            }
            return res
        }
    }
}