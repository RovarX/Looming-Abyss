package block.costomizableCrafter.tile

import block.costomizableCrafter.assist.ElementArea
import block.costomizableCrafter.dealer.Dealer

class LATiles(val width: Int, val height: Int) {

	private val widthWithBorder = width + 2
	private val heightWithBorder = height + 2

	val dealers: MutableList<Dealer> = ArrayList()
	val array: Array<LATile>
	val areas: MutableSet<ElementArea> = LinkedHashSet()
	val freeArea = ArrayDeque<ElementArea>()

	var curIndex: Int = 0

	private val dirArray = arrayOf(
		Pair(0, 1),
		Pair(1, 0),
		Pair(0, -1),
		Pair(-1, 0)
	)

	init {

        array=Array<LATile>(widthWithBorder * heightWithBorder){index ->
            val x = index % widthWithBorder
            val y = index / widthWithBorder
            LATile(x, y, this)
        }

	}

	fun update() {
		for (dealer in dealers) {
			dealer.update(this)
			dealer.update(this)
		}
	}

	fun addDealer(dealer: Dealer) {
		dealers.add(dealer)
	}


	fun addElementTo(es: LATile.ES, tile: LATile) {
		tile.applyFrom(es)
	}

	fun getInnerTile(x: Int, y: Int): LATile? {
		if (x < 0 || x >= widthWithBorder || y < 0 || y >= heightWithBorder) {
			return null
		}
		return array[y * widthWithBorder + x]
	}

	fun getInnerTile(tile: LATile, d: Int): LATile? {
		return getInnerTile(
			tile.x + dirArray[d].first,
			tile.y + dirArray[d].second
		)
	}

	fun getNearTiles(tile: LATile): List<LATile> {
		val result = ArrayList<LATile>(4)
		for (d in 0 until 4) {
			getInnerTile(tile, d)?.let { result.add(it) }
		}
		return result
	}

	fun createArea(): ElementArea {
		return if (freeArea.isEmpty()) {
			val area = ElementArea(curIndex++, this)
			areas.add(area)
			area
		} else {
			val area = freeArea.removeFirst()
			areas.add(area)
			area
		}
	}

}

