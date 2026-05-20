package block.customizableCrafter.component.transport

import arc.func.Prov
import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.component.Component
import block.customizableCrafter.tile.LATile
import element.Elements
import kotlin.math.min

class Pipe(name: String) : Component(name) {

    /**max transport rate*/
    var maxRate = 0.0f

    init {
        update = true
        rotate = true
        buildType = Prov { PipeBuild() }
    }

    inner class PipeBuild : ComponentBuild(), LiquidIn {

        override val es = ElementState()
        override val capacity: Float
            get() = this@Pipe.maxRate

        /**logical pipe direction order: up, right, down, left*/
        private val inputOrder = intArrayOf(0, 1, 2, 3)

        override fun updatec() {

            val self = selfTile() ?: return
            self.componentBuild = this
            self.acted = true

            if (es.mass <= 0.0) {
                es.toNull()
            }

            if (capacity <= 0f) return

            val front = tileAt(rotate) ?: return
            val frontBuild = front.componentBuild as? LiquidIn ?: return
            if (!canTransferForward(front, frontBuild)) return

            val movedForward = pushForward(frontBuild)
            pullInputs(front)

            if (movedForward && frontBuild is PipeBuild && front.acted) {
                frontBuild.chainForwardOnly()
            }

            if (es.mass <= 0.0) {
                es.toNull()
            }
        }

        private fun chainForwardOnly() {
            if (capacity <= 0f) return

            val self = selfTile() ?: return
            val front = tileAt(rotate) ?: return
            val frontBuild = front.componentBuild as? LiquidIn ?: return
            if (!canTransferForward(front, frontBuild)) return

            val movedForward = pushForward(frontBuild)
            if (movedForward && frontBuild is PipeBuild && front.acted) {
                frontBuild.chainForwardOnly()
            }
            self.componentBuild = this
            self.acted = true
        }

        private fun pushForward(frontBuild: LiquidIn): Boolean {
            val room = frontBuild.capacity.toDouble() - frontBuild.es.mass
            if (room <= 0.0 || es.mass <= 0.0) return false

            moveLiquid(es, frontBuild.es, min(es.mass, room))
            return true
        }

        private fun pullInputs(front: LATile) {
            val room = capacity.toDouble() - es.mass
            if (room <= 0.0) return

            val currentElement = es.element
            val candidates = ArrayList<Pair<Int, LATile>>(4)

            for (dir in inputOrder) {
                val source = tileAt(dir) ?: continue
                if (source === front) continue

                val sourceBuild = source.componentBuild as? LiquidIn ?: continue
                if (sourceBuild.es.mass <= 0.0 || sourceBuild.es.element === Elements.vacuum) continue
                if (currentElement !== Elements.vacuum && sourceBuild.es.element !== currentElement) continue

                if (sourceBuild is PipeBuild) {
                    if (sourceBuild.rotate != opposite(dir)) continue
                    if (rotate == dir && sourceBuild.rotate == opposite(dir)) continue
                }

                candidates.add(dir to source)
            }

            if (candidates.isEmpty()) return

            val selected = candidates.minWith(compareBy<Pair<Int, LATile>> { it.first }).second
            val sourceBuild = selected.componentBuild as? LiquidIn ?: return
            val move = min(sourceBuild.es.mass, room)
            if (move <= 0.0) return

            moveLiquid(sourceBuild.es, es, move)
        }

        private fun canTransferForward(front: LATile, frontBuild: LiquidIn): Boolean {
            if (!front.canFlowIn()) return false
            if (frontBuild is PipeBuild && frontBuild.rotate == opposite(rotate)) return false

            val frontEs = frontBuild.es
            if (frontEs.element === Elements.vacuum || frontEs.mass <= 0.0) return true
            if (frontEs.element !== es.element) return false
            return frontEs.phase == es.phase
        }

        private fun moveLiquid(from: ElementState, to: ElementState, amount: Double) {
            if (amount <= 0.0 || from.mass <= 0.0) return

            val move = min(amount, from.mass)
            if (move <= 0.0) return

            val ratio = move / from.mass
            val movedHeat = from.heat * ratio

            if (to.element === Elements.vacuum || to.mass <= 0.0) {
                to.element = from.element
                to.phase = from.phase
            }

            to.addMH(move, movedHeat)
            from.addMH(-move, -movedHeat)

            if (from.mass <= 0.0) {
                from.toNull()
            }
        }

        private fun selfTile(): LATile? {
            return tiles.getTile(tx, ty)
        }

        private fun tileAt(dir: Int): LATile? {
            val self = selfTile() ?: return null
            return tiles.getTile(self, dir)
        }


        private fun opposite(dir: Int): Int {
            return (dir + 2) % 4
        }
    }
}