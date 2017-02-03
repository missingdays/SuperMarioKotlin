package com.lazyviking.mariokotlin.sprites.tileObjects

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import com.badlogic.gdx.math.Vector2
import com.lazyviking.mariokotlin.COIN_BIT
import com.lazyviking.mariokotlin.PPM
import com.lazyviking.mariokotlin.scenes.Hud
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.sprites.items.ItemDef
import com.lazyviking.mariokotlin.sprites.items.Mushroom
import com.lazyviking.mariokotlin.utils.AssetManager

/**
 * Created by missingdays on 03.02.17.
 */
class Coin : InteractiveTileObject{
    private val tileSet : TiledMapTileSet
    private val BLANK_COIN = 28

    constructor(screen : PlayScreen, mapObject : RectangleMapObject, hud : Hud, assetManager : AssetManager)
            : super(screen, mapObject, hud, assetManager){

        tileSet = map.tileSets.getTileSet("tileset_gutter")
        fixture.userData = this
        setCategoryFilter(COIN_BIT.toShort())
    }

    override fun onHeadHit(mario: Mario) {
        if(getCell().tile.id == BLANK_COIN){
            assetManager.bumpSound.play()
        } else {
            if(mapObject.properties.containsKey("mushroom")){
                val idef = ItemDef(Vector2(body.position.x, body.position.y + 16 / PPM), Mushroom::class)
                screen.spawnItem(idef)

                assetManager.powerupSpawnSound.play()
            } else {
                assetManager.coinSound.play()
            }

            getCell().tile = tileSet.getTile(BLANK_COIN)
            hud.addScore(100)
        }
    }
}