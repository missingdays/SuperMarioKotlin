package com.lazyviking.mariokotlin.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.Viewport
import com.lazyviking.mariokotlin.SuperMario
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.lazyviking.mariokotlin.PPM
import com.lazyviking.mariokotlin.V_HEIGHT
import com.lazyviking.mariokotlin.V_WIDTH
import com.lazyviking.mariokotlin.scenes.Hud
import com.lazyviking.mariokotlin.sprites.Mario
import com.lazyviking.mariokotlin.sprites.items.Item
import com.lazyviking.mariokotlin.sprites.items.ItemDef
import com.lazyviking.mariokotlin.sprites.items.Mushroom
import com.lazyviking.mariokotlin.sprites.tools.B2WorldCreator
import com.lazyviking.mariokotlin.sprites.tools.WorldContactListener
import java.util.concurrent.LinkedBlockingQueue


/**
 * Created by missingdays on 01.02.17.
 */
class PlayScreen : Screen {
    private val game : SuperMario
    val atlas : TextureAtlas

    private val gamecam : OrthographicCamera
    private val viewport : Viewport
    val hud : Hud

    private val maploader : TmxMapLoader
    val map : TiledMap
    private val renderer : OrthogonalTiledMapRenderer

    val world : World
    private val b2dr: Box2DDebugRenderer
    private val creator: B2WorldCreator

    //sprites
    private val player: Mario

    private val music: Music

    private val items: com.badlogic.gdx.utils.Array<Item>
    private val itemsToSpawn: LinkedBlockingQueue<ItemDef>

    constructor(game : SuperMario) {
        this.game = game
        this.atlas = TextureAtlas("Mario_and_Enemies.pack")

        gamecam = OrthographicCamera()
        viewport = FitViewport(V_WIDTH / PPM, V_HEIGHT / PPM, gamecam)

        hud = Hud(game.batch!!)

        maploader = TmxMapLoader()
        map = maploader.load("level1.tmx")
        renderer = OrthogonalTiledMapRenderer(map, 1 / PPM)

        gamecam.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)

        world = World(Vector2(0f, -10f), true)

        b2dr = Box2DDebugRenderer()

        creator = B2WorldCreator(this, game.manager!!)

        player = Mario(this, game.manager!!)

        world.setContactListener(WorldContactListener())

        music = game.manager!!.marioMusic
        music.isLooping = true
        music.volume = 0.3f
        music.play()

        items = com.badlogic.gdx.utils.Array<Item>()
        itemsToSpawn = LinkedBlockingQueue()
    }

    fun spawnItem(idef : ItemDef){
        itemsToSpawn.add(idef)
    }

    fun handleSpawningItems(){
        if(itemsToSpawn.isNotEmpty()){
            val idef : ItemDef = itemsToSpawn.poll()

            if(idef.type == Mushroom::class){
                items.add(Mushroom(this, idef.position.x, idef.position.y))
            }
        }
    }

    override fun show(){}

    fun handleInput(dt : Float){
        if(!player.isDead){
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                player.jump()
            }

            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                player.moveRight()
            }

            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                player.moveLeft()
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                player.fire()
            }
        }
    }

    fun update(dt : Float){
        handleInput(dt)
        handleSpawningItems()

        world.step(1 / 60f, 6, 2)

        player.update(dt)

        for(enemy in creator.getEnemies()){
            enemy.update(dt)

            if(enemy.getX() < player.getX() + 224 / PPM){
                enemy.b2body.isActive = true
            }
        }

        for(item in items){
            item.update(dt)
        }

        if(!player.isDead){
            gamecam.position.x = player.b2body.position.x
        }

        gamecam.update()

        renderer.setView(gamecam)
    }

    override fun render(dt : Float){
        update(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderer.render()

        b2dr.render(world, gamecam.combined)

        game.batch?.projectionMatrix = gamecam.combined
        game.batch?.begin()

        player.draw(game.batch!!)

        for(enemy in creator.getEnemies()){
            enemy.draw(game.batch)
        }

        for(item in items){
            item.draw(game.batch!!)
        }

        game.batch?.end()

        game.batch?.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()

        if(gameOver()){
            game.screen = GameOverScreen(game)
            dispose()
        }
    }

    fun gameOver() : Boolean {
        if(player.isDead && player.stateTimer > 3){
            return true
        }

        return false
    }

    override fun resize(width : Int, height : Int){
        viewport.update(width, height)
    }

    override fun dispose() {
        //dispose of all our opened resources
        map.dispose()
        renderer.dispose()
        world.dispose()
        b2dr.dispose()
        hud.dispose()
    }

    override fun pause(){}

    override fun resume(){}

    override fun hide(){}


}