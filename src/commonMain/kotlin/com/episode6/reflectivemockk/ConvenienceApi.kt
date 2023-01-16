package com.episode6.reflectivemockk

import io.mockk.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

/**
 * Apply reflective stubs to an existing mockK.
 *
 * Sample Usage (to support a builder):
 *
 * val obj = mockk<SomeBuilder>()
 * obj.reflectiveStubs {
 *   defaultAnswer { self }
 * }
 *
 * @receiver Must be a mockK.
 */
public inline fun <reified T : Any> T.reflectiveStubs(stubbing: ReflectiveStubbing<T>.() -> Unit): T = apply {
  ReflectiveStubbing<T>(this, typeOf<T>()).stubbing()
}

/**
 * Convenience method to create a new mockL with reflective stubs.
 *
 * Sample Usage (to support a builder):
 *
 * val obj = reflectiveMockk<SomeBuilder> {
 *   defaultAnswer { self }
 * }
 */
public inline fun <reified T : Any> reflectiveMockk(
  name: String? = null,
  relaxed: Boolean = false,
  vararg moreInterfaces: KClass<*>,
  relaxUnitFun: Boolean = false,
  stubbing: ReflectiveStubbing<T>.() -> Unit
): T = mockk<T>(
  name = name,
  relaxed = relaxed,
  moreInterfaces = moreInterfaces,
  relaxUnitFun = relaxUnitFun,
).apply { reflectiveStubs(stubbing) }

public class ReflectiveStubbing<T : Any>(public val self: T, private val ktype: KType) {

  public val kClass: KClass<*> get() = self.kotlinClass
  public val memberProperties: Collection<KProperty1<*, *>> get() = kClass.memberProperties
  public val memberFunctions: Collection<KFunction<*>> get() = kClass.memberFunctions
  public val normalMemberFunctions: Collection<KFunction<*>> get() = memberFunctions.filter { !it.isSuspend }
  public val suspendMemberFunctions: Collection<KFunction<*>> get() = memberFunctions.filter { it.isSuspend }

  public inline fun <reified R : Any?> Collection<KCallable<*>>.filterReturnType(): Collection<KCallable<*>> =
    filter { it.returnType.classifier == R::class }

  public fun MockKMatcherScope.callTo(callable: KCallable<*>): Any? = callTo(callable, self, ktype)
  public suspend fun MockKMatcherScope.suspendCallTo(callable: KCallable<*>): Any? = suspendCallTo(callable, self, ktype)

  public fun everyCallTo(callable: KCallable<*>): MockKStubScope<Any?, Any?> = every { callTo(callable) }
  public fun coEveryCallTo(callable: KCallable<*>): MockKStubScope<Any?, Any?> = coEvery { suspendCallTo(callable) }

  public fun answerEveryCallIn(calls: Collection<KCallable<*>>, answer: AnyAnswer) {
    calls.forEach { everyCallTo(it) answers (answer) }
  }

  public fun coAnswerEveryCallIn(calls: Collection<KCallable<*>>, answer: CoAnyAnswer) {
    calls.forEach { coEveryCallTo(it) coAnswers (answer) }
  }

  public fun defaultAnswer(answer: AnyAnswer) {
    answerEveryCallIn(calls = memberProperties + normalMemberFunctions, answer)
    coAnswerEveryCallIn(calls = suspendMemberFunctions, answer)
  }
}

public typealias AnyAnswer = MockKAnswerScope<Any?, Any?>.(Call) -> Any?
public typealias CoAnyAnswer = suspend MockKAnswerScope<Any?, Any?>.(Call) -> Any?
