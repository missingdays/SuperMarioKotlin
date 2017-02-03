package com.lazyviking.mariokotlin.sprites.enemies

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.lazyviking.mariokotlin.*
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.utils.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.lazyviking.mariokotlin.sprites.Mario


/**
 * Created by missingdays on 03.02.17.
 */

enum class State {
    WALKING,
    MOVING_SHELL,
    STANDING_SHELL
}

class Turtle : Enemy{
    val KICK_LEFT = -2f
    val KICK_RIGHT = 2f

    var currentState : State = State.WALKING
    var previousState : State = State.WALKING

    var stateTime : Float = 0f

    private val walkAnimation : Animation<TextureRegion>

    private val frames : Array<TextureRegion> = com.badlogic.gdx.utils.Array()

    private val shell : TextureRegion
    private var setToDestroy = false
    private var destroyed = false

    constructor(screen : PlayScreen, x : Float, y : Float, assetManager : AssetManager)
            : super(screen, x, y, assetManager){

        frames.add(TextureRegion(screen.atlas.findRegion("turtle"), 0, 0, 16, 24))
        frames.add(TextureRegion(screen.atlas.findRegion("turtle"), 16, 0, 16, 24))

        shell = TextureRegion(screen.atlas.findRegion("turtle"), 64, 0, 16, 24)

        walkAnimation = Animation<TextureRegion>(0.2f, frames)

        setBounds(x, y, 16 / PPM, 16 / PPM)
    }

    override protected fun defineEnemy(): Body {
        val bdef = BodyDef()
        bdef.position.set(x, y)
        bdef.type = BodyDef.BodyType.DynamicBody

        val b2body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()
        shape.radius = 6 / PPM

        fdef.filter.categoryBits = ENEMY_BIT.toShort()
        fdef.filter.maskBits = (GROUND_BIT or
            COIN_BIT or
            BRICK_BIT or
            ENEMY_BIT or
            OBJECT_BIT or
            MARIO_BIT).toShort()

        fdef.shape = shape
        b2body.createFixture(fdef).userData = this

        val head = PolygonShape()
        val vertice = arrayOfNulls<Vector2>(4)
        vertice[0] = Vector2(-5f, 8f).scl(1 / PPM)
        vertice[1] = Vector2(5f, 8f).scl(1 / PPM)
        vertice[2] = Vector2(-3f, 3f).scl(1 / PPM)
        vertice[3] = Vector2(3f, 3f).scl(1 / PPM)
        head.set(vertice)

        fdef.shape = head
        fdef.restitution = 1.8f
        fdef.filter.categoryBits = ENEMY_HEAD_BIT.toShort()
        b2body.createFixture(fdef).userData = this

        return b2body

    }

    fun getFrame(dt : Float) : TextureRegion {
        val region : TextureRegion

        when(currentState){
            State.MOVING_SHELL -> region = shell
            State.STANDING_SHELL -> region = shell
            else -> region = walkAnimation.getKeyFrame(stateTime, true)
        }

        if(velocity.x > 0 && !region.isFlipX){
            region.flip(true, false)
        }

        if(velocity.x < 0 && region.isFlipX){
            region.flip(true, false)
        }

        stateTime = if(currentState == previousState) stateTime + dt else 0f

        previousState = currentState

        return region
    }

    override fun update(dt: Float) {
        setRegion(getFrame(dt))

        if(currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING
            velocity.x = 1f
        }

        setPosition(b2body.position.x - width / 2, b2body.position.y - 8 /PPM)
        b2body.linearVelocity = velocity
    }

    override fun hitOnHead(mario: Mario) {
        if(currentState == State.STANDING_SHELL){
            if(mario.b2body.position.x > b2body.position.x){
                velocity.x = KICK_LEFT
            } else {
                velocity.x = KICK_RIGHT
            }

            currentState = State.MOVING_SHELL
        } else {
            currentState = State.STANDING_SHELL
            velocity.x = 0f
        }
    }

    override fun hitByEnemy(enemy: Enemy) {
        reverseVelocity(true, false)
    }

    fun kick(marioBody : Body){
        if(b2body.position.x > marioBody.position.x){
            velocity.x = KICK_RIGHT
        } else {
            velocity.x = KICK_LEFT
        }

        currentState = State.MOVING_SHELL
    }

    fun isStanding() : Boolean {
        return currentState == State.STANDING_SHELL
    }

    fun isMoving() : Boolean {
        return currentState == State.MOVING_SHELL
    }

}