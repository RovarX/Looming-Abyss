package block.customizableCrafter.dealer

import block.customizableCrafter.tile.LATile
import block.customizableCrafter.tile.LATiles
import element.Elements

class PushProcessor: Processor() {

    /**
     * Try to push the element in [tile] to nearby tiles.
     * Direct push has priority; otherwise recursively push blocking neighbors up to [maxDepth].
     */
    fun process(tiles: LATiles, tile: LATile, maxDepth: Int = 3): Boolean {
        super.process(tiles)
        if (maxDepth < 0) return false
        return processInternal(tile, maxDepth, HashSet())
    }

    fun processInternal(tile: LATile, depth: Int, visited: HashSet<LATile>): Boolean {
        if (!tile.canFlowOut()) return false
        if (!visited.add(tile)) return false

        // Direct push: prefer the neighbor with smaller mass first.
        for (near in sortedNeighborsByMass(tile)) {
            if (canAcceptDirectly(tile, near)) {
                moveAll(tile, near)
                visited.remove(tile)
                return true
            }
        }

        if (depth <= 0) {
            visited.remove(tile)
            return false
        }

        // Recursive push: also try lighter blocking neighbors first.
        for (near in sortedNeighborsByMass(tile)) {
            if (!near.canFlowOut() || visited.contains(near)) continue
            if (!processInternal(near, depth - 1, visited)) continue

            if (canAcceptDirectly(tile, near)) {
                moveAll(tile, near)
                visited.remove(tile)
                return true
            }
        }

        visited.remove(tile)
        return false
    }

    fun sortedNeighborsByMass(tile: LATile): List<LATile> {
        return tiles.getNearTiles(tile)
            .mapIndexedNotNull { index, near -> near?.let { index to it } }
            .sortedWith(compareBy<Pair<Int, LATile>> { it.second.es.mass }.thenBy { it.first })
            .map { it.second }
    }

    fun canAcceptDirectly(from: LATile, to: LATile): Boolean {
        if (!to.canFlowIn()) return false

        val fromEs = from.es
        val toEs = to.es
        return toEs.element === Elements.vacuum ||
            (toEs.element === fromEs.element && toEs.phase == fromEs.phase)
    }

    fun moveAll(from: LATile, to: LATile) {
        val fromEs = from.es
        val toEs = to.es

        if (toEs.element === Elements.vacuum) {
            toEs.element = fromEs.element
            toEs.phase = fromEs.phase
        }

        toEs.addMH(fromEs.mass, fromEs.heat)
        fromEs.toNull()

        tiles.setTileToArea(to)

        fromEs.changed = true
    }
}