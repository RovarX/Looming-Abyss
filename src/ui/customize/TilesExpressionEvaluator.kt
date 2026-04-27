package ui.customize

import block.customizableCrafter.tile.LATiles
import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Field
import java.lang.reflect.Method

object TilesExpressionEvaluator {

    private data class Segment(val name: String, val index: Int?)
    private data class PropertyResult(val found: Boolean, val value: Any?)
    private data class IndexResult(val supported: Boolean, val inRange: Boolean, val value: Any?)

    private val segmentRegex = Regex("^([A-Za-z_][A-Za-z0-9_]*)(?:\\[(\\d+)])?$")

    fun eval(expression: String, tiles: LATiles?): String {
        val text = expression.trim()
        if (text.isEmpty()) return "请输入表达式"
        if (text.contains('(') || text.contains(')')) return "不支持方法调用"
        if (tiles == null) return "view.tiles = null"

        val segments = parse(text) ?: return "表达式非法"
        if (segments.isEmpty() || segments.first().name != "tiles") return "表达式必须以 tiles 开头"

        var current: Any? = tiles
        var path = "tiles"

        if (segments.size == 1) return stringify(current)

        for (i in 1 until segments.size) {
            val seg = segments[i]

            if (current == null) return "空引用: $path"

            val property = readProperty(current, seg.name)
            if (!property.found) return "字段不存在: $path.${seg.name}"

            current = property.value
            path += ".${seg.name}"

            val index = seg.index
            if (index != null) {
                if (current == null) return "空引用: $path"
                val indexed = readIndex(current, index)
                if (!indexed.supported) return "不可下标访问: $path"
                if (!indexed.inRange) return "下标越界: $path[$index]"
                current = indexed.value
                path += "[$index]"
            }
        }

        return stringify(current)
    }

    private fun parse(expression: String): List<Segment>? {
        val parts = expression.split('.')
        if (parts.any { it.isEmpty() }) return null

        val result = ArrayList<Segment>(parts.size)
        for (part in parts) {
            val m = segmentRegex.matchEntire(part) ?: return null
            val name = m.groupValues[1]
            val index = m.groupValues[2].let { if (it.isEmpty()) null else it.toIntOrNull() ?: return null }
            result.add(Segment(name, index))
        }
        return result
    }

    private fun readProperty(target: Any, name: String): PropertyResult {
        try {
            val field: Field? = target.javaClass.fields.firstOrNull { it.name == name }
            if (field != null) {
                return PropertyResult(true, field.get(target))
            }
        } catch (_: Exception) {
        }

        try {
            val field: Field? = target.javaClass.declaredFields.firstOrNull { it.name == name }
            if (field != null) {
                field.isAccessible = true
                return PropertyResult(true, field.get(target))
            }
        } catch (_: Exception) {
        }

        val suffix = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val getterNames = arrayOf("get$suffix", "is$suffix")
        for (methodName in getterNames) {
            try {
                val method: Method? = target.javaClass.methods.firstOrNull {
                    it.name == methodName && it.parameterCount == 0
                }
                if (method != null) {
                    return PropertyResult(true, method.invoke(target))
                }
            } catch (_: Exception) {
            }
        }

        return PropertyResult(false, null)
    }

    private fun readIndex(target: Any, index: Int): IndexResult {
        if (index < 0) return IndexResult(true, false, null)

        if (target is List<*>) {
            return if (index < target.size) IndexResult(true, true, target[index]) else IndexResult(true, false, null)
        }

        if (target.javaClass.isArray) {
            val size = ReflectArray.getLength(target)
            return if (index < size) IndexResult(true, true, ReflectArray.get(target, index)) else IndexResult(true, false, null)
        }

        return IndexResult(false, false, null)
    }

    private fun stringify(value: Any?): String {
        if (value == null) return "null"

        if (value is Collection<*>) {
            return value.joinToString(prefix = "[", postfix = "]") { it?.toString() ?: "null" }
        }

        if (value.javaClass.isArray) {
            val size = ReflectArray.getLength(value)
            return (0 until size)
                .joinToString(prefix = "[", postfix = "]") { i -> ReflectArray.get(value, i)?.toString() ?: "null" }
        }

        return value.toString()
    }
}

