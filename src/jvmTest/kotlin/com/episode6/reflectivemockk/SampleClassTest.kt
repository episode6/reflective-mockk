package com.episode6.reflectivemockk

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class SampleClassTest {
  @Test fun testHelloWorld() {
    assertThat(SampleClass().hello).isEqualTo("Hello World")
  }
}
