package com.episode6.reflectivemockk

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

/**
 * Resolve a type that is referenced in the @receiver
 */
internal fun KType.resolveType(referencedType: KType): KType =
  when (val referencedClassifier = referencedType.classifier) {
    is KTypeParameter -> resolveTypeParam(referencedClassifier)
    is KClass<*>      -> referencedClassifier.createType(
      arguments = referencedType.arguments.map { resolveTypeProjection(it) },
      nullable = referencedType.isMarkedNullable
    )

    else              -> throw TypeTokenResolutionError(referencedType = referencedType, context = this)
  }

private fun KType.resolveTypeProjection(referencedType: KTypeProjection): KTypeProjection =
  when (val referencedClassifier = referencedType.type?.classifier) {
    is KTypeParameter -> referencedType.copy(type = resolveTypeParam(referencedClassifier))
    else              -> referencedType
  }

private fun KType.resolveTypeParam(param: KTypeParameter): KType {
  val index = indexOfTypeParamNamed(param.name)
  return when {
    index < 0 -> param.upperBounds[0] // kind of a hack, wont work with complex generics
    else      -> arguments[index].type!!
  }
}

private fun KType.indexOfTypeParamNamed(name: String): Int =
  (classifier as KClass<*>).typeParameters.indexOfFirst { it.name == name }

/**
 * Thrown when an error occurs while trying to resolve a [KType] into a concrete [TypeToken]
 */
public class TypeTokenResolutionError(referencedType: KType, context: KType) :
  AssertionError("Error resolving type $referencedType given context of $context")
