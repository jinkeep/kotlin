package org.jetbrains.kotlin.container.tests

import org.jetbrains.kotlin.container.*
import org.junit.Test
import java.io.Closeable
import javax.inject.Inject
import kotlin.test.*

interface TestComponentInterface {
    public val disposed: Boolean
    fun foo()
}

interface TestClientComponentInterface

class TestComponent : TestComponentInterface, Closeable {
    public override var disposed: Boolean = false
    override fun close() {
        disposed = true
    }

    override fun foo() {
        throw UnsupportedOperationException()
    }
}

class ManualTestComponent(val name: String) : TestComponentInterface, Closeable {
    public override var disposed: Boolean = false
    override fun close() {
        disposed = true
    }

    override fun foo() {
        throw UnsupportedOperationException()
    }
}

class TestClientComponent(val dep: TestComponentInterface) : TestClientComponentInterface, Closeable {
    override fun close() {
        if (dep.disposed)
            throw Exception("Dependency shouldn't be disposed before dependee")
        disposed = true
    }

    var disposed: Boolean = false
}

class TestClientComponent2() : TestClientComponentInterface {
}

class ComponentContainerTest {
    Test fun should_throw_when_not_composed() {
        val container = createContainer("test") {}
        fails {
            container.resolve<TestComponentInterface>()
        }
    }

    Test fun should_resolve_to_null_when_empty() {
        val container = createContainer("test") { }
        assertNull(container.resolve<TestComponentInterface>())
    }

    Test fun should_resolve_to_instance_when_registered() {
        val container = createContainer("test") { useImpl<TestComponent>() }

        val descriptor = container.resolve<TestComponentInterface>()
        assertNotNull(descriptor)
        val instance = descriptor!!.getValue() as TestComponentInterface
        assertNotNull(instance)
        fails {
            instance.foo()
        }
    }

    Test fun should_resolve_instance_dependency() {
        val container = createContainer("test") {
            useInstance(ManualTestComponent("name"))
            useImpl<TestClientComponent>()
        }

        val descriptor = container.resolve<TestClientComponent>()
        assertNotNull(descriptor)
        val instance = descriptor!!.getValue() as TestClientComponent
        assertNotNull(instance)
        assertNotNull(instance.dep)
        fails {
            instance.dep.foo()
        }
        assertTrue(instance.dep is ManualTestComponent)
        assertEquals("name", (instance.dep as ManualTestComponent).name)
        container.close()
        assertTrue(instance.disposed)
        assertFalse(instance.dep.disposed) // should not dispose manually passed instances
    }

    Test fun should_resolve_type_dependency() {
        val container = createContainer("test") {
            useImpl<TestComponent>()
            useImpl<TestClientComponent>()
        }

        val descriptor = container.resolve<TestClientComponent>()
        assertNotNull(descriptor)
        val instance = descriptor!!.getValue() as TestClientComponent
        assertNotNull(instance)
        assertNotNull(instance.dep)
        fails {
            instance.dep.foo()
        }
        container.close()
        assertTrue(instance.disposed)
        assertTrue(instance.dep.disposed)
    }

    Test fun should_resolve_multiple_types() {
        createContainer("test") {
            useImpl<TestComponent>()
            useImpl<TestClientComponent>()
            useImpl<TestClientComponent2>()
        }.use {
            val descriptor = it.resolveMultiple<TestClientComponentInterface>()
            assertNotNull(descriptor)
            assertEquals(2, descriptor.count())
        }
    }

    Test fun should_resolve_singleton_types_to_same_instances() {
        createContainer("test") {
            useImpl<TestComponent>()
            useImpl<TestClientComponent>()
        }.use {
            val descriptor1 = it.resolve<TestClientComponentInterface>()
            assertNotNull(descriptor1)
            val descriptor2 = it.resolve<TestClientComponentInterface>()
            assertNotNull(descriptor2)
            assertTrue(descriptor1 == descriptor2)
            assertTrue(descriptor1!!.getValue() == descriptor2!!.getValue())
        }
    }

    class WithSetters {
        var isSetterCalled = false

        var tc: TestComponent? = null
            @Inject set(v) {
                isSetterCalled = true
            }
    }

    Test fun should_inject_properties_of_singletons() {
        val withSetters = createContainer("test") {
            useImpl<WithSetters>()
        }.get<WithSetters>()

        assertTrue(withSetters.isSetterCalled)
    }

    Test fun should_not_inject_properties_of_instances() {
        val withSetters = WithSetters()
        createContainer("test") {
            useInstance(withSetters)
        }

        assertFalse(withSetters.isSetterCalled)
    }

    Test fun should_discover_dependencies_recursively() {
        class C

        class B {
            var c: C? = null
                @Inject set
        }


        class A {
            var b: B? = null
                @Inject set
        }

        val a = createContainer("test") {
            useImpl<A>()
        }.get<A>()

        val b = a.b
        assertTrue(b is B)
        val c = b!!.c
        assertTrue(c is C)
    }

}