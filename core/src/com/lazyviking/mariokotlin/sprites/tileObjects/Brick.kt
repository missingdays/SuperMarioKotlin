package com.lazyviking.mariokotlin.sprites.tileObjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.lazyviking.mariokotlin.BRICK_BIT
import com.lazyviking.mariokotlin.DESTROYED_BIT
import com.lazyviking.mariokotlin.scenes.Hud
import com.lazyviking.mariokotlin.screens.PlayScreen
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.utils.AssetManager

/**
 * Created by missingdays on 03.02.17.
 */

class Brick : InteractiveTileObject{

    constructor(screen : PlayScreen, mapObject : RectangleMapObject, hud : Hud, assetManager : AssetManager)
            : super(screen, mapObject, hud, assetManager){
        fixture.userData = this
        setCategoryFilter(BRICK_BIT.toShort())
    }

    override fun onHeadHit(mario: Mario) {
        if(mario.isBig){
            setCategoryFilter(DESTROYED_BIT.toShort())
            getCell().tile = null
            hud.addScore(200)
            assetManager.breakBlockSound.play()
        }

        assetManager.bumpSound.play()
    }
}