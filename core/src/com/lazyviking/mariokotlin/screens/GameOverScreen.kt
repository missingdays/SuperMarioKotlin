package com.lazyviking.mariokotlin.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.lazyviking.mariokotlin.SuperMario
import com.lazyviking.mariokotlin.V_HEIGHT
import com.lazyviking.mariokotlin.V_WIDTH

/**
 * Created by missingdays on 03.02.17.
 */
class GameOverScreen : Screen {
    private val viewport : Viewport = FitViewport(V_WIDTH, V_HEIGHT, OrthographicCamera())
    private val stage : Stage

    private val game : Game

    constructor(game : Game){
        this.game = game

        stage = Stage(viewport, (game as SuperMario).batch)

        val font = Label.LabelStyle(BitmapFont(), Color.WHITE)

        val table = Table()
        table.center()
        table.setFillParent(true)

        val gameOverLabel = Label("GAME OVER", font)
        val playAgainLabel = Label("Click to Play Again", font)

        table.add(gameOverLabel).expandX()
        table.row()
        table.add(playAgainLabel).expandX()

        stage.addActor(table)
    }

    override fun show(){

    }

    override fun render(dt : Float){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.draw()

        if(Gdx.input.isTouched){
            game.screen = PlayScreen(game as SuperMario)
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume(){}
}