import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;
import processing.core.PImage;

public class PoseCollection {
	private Queue<Pose> poses;
	
	public PoseCollection(PApplet app){
		poses = new LinkedList<Pose>();
//		addAllPoses(app);
	}
	
	public void addAllPoses(PApplet app) {
		System.out.println("Add all poses");
		PImage img = app.loadImage("data/firstpose.png");
		addPose(img, 110, 110, 180, 180, 160, 120, 180, 90);
		img = app.loadImage("data/secondpose.png");
		addPose(img, 60, 60, 150, 150, 120, 120, 150, 150);
		img = app.loadImage("data/thirdpose.png");
		addPose(img, 40, 130, 150, 160, 170, 150, 175, 175);
	}
	
	public void drawPose(PApplet app){
		Pose pose = poses.peek();
		if (pose != null) {
			pose.draw(app);
		}
	}
	
	public Pose getCurrentPose() {
		Pose pose = poses.peek();
		while (pose == null && !poses.isEmpty()) {
			poses.poll();
			pose = poses.peek();
		}
		return pose;
	}
	
	public void addPose(PImage image, float leftShoulderAngle, float rightShoulderAngle, float leftElbowAngle, float rightElbowAngle, 
			float leftHipAngle, float rightHipAngle, float leftKneeAngle, float rightKneeAngle){
		poses.add(new Pose(image, leftShoulderAngle, rightShoulderAngle, leftElbowAngle, rightElbowAngle, 
				leftHipAngle, rightHipAngle, leftKneeAngle, rightKneeAngle));
	}
	
	public boolean removePose() {
		Pose removedPose = poses.poll();
		return (removedPose != null);
	}
	
	public boolean isEmpty() {
		return poses.size() == 0;
	}
}
