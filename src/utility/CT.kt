package utility

import arc.Core
import arc.graphics.g2d.TextureRegion
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10

/**an object contains some constant for this mod*/
object CT {
    val modName = "looming-abyss"

    val developeMode = true

    /**direction array*/
    val dir = arrayOf(
        intArrayOf(0,1),
        intArrayOf(1,0),
        intArrayOf(0,-1),
        intArrayOf(-1,0)
    )

    /**The more convenient way to get a texture region*/
    fun getRegion(s:String): TextureRegion{
        return Core.atlas.find("$modName-$s", s)
    }

    fun format(d:Double,digit: Int):String{
        return format(d,digit,"")
    }

    fun format(d:Double, digit:Int, end:String):String{
        require(digit > 0) { "digit must be greater than 0" }

        val negative = d < 0

        if (d.isNaN() || d.isInfinite()) return "$d$end"

        val (scaled, unit) = scaleByUnit(d)
        val number = toPlainSignificant(scaled, digit)
        return buildString {
            if(negative){
                append('-')
            }
            append(number)
            if (unit.isNotEmpty()) append(" ").append(unit)
            append(end)
        }
    }

    fun scaleByUnit(value: Double): Pair<Double, String> {

        val absValue = abs(value)
        return when {
            absValue >= 1e9 -> value / 1e9 to "B"
            absValue >= 1e6 -> value / 1e6 to "M"
            absValue >= 1e3 -> value / 1e3 to "K"
            absValue < 1.0 && absValue >= 1e-3 -> value * 1e3 to "m"
            absValue < 1e-3 && absValue >= 1e-6 -> value * 1e6 to "μ"
            else -> value to ""
        }
    }


    fun toPlainSignificant(value: Double, digit: Int): String {
        if (value == 0.0) return "0"

        val exponent = floor(log10(abs(value))).toInt()
        val scale = digit - 1 - exponent
        val rounded = BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP)
        return rounded.stripTrailingZeros().toPlainString()
    }
}