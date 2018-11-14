import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class PoseCollection {
	private ArrayList<Pose> poses;

	private PImage poseImage;
	private Pose pose;
	
	
	public PoseCollection(){
		poses = new ArrayList<Pose>();
		
		//for(int i = 0; i <= poses.size(); i++){
			//poses.add(pose = new Pose(pose.getImage(), pose.getLeftArmAngle(), pose.getRightArmAngle() ));
		//}
			
	}
	public void drawPose(PApplet app){
		pose.draw(app);
	}
	
	public void addPose(PImage image, float leftAngle, float rightAngle){
		
		poses.add(new Pose(image, leftAngle, rightAngle));
	}
	
	public ArrayList<Pose> getPoses() {
		return poses;
	}

	public void setPoses(ArrayList<Pose> poses) {
		this.poses = poses;
	}
	
}
