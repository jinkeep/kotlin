package to

import d.g
import d.c
import d.ext
import d.A
import d.T
import d.Outer
import d.O1
import d.O2
import d.E
import d.ClassObject

fun f(a: A, t: T) {
    g(A(c).ext())
    O1.f()
    O2
    E.ENTRY
}

fun f2(i: Outer.Inner, n: Outer.Nested, e: Outer.NestedEnum, o: Outer.NestedObj, t: Outer.NestedTrait, a: Outer.NestedAnnotation) {
    ClassObject
}