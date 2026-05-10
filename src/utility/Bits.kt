package utility


class Bits {

    var value = 0uL

    fun and(b:Bits):Bits{
        value = value and b.value
        return this
    }

    fun and(v:Int):Bits{
        value=value and v.toULong()
        return this
    }

    fun or(b:Bits):Bits{
        value = value or b.value
        return this
    }
    fun or(v:Int):Bits{
        value = value or v.toULong()
        return this
    }

    fun xor(b:Bits):Bits{
        value = value xor b.value
        return this
    }

    fun clear():Bits{
        value = 0uL
        return this
    }

    fun copy(): Bits {
        return Bits().apply {
            value = this@Bits.value
        }
    }

    fun equal(b:Bits):Boolean{
        return value==b.value
    }
}