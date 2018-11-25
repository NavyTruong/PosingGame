import java.io.IOException;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinect.Quat;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * The game tracks user’s right arm. User needs to collects 5 power balls when the monster is sleeping. 
 * If the monster is awake and the arm is moving then the user loses their power. 
 * They have 5 power levels to lose. 
 * When all power balls are collected, the monster is defeated if touched.
 * User instruction is shown on screen before the game starts.
 * @author Natalie Truong and Carla 
 *
 */
public class PosingGame extends PApplet {
	private static int PROJECTOR_WIDTH = 1024;
	private static int PROJECTOR_HEIGHT = 786;
	private static float PROJECTOR_RATIO = (float)PROJECTOR_HEIGHT/(float)PROJECTOR_WIDTH;
	private TCPBodyReceiver kinectReader;
	private PoseCollection poses;
	private Pose currentPose;
	
	private float leftShoulderAngle;
	private float rightShoulderAngle;
	private float leftElbowAngle;
	private float rightElbowAngle;
	private float leftHipAngle;
	private float rightHipAngle;
	private float leftKneeAngle;
	private float rightKneeAngle;
	
	private boolean isGameOver = false;

	public void createWindow(boolean useP2D, boolean isFullscreen, float windowsScale) {
		if (useP2D) {
			if(isFullscreen) {
				fullScreen(P2D);  			
			} else {
				size((int)(PROJECTOR_WIDTH * windowsScale), (int)(PROJECTOR_HEIGHT * windowsScale), P2D);
			}
		} else {
			if(isFullscreen) {
				fullScreen();  			
			} else {
				size((int)(PROJECTOR_WIDTH * windowsScale), (int)(PROJECTOR_HEIGHT * windowsScale));
			}
		}		
	}
	
	// use lower numbers to zoom out (show more of the world)
	// zoom of 1 means that the window is 2 meters wide and appox 1 meter tall in real world units
	// sets 0,0 to center of screen
	public void setScale(float zoom) {
		scale(zoom* width/2.0f, zoom * -width/2.0f);
		translate(1f/zoom , -PROJECTOR_RATIO/zoom );
	}

	public void settings() {
		createWindow(true, true, .5f);
	}

	public void setup(){	
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect tao kinect server");
			exit();
		}
		poses = new PoseCollection(this);
	}
	
	public void draw(){
		setScale(.5f);
		noStroke();
		background(255,255,255);
		//draw person
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		poses.drawPose(this);
		Body person = bodyData.getPerson(0);
		if(person != null && !isGameOver){
			PVector head = person.getJoint(Body.HEAD);
			// Quat q = person.getJointOrientation(Body.HAND_LEFT);
			PVector spine = person.getJoint(Body.SPINE_SHOULDER);
			PVector spineBase = person.getJoint(Body.SPINE_BASE);
			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
			PVector shoulderLeft = person.getJoint(Body.SHOULDER_LEFT);
			PVector handRight = person.getJoint(Body.HAND_RIGHT);
			PVector handLeft = person.getJoint(Body.HAND_LEFT);
			PVector elbowRight = person.getJoint(Body.ELBOW_RIGHT);
			PVector elbowLeft = person.getJoint(Body.ELBOW_LEFT);
			PVector hipRight = person.getJoint(Body.HIP_RIGHT);
			PVector hipLeft = person.getJoint(Body.HIP_LEFT);
			PVector kneeRight  = person.getJoint(Body.KNEE_RIGHT);
			PVector kneeLeft = person.getJoint(Body.KNEE_LEFT);
			PVector footRight = person.getJoint(Body.FOOT_RIGHT);
			PVector footLeft = person.getJoint(Body.FOOT_LEFT);
			fill(255,255,255);
			
			noStroke();
			drawConnection(elbowRight, shoulderRight);
			drawConnection(elbowLeft, shoulderLeft);
			drawConnection(hipRight, shoulderRight);
			drawConnection(hipLeft, shoulderLeft);
			drawConnection(spine, shoulderRight);
			drawConnection(spine, shoulderLeft);
			drawConnection(head, spine);
			drawConnection(elbowRight, handRight);
			drawConnection(elbowLeft, handLeft);
			drawConnection(spine, spineBase);
			drawConnection(spineBase, hipRight);
			drawConnection(spineBase, hipLeft);
			drawConnection(kneeRight, hipRight);
			drawConnection(kneeLeft, hipLeft);
			drawConnection(kneeLeft, footLeft);
			drawConnection(kneeRight, footRight);
			
			drawJoint(head);
			drawJoint(spine);
			drawJoint(spineBase);
			drawJoint(shoulderRight);
			drawJoint(shoulderLeft);
			drawJoint(handRight);
			drawJoint(handLeft);
			drawJoint(elbowRight);
			drawJoint(elbowLeft);
			drawJoint(hipRight);
			drawJoint(hipLeft);
			drawJoint(kneeRight);
			drawJoint(kneeLeft);
			drawJoint(footRight);
			drawJoint(footLeft);
			
			currentPose = poses.getCurrentPose();
			
			leftShoulderAngle = calculateAngle(elbowLeft, shoulderLeft, hipLeft, currentPose.getLeftShoulderAngle());
			rightShoulderAngle = calculateAngle(elbowRight, shoulderRight, hipRight, currentPose.getRightShoulderAngle());
			leftElbowAngle = calculateAngle(handLeft, elbowLeft, shoulderLeft, currentPose.getLeftElbowAngle());
			rightElbowAngle = calculateAngle(handRight, elbowRight, shoulderRight, currentPose.getRightElbowAngle());
			leftHipAngle = calculateAngle(shoulderLeft, hipLeft, kneeLeft, currentPose.getLeftHipAngle());
			rightHipAngle = calculateAngle(shoulderRight, hipRight, kneeRight, currentPose.getRightHipAngle());
			leftKneeAngle = calculateAngle(hipLeft, kneeLeft, footLeft, currentPose.getLeftKneeAngle());
			rightKneeAngle = calculateAngle(hipRight, kneeRight, footRight, currentPose.getRightKneeAngle());
			
			boolean isCorrectPose = checkCorrectPose(shoulderLeft, shoulderRight, elbowLeft, elbowRight, hipLeft, hipRight, kneeLeft, kneeRight);
			if (isCorrectPose) {
				poses.removePose();
				System.out.println("True Pose");
				if (poses.isEmpty()) {
					isGameOver = true;
				}
			}
		}
	}
	
	private boolean checkCorrectPose(PVector shoulderLeft, PVector shoulderRight, PVector elbowLeft, PVector elbowRight, 
			PVector hipLeft, PVector hipRight, PVector kneeLeft, PVector kneeRight) {
		boolean isCorrect = true;
		
		if (leftShoulderAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftShoulderAngle, currentPose.getLeftShoulderAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightShoulderAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightShoulderAngle, currentPose.getRightShoulderAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (leftElbowAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftElbowAngle, currentPose.getLeftElbowAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightElbowAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightElbowAngle, currentPose.getRightElbowAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (leftHipAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftHipAngle, currentPose.getLeftHipAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightHipAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightHipAngle, currentPose.getRightHipAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (leftKneeAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftKneeAngle, currentPose.getLeftKneeAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightKneeAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightKneeAngle, currentPose.getRightKneeAngle());
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		return isCorrect;
	}
	
	private void drawJointArc(PVector startJoint, PVector midJoint, PVector endJoint, float angle, float rightAngle) {
		boolean isJointCorrect = checkCorrectJoint(angle, rightAngle);
		noStroke();
		if (isJointCorrect) {
			fill(0, 255, 255);
		} else {
			fill(255,0,0);
		}
		PVector startCurve = PVector.add(startJoint, midJoint).div(2);
//		startCurve = PVector.add(startCurve, midJoint).div(2);
		PVector endCurve = PVector.add(midJoint, endJoint).div(2);
//		endCurve = PVector.add(midJoint, endCurve).div(2);
		beginShape();
		vertex(midJoint.x, midJoint.y);
		vertex(startCurve.x, startCurve.y);
		vertex(endCurve.x, endCurve.y);
		vertex(midJoint.x, midJoint.y);
		endShape();
		strokeWeight(.3f);
		curve (midJoint.x, midJoint.y, startCurve.x, startCurve.y, endCurve.x, endCurve.y, midJoint.x, midJoint.y);
	}
	
	private boolean checkCorrectJoint(float currentAngle, float targetAngle) {
		if (Math.abs(currentAngle - targetAngle) < 10) {
			return true;
		}
		return false;
	}
	
	private void drawJoint (PVector v) {
		if (v != null) {
			fill(0, 0, 0);
			ellipse(v.x, v.y, .05f,.05f);
		}
	}
	
	private void drawConnection (PVector v1, PVector v2) {
		if (v1 != null && v2 != null) {
			stroke(0, 0,0);
			strokeWeight(.02f);
			line(v1.x,v1.y, v2.x, v2.y);
		}
	}
	
	private float calculateAngle(PVector startJoint, PVector midJoint, PVector endJoint, float rightAngle) {
		if (startJoint != null && midJoint != null && endJoint != null) {
			PVector orientation = PVector.sub(midJoint, endJoint);
			float angle = Math.abs(angleOf(startJoint, midJoint, orientation));
			drawJointArc(startJoint, midJoint, endJoint, angle, rightAngle);
			return angle;
		}
		return -1;
	}
	
	private float angleOf(PVector v1, PVector v2, PVector axis) {
		PVector limb = PVector.sub(v2, v1);
		return degrees(PVector.angleBetween(limb, axis));
	}
	
	public static void main(String[] args) {
		PApplet.main(PosingGame.class.getName());
	}
}