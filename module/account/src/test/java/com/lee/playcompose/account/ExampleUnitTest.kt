package com.lee.playcompose.account

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        // state = 5，二进制表示是101，从左到右分别是bit3 bit2 bit1
//        println("result:${convertDecimalToBinary(0)}") // 0 0 0
//        println("result:${convertDecimalToBinary(1)}") // 0 0 1
//        println("result:${convertDecimalToBinary(2)}") // 0 1 0
//        println("result:${convertDecimalToBinary(3)}") // 0 1 1
//        println("result:${convertDecimalToBinary(4)}") // 1 0 0
//        println("result:${convertDecimalToBinary(5)}") // 1 0 1
//        println("result:${convertDecimalToBinary(6)}") // 1 1 0
//        println("result:${convertDecimalToBinary(7)}") // 1 1 1

        val state = 4294967294

        println("2#->${convertDecimalToBinary(state)}")
        println("bit 1 >>>>>>>>> : ${valueAtBit(state, 1)}")
        println("bit 2 >>>>>>>>> : ${valueAtBit(state, 2)}")
        println("bit 3 >>>>>>>>> : ${valueAtBit(state, 3)}")
        println("bit 4 >>>>>>>>> : ${valueAtBit(state, 4)}")
        println("bit 5 >>>>>>>>> : ${valueAtBit(state, 5)}")

        // 以后可能会扩展新的配置，例如增加bit5, bit4，假设state=30，二进制表示是11110
    }

    private fun valueAtBit(num: Long, bit: Int): Long {
        return (num shr (bit - 1)) and 1
    }

    private fun convertDecimalToBinary(num: Long): Long {
        var n = num
        var binaryNumber: Long = 0
        var remainder: Int
        var i = 1

        while (n != 0L) {
            remainder = (n % 2).toInt()
            n /= 2
            binaryNumber += (remainder * i).toLong()
            i *= 10
        }
        return binaryNumber
    }
}