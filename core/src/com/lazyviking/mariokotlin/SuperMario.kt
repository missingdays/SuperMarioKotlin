package com.lazyviking.mariokotlin

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.utils.AssetManager

val V_WIDTH : Float = 400f
val V_HEIGHT : Float = 208f
val PPM : Float = 100f

const val NOTHING_BIT = 0
const val GROUND_BIT = 1
const val MARIO_BIT = 2
const val BRICK_BIT = 4
const val COIN_BIT = 8
const val DESTROYED_BIT = 16
const val OBJECT_BIT = 32
const val ENEMY_BIT = 64
const val ENEMY_HEAD_BIT = 128
const val ITEM_BIT = 256
const val MARIO_HEAD_BIT = 512
const val FIREBALL_BIT = 1024

class SuperMario : Game() {

    internal var batch: SpriteBatch? = null
    internal var img: Texture? = null

    var manager : AssetManager? = null

    override fun create() {
        batch = SpriteBatch()

        manager =  AssetManager()

        setScreen(PlayScreen(this))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        super.dispose()
        manager?.dispose()
        batch?.dispose()
    }
}
