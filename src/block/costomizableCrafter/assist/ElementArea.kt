package block.costomizableCrafter.assist

import block.costomizableCrafter.tile.LATile
import block.costomizableCrafter.tile.LATiles
import element.Element

class ElementArea(val index:Int, val tiles: LATiles) {

    /** 区域内所有格子，元素相同且连通  */
    var areaTiles = mutableSetOf<LATile>()

    /** 区域边缘格子  */
    var edgeTiles = mutableSetOf<LATile>()

    /** 区域的元素类型  */
    var element: Element? = null

    /** 相邻的其他格子  */
    var nearTiles = mutableSetOf<LATile>()

    /** 标记递归压缩时是否已尝试，防止死循环  */
    var tryCompressed: Boolean = false

    /** 标记是否已反应  */
    var reacted: Boolean = false

    /** 递归查找区域内所有连通格子  */
    fun buildArea(startTile: LATile) {
        var isEdge = false
        for (nearTile in tiles.getNearTiles(startTile)) {
            if (nearTile.isEdge) {
                continue
            }
            if (nearTile.element === this.element && !areaTiles.contains(nearTile)) {
                areaTiles.add(nearTile)
                nearTile.elementArea = this
                buildArea(nearTile)
            }
            if (nearTile.element !== this.element) {
                nearTiles.add(nearTile)
                isEdge = true
            }
        }
        if (isEdge) {
            edgeTiles.add(startTile)
        }
    }

    fun addTile(tile: LATile) {
        areaTiles.add(tile)
        tile.elementArea = this
        updateEdge(tile, 1)
    }

    fun removeTile(tile: LATile) {
        areaTiles.remove(tile)
        edgeTiles.remove(tile)
        tile.elementArea = null
        if (areaTiles.isEmpty()) {
            dissolveElementArea()
            return
        }
        updateEdge(tile, -1)
    }

    private fun updateEdge(tile: LATile, changeState: Int) {
        when (changeState) {
            1 -> {
                nearTiles.remove(tile)
                var isEdge = false
                val ns: List<LATile> = tiles.getNearTiles(tile)
                for (nearTile in ns) {
                    if (nearTile.isEdge) continue

                    val nearArea: ElementArea? = nearTile.elementArea
                    if (nearArea !== this) {
                        if (nearArea != null && nearArea.element === element) {
                            linkArea(nearArea, nearTile)
                            updateEdge(nearTile, 1)
                            continue
                        }
                        nearTiles.add(nearTile)
                        isEdge = true
                        continue
                    }

                    if (edgeTiles.contains(nearTile)) {
                        var stillEdge = false
                        for (nearNearTile in tiles.getNearTiles(nearTile)) {
                            if (nearNearTile.elementArea !== this) {
                                stillEdge = true
                                break
                            }
                        }
                        if (!stillEdge) {
                            edgeTiles.remove(nearTile)
                        }
                    }
                }
                if (isEdge) {
                    edgeTiles.add(tile)
                } else {
                    edgeTiles.remove(tile)
                }
            }

            -1 -> {
                var count = 0
                nearTiles.add(tile)
                val ns: List<LATile> = tiles.getNearTiles(tile)
                for (nearTile in ns) {
                    if (nearTile.isEdge) continue

                    if (nearTile.elementArea === this) {
                        edgeTiles.add(nearTile)
                        count++
                        continue
                    }

                    if (nearTiles.contains(nearTile)) {
                        var stillNear = false
                        val nns: List<LATile> = tiles.getNearTiles(nearTile)
                        for (nearNearTile in nns) {
                            if (nearNearTile.elementArea === this) {
                                stillNear = true
                                break
                            }
                        }
                        if (!stillNear) {
                            nearTiles.remove(nearTile)
                        }
                    }
                }
                if (count >= 2) {
                    breakArea()
                }
            }

            else -> {}
        }
    }

    private fun linkArea(area: ElementArea, linkTile: LATile) {
        if (area === this) return
        for (tile in area.areaTiles) {
            tile.elementArea = this
        }
        areaTiles.addAll(area.areaTiles)
        nearTiles.addAll(area.nearTiles)
        edgeTiles.addAll(area.edgeTiles)
        areaTiles.add(linkTile)
        area.dissolveElementArea()
    }

    private fun breakArea() {
        val oldElement = element
        val oldTiles = areaTiles.toList()

        for (tile in oldTiles) {
            tile.elementArea = null
        }

        for (tile in oldTiles) {
            if (tile.elementArea == null) {
                val newArea = tiles.createArea()
                newArea.element = oldElement
                newArea.addTile(tile)
                newArea.buildArea(tile)
            }
        }
        dissolveElementArea()
    }

    fun dissolveElementArea() {
        if (!tiles.areas.remove(this)) return
        refresh()
        tiles.freeArea.addLast(this)
    }
    
    fun refresh(){
        areaTiles = mutableSetOf()
        edgeTiles = mutableSetOf()
        nearTiles = mutableSetOf()
        tryCompressed=false
        reacted=false
        element=null
    }
}