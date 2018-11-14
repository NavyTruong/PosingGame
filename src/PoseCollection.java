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
		addPose(img, 90, 90);
	}
	
	public void drawPose(PApplet app){
		Pose pose = poses.peek();
		pose.draw(app);
	}
	
	public void addPose(PImage image, float leftAngle, float rightAngle){
		poses.add(new Pose(image, leftAngle, rightAngle));
	}
}
