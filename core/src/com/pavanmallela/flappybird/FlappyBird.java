package com.pavanmallela.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
    Texture gameover;
//    ShapeRenderer shapeRenderer;

    Circle birdCircle;
    Rectangle[] topTubeRectangle;
    Rectangle[] bottomTubeRectangle;
    Texture topTube;
    Texture bottomTube;
    float gap = 400;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;

    Texture[] birds;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;

    int gameState = 0;
    float gravity = 2;

    int score = 0;
    int scoringTube = 0;

    BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
		birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        gameover = new Texture("gameover.png");

//        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        //set font properties
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;
        topTubeRectangle = new Rectangle[numberOfTubes];
        bottomTubeRectangle = new Rectangle[numberOfTubes];

        startGame();
	}

    public void startGame() {
        //center the bird on Y coordinate
        birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
        //first set of tubes
        for(int i = 0; i < numberOfTubes; i++){
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

            //initialize rectangle shapes
            topTubeRectangle[i] = new Rectangle();
            bottomTubeRectangle[i] = new Rectangle();
        }
    }

	@Override
	public void render () {

        //draw background
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //start playing on tap
        if(gameState == 1) {

            //scoring
            if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2) {
                score++;
                Gdx.app.log("Score: ", String.valueOf(score));
                if(scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }

            //jump on tap
            if(Gdx.input.justTouched()){
                velocity = -30;
            }
            //roll tubes right to left
            for(int i = 0; i < numberOfTubes; i++) {
                //when tubes are off left completely, shift tubes to complete right
                if(tubeX[i] < - topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                } else {
                    tubeX[i] = tubeX[i] - tubeVelocity;
                }

                //draw tubes
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                //draw rectangle shape for collision detection
                topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }

            //sprite fall off
            if(birdY > 0) { //prevent the sprite going down off screen
                velocity = velocity + gravity;
                birdY = birdY - velocity;
            } else {
                gameState = 2;
            }
        } else if (gameState == 0) {
            //tap to start
            if(Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            //gameover
            batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);

            //tap to restart
            if(Gdx.input.justTouched()) {
                gameState = 1;
                score = 0;
                scoringTube = 0;
                velocity = 0;
                startGame();
            }
        }

        //Flap wings
        if(flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        //draw flapping bird
		batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);


        //display font (score)
        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
        birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);

        //draw bird shape for collision detection
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        //draw tube shape
        for(int i = 0; i < numberOfTubes; i++) {
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            //collision
            if(Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])) {
//                Gdx.app.log("Collision", "Yes");
                gameState = 2;
            }
        }

//        shapeRenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
