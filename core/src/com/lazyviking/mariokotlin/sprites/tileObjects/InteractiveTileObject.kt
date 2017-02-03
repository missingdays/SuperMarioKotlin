package com.lazyviking.mariokotlin.sprites.tileObjects

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import com.lazyviking.mariokotlin.PPM
import com.lazyviking.mariokotlin.scenes.Hud
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.utils.AssetManager

/**
 * Created by missingdays on 03.02.17.
 */
abstract open class InteractiveTileObject {
    protected val world : World
    protected val map : TiledMap
    protected val bounds : Rectangle
    protected val body : Body
    protected val screen : PlayScreen
    protected val mapObject : RectangleMapObject
    protected val fixture : Fixture
    protected val hud : Hud
    protected val assetManager : AssetManager

    constructor(screen : PlayScreen, mapObject : RectangleMapObject, hud : Hud, assetManager : AssetManager){
        this.mapObject = mapObject
        this.screen = screen
        this.world = screen.world
        this.map = screen.map
        this.hud = hud
        this.assetManager = assetManager

        this.bounds = mapObject.rectangle

        val bdef = BodyDef()
        val fdef = FixtureDef()
        val shape = PolygonShape()

        bdef.type = BodyDef.BodyType.StaticBody
        bdef.position.set((bounds.x + bounds.width / 2) / PPM, (bounds.y + bounds.height / 2) / PPM);

        body = world.createBody(bdef)

        shape.setAsBox(bounds.width / 2 / PPM, bounds.height / 2 / PPM)
        fdef.shape = shape
        fixture = body.createFixture(fdef)
    }

    abstract fun onHeadHit(mario : Mario)

    fun setCategoryFilter(filterBit : Short){
        val filter = Filter()
        filter.categoryBits = filterBit
        fixture.filterData = filter
    }

    fun getCell() : TiledMapTileLayer.Cell {
        val layer : TiledMapTileLayer = map.layers.get(1) as TiledMapTileLayer

        return layer.getCell((body.position.x * PPM / 16).toInt(), (body.position.y * PPM / 16).toInt())
    }
}