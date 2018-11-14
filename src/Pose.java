import processing.core.PApplet;
import processing.core.PImage;

public class Pose {

	private PImage image;
	private float leftArmAngle;
	private float rightArmAngle;
	public PImage poseImage;
	private PApplet app;
	
	public Pose(PImage image, float leftArmAngle, float rightArmAngle ){
		this.image = image;
		this.leftArmAngle = leftArmAngle;
		this.rightArmAngle = rightArmAngle;	
		
		
	}
	
	public PImage getImage() {
		return image;
	}
	
	public void setImage(PImage image) {
		this.image = image;
	}
	
	public float getLeftArmAngle() {
		return leftArmAngle;
	}
	
	public void setLeftArmAngle(float leftArmAngle) {
		this.leftArmAngle = leftArmAngle;
	}
	
	public float getRightArmAngle() {
		return rightArmAngle;
	}
	
	public void setRightArmAngle(float rightArmAngle) {
		this.rightArmAngle = rightArmAngle;
	}
	//public void settings()
	public void draw(PApplet app){
		this.app = app;
		//app.stroke(255, 0, 0);
		//app.ellipse(100, 100, 20, 20);
		//app.line(100, 110, 100, 140);
		//app.line(100, 125,80, 110);
		//app.line(100, 125, 120, 110);
		poseImage = app.loadImage("poses.png");
		app.pushMatrix();
		app.scale(1,-1);
		app.image(poseImage, -1, -1, 2, 2);
		app.popMatrix();
		
	}	
}
