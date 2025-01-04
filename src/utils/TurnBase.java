package utils;

import enemy.Enemy;
import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import player.Player;

import java.util.List;
import java.util.Random;

import SPane.GameOverPane;
import SPane.GameWinPane;
import SPane.TurnBasePane;
import base.Unit;

public class TurnBase {
    private Player player;
    private List<Enemy> enemies;
    private TurnBasePane gamePane;
    private boolean isPlayerTurn;
    private int extraDamage = 0;        
    private int extraDefense = 0;
	private MediaPlayer mediabackground;
	private MediaPlayer currentMediaPlayer;

    public TurnBase(Player player, List<Enemy> enemies, TurnBasePane gamePane) {
        this.player = player;
        this.enemies = enemies;
        this.gamePane = gamePane;
        this.isPlayerTurn = true; 
        Media md = SetMediaBackground("sound/TurnBase_Soundbackground.mp3");
        mediabackground = new MediaPlayer(md);
        mediabackground.setCycleCount(MediaPlayer.INDEFINITE);
        mediabackground.setVolume(0.1);
        mediabackground.play();
    }



    public void startTurn() {
        if (player.isAlive() && enemies.stream().anyMatch(Enemy::isAlive)) { 
        	//stream anymatch for check if any enemies is alive
            gamePane.updateTurnStatus(isPlayerTurn);
            if (isPlayerTurn) {
                playerTurn();
            } else {
                enemyTurn();
            }
        } else {
            endGame();
        }
    }

    public void endPlayerTurn() {
        isPlayerTurn = false;
        startTurn();
    }

    private void playerTurn() {
        gamePane.getBuffAttackButton().setVisible(true);
        gamePane.getBuffDefenseButton().setVisible(true);
        if(extraDamage > 0 || extraDamage > 0) {
        	gamePane.getBuffAttackButton().setOpacity(0.5);
        	gamePane.getBuffDefenseButton().setOpacity(0.5);
        }
        else {
        	gamePane.getBuffAttackButton().setOpacity(1);
        	gamePane.getBuffDefenseButton().setOpacity(1);
        }
     
    }

    private void enemyTurn() {
    	isPlayerTurn = false;
    	gamePane.getBuffAttackButton().setVisible(false);
        gamePane.getBuffDefenseButton().setVisible(false);
        gamePane.setEnemyFadeEffect(null, false);
        // Choose Enemy To attack Player by Random
        Enemy attackingEnemy = getRandomAliveEnemy();
        if (attackingEnemy != null) {
            // get Image
            ImageView enemyImageView = gamePane.getEnemyImageView(attackingEnemy);
            if (enemyImageView != null) {
                gamePane.performAttack(enemyImageView, gamePane.getPlayerImage(), attackingEnemy, () -> {
                    // โจมตีผู้เล่น
                    double s = Math.random();
                    if (s < this.chanceToMiss(attackingEnemy)) {
                        gamePane.showMissText(gamePane.getPlayerImage());
                        if (getExtraDefense() > 0) {
                        	player.setDefense(player.getDefense()-getExtraDefense());
                        	setExtraDefense(0);
                        }
                    } else {
                    	if (getExtraDefense() > 0) {
                    		attackingEnemy.attack(player);
                        	player.setDefense(player.getDefense()-getExtraDefense());
                        	setExtraDefense(0);
                        }
                    	else {
                    		attackingEnemy.attack(player);
                    	}
                        gamePane.playAttackEffect(gamePane.getPlayerImage());
                        gamePane.showDamageText(gamePane.getPlayerImage(),attackingEnemy.getDamage());
                    }
                    gamePane.updatePlayerStatus();
                    gamePane.updateEnemyStatus();
                });
            } else {
                // ถ้าไม่มี ImageView ของศัตรู
                endGame();
            }
        }
    }
    public void handleBuffDefenseClick(MouseEvent event) {
    	if (isPlayerTurn() && extraDefense == 0 && extraDamage == 0) {
            this.playSound("sound/HEAL_SoundEffect.mp3", 0.7);
    		double increasearmor = player.getDefense()*0.5;
    		extraDefense += (int) increasearmor;
    		player.setDefense(player.getDefense()+this.extraDefense);
    		gamePane.updatePlayerStatus();
            gamePane.createShadowEffect(gamePane.getPlayerImage(), Color.GREEN);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
            	endPlayerTurn();
            	gamePane.setAnimationRunning(false);
            });
            gamePane.setAnimationRunning(true);
            pause.play();
        }
    }
    public void handleBuffAttackClick(MouseEvent event) {
    	if (isPlayerTurn() && extraDamage == 0 && extraDefense == 0) {
            playSound("sound/HEAL_SoundEffect.mp3",0.7);
    		double increaseDamage = player.getAtk()*0.5;
    		extraDamage += (int) increaseDamage;
            player.setAtk(player.getAtk() + extraDamage);
            gamePane.updatePlayerStatus();
            gamePane.createShadowEffect(gamePane.getPlayerImage(), Color.BLUE);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
            	endPlayerTurn();
            	gamePane.setAnimationRunning(false);
            });
            gamePane.setAnimationRunning(true);
            pause.play();
        }
    }
    private Enemy getRandomAliveEnemy() {
        Random random = new Random();
        List<Enemy> aliveEnemies = enemies.stream().filter(Enemy::isAlive).toList();
        if (!aliveEnemies.isEmpty()) {
            return aliveEnemies.get(random.nextInt(aliveEnemies.size()));
        }
        return null;
    }
    private void endGame() {
        if (player.isAlive()) {
            gamePane.getTurnStatusLabelPlayer().setText("Player Wins!");
            gamePane.getClickEnemyToAttackLabel().setVisible(false);
            this.mediabackground.stop();
        	if (GameStart.getRound() == 3) {
        		GameStart.clear();
        		GameWinPane wingame = new GameWinPane();
        		GameStart.getAppRoot().getChildren().add(wingame);
        	}else {
                gamePane.getClickEnemyToAttackLabel().setVisible(false);
                this.mediabackground.stop();
                GameStart.clear();
                GameStart.getTime().start();
                GameStart.initContent(GameStart.getRound());
        	}
        } else {
            gamePane.getTurnStatusLabelEnemy().setText("Enemies Wins!");
            gamePane.getClickEnemyToAttackLabel().setVisible(false);
            this.mediabackground.stop();
            GameStart.clear();
			GameOverPane gameOverPane = new GameOverPane();
			GameStart.getAppRoot().getChildren().add(gameOverPane);
            //go to GameoverPane
        }
    }
    public double chanceToMiss(Unit chractor) {
    	if(chractor instanceof Player) {
    		Player player = (Player) chractor;
    		return 1 - ((double)(player.getSpeed()) / 100);
    	}
    	Enemy enemy = (Enemy) chractor;
    	return 1 - ((double)(enemy.getSpeed()) / 100);
    }
    private Media SetMediaBackground(String mediaPath) {
		Media bg = null;
		try {
            String classLoaderPath = ClassLoader.getSystemResource(mediaPath).toString();
            bg = new Media(classLoaderPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Not fount: "+ mediaPath);
        }
		return bg;
	}
	
    public void playSound(String mediaPath,double Volume) {
        try {
            // หยุดเสียงเก่าหากมี
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
            }
            String classLoaderPath = ClassLoader.getSystemResource(mediaPath).toString();
            Media media = new Media(classLoaderPath);
            currentMediaPlayer = new MediaPlayer(media);
            currentMediaPlayer.setVolume(Volume); // ตั้งระดับเสียง
            currentMediaPlayer.setOnError(() -> System.out.println("Error: " + currentMediaPlayer.getError()));
            currentMediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading or playing sound: " + mediaPath);
        }
    }

	public void setPlayerTurn(boolean isPlayerTurn) {
		this.isPlayerTurn = isPlayerTurn;
	}

	public int getExtraDamage() {
		return extraDamage;
	}

	public void setExtraDamage(int extraDamage) {
		this.extraDamage = extraDamage;
	}

	public int getExtraDefense() {
		return extraDefense;
	}

	public void setExtraDefense(int extraDefense) {
		this.extraDefense = extraDefense;
	}
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
	
    
}
