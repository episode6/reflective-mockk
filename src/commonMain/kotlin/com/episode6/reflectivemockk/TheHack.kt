package com.episode6.reflectivemockk

import io.mockk.ConstantMatcher
import io.mockk.Matcher
import io.mockk.MockKGateway
import io.mockk.MockKMatcherScope
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/**
 * The reflective hack that powers the entire library.
 * This won't necessary if https://github.com/mockk/mockk/pull/1005 is merged.
 */
public fun <T : Any> MockKMatcherScope.any(kclass: KClass<T>): T =
  match(ConstantMatcher<T>(true), kclass)

private fun <T : Any> MockKMatcherScope.match(matcher: Matcher<T>, kclass: KClass<T>): T =
  findCallRecorder().matcher(matcher, kclass)

private fun MockKMatcherScope.findCallRecorder(): MockKGateway.CallRecorder {
  val func = MockKMatcherScope::class.memberProperties
    .find { it.returnType.classifier == MockKGateway.CallRecorder::class }
  return func!!.call(this) as MockKGateway.CallRecorder
}
