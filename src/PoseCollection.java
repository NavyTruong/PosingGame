import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;
import processing.core.PImage;

public class PoseCollection {
	private Queue<Pose> poses;
	private PImage firstImg;
	private PImage secondImg;
	private PImage thirdImg;
	private PImage fourthImg;
	private PImage fifthImg;
	private PImage sixthImg;
	/**
	 * Initiate a queue to work with the poses
	 * @param app
	 */
	public PoseCollection(PApplet app){
		poses = new LinkedList<Pose>();
		firstImg = app.loadImage("data/firstpose.png");
		secondImg = app.loadImage("data/secondpose.png");
		thirdImg = app.loadImage("data/thirdpose.png");
		fourthImg = app.loadImage("data/fourthpose.png");
		fifthImg = app.loadImage("data/fifthpose.png");
		sixthImg = app.loadImage("data/sixthpose.png");
	}
	
	public void addAllPoses(PApplet app) {
		addPose(firstImg, 110, 110, 180, 180, 160, 120, 180, 90);
		addPose(secondImg, 60, 60, 150, 150, 120, 120, 150, 150);
		addPose(thirdImg, 40, 130, 150, 160, 170, 150, 175, 175);
		addPose(fourthImg, 70, 70, 70, 70, 170, 130, 170, 120);
		addPose(fifthImg, 160, 100, 170, 180, 170, 140, 150, 155);
		addPose(sixthImg, 75, 70, 140, 80, 160, 170, 120, 170);
	}
	
	/**
	 * Draw the current pose to the main sketch
	 * @param app the main sketch
	 */
	public void drawPose(PApplet app){
		Pose pose = poses.peek();
		if (pose != null) {
			pose.draw(app);
		}
	}
	
	/**
	 * Get the first pose in the queue
	 * @return
	 */
	public Pose getCurrentPose() {
		Pose pose = poses.peek();
		while (pose == null && !poses.isEmpty()) {
			poses.poll();
			pose = poses.peek();
		}
		return pose;
	}
	
	/**
	 * Add a pose to the collection
	 * @param image image of the pose
	 * @param leftShoulderAngle
	 * @param rightShoulderAngle
	 * @param leftElbowAngle
	 * @param rightElbowAngle
	 * @param leftHipAngle
	 * @param rightHipAngle
	 * @param leftKneeAngle
	 * @param rightKneeAngle
	 */
	public void addPose(PImage image, float leftShoulderAngle, float rightShoulderAngle, float leftElbowAngle, float rightElbowAngle, 
			float leftHipAngle, float rightHipAngle, float leftKneeAngle, float rightKneeAngle){
		poses.add(new Pose(image, leftShoulderAngle, rightShoulderAngle, leftElbowAngle, rightElbowAngle, 
				leftHipAngle, rightHipAngle, leftKneeAngle, rightKneeAngle));
	}
	
	/**
	 * Pop a pose off the queue
	 */
	public void removePose() {
		poses.poll();
	}
	
	/**
	 * Check if the queue is empty
	 * @return
	 */
	public boolean isEmpty() {
		return poses.isEmpty();
	}
}
