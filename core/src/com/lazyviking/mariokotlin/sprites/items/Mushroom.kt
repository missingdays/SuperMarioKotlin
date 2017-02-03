package com.lazyviking.mariokotlin.sprites.items

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.lazyviking.mariokotlin.*
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Mario

/**
 * Created by missingdays on 03.02.17.
 */
class Mushroom : Item{

    val SPEED = 0.7f

    constructor(screen : PlayScreen, x : Float, y : Float) : super(screen, x, y){
        setRegion(screen.atlas.findRegion("mushroom"), 0, 0, 16, 16)
        velocity.x = SPEED
    }

    override fun defineItem(): Body {
        val bdef = BodyDef()
        bdef.position.set(x, y)
        bdef.type = BodyDef.BodyType.DynamicBody
        val body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()
        shape.radius = 6 / PPM

        fdef.filter.categoryBits = ITEM_BIT.toShort()
        fdef.filter.maskBits = (MARIO_BIT or
                OBJECT_BIT or
                GROUND_BIT or
                COIN_BIT or
                BRICK_BIT).toShort()

        fdef.shape = shape
        body.createFixture(fdef).userData = this

        return body
    }

    override fun use(mario: Mario) {
        destroy()
        mario.grow()
    }

    override fun update(dt : Float){
        super.update(dt)

        setPositionFromB2Body(body)

        velocity.y = body.linearVelocity.y
        body.linearVelocity = velocity
    }

}