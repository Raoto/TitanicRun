package com.titanicrun.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.titanicrun.game.Objects.PlayObjects.Animation;
import com.titanicrun.game.Objects.SystemObjects.AudioPlayerInt;
import com.titanicrun.game.Objects.PlayObjects.BackgroundCreator;
import com.titanicrun.game.Objects.SystemObjects.Balance;
import com.titanicrun.game.Objects.PlayObjects.EnemiesCreator;
import com.titanicrun.game.Objects.PlayObjects.FallObjectsCreator;
import com.titanicrun.game.Objects.PlayObjects.Player;
import com.titanicrun.game.Objects.SystemObjects.PlayerAnimation;
import com.titanicrun.game.Objects.PlayObjects.Score;
import com.titanicrun.game.Objects.PlayObjects.Shadow;
import com.titanicrun.game.Objects.PlayObjects.Water;
import com.titanicrun.game.TitanicClass;

import java.util.ArrayList;

/**
 * Created by Никита on 28.01.2016.
 */
public class GameScreen extends Screen {
    public Player player;
    public Score score;
    public Water water;
    public AudioPlayerInt playBGM;
    public Balance playBallance;
    private Shadow shadow;
    private Texture night;
    private EnemiesCreator enemiesCreator;
    private BackgroundCreator backFirstLvl, backSecondLvl;
    private DeathScreen deathScreen;
    private ArrayList<PlayerAnimation> playerAnimations;
    private FallObjectsCreator fallObj;
    public boolean pause;

    public GameScreen(GameScreenManager gameScreenManager, Balance balance) {
        super(gameScreenManager);
        this.playBallance = balance;
        balance.drawPosition.y = TitanicClass.ScreenHeight - TitanicClass.scoreABC[0].getHeight()-5;
        balance.getBalance();
        Load();
    }
    public void Load() {
        pause = true;
        shadow = new Shadow(this, anim("backs/shadow.png"));
        night = new Texture("backs/night.png");
        playerAnimations = new ArrayList<PlayerAnimation>();
        for(int i = 1; i <= 4; i++)
            playerAnimations.add(new PlayerAnimation(new Animation(new Texture[]{new Texture("players/"+i+"player.png"),
                    new Texture("players/"+i+"player2.png"),
                    new Texture("players/"+i+"player3.png"), new Texture("players/"+i+"player2.png")},2),
                    anim("players/"+i+"playerFront.png"), anim("players/"+i+"playerPreview.png")));
        Preferences sittings = Gdx.app.getPreferences("Animation");
        int playerIndex = sittings.getInteger("Animation");
        player = new Player(this, playerAnimations.get(playerIndex).run, playerAnimations.get(playerIndex).fly);
        enemiesCreator = new EnemiesCreator(this, anim("object.png"),60);
        score = new Score();
        backFirstLvl = new BackgroundCreator(this, anim("backs/backUsuall.png"), anim("backs/backPrev.png"),false);
        backSecondLvl = new BackgroundCreator(this, anim("backs/2backUsuall.png"), anim("backs/2backPrev.png"),true);
        Animation waterAnim = new Animation(new Texture[]{new Texture("water.png"),new Texture("water2.png"),
                new Texture("water3.png")},10);
        water = new Water(this, waterAnim,
                new Vector2(0,-waterAnim.getTexture().getHeight()/4), 0, 1.5f, 0.5f);

       // playBGM = new AudioPlayerInt();
       // playBGM.create();
        Animation[] animations = new Animation[8];
        for(int i = 1; i < 8; i++) {
            animations[i-1] = anim("fallObj/fall"+i+".png");
        }
        animations[7] = new Animation(new Texture[]{new Texture("fallObj/fallMan.png"),new Texture("fallObj/fallMan2.png")},8);
        fallObj = new FallObjectsCreator(this,animations,600);
        player.animation.update();
        water.update();
        backFirstLvl.update();
    }
    @Override
    public void update() {
        if(!pause) {
            if(Gdx.input.justTouched()) {
                Rectangle pauseRect = new Rectangle(0,TitanicClass.ScreenHeight/2,TitanicClass.ScreenWidth,
                        TitanicClass.ScreenHeight/2);
                if (TitanicClass.getMouse().overlaps(pauseRect)) {
                    pause = true;
                   // playBGM.waterSound.pause();
                    gameScreenManager.setScreen(new PauseScreen(gameScreenManager, this));
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.P)) {
                pause = true;
                // playBGM.waterSound.pause();
                gameScreenManager.setScreen(new PauseScreen(gameScreenManager, this));
            }
            player.update();
            enemiesCreator.update();
            shadow.update();
            fallObj.update();
        }
        else {
            if (Gdx.input.justTouched()) {
                pause = false;
                backFirstLvl.pause = false;
                //playBGM.waterSound.play();
            }
        }
        player.animation.update();
        water.update();
        if(score.getScore() >= 0 && score.getScore() <=100)
            backFirstLvl.update();
        if(score.getScore() >= 90 && score.getScore() <=190)
            backSecondLvl.update();

        playBallance.drawPosition.x = TitanicClass.ScreenWidth-5-
                (playBallance.getBalance() + "").length() * TitanicClass.scoreABC[0].getWidth();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(night, 0, 0);
        backFirstLvl.render(spriteBatch);
        backSecondLvl.render(spriteBatch);
        player.render(spriteBatch);
        enemiesCreator.render(spriteBatch);
        fallObj.render(spriteBatch);
        water.render(spriteBatch);
        if(score.getScore()>50)
            shadow.render(spriteBatch);
        score.render(spriteBatch);
        playBallance.render(spriteBatch);
    }

    public void Die() {
//        playBGM.dispose();
        deathScreen = new DeathScreen(gameScreenManager,this);
        gameScreenManager.setScreen(deathScreen);
    }
}
