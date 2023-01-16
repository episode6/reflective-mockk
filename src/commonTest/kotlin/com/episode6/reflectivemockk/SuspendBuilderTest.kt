@file:OptIn(ExperimentalCoroutinesApi::class)

package com.episode6.reflectivemockk

import io.mockk.coVerify
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SuspendBuilderTest {

  interface TestBuilder {
    suspend fun step1(): TestBuilder
    suspend fun step2(): TestBuilder
    suspend fun step3(): TestBuilder
  }

  @Test fun testSimpleBuilder() = runTest {
    val builder = reflectiveMockk<TestBuilder> { defaultAnswer { mock } }

    builder.step1().step2().step3()

    coVerify {
      builder.step1()
      builder.step2()
      builder.step3()
    }
  }

  @Test fun testSimpleBuilder2() = runTest {
    val builder = reflectiveMockk<TestBuilder> {
      coAnswerEveryCallIn(suspendMemberFunctions) { mock }
    }

    builder.step1().step2().step3()

    coVerify {
      builder.step1()
      builder.step2()
      builder.step3()
    }
  }
}
