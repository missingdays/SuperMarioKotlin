package com.lazyviking.mariokotlin.sprites.other

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.lazyviking.mariokotlin.*
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Sprite

/**
 * Created by missingdays on 03.02.17.
 */

class Fireball : Sprite {
    val screen : PlayScreen
    val world : World
    val frames : Array<TextureRegion> = Array()
    val fireAnimation : Animation<TextureRegion>

    var stateTime : Float = 0f
    var destroyed = false
    var setToDestroy = false
    val fireRight : Boolean

    val b2body : Body

    constructor(screen : PlayScreen, x : Float, y : Float, fireRight : Boolean){
        this.screen = screen
        this.world = screen.world
        this.fireRight = fireRight

        for (i in 0 until 4) {
            frames.add(TextureRegion(screen.atlas.findRegion("fireball"), i * 8, 0, 8, 8))
        }

        fireAnimation = Animation(0.2f, frames)

        setRegion(fireAnimation.getKeyFrame(0f))

        setBounds(x, y, 6 / PPM, 6 / PPM)

        b2body = defineFireball()
    }

    fun defineFireball() : Body {
        val bdef = BodyDef()
        val xSpawn = if(fireRight) x + 12 / PPM else x - 12 / PPM
        bdef.position.set(xSpawn, y)
        bdef.type = BodyDef.BodyType.DynamicBody

        val b2body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()
        shape.radius = 3 / PPM

        fdef.filter.categoryBits = FIREBALL_BIT.toShort()
        fdef.filter.maskBits = (GROUND_BIT or
                COIN_BIT or
                BRICK_BIT or
                ENEMY_BIT or
                OBJECT_BIT).toShort()

        fdef.shape = shape
        fdef.restitution = 1f
        fdef.friction = 0f

        b2body.createFixture(fdef).userData = this
        b2body.linearVelocity = Vector2(if(fireRight) 2f else -2f, 2.5f)

        return b2body
    }

    fun update(dt : Float){
        stateTime += dt
        setRegion(fireAnimation.getKeyFrame(stateTime, true))
        setPositionFromB2Body(b2body)

        if(stateTime > 3 || setToDestroy){
            if(!destroyed){
                world.destroyBody(b2body)
                destroyed = true
            }
        }

        if(!destroyed){
            if(b2body.linearVelocity.y > 2f){
                b2body.linearVelocity.y = 2f
            }

            if((fireRight && b2body.linearVelocity.x < 0) || (!fireRight && b2body.linearVelocity.x > 0))
                setToDestroy = true
        }
    }


}