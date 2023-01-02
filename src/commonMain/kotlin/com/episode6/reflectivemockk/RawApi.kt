package com.episode6.reflectivemockk

import io.mockk.MockKMatcherScope
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend
import kotlin.reflect.typeOf

/**
 * Allows for reflection-based stubbing of methods.
 *
 * Usage example (mock all member functions in one mock):
 *
 * val mock = mockk<SomeClass>()
 * mock.javaClass.kotlin.memberFunctions.forEach {
 *   every { callTo(it, receiver = mock) } answers { /* some default answer */ }
 * }
 *
 * Note: [receiver] must be a mockk
 */
public inline fun <reified RECEIVER : Any> MockKMatcherScope.callTo(callable: KCallable<*>, receiver: RECEIVER): Any? =
  callTo(callable = callable, receiver = receiver, receiverType = typeOf<RECEIVER>())

/**
 * Allows for reflection-based stubbing of methods.
 *
 * Usage example (mock all member functions in one mock):
 *
 * val mock = mockk<SomeClass>()
 * val mockType = typeOf<SomeClass>()
 * mock.javaClass.kotlin.memberFunctions.forEach {
 *   every { callTo(it, receiver = mock, receiverType = mockType) } answers { /* some default answer */ }
 * }
 *
 * Note: [receiver] must be a mockk
 */
public fun MockKMatcherScope.callTo(callable: KCallable<*>, receiver: Any, receiverType: KType): Any? {
  val allParams: List<Any> = listOf(receiver) + paramMatchersFor(callable, receiverType)
  return callable.call(*allParams.toTypedArray())
}

/**
 * Allows for reflection-based stubbing of suspend methods.
 *
 * Usage example (mock all member functions in one mock):
 *
 * val mock = mockk<SomeClass>()
 * mock.javaClass.kotlin.memberFunctions.forEach {
 *   every { callTo(it, receiver = mock) } answers { /* some default answer */ }
 * }
 *
 * Note: [receiver] must be a mockk
 */
public suspend inline fun <reified RECEIVER : Any> MockKMatcherScope.suspendCallTo(callable: KCallable<*>, receiver: RECEIVER): Any? =
  suspendCallTo(callable = callable, receiver = receiver, receiverType = typeOf<RECEIVER>())

/**
 * Allows for reflection-based stubbing of suspend methods.
 *
 * Usage example (mock all member functions in one mock):
 *
 * val mock = mockk<SomeClass>()
 * val mockType = typeOf<SomeClass>()
 * mock.javaClass.kotlin.memberFunctions.forEach {
 *   every { callTo(it, receiver = mock, receiverType = mockType) } answers { /* some default answer */ }
 * }
 *
 * Note: [receiver] must be a mockk
 */
public suspend fun MockKMatcherScope.suspendCallTo(callable: KCallable<*>, receiver: Any, receiverType: KType): Any? {
  val allParams: List<Any> = listOf(receiver) + paramMatchersFor(callable, receiverType)
  return callable.callSuspend(*allParams.toTypedArray())
}

private fun MockKMatcherScope.paramMatchersFor(callable: KCallable<*>, receiverType: KType): List<Any> =
  callable.parameters.drop(1) // first param is always receiver
    .map { any(receiverType.resolveInnerType(it.type).classifier as KClass<*>) }
