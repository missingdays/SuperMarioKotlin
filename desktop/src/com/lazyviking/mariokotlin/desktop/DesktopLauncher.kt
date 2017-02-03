package com.lazyviking.mariokotlin.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.lazyviking.mariokotlin.SuperMario

fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.width = 1200
    config.height = 624
    LwjglApplication(SuperMario(), config)
}

