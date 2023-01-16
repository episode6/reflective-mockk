package com.episode6.reflectivemockk

import io.mockk.verify
import kotlin.test.Test

class BuilderTest {

  interface TestBuilder {
    fun step1(): TestBuilder
    fun step2(): TestBuilder
    fun step3(): TestBuilder
  }

  @Test fun testSimpleBuilder() {
    val builder = reflectiveMockk<TestBuilder> { defaultAnswer { mock } }

    builder.step1().step2().step3()

    verify {
      builder.step1()
      builder.step2()
      builder.step3()
    }
  }

  @Test fun testSimpleBuilder2() {
    val builder = reflectiveMockk<TestBuilder> {
      answerEveryCallIn(normalMemberFunctions) { mock }
    }

    builder.step1().step2().step3()

    verify {
      builder.step1()
      builder.step2()
      builder.step3()
    }
  }

  @Test fun testSimpleBuilder3() {
    val builder = reflectiveMockk<TestBuilder> {
      normalMemberFunctions
        .filterReturnType<TestBuilder>()
        .forEach { everyCallTo(it) returns mock }
    }

    builder.step1().step2().step3()

    verify {
      builder.step1()
      builder.step2()
      builder.step3()
    }
  }
}
