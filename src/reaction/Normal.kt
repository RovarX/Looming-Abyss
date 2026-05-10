package reaction

import block.customizableCrafter.assist.ElementState
import block.customizableCrafter.dealer.Processors
import block.customizableCrafter.tile.LATile
import element.Element
import element.Elements
import element.Phase
import utility.Bits
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.math.min

object Normal {

    fun isIngredient(r: Reaction,e:Element):Boolean{
        return r.ingredients.containsKey(e)
    }

    fun onTile(r: Reaction,tile: LATile):Boolean{
        if(tile.es.element!==r.base){
            return false
        }
        val tempBits = Bits().also{
            it.or(tile.es.element.id)
            for(nearTile in tile.getNearTiles()){
                if(nearTile==null){
                    continue
                }
                it.or(nearTile.es.element.id)
            }
        }
        if(!tempBits.and(r.bits).equal(r.bits)){
            return false
        }
        tempBits.clear().also{
            if(!r.requirement.checkTile(tile)){
                return false
            }
            it.or(tile.es.element.id)
            for(nearTile in tile.getNearTiles()){
                if(nearTile==null){
                    continue
                }
                if(!r.requirement.checkTile(nearTile)){
                    continue
                }
                it.or(nearTile.es.element.id)
            }
        }
        return tempBits.and(r.bits).equal(r.bits)
    }

    fun reactOn(r:Reaction,tile:LATile):Boolean{

        val allTiles = ArrayList<LATile>(5)
        allTiles.add(tile)
        tile.getNearTiles().forEach { near -> if (near != null && near.canReact()) allTiles.add(near) }

        val availableMass = HashMap<Element, Double>()
        val providers = HashMap<Element, MutableList<LATile>>()

        for (t in allTiles) {
            val es = t.es
            if (es.mass <= 0.0) continue
            if (!r.requirement.checkTile(t)) continue
            if (!r.ingredients.containsKey(es.element)) continue

            availableMass[es.element] = (availableMass[es.element] ?: 0.0) + es.mass
            providers.getOrPut(es.element) { ArrayList() }.add(t)
        }

        var reactRate = Double.POSITIVE_INFINITY

        for ((element, ratioF) in r.ingredients) {
            val ratio = ratioF.toDouble()
            if (ratio <= 0.0) return false

            val mass = availableMass[element] ?: 0.0
            if (mass <= 0.0) return false

            reactRate = min(reactRate, mass / ratio)
        }

        if (!reactRate.isFinite() || reactRate <= 0.0) return false
        if (r.maxRate > 0f) reactRate = min(reactRate, r.maxRate.toDouble())
        if (reactRate <= 0.0) return false

        val snapshots = HashMap<LATile, ElementState>()

        fun backup(t: LATile) {
            if (snapshots.containsKey(t)) return
            snapshots[t] = ElementState().also { it.copyAllFrom(t.es) }
        }

        fun rollback() {
            snapshots.forEach { (t, state) -> t.es.copyAllFrom(state) }
        }

        var consumedMass = 0.0
        var consumedHeat = 0.0

        for ((element, ratioF) in r.ingredients) {
            val needMass = ratioF.toDouble() * reactRate
            val sourceTiles = providers[element] ?: run {
                rollback()
                return false
            }

            val totalMass = sourceTiles.sumOf { it.es.mass }
            if (totalMass <= 0.0 || totalMass + 1e-9 < needMass) {
                rollback()
                return false
            }

            var remaining = needMass
            for (i in sourceTiles.indices) {
                if (remaining <= 0.0) break

                val source = sourceTiles[i]
                backup(source)

                val sourceEs = source.es
                val sourceMassBefore = sourceEs.mass
                if (sourceMassBefore <= 0.0) continue

                val planned = if (i == sourceTiles.lastIndex) remaining else needMass * (sourceMassBefore / totalMass)
                val takeMass = min(remaining, min(planned, sourceMassBefore))
                if (takeMass <= 0.0) continue

                val takeHeat = sourceEs.heat * (takeMass / sourceMassBefore)
                sourceEs.addMH(-takeMass, -takeHeat)

                if (sourceEs.mass <= 1e-9) {
                    sourceEs.toNull()
                }

                consumedMass += takeMass
                consumedHeat += takeHeat
                remaining -= takeMass
            }

            if (remaining > 1e-6) {
                rollback()
                return false
            }
        }

        val (product, productRatioF) = r.products.entries.first().let { it.key to it.value }
        val productRatio = productRatioF.toDouble()
        if (productRatio <= 0.0) {
            rollback()
            return false
        }

        val produceMass = productRatio * reactRate
        if (kotlin.math.abs(produceMass - consumedMass) > 1e-6) {
            rollback()
            return false
        }

        if (tile.es.element !== Elements.vacuum && !Processors.push.process(tile.tiles, tile)) {
            rollback()
            return false
        }

        tile.es.apply{
            element=product
            phase=Phase.liquid
            addMH(produceMass,consumedHeat)
        }

        tile.tiles.setTileToArea(tile)

        return true
    }
}