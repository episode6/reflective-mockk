@file:OptIn(ExperimentalCoroutinesApi::class)

package com.episode6.reflectivemockk

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.reflect.full.memberFunctions

class ConvenienceApiSuspendTest {
  interface TestInterface {
    suspend fun someFunction(input: String): String
  }

  interface TestGenericInterface<T> {
    suspend fun someFunction(input: T): T
  }

  interface TestInterfaceWithGenericFunction {
    suspend fun <T> someFunction(input: T): T
  }

  @Test fun testUsageWithSimpleInterface() = runTest {
    val mockTestClass = mockk<TestInterface>().reflectiveStubs {
      coAnswerEveryCallIn(suspendMemberFunctions) { "mocked" }
    }

    val result = mockTestClass.someFunction("something")

    assertThat(result).isEqualTo("mocked")
    coVerify { mockTestClass.someFunction("something") }
  }

  @Test fun testUsageWithGenericInterface() = runTest {
    val mockTestClass = mockk<TestGenericInterface<String>>().reflectiveStubs {
      coAnswerEveryCallIn(suspendMemberFunctions) { "mocked" }
    }

    val result = mockTestClass.someFunction("something")

    assertThat(result).isEqualTo("mocked")
    coVerify { mockTestClass.someFunction("something") }
  }

  @Test fun testUsageWithGenericFunction() = runTest {
    val mockTestClass = mockk<TestInterfaceWithGenericFunction>().reflectiveStubs {
      coAnswerEveryCallIn(suspendMemberFunctions) { "mocked" }
    }

    val result = mockTestClass.someFunction("something")

    assertThat(result).isEqualTo("mocked")
    coVerify { mockTestClass.someFunction("something") }
  }
}
