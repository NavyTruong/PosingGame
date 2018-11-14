import java.io.IOException;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
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
	}
	
	public void draw(){
		setScale(.5f);
		noStroke();
		background(255,255,255);
		//draw person
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		Body person = bodyData.getPerson(0);
		if(person != null){
			PVector head = person.getJoint(Body.HEAD);
			PVector spine = person.getJoint(Body.SPINE_SHOULDER);
			PVector spineBase = person.getJoint(Body.SPINE_BASE);
			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
			PVector shoulderLeft = person.getJoint(Body.SHOULDER_LEFT);
			PVector handRight = person.getJoint(Body.HAND_RIGHT);
			PVector handLeft = person.getJoint(Body.HAND_LEFT);
			PVector elbowRight = person.getJoint(Body.ELBOW_RIGHT);
			PVector elbowLeft = person.getJoint(Body.ELBOW_LEFT);
//			PVector hipRight = person.getJoint(Body.HIP_RIGHT);
//			PVector hipLeft = person.getJoint(Body.HIP_LEFT);
			PVector kneeRight  = person.getJoint(Body.KNEE_RIGHT);
			PVector kneeLeft = person.getJoint(Body.KNEE_LEFT);
			PVector footRight = person.getJoint(Body.FOOT_RIGHT);
			PVector footLeft = person.getJoint(Body.FOOT_LEFT);
			fill(255,255,255);
			noStroke();
			drawConnection(elbowRight, spine);
			drawConnection(spine, spineBase);
			drawJoint(head);
			drawJoint(spine);
			drawJoint(spineBase);
//			drawJoint(shoulderRight);
//			drawJoint(shoulderLeft);
			drawJoint(handRight);
			drawJoint(handLeft);
			drawJoint(elbowRight);
			drawJoint(elbowLeft);
//			drawJoint(hipRight);
//			drawJoint(hipLeft);
			drawJoint(kneeRight);
			drawJoint(kneeLeft);
			drawJoint(footRight);
			drawJoint(footLeft);

			scale(.01f);
			scale(1, -1);
			float rightShoulderAngle = calculateAngle(elbowRight, spine, spineBase);
			if (rightShoulderAngle > 0) {
				displayAngle(rightShoulderAngle, elbowRight);
			}
			
			float leftShoulderAngle = calculateAngle(elbowLeft, spine, spineBase);
			if (leftShoulderAngle > 0) {
				displayAngle(leftShoulderAngle, elbowLeft);
			}
		}
	}
	
	private void displayAngle(float degree, PVector location) {
		fill(255,0,0);
		text("" + (int)(degree) + "", location.x, location.y);
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
	
	private float calculateAngle(PVector a, PVector b, PVector c) {
		if (a != null && b!=null && c!=null) {
			PVector orientation = PVector.sub(b, c);
			return Math.abs(angleOf(a, b, orientation));
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