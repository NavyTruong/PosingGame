import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;
import processing.core.PImage;

public class PoseCollection {
	private Queue<Pose> poses;
	
	public PoseCollection(PApplet app){
		poses = new LinkedList<Pose>();
		
		//for(int i = 0; i <= poses.size(); i++){
			//poses.add(pose = new Pose(pose.getImage(), pose.getLeftArmAngle(), pose.getRightArmAngle() ));
		//}
		PImage img = app.loadImage("poses.png");
		addPose(img, 90, 90, 180, 180, 90, 120, 180, 45);
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
}
