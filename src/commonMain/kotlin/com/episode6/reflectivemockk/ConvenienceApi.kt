package com.episode6.reflectivemockk

import io.mockk.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

public inline fun <reified T : Any> T.reflectiveStubs(stubbing: ReflectiveStubbing<T>.() -> Unit) {
  ReflectiveStubbing<T>(this, typeOf<T>()).stubbing()
}

public inline fun <reified T : Any> reflectiveMockk(stubbing: ReflectiveStubbing<T>.() -> Unit): T =
  mockk<T>().apply { reflectiveStubs(stubbing) }

public class ReflectiveStubbing<T : Any>(public val mock: T, private val ktype: KType) {
  public val kclass: KClass<*> get() = mock.kotlinClass
  public val functions: Collection<KFunction<*>> get() = kclass.memberFunctions.filter { !it.isSuspend }
  public val suspendFunctions: Collection<KFunction<*>> get() = kclass.memberFunctions.filter { it.isSuspend }
  public val properties: Collection<KProperty1<*, *>> get() = kclass.memberProperties

  public fun everyCallTo(callable: KCallable<*>): MockKStubScope<Any?, Any?> = every { callTo(callable, mock, ktype) }
  public fun coEveryCallTo(callable: KCallable<*>): MockKStubScope<Any?, Any?> = coEvery { callTo(callable, mock, ktype) }

  @Suppress("UNCHECKED_CAST")
  public fun defaultAnswer(answer: MockKAnswerScope<T, Any?>.(Call) -> T) {
    (functions + properties).forEach { everyCallTo(it) answers(answer as AnyAnswer<T>) }
    suspendFunctions.forEach { coEveryCallTo(it) answers(answer as AnyAnswer<T>) }
  }
}

private typealias AnyAnswer<T> = MockKAnswerScope<Any?, Any?>.(Call) -> T
