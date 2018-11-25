import processing.core.PApplet;
import processing.core.PImage;

public class Pose {

	private PImage image;
	private float leftShoulderAngle;
	private float rightShoulderAngle;
	private float leftElbowAngle;
	private float rightElbowAngle;
	private float leftHipAngle;
	private float rightHipAngle;
	private float leftKneeAngle;
	private float RightKneeAngle;
	public PImage poseImage;
	
	public Pose(PImage image, float leftShoulderAngle, float rightShoulderAngle, float leftElbowAngle, float rightElbowAngle, 
			float leftHipAngle, float rightHipAngle, float leftKneeAngle, float rightKneeAngle){
		this.image = image;
		this.setLeftShoulderAngle(leftShoulderAngle);
		this.setRightShoulderAngle(rightShoulderAngle);
		this.setLeftElbowAngle(leftElbowAngle);
		this.setRightElbowAngle(rightElbowAngle);
		this.setLeftHipAngle(leftHipAngle);
		this.setRightHipAngle(rightHipAngle);
		this.setLeftKneeAngle(leftKneeAngle);
		this.setRightKneeAngle(rightKneeAngle);
	}
	
	public PImage getImage() {
		return image;
	}
	
	public void setImage(PImage image) {
		this.image = image;
	}
	
	//public void settings()
	public void draw(PApplet app){
		app.pushMatrix();
		app.scale(1,-1);
		app.image(this.image, (float)-1.5, (float)-1.5, 2, 2);
		app.popMatrix();
	}

	public float getLeftShoulderAngle() {
		return leftShoulderAngle;
	}

	public void setLeftShoulderAngle(float leftShoulderAngle) {
		this.leftShoulderAngle = leftShoulderAngle;
	}

	public float getRightShoulderAngle() {
		return rightShoulderAngle;
	}

	public void setRightShoulderAngle(float rightShoulderAngle) {
		this.rightShoulderAngle = rightShoulderAngle;
	}

	public float getLeftElbowAngle() {
		return leftElbowAngle;
	}

	public void setLeftElbowAngle(float leftElbowAngle) {
		this.leftElbowAngle = leftElbowAngle;
	}

	public float getLeftHipAngle() {
		return leftHipAngle;
	}

	public void setLeftHipAngle(float leftHipAngle) {
		this.leftHipAngle = leftHipAngle;
	}

	public float getRightHipAngle() {
		return rightHipAngle;
	}

	public void setRightHipAngle(float rightHipAngle) {
		this.rightHipAngle = rightHipAngle;
	}

	public float getLeftKneeAngle() {
		return leftKneeAngle;
	}

	public void setLeftKneeAngle(float leftKneeAngle) {
		this.leftKneeAngle = leftKneeAngle;
	}

	public float getRightKneeAngle() {
		return RightKneeAngle;
	}

	public void setRightKneeAngle(float rightKneeAngle) {
		RightKneeAngle = rightKneeAngle;
	}

	public float getRightElbowAngle() {
		return rightElbowAngle;
	}

	public void setRightElbowAngle(float rightElbowAngle) {
		this.rightElbowAngle = rightElbowAngle;
	}	
}
