package com.episode6.reflectivemockk

import kotlin.reflect.KClass

internal actual val Any.kotlinClass: KClass<*> get() = javaClass.kotlin
