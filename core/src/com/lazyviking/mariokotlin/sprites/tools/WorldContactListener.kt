package com.lazyviking.mariokotlin.sprites.tools

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.lazyviking.mariokotlin.*
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.sprites.enemies.Enemy
import com.lazyviking.mariokotlin.sprites.items.Item
import com.lazyviking.mariokotlin.sprites.other.Fireball
import com.lazyviking.mariokotlin.sprites.tileObjects.InteractiveTileObject

/**
 * Created by missingdays on 03.02.17.
 */

class WorldContactListener : ContactListener {
    override fun beginContact(contact: Contact?) {
        if(contact == null){
            return
        }

        val fixA = contact.fixtureA
        val fixB = contact.fixtureB

        val catA = fixA.filterData.categoryBits.toInt()
        val catB = fixA.filterData.categoryBits.toInt()

        val cDef = fixA.filterData.categoryBits.toInt() or fixB.filterData.categoryBits.toInt()

        when(cDef) {
            MARIO_HEAD_BIT or BRICK_BIT -> {
                if(catA == MARIO_HEAD_BIT){
                    (fixB.userData as InteractiveTileObject).onHeadHit(fixA.userData as Mario)
                } else {
                    (fixA.userData as InteractiveTileObject).onHeadHit(fixB.userData as Mario)
                }
            }

            MARIO_HEAD_BIT or COIN_BIT -> {
                if(catA == MARIO_HEAD_BIT){
                    (fixB.userData as InteractiveTileObject).onHeadHit(fixA.userData as Mario)
                } else {
                    (fixA.userData as InteractiveTileObject).onHeadHit(fixB.userData as Mario)
                }
            }

            ENEMY_HEAD_BIT or MARIO_BIT -> {
                if(catA == ENEMY_HEAD_BIT){
                    (fixA.userData as Enemy).hitOnHead(fixB.userData as Mario)
                } else {
                    (fixB.userData as Enemy).hitOnHead(fixA.userData as Mario)
                }
            }

            ENEMY_BIT or OBJECT_BIT -> {
                if(catA == ENEMY_BIT){
                    (fixA.userData as Enemy).reverseVelocity(true, false)
                } else {
                    (fixB.userData as Enemy).reverseVelocity(true, false)
                }
            }

            MARIO_BIT or ENEMY_BIT -> {
                if(catA == MARIO_BIT){
                    (fixA.userData as Mario).hit(fixB.userData as Enemy)
                } else {
                    (fixB.userData as Mario).hit(fixA.userData as Enemy)
                }
            }

            ENEMY_BIT or ENEMY_BIT -> {
                (fixA.userData as Enemy).hitByEnemy(fixB.userData as Enemy)
                (fixB.userData as Enemy).hitByEnemy(fixA.userData as Enemy)
            }

            ITEM_BIT or ENEMY_BIT -> {
                if(catA == ITEM_BIT){
                    (fixA.userData as Item).reverseVelocity(true, false)
                } else {
                    (fixB.userData as Item).reverseVelocity(true, false)
                }
            }

            ITEM_BIT or MARIO_BIT -> {
                if(catA == ITEM_BIT){
                    (fixA.userData as Item).use(fixB.userData as Mario)
                } else {
                    (fixB.userData as Item).use(fixA.userData as Mario)
                }
            }

            FIREBALL_BIT or OBJECT_BIT -> {
                if(catA == FIREBALL_BIT){
                    (fixA.userData as Fireball).setToDestroy = true
                } else {
                    (fixB.userData as Fireball).setToDestroy = true
                }
            }
        }
    }

    override fun endContact(contact: Contact?) {
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    }
}