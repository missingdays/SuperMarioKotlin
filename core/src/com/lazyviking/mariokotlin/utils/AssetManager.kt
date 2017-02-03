package com.lazyviking.mariokotlin.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

/**
 * Created by missingdays on 01.02.17.
 */
class AssetManager() : com.badlogic.gdx.assets.AssetManager(){

    val marioMusic : Music
    val coinSound : Sound
    val bumpSound : Sound
    val breakBlockSound : Sound
    val powerupSpawnSound : Sound
    val powerupSound : Sound
    val powerdownSound : Sound
    val stompSound : Sound
    val marioDieSound : Sound

    fun newMusic(path : String) : Music {
        return Gdx.audio.newMusic(Gdx.files.internal(path));
    }

    fun newSound(path : String) : Sound {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    init {
        marioMusic = newMusic("audio/music/mario_music.ogg")
        coinSound = newSound("audio/sounds/coin.wav")
        bumpSound = newSound("audio/sounds/bump.wav")
        breakBlockSound = newSound("audio/sounds/breakblock.wav")
        powerupSpawnSound = newSound("audio/sounds/powerup_spawn.wav")
        powerupSound = newSound("audio/sounds/powerup.wav")
        powerdownSound = newSound("audio/sounds/powerdown.wav")
        stompSound = newSound("audio/sounds/stomp.wav")
        marioDieSound = newSound("audio/sounds/mariodie.wav")

        finishLoading()
    }

    override fun dispose() {
        super.dispose()

        marioMusic.dispose()
        coinSound.dispose()
        bumpSound.dispose()
        breakBlockSound.dispose()
        powerupSound.dispose()
        powerupSpawnSound.dispose()
        powerdownSound.dispose()
        stompSound.dispose()
        marioMusic.dispose()
    }
}