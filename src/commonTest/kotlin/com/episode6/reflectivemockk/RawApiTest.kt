package com.episode6.reflectivemockk

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.reflect.full.memberFunctions

class RawApiTest {
  interface TestInterface {
    fun someFunction(input: String): String
  }

  interface TestGenericInterface<T> {
    fun someFunction(input: T): T
  }

  interface TestInterfaceWithGenericFunction {
    fun <T> someFunction(input: T): T
  }

  @Test fun testUsageWithSimpleInterface() {
    val mockTestClass = mockk<TestInterface>()
    val func = mockTestClass.kotlinClass.memberFunctions.find { it.name == "someFunction" }!!
    every { callTo(func, receiver = mockTestClass) } returns "mocked"

    val result = mockTestClass.someFunction("something")

    assertThat(result).isEqualTo("mocked")
    verify { mockTestClass.someFunction("something") }
  }

  @Test fun testUsageWithGenericInterface() {
    val mockTestClass = mockk<TestGenericInterface<String>>()
    val func = mockTestClass.kotlinClass.memberFunctions.find { it.name == "someFunction" }!!
    every { callTo(func, receiver = mockTestClass) } returns "mocked"

    val result = mockTestClass.someFunction("something")

    assertThat(result).isEqualTo("mocked")
    verify { mockTestClass.someFunction("something") }
  }

  @Test fun testUsageWithGenericFunction() {
    val mockTestClass = mockk<TestInterfaceWithGenericFunction>()
    val func = mockTestClass.kotlinClass.memberFunctions.find { it.name == "someFunction" }!!
    every { callTo(func, receiver = mockTestClass) } returns "mocked"

    val result = mockTestClass.someFunction("something")

    assertThat(result).isEqualTo("mocked")
    verify { mockTestClass.someFunction("something") }
  }
}
