package com.lazyviking.mariokotlin.sprites.enemies


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.lazyviking.mariokotlin.*
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.badlogic.gdx.math.Vector2
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.utils.AssetManager


/**
 * Created by missingdays on 02.02.17.
 */

class Goomba : Enemy{
    private var stateTime : Float = 0f
    private val walkAnimation : Animation<TextureRegion>
    private val frames : Array<TextureRegion> = Array()
    private var setToDestroy : Boolean = false
    private var destroyed : Boolean = false

    public var angle : Float = 0f

    constructor(screen : PlayScreen, x : Float, y : Float, assetManager : AssetManager)
            : super(screen, x, y, assetManager){

        for(i in 0 until 2){
            val region = TextureRegion(screen.atlas.findRegion("goomba"), i*16, 0, 16, 16)
            frames.add(region)
        }

        walkAnimation = Animation(0.4f, frames)

        setBounds(getX(), getY(), 16 / PPM, 16 / PPM)
    }

    override fun update(dt : Float){
        stateTime += dt

        if(setToDestroy && !destroyed){
            world.destroyBody(b2body)
            destroyed = true
            setRegion(TextureRegion(screen.atlas.findRegion("goomba"), 32, 0, 16, 16))
        } else if(!destroyed){
            b2body.linearVelocity = velocity
            setPositionFromB2Body(b2body)
            setRegion(walkAnimation.getKeyFrame(stateTime, true))
        }
    }

    override fun defineEnemy() : Body {
        val bdef = BodyDef()
        bdef.position.set(x, y)
        bdef.type = BodyDef.BodyType.DynamicBody

        val b2body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()

        shape.radius = 6 / PPM

        fdef.filter.categoryBits = ENEMY_BIT.toShort()
        fdef.filter.maskBits = GROUND_BIT.or(COIN_BIT).or(
                BRICK_BIT).or(ENEMY_BIT).or(OBJECT_BIT).or(MARIO_BIT).toShort()

        fdef.shape = shape
        b2body.createFixture(fdef).userData = this

        val head = PolygonShape()

        val vertices = arrayOfNulls<Vector2>(4)
        vertices[0] = Vector2(-5f, 8f).scl(1 / PPM)
        vertices[1] = Vector2(5f, 8f).scl(1 / PPM)
        vertices[2] = Vector2(-3f, 3f).scl(1 / PPM)
        vertices[3] = Vector2(3f, 3f).scl(1 / PPM)
        head.set(vertices)

        fdef.shape = head
        fdef.restitution = 0.5f
        fdef.filter.categoryBits = ENEMY_HEAD_BIT.toShort()
        b2body.createFixture(fdef).userData = this

        return b2body
    }

    override fun draw(batch : Batch){
        if(!destroyed || stateTime < 1){
            super.draw(batch)
        }
    }

    override fun hitOnHead(mario: Mario) {
        setToDestroy = true
        assetManager.stompSound.play()
    }

    override fun hitByEnemy(enemy: Enemy) {
        if (enemy is Turtle && enemy.isMoving())
            setToDestroy = true
        else
            reverseVelocity(true, false)
    }
}