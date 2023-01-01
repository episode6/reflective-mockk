package reflectivemockk

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.episode6.reflectivemockk.any
import com.episode6.reflectivemockk.findCallRecorder
import io.mockk.*
import kotlin.test.Test

class TheHackTest {

  class TestClass {
    fun withHiFive(input: String): String = "HiFive:$input"
  }

  @Test fun testFindRecorderWorks() {
    val callRecorder = mockk<MockKGateway.CallRecorder>()
    val lambda = CapturingSlot<Function<*>>()
    val scope = MockKMatcherScope(callRecorder, lambda)

    val result = scope.findCallRecorder()

    assertThat(result).isNotNull().isEqualTo(callRecorder)
  }

  @Test fun testManualUseOfAny() {
    val mockTestClass = mockk<TestClass> {
      every { withHiFive(any(String::class)) } returns "mocked"
    }

    val result = mockTestClass.withHiFive("some input")

    assertThat(result).isEqualTo("mocked")
  }
}
