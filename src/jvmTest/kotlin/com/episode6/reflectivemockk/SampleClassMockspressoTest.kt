package com.episode6.gradlenow.kmpinclusivedeploy

import assertk.assertThat
import com.episode6.gradlenow.testsupport.MockspressoBuilder
import com.episode6.gradlenow.testsupport.buildExtension
import com.episode6.gradlenow.testsupport.isHelloWorld
import com.episode6.mockspresso2.plugins.core.makeRealObjectsUsingPrimaryConstructor
import com.episode6.mockspresso2.realImplementation
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SampleClassMockspressoTest {

  @RegisterExtension val mxo = MockspressoBuilder()
    .makeRealObjectsUsingPrimaryConstructor()
    .buildExtension()

  private val sample: SampleClass by mxo.realImplementation()

  @Test fun testHelloWorld() {
    assertThat(sample.hello).isHelloWorld()
  }
}
