package com.episode6.reflectivemockk

import io.mockk.MockKMatcherScope
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Accepts a raw KType (that was found in the context of [RECEIVER]), resolves the type and returns
 * [any] using the resolved type.
 */
@PublishedApi
internal fun MockKMatcherScope.reflectiveAny(receiverType: KType, rawParamType: KType): Any {
  val paramType = receiverType.resolveInnerType(rawParamType)
  return any(paramType.classifier as KClass<*>)
}

public inline fun <reified RECEIVER: Any> MockKMatcherScope.callTo(receiver: RECEIVER, callable: KCallable<*>): Any? {
  val receiverType = typeOf<RECEIVER>()
  val params = callable.parameters.drop(1) // first param is always receiver
    .map { reflectiveAny(receiverType = receiverType, rawParamType = it.type) }
  val allParams: List<Any> = listOf(receiver) + params
  return callable.call(*allParams.toTypedArray())
}
