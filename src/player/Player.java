package player;

import java.util.ArrayList;

import base.Unit;
import enemy.Enemy;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class Player extends Unit{

	private int hp;
	private int atk; 
	private int speed;
	private int defense;
	private int maxhp;
	private int maxdefense;
	private int damage;
	private boolean alive;
	private Image rightStay;
	private Image leftStay;
	private Image imageFight;
	private Image imageStay;
	private ImageView imageView;
	private ArrayList<Image> walkRight;
	private ArrayList<Image> walkLeft;
	private Timeline walkRightAnimation;
	private Timeline walkLeftAnimation;
	private boolean isWalkRight = false;
	private boolean isWalkLeft = false;
	
	public Player(int hp , int atk , int speed , int defense ,String Right,String Left ,
			String Right_1,String Right_2,String Left_1,String Left_2,String Stay , String Fight) {
		this.setMaxhp(hp);
		this.setMaxdefense(defense);
		this.setHp(hp);
		this.setAtk(atk);
		this.setSpeed(speed);
		this.setDamage(0);
		this.setDefense(defense);
		this.setAlive(true);
		this.setImageRightByPath(Right);
		this.setImageLeftByPath(Left);
		this.setImageStay(Stay);
		this.setImageFight(Fight);
		walkRight = new ArrayList<Image>();
		walkLeft = new ArrayList<Image>();
		try {
            String classLoaderPath = ClassLoader.getSystemResource(Right_1).toString();
            Image right1 = new Image(classLoaderPath);
            String classLoaderPath1 = ClassLoader.getSystemResource(Right_2).toString();
            Image right2 = new Image(classLoaderPath1);
            String classLoaderPath2 = ClassLoader.getSystemResource(Left_1).toString();
            Image left1 = new Image(classLoaderPath2);
            String classLoaderPath3 = ClassLoader.getSystemResource(Left_2).toString();
            Image left2 = new Image(classLoaderPath3);
            walkRight.add(right1);
            if (!(this instanceof Magician)) {
            	walkRight.add(this.rightStay);
            }
            walkRight.add(right2);
            walkLeft.add(left1);
            if (!(this instanceof Magician)) {
            	walkLeft.add(this.leftStay);
            }
            walkLeft.add(left2);
            System.out.println("found all image");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("not found Image Right or left");
        }
		createWalkRightAnimation();
		createWalkLeftAnimation();
		//createWalkRightAnimation(); 
		
	}
	
	public void attack(Unit other) {
		if(other instanceof Enemy) {
			Enemy enemy = (Enemy) other;
			enemy.setHp(enemy.getHp()-Math.max(0,(atk-enemy.getDefense())));
			this.setDamage(Math.max(0,(atk-enemy.getDefense())));
			if(enemy.getHp() == 0) {
				enemy.setAlive(false);
			}
		}
		
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		if (hp > this.getMaxhp())hp = this.getMaxhp(); 
		this.hp = Math.max(0, hp);
	}
	public int getAtk() {
		return atk;
	}
	public void setAtk(int atk) {
		this.atk = Math.max(0, atk);
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		if(speed > 100) {
			this.speed = 100;
		}
		else{
			this.speed = Math.max(0, speed);
		}
	}
	public int getDefense() {
		return defense;
	}
	public void setDefense(int defense) {
		this.defense = Math.max(0,defense);
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Image getImageRight() {
		return rightStay;
	}
	public void setImageRightByPath(String imagePath) {
    	try {
            String classLoaderPath = ClassLoader.getSystemResource(imagePath).toString();
            this.rightStay =new Image(classLoaderPath);
            setImageView(new ImageView(rightStay));
            this.getChildren().clear();
            this.getChildren().add(this.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public Image getImageLeft() {
		return leftStay;
	}

	public void setImageLeftByPath(String imagePath) {
    	try {
            String classLoaderPath = ClassLoader.getSystemResource(imagePath).toString();
            this.leftStay =new Image(classLoaderPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		imageView.setFitHeight(80);
	    imageView.setFitWidth(80);
	    this.imageView = imageView;
	}
	private void createWalkRightAnimation() {
        walkRightAnimation = new Timeline();
        // Loop through the walkRight images and create a KeyFrame for each
        for (int i = 0; i < walkRight.size(); i++) {
            int frameIndex = i;
            KeyFrame frame = new KeyFrame(
                Duration.millis(200 * (frameIndex+1)), // Adjust the duration as needed
                e -> imageView.setImage(walkRight.get(frameIndex))
            );
            walkRightAnimation.getKeyFrames().add(frame);
        }

        walkRightAnimation.setCycleCount(Timeline.INDEFINITE); // Repeat the animation
    }
	private void createWalkLeftAnimation() {
        walkLeftAnimation = new Timeline();
        // Loop through the walkRight images and create a KeyFrame for each
        for (int i = 0; i < walkLeft.size(); i++) {
            int frameIndex = i;
            KeyFrame frame = new KeyFrame(
                Duration.millis(200 * (frameIndex+1)), // Adjust the duration as needed
                e -> imageView.setImage(walkLeft.get(frameIndex))
            );
            walkLeftAnimation.getKeyFrames().add(frame);
        }

        walkLeftAnimation.setCycleCount(Timeline.INDEFINITE); // Repeat the animation
    }
	public void createAttackEffect(Node player) {
        DropShadow attackEffect = new DropShadow();
        attackEffect.setColor(Color.RED);
        attackEffect.setRadius(0);

        ((Player)player).getImageView().setEffect(attackEffect);

        Timeline flickerEffect = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(attackEffect.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(attackEffect.radiusProperty(), 20)),
            new KeyFrame(Duration.millis(200), new KeyValue(attackEffect.radiusProperty(), 0))
        );
        flickerEffect.setCycleCount(6);
        flickerEffect.setOnFinished(e -> ((Player)player).getImageView().setEffect(null));
        flickerEffect.play();
    }
    // Method to start the walking animation when moving right
    public void startWalkingRight() {
    	walkRightAnimation.play();
    	isWalkRight = true;
    }
    public void startWalkingLeft() {
    	walkLeftAnimation.play();
    	isWalkLeft = true;
    }

    // Method to stop the walking animation
    public void stopWalking() {
    	if (this.isWalkRight) {
    		walkRightAnimation.stop();
    		imageView.setImage(rightStay);
    		isWalkRight = false;
    	}else if (this.isWalkLeft){
    		walkLeftAnimation.stop();
    		imageView.setImage(leftStay);
    		isWalkLeft = false;
    	}
    }

	public int getMaxhp() {
		return maxhp;
	}

	public void setMaxhp(int maxhp) {
		this.maxhp = maxhp;
	}

	public int getMaxdefense() {
		return maxdefense;
	}

	public void setMaxdefense(int maxdefense) {
		this.maxdefense = maxdefense;
	}
	
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public Image getImageStay() {
		return imageStay;
	}
	public Image getImageFight() {
		return imageFight;
	}
	public void setImageStay(String imageStayPath) {
    	try {
            String classLoaderPath = ClassLoader.getSystemResource(imageStayPath).toString();
            this.imageStay=new Image(classLoaderPath);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
	public void setImageFight(String imageFightPath) {
    	try {
            String classLoaderPath = ClassLoader.getSystemResource(imageFightPath).toString();
            this.imageFight=new Image(classLoaderPath);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

	
}
