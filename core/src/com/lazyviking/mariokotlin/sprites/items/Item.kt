package com.lazyviking.mariokotlin.sprites.items

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.lazyviking.mariokotlin.PPM
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.sprites.Sprite

/**
 * Created by missingdays on 03.02.17.
 */
open abstract class Item : Sprite {
    protected val screen : PlayScreen
    protected val world : World
    protected val velocity = Vector2(0f, 0f)
    protected var toDestroy = false
    protected var destroyed = false
    protected val body : Body

    constructor(screen : PlayScreen, x : Float, y : Float){
        this.screen = screen
        this.world = screen.world

        setPosition(x, y)
        setBounds(getX(), getY(), 16 / PPM, 16 / PPM)

        body = defineItem()
    }

    public abstract fun defineItem() : Body
    public abstract fun use(mario : Mario)

    open fun update(dt : Float){
        if(toDestroy && !destroyed){
            world.destroyBody(body)
            destroyed = true
        }
    }

    override fun draw(batch : Batch){
        if(!destroyed){
            super.draw(batch)
        }
    }

    fun destroy(){
        toDestroy = true
    }

    fun reverseVelocity(x : Boolean, y : Boolean){
        if(x){
            velocity.x = -velocity.x
        }

        if(y){
            velocity.y = -velocity.y
        }
    }
}