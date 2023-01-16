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
 * Convenience method to create a new mockK with reflective stubs.
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

/**
 * The receiver used to conveniently define reflective stubs on a mockK.
 */
public class ReflectiveStubbing<T : Any>(

  /**
   * The mockK that reflective stubs are applied to.
   */
  public val self: T,

  /**
   * The ktype of [self], generated using [typeOf]
   */
  private val kType: KType,
) {

  /**
   * Convenience accessor the [KClass] of [self]. Reflective properties and functions should all come from
   * this instance and not [kType], since kType might be an interface with inaccessible methods.
   */
  public val kClass: KClass<*> get() = self.kotlinClass

  /**
   * Convenience accessor for [kotlin.reflect.full.memberProperties] via [kClass]
   */
  public val memberProperties: Collection<KProperty1<*, *>> get() = kClass.memberProperties

  /**
   * Convenience accessor for [kotlin.reflect.full.memberFunctions] via [kClass]
   */
  public val memberFunctions: Collection<KFunction<*>> get() = kClass.memberFunctions

  /**
   * Convenience accessor for non-suspendable functions via [kClass]
   */
  public val normalMemberFunctions: Collection<KFunction<*>> get() = memberFunctions.filter { !it.isSuspend }

  /**
   * Convenience accessor for suspendable functions via [kClass]
   */
  public val suspendMemberFunctions: Collection<KFunction<*>> get() = memberFunctions.filter { it.isSuspend }

  /**
   * Convenience function to filter a collection of [KCallable] by return-type
   */
  public inline fun <reified R : Any?> Collection<KCallable<*>>.filterReturnType(): Collection<KCallable<*>> =
    filter { it.returnType.classifier == R::class }

  /**
   * Convenience function to reflectively match a call to the given [KCallable].
   * [callable] must be a normal member function or property of [self].
   */
  public fun MockKMatcherScope.callTo(callable: KCallable<*>): Any? = callTo(callable, self, kType)

  /**
   * Convenience function to reflectively match a call to the given suspendable [KCallable].
   * [callable] must be a suspendable member function of [self].
   */
  public suspend fun MockKMatcherScope.suspendCallTo(callable: KCallable<*>): Any? =
    suspendCallTo(callable, self, kType)

  /**
   * Convenience function for every { callTo(callable) }
   * [callable] must be a normal member function or property of [self].
   */
  public fun everyCallTo(callable: KCallable<*>): MockKStubScope<Any?, Any?> = every { callTo(callable) }

  /**
   * Convenience function for coEvery { suspendCallTo(callable) }
   * [callable] must be a suspendable member function of [self].
   */
  public fun coEveryCallTo(callable: KCallable<*>): MockKStubScope<Any?, Any?> = coEvery { suspendCallTo(callable) }

  /**
   * Convenience function to apply the same answer to every [KCallable] in [calls]
   */
  public fun answerEveryCallIn(
    calls: Collection<KCallable<*>>,
    answer: MockKAnswerScope<Any?, Any?>.(Call) -> Any?
  ) { calls.forEach { everyCallTo(it) answers (answer) } }

  /**
   * Convenience function to apply the same suspendable answer to [KCallable] in [calls]
   */
  public fun coAnswerEveryCallIn(
    calls: Collection<KCallable<*>>,
    answer: suspend MockKAnswerScope<Any?, Any?>.(Call) -> Any?
  ) { calls.forEach { coEveryCallTo(it) coAnswers (answer) } }

  /**
   * Convenience function to apply the same answer to every member property and function in [self]
   */
  public fun defaultAnswer(answer: MockKAnswerScope<Any?, Any?>.(Call) -> Any?) {
    answerEveryCallIn(calls = memberProperties + normalMemberFunctions, answer)
    coAnswerEveryCallIn(calls = suspendMemberFunctions, answer)
  }
}
