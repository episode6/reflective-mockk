package reflectivemockk

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.episode6.reflectivemockk.any
import com.episode6.reflectivemockk.findCallRecorder
import com.episode6.reflectivemockk.kotlinClass
import com.episode6.reflectivemockk.resolveInnerType
import io.mockk.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.test.Test

class TheHackTest {

  interface TestInterface {
    fun someFunction(input: String): String
  }

  interface TestGenericInterface<T> {
    fun someFunction(input: T): T
  }

  interface TestInterfaceWithGenericFunction {
    fun <T> someFunction(input: T): T
  }

  @Test fun testFindRecorderWorks() {
    val callRecorder = mockk<MockKGateway.CallRecorder>()
    val lambda = CapturingSlot<Function<*>>()
    val scope = MockKMatcherScope(callRecorder, lambda)

    val result = scope.findCallRecorder()

    assertThat(result).isNotNull().isEqualTo(callRecorder)
  }

  @Test fun testManualUseOfAnyOnSimpleInterface() {
    val mockTestClass = mockk<TestInterface> {
      every { someFunction(any(String::class)) } returns "mocked"
    }

    val result = mockTestClass.someFunction("some input")

    assertThat(result).isEqualTo("mocked")
  }

  @Test fun testManualUseOfAnyOnGenericObject() {
    val mockTestClass = mockk<TestGenericInterface<String>> {
      every { someFunction(any(String::class)) } returns "mocked"
    }

    val result = mockTestClass.someFunction("some input")

    assertThat(result).isEqualTo("mocked")
  }

  @Test fun testManualUseOfAnyOnGenericFunction() {
    val mockTestClass = mockk<TestInterfaceWithGenericFunction> {
      every { someFunction(any(String::class)) } returns "mocked"
    }

    val result = mockTestClass.someFunction("some input")

    assertThat(result).isEqualTo("mocked")
  }

  @Suppress("UNCHECKED_CAST")
  @Test fun testManualReflectiveAnyOnSimpleInterface() {
    val mockTestClass = mockk<TestInterface>()
    val typeOf = typeOf<TestInterface>()
    val func = mockTestClass.kotlinClass.memberFunctions.find { it.name == "someFunction" }!!
    val paramType = typeOf.resolveInnerType(func.parameters[1].type)
    every { func.call(mockTestClass, any(paramType.classifier as KClass<Any>)) } answers { "mocked" }

    val result = mockTestClass.someFunction("some input")

    assertThat(result).isEqualTo("mocked")
  }

  @Suppress("UNCHECKED_CAST")
  @Test fun testManualReflectiveAnyOnGenericInterface() {
    val mockTestClass = mockk<TestGenericInterface<String>>()
    val testClassType = typeOf<TestGenericInterface<String>>()
    val func = mockTestClass.kotlinClass.memberFunctions.find { it.name == "someFunction" }!!
    val paramType = testClassType.resolveInnerType(func.parameters[1].type)
    every {
      func.call(mockTestClass, any(paramType.classifier as KClass<Any>))
    } answers { "mocked" }

    val result = mockTestClass.someFunction("some input")

    assertThat(result).isEqualTo("mocked")
  }

  @Suppress("UNCHECKED_CAST")
  @Test fun testManualReflectiveAnyOnGenericFunction() {
    val mockTestClass = mockk<TestInterfaceWithGenericFunction>()
    val typeOf = typeOf<TestInterfaceWithGenericFunction>()
    val func = mockTestClass.kotlinClass.memberFunctions.find { it.name == "someFunction" }!!
    val paramType = typeOf.resolveInnerType(func.parameters[1].type)
    every { func.call(mockTestClass, any(paramType.classifier as KClass<Any>)) } answers { "mocked" }

    val result = mockTestClass.someFunction("some input")

    assertThat(result).isEqualTo("mocked")
  }
}
