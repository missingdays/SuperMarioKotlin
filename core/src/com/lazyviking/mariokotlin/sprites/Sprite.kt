package com.lazyviking.mariokotlin.sprites

import com.badlogic.gdx.physics.box2d.Body

/**
 * Created by missingdays on 02.02.17.
 */
open class Sprite : com.badlogic.gdx.graphics.g2d.Sprite(){
    init {}

    fun setPositionFromB2Body(b2body : Body){
        setPosition(b2body.position.x - width / 2, b2body.position.y - height / 2)
    }
}