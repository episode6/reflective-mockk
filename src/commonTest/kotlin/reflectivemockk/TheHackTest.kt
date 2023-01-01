package reflectivemockk

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.episode6.reflectivemockk.findCallRecorder
import io.mockk.*
import kotlin.test.Test

class TheHackTest {
  @Test fun testFindRecorderWorks() {
    val callRecorder = mockk<MockKGateway.CallRecorder>()
    val lambda = CapturingSlot<Function<*>>()
    val scope = MockKMatcherScope(callRecorder, lambda)

    val result = scope.findCallRecorder()

    assertThat(result).isNotNull().isEqualTo(callRecorder)
  }
}
