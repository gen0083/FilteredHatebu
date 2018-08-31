package jp.gcreate.product.filteredhatebu

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class MockkTest {
    class TestClass(val arg: String = "test", val args: IntArray = intArrayOf(1, 2)) {
        fun printOut() {
            println(arg)
        }
        
        fun getInt(position: Int): Int {
            return args[position]
        }
    }
    
    @Test fun mock_printOut() {
        val mock = mockk<TestClass>() {
            every { printOut() } answers { println("mock") }
        }
        assertThat(mock.printOut())
    }
    
    @Test fun mock_args() {
        val mock = spyk<TestClass>() {
            every { getInt(3) }.returns(10)
        }
        assertThat(mock.getInt(0)).isEqualTo(1)
        assertThat(mock.getInt(1)).isEqualTo(2)
        assertThat(mock.getInt(3)).isEqualTo(10)
        assertThatThrownBy { mock.getInt(2) }
            .isInstanceOf(ArrayIndexOutOfBoundsException::class.java)
    }
}
