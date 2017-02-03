package com.lazyviking.mariokotlin.sprites.enemies

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.sprites.Sprite
import com.lazyviking.mariokotlin.utils.AssetManager

/**
 * Created by missingdays on 02.02.17.
 */
abstract class Enemy : Sprite {
    protected val world : World
    protected val screen : PlayScreen

    public val b2body : Body
    public val velocity : Vector2 = Vector2(-1f, -2f)
    public val assetManager : AssetManager

    constructor(screen : PlayScreen, x : Float, y : Float, assetManager : AssetManager){
        this.world = screen.world
        this.screen = screen
        this.assetManager = assetManager

        setPosition(x, y)

        b2body = defineEnemy()

        b2body.isActive = false
    }

    protected abstract fun defineEnemy() : Body
    abstract fun update(dt : Float)
    abstract fun hitOnHead(mario : Mario)
    abstract fun hitByEnemy(enemy : Enemy)

    fun reverseVelocity(x : Boolean, y : Boolean){
        if(x){
            velocity.x = -velocity.x
        }

        if(y){
            velocity.y = -velocity.y
        }
    }
}