package com.episode6.reflectivemockk

import kotlin.reflect.KClass

internal expect val Any.kotlinClass: KClass<*>
