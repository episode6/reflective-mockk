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
    is KTypeParameter -> resolveTypeParamNamed(referencedClassifier.name)
    is KClass<*>      -> referencedClassifier.createType(
      arguments = referencedType.arguments.map { resolveTypeProjection(it) },
      nullable = referencedType.isMarkedNullable
    )
    else              -> throw TypeTokenResolutionError(referencedType = referencedType, context = this)
  }

private fun KType.resolveTypeProjection(referencedType: KTypeProjection): KTypeProjection =
  when (val referencedClassifier = referencedType.type?.classifier) {
    is KTypeParameter -> referencedType.copy(type = resolveTypeParamNamed(referencedClassifier.name))
    else              -> referencedType
  }

private fun KType.resolveTypeParamNamed(name: String): KType =
  arguments[indexOfTypeParamNamed(name)].type!!

private fun KType.indexOfTypeParamNamed(name: String): Int =
  (classifier as KClass<*>).typeParameters.indexOfFirst { it.name == name }

/**
 * Thrown when an error occurs while trying to resolve a [KType] into a concrete [TypeToken]
 */
public class TypeTokenResolutionError(referencedType: KType, context: KType) :
  AssertionError("Error resolving type $referencedType given context of $context")
