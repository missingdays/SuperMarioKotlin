package com.lazyviking.mariokotlin.sprites

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.lazyviking.mariokotlin.*
import com.badlogic.gdx.physics.box2d.Filter;
import com.lazyviking.mariokotlin.sprites.enemies.Enemy
import com.lazyviking.mariokotlin.sprites.enemies.Turtle
import com.lazyviking.mariokotlin.sprites.other.Fireball
import com.lazyviking.mariokotlin.utils.AssetManager




/**
 * Created by missingdays on 02.02.17.
 */

enum class State {
    FALLING,
    JUMPING,
    STANDING,
    RUNNING,
    GROWING,
    DEAD
}

class Mario : Sprite{

    val RUN_SPEED = 0.1f

    var currentState : State = State.STANDING
    var previousState : State = State.STANDING

    val world : World
    var b2body : Body

    private val marioStand: TextureRegion
    private val marioRun: Animation<TextureRegion>
    private val marioJump: TextureRegion
    private val marioDead: TextureRegion
    private val bigMarioStand: TextureRegion
    private val bigMarioJump: TextureRegion
    private val bigMarioRun: Animation<TextureRegion>
    private val growMario: Animation<TextureRegion>

    var stateTimer: Float = 0f
    private var runningRight: Boolean = true
    var isBig: Boolean = false
    private var runGrowAnimation: Boolean = false
    private var timeToDefineBigMario: Boolean = false
    private var timeToRedefineMario: Boolean = false
    var isDead: Boolean = false
    private val screen: PlayScreen

    private val fireballs: Array<Fireball> = Array()

    private val assetManager : AssetManager

    constructor(screen : PlayScreen, assetManager : AssetManager){
        this.screen = screen
        this.world = screen.world

        this.assetManager = assetManager

        val frames = com.badlogic.gdx.utils.Array<TextureRegion>()

        for(i in 1..4){
            val region = TextureRegion(screen.atlas.findRegion("little_mario"), i*16, 0, 16, 16)
            frames.add(region)
        }
        marioRun = Animation(0.1f, frames)

        frames.clear()

        for(i in 1..4){
            val region = TextureRegion(screen.atlas.findRegion("big_mario"), i*16, 0, 16, 32)
            frames.add(region)
        }

        bigMarioRun = Animation(0.1f, frames)

        frames.clear()

        frames.add(TextureRegion(screen.atlas.findRegion("big_mario"), 240, 0, 16, 32))
        frames.add(TextureRegion(screen.atlas.findRegion("big_mario"), 0, 0, 16, 32))
        frames.add(TextureRegion(screen.atlas.findRegion("big_mario"), 240, 0, 16, 32))
        frames.add(TextureRegion(screen.atlas.findRegion("big_mario"), 0, 0, 16, 32))
        growMario = Animation(0.2f, frames)

        marioJump = TextureRegion(screen.atlas.findRegion("little_mario"), 80, 0, 16, 16)
        bigMarioJump = TextureRegion(screen.atlas.findRegion("big_mario"), 80, 0, 16, 32)

        //create texture region for mario standing
        marioStand = TextureRegion(screen.atlas.findRegion("little_mario"), 0, 0, 16, 16)
        bigMarioStand = TextureRegion(screen.atlas.findRegion("big_mario"), 0, 0, 16, 32)

        //create dead mario texture region
        marioDead = TextureRegion(screen.atlas.findRegion("little_mario"), 96, 0, 16, 16)

        b2body = defineSmallMario()

        setBounds(0f, 0f, 16 / PPM, 16 / PPM)
        setRegion(marioStand)
    }

    fun update(dt : Float){
        if(screen.hud.timeUp){
            die()
        }

        if(isBig){
            setPosition(b2body.position.x - width / 2, b2body.position.y - height / 2 - 6 / PPM)
        } else {
            setPositionFromB2Body(b2body)
        }

        setRegion(getFrame(dt))

        if(timeToDefineBigMario){
            b2body = defineBigMario()
        }

        if(timeToRedefineMario){
            b2body = defineSmallMario()
        }

        for(fireball in fireballs){
            fireball.update(dt)

            if(fireball.destroyed){
                fireballs.removeValue(fireball, true)
            }
        }
    }

    fun getFrame(dt : Float) : TextureRegion {
        currentState = getState()

        val region : TextureRegion

        when(currentState){
            State.DEAD -> region = marioDead

            State.GROWING -> {
                region = growMario.getKeyFrame(stateTimer)

                if(growMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation = false
                }
            }

            State.JUMPING -> {
                region = if(isBig) bigMarioJump else marioJump
            }

            State.RUNNING -> {
                region = if(isBig) bigMarioRun.getKeyFrame(stateTimer, true) else
                    marioRun.getKeyFrame(stateTimer, true)
            }

            else -> {
                region = if(isBig) bigMarioStand else marioStand
            }
        }

        if((b2body.linearVelocity.x < 0 || !runningRight) && !region.isFlipX){
            region.flip(true, false)
            runningRight = false
        } else if((b2body.linearVelocity.x > 0 || runningRight) && region.isFlipX){
            region.flip(true, false)
            runningRight = true
        }

        stateTimer = if(currentState == previousState) stateTimer + dt else 0f

        previousState = currentState

        return region
    }

    fun getState() : State {
        if(isDead){
            return State.DEAD
        } else if(runGrowAnimation){
            return State.GROWING
        } else if(b2body.linearVelocity.y > 0 && currentState == State.JUMPING
                || b2body.linearVelocity.y < 0 && previousState == State.JUMPING ){
            return State.JUMPING
        } else if(b2body.linearVelocity.y < 0){
            return State.FALLING
        } else if(b2body.linearVelocity.x != 0f){
            return State.RUNNING
        } else {
            return State.STANDING
        }
    }

    fun grow(){
        if(!isBig){
            runGrowAnimation = true
            isBig = true
            timeToDefineBigMario = true
            setBounds(x, y, width, height*2)
            assetManager.powerupSound.play()
        }
    }

    fun die(){
        if(!isDead){
            assetManager.marioMusic.stop()
            assetManager.marioDieSound.play()

            isDead = true

            val filter: Filter = Filter()
            filter.maskBits = NOTHING_BIT.toShort()

            for(fixture in b2body.fixtureList){
                fixture.filterData = filter
            }

            b2body.applyLinearImpulse(Vector2(0f, 4f), b2body.worldCenter, true)

        }
    }

    fun jump(){
        if(currentState != State.JUMPING){
            b2body.applyLinearImpulse(Vector2(0f, 4f), b2body.worldCenter, true)
            currentState = State.JUMPING
        }
    }

    fun moveRight(){
        b2body.applyLinearImpulse(Vector2(RUN_SPEED, 0f), b2body.worldCenter, true)
    }

    fun moveLeft(){
        b2body.applyLinearImpulse(Vector2(-RUN_SPEED, 0f), b2body.worldCenter, true)
    }

    fun hit(enemy : Enemy){
        if(enemy is Turtle && enemy.isStanding()){
            enemy.kick(b2body)
        } else {
            if(isBig){
                isBig = false
                timeToRedefineMario = true
                setBounds(x, y, width, height/2)
                assetManager.powerdownSound.play()
            } else {
                die()
            }
        }
    }

    fun defineSmallMario() : Body {

        val bdef = BodyDef()

        // If mario was already created
        if(b2body != null) {
            val position = b2body.position
            world.destroyBody(b2body)
            bdef.position.set(position)
        } else {
            bdef.position.set(32 / PPM, 64 / PPM)
        }

        bdef.type = BodyDef.BodyType.DynamicBody
        b2body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()
        shape.radius = 6 / PPM
        fdef.filter.categoryBits = MARIO_BIT.toShort()
        fdef.filter.maskBits = (GROUND_BIT or
                COIN_BIT or
                BRICK_BIT or
                ENEMY_BIT or
                OBJECT_BIT or
                ENEMY_HEAD_BIT or
                ITEM_BIT).toShort()

        fdef.shape = shape
        b2body.createFixture(fdef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2 / PPM, 6 / PPM), Vector2(2 / PPM, 6 / PPM))
        fdef.filter.categoryBits = MARIO_HEAD_BIT.toShort()
        fdef.shape = head
        fdef.isSensor = true

        b2body.createFixture(fdef).userData = this

        timeToRedefineMario = false

        return b2body
    }

    fun defineBigMario() : Body {

        val currentPosition = b2body.position
        world.destroyBody(b2body)

        val bdef = BodyDef()
        bdef.position.set(currentPosition.add(0f, 10 / PPM))
        bdef.type = BodyDef.BodyType.DynamicBody
        b2body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()
        shape.radius = 6 / PPM
        fdef.filter.categoryBits = MARIO_BIT.toShort()
        fdef.filter.maskBits = (GROUND_BIT or
                COIN_BIT or
                BRICK_BIT or
                ENEMY_BIT or
                OBJECT_BIT or
                ENEMY_HEAD_BIT or
                ITEM_BIT).toShort()

        fdef.shape = shape
        b2body.createFixture(fdef).userData = this
        shape.position = Vector2(0f, -14 / PPM)
        b2body.createFixture(fdef).userData = this

        val head = EdgeShape()
        head.set(Vector2(-2 / PPM, 6 / PPM), Vector2(2 / PPM, 6 / PPM))
        fdef.filter.categoryBits = MARIO_HEAD_BIT.toShort()
        fdef.shape = head
        fdef.isSensor = true

        b2body.createFixture(fdef).userData = this
        timeToDefineBigMario = false

        return b2body
    }

    fun fire(){
        fireballs.add(Fireball(screen, b2body.position.x, b2body.position.y, runningRight))
    }

    override fun draw(batch : Batch){
        super.draw(batch)

        for(fireball in fireballs){
            fireball.draw(batch)
        }
    }
}