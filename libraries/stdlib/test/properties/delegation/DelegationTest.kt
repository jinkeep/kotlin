package test.properties.delegation

import org.junit.Test as test
import kotlin.test.*
import kotlin.properties.*

class NotNullVarTest() {
    test fun doTest() {
        NotNullVarTestGeneric("a", "b").doTest()
    }
}

private class NotNullVarTestGeneric<T>(val a1: String, val b1: T) {
    var a: String by Delegates.notNull()
    var b by Delegates.notNull<T>()

    public fun doTest() {
        a = a1
        b = b1
        assertTrue(a == "a", "fail: a should be a, but was $a")
        assertTrue(b == "b", "fail: b should be b, but was $b")
    }
}

class ObservablePropertyInChangeSupportTest: ChangeSupport() {

    var b by property(init = 2)
    var c by property(3)

    test fun doTest() {
        var result = false
        addChangeListener("b", object: ChangeListener {
            public override fun onPropertyChange(event: ChangeEvent) {
                result = true
            }
        })
        addChangeListener("c", object: ChangeListener {
            public override fun onPropertyChange(event: ChangeEvent) {
                result = false
            }
        })
        b = 4
        assertTrue(b == 4, "fail: b != 4")
        assertTrue(result, "fail: result should be true")
    }
}

class ObservablePropertyTest {
    var result = false

    var b by Delegates.observable(1, { pd, o, n -> result = true})

    test fun doTest() {
        b = 4
        assertTrue(b == 4, "fail: b != 4")
        assertTrue(result, "fail: result should be true")
    }
}

class A(val p: Boolean)

class VetoablePropertyTest {
    var result = false
    var b by Delegates.vetoable(A(true), { pd, o, n -> result = n.p == true; result})

    test fun doTest() {
        val firstValue = A(true)
        b = firstValue
        assertTrue(b == firstValue, "fail1: b should be firstValue = A(true)")
        assertTrue(result, "fail2: result should be true")
        b = A(false)
        assertTrue(b == firstValue, "fail3: b should be firstValue = A(true)")
        assertFalse(result, "fail4: result should be false")
    }
}
