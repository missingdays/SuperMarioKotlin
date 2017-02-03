package com.lazyviking.mariokotlin.sprites.tools

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.lazyviking.mariokotlin.GROUND_BIT
import com.lazyviking.mariokotlin.NOTHING_BIT
import com.lazyviking.mariokotlin.OBJECT_BIT
import com.lazyviking.mariokotlin.PPM
import com.lazyviking.mariokotlin.scenes.Hud
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.enemies.Enemy
import com.lazyviking.mariokotlin.sprites.enemies.Goomba
import com.lazyviking.mariokotlin.sprites.enemies.Turtle
import com.lazyviking.mariokotlin.sprites.tileObjects.Coin
import com.lazyviking.mariokotlin.sprites.tileObjects.Brick
import com.lazyviking.mariokotlin.utils.AssetManager


/**
 * Created by missingdays on 03.02.17.
 */
class B2WorldCreator {
    private val goombas : Array<Goomba> = Array()
    private var turtles : Array<Turtle> = Array()

    private val world : World
    private val map : TiledMap
    private val assetManager : AssetManager
    private val hud : Hud

    constructor(screen : PlayScreen, assetManager : AssetManager){
        world = screen.world
        map = screen.map
        hud = screen.hud

        this.assetManager = assetManager

        createObjectFromLayer(2, GROUND_BIT.toShort())
        createObjectFromLayer(3, OBJECT_BIT.toShort())

        for (mapObject in map.layers.get(5).objects.getByType(RectangleMapObject::class.java)) {
            Brick(screen, mapObject, hud, assetManager)
        }

        //create coin bodies/fixtures
        for (mapObject in map.layers.get(4).objects.getByType(RectangleMapObject::class.java)) {

            Coin(screen, mapObject, hud, assetManager)
        }

        for (mapObject in map.layers.get(6).objects.getByType(RectangleMapObject::class.java)) {
            val rect = mapObject.rectangle
            goombas.add(Goomba(screen, rect.getX() / PPM, rect.getY() / PPM, assetManager))
        }

        for (mapObject in map.layers.get(7).objects.getByType(RectangleMapObject::class.java)) {
            val rect = (mapObject as RectangleMapObject).rectangle
            turtles.add(Turtle(screen, rect.getX() / PPM, rect.getY() / PPM, assetManager))
        }
    }

    fun createObjectFromLayer(layer : Int, categoryBits : Short){
        val bdef = BodyDef()
        val shape = PolygonShape()
        val fdef = FixtureDef()

        var body : Body

        for(mapObject in map.layers.get(layer).objects.getByType(RectangleMapObject::class.java)){
            val rect : Rectangle = mapObject.rectangle

            bdef.type = BodyDef.BodyType.StaticBody
            bdef.position.set((rect.x + rect.width / 2) / PPM, (rect.y + rect.height / 2) / PPM)

            body = world.createBody(bdef)

            shape.setAsBox(rect.width / 2 / PPM, rect.height / 2 / PPM)
            fdef.shape = shape
            fdef.filter.categoryBits = categoryBits
            body.createFixture(fdef)
        }
    }

    fun getEnemies() : Array<Enemy>{
        val enemies = Array<Enemy>()

        enemies.addAll(goombas)
        enemies.addAll(turtles)

        return enemies
    }
}