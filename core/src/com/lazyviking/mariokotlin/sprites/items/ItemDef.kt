package com.lazyviking.mariokotlin.sprites.items

import com.badlogic.gdx.math.Vector2
import kotlin.reflect.KClass

/**
 * Created by missingdays on 03.02.17.
 */
data class ItemDef(val position : Vector2, val type : KClass<*>)