package com.lazyviking.mariokotlin.scenes

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.lazyviking.mariokotlin.V_HEIGHT
import com.lazyviking.mariokotlin.V_WIDTH
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by missingdays on 02.02.17.
 */
class Hud : Disposable {

    val stage : Stage
    private val viewport : Viewport = FitViewport(V_WIDTH, V_HEIGHT, OrthographicCamera())

    private var worldTimer : Int = 300
    var timeUp : Boolean = false
    private var timeCount : Float = 0f
    private var score : Int = 0

    private var countdownLabel : Label = defaultLabel(String.format("%03d", worldTimer))
    private val scoreLabel : Label = defaultLabel(String.format("%06d", score))
    private val timeLabel : Label = defaultLabel("TIME")
    private val levelLabel : Label = defaultLabel("1-1")
    private val worldLabel : Label = defaultLabel("WORLD")
    private val marioLabel : Label = defaultLabel("MARIO")

    constructor(batch : SpriteBatch){
        stage = Stage(viewport, batch)

        val table = Table()
        table.top()
        table.setFillParent(true)

        table.add(marioLabel).expandX().padTop(10f)
        table.add(worldLabel).expandX().padTop(10f)
        table.add(timeLabel).expandX().padTop(10f)

        table.row()

        table.add(scoreLabel).expandX()
        table.add(levelLabel).expandX()
        table.add(countdownLabel).expandX()

        stage.addActor(table)
    }

    fun update(dt : Float){
        timeCount += dt

        if(timeCount >= 1){
            if(worldTimer > 0){
                worldTimer--;
            } else {
                timeUp = true;
            }

            countdownLabel.setText(String.format("%03d", worldTimer))
            timeCount = 0f
        }

    }

    fun addScore(value : Int){
        score += value
        scoreLabel.setText(String.format("%06d", score))
    }

    override fun dispose() {
        stage.dispose()
    }
}

fun defaultLabel(text : String) : Label{
    return Label(text, Label.LabelStyle(BitmapFont(), Color.WHITE))
}