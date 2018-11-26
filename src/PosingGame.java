import java.io.IOException;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import processing.core.PApplet;
import processing.core.PImage;
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

	private boolean gameStart = false;
	
	private static final int TIME = 120;
	private int startTime;
	private int currentTime = -2;
	
	private PImage instructionImg;
	private PImage gameOverImg;
	private PImage startButtonImg;
	private PImage timeLabel;
	
	private int score = 0;
	
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
		instructionImg = loadImage("data/instruction.png");
		gameOverImg = loadImage("data/gameover.png");
		startButtonImg = loadImage("data/startbutton.png");
		timeLabel = loadImage("data/time.png");
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
		
		drawTimeBar();
		drawScore();
		
		//draw person
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		if (!gameStart) {
			pushMatrix();
			scale(1,-1);		
			// load instruction and start button
			if (currentTime == -2) {
				image(instructionImg, -2, -1.75f, 4, 3);
			} else {
				image(gameOverImg, -2, -1.75f, 4, 3);
			}
			//image(startButtonImg, -.2f, -.6f, .4f, .2f);
			popMatrix();
			image(startButtonImg, -.2f, -.6f, .4f, .2f);
		}
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
			
			if (spine != null && shoulderLeft != null && shoulderRight != null && hipLeft != null && hipRight != null) {
				beginShape();
				vertex(shoulderLeft.x, shoulderLeft.y);
				vertex(spine.x, spine.y);
				vertex(shoulderRight.x, shoulderRight.y);
				vertex(hipRight.x, hipRight.y);
				vertex(hipLeft.x, hipLeft.y);
				endShape();
			}
			
			if (handRight!=null && !gameStart) {
				gameStart = checkTouchStartButton(handRight);
				if (gameStart) {
					startTime = millis();
					score = 0;
					poses.addAllPoses(this);
				}
			}
			
			if (gameStart) {
				poses.drawPose(this);
				currentTime = TIME - (millis() - startTime)/1000;
				if (currentTime == 0) {
					gameStart = false;
				}
				
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
					score += 5;
					if (poses.isEmpty()) {
						gameStart = false;
						currentTime = -1;
					}
				}
			}
		}
	}
	
	private void drawTimeBar() {
		fill(0,0,0);
		float x = -1f;
		float y = 1.2f;
		float barWidth = 1f;
		float barHeight = .1f;
		final float BORDER = .01f;
		image(timeLabel, x, y, 0.4f, 0.4f);
		rect(x, y, barWidth, barHeight);
		fill(244, 137, 66);
		// draw the orange bar if the level is not 0
		if (currentTime > 0) {
			rect(x+BORDER, y, barWidth*currentTime/TIME-BORDER*2, barHeight-BORDER*2, 7);
		}
	}
	
	private void drawScore() {
		fill(255,0,0);
		pushMatrix();
		scale(.006f);
		scale(1,-1);
		textSize(43);
		text("score: "+score, 80, -200);
		popMatrix();
	}
	
	private boolean checkCorrectPose(PVector shoulderLeft, PVector shoulderRight, PVector elbowLeft, PVector elbowRight, 
			PVector hipLeft, PVector hipRight, PVector kneeLeft, PVector kneeRight) {
		boolean isCorrect = true;
		
		if (leftShoulderAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftShoulderAngle, currentPose.getLeftShoulderAngle());
			if (isJointCorrect) {
				drawCorrectJoint(shoulderLeft);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightShoulderAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightShoulderAngle, currentPose.getRightShoulderAngle());
			if (isJointCorrect) {
				drawCorrectJoint(shoulderRight);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (leftElbowAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftElbowAngle, currentPose.getLeftElbowAngle());
			if (isJointCorrect) {
				drawCorrectJoint(elbowLeft);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightElbowAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightElbowAngle, currentPose.getRightElbowAngle());
			if (isJointCorrect) {
				drawCorrectJoint(elbowRight);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (leftHipAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftHipAngle, currentPose.getLeftHipAngle());
			if (isJointCorrect) {
				drawCorrectJoint(hipLeft);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightHipAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightHipAngle, currentPose.getRightHipAngle());
			if (isJointCorrect) {
				drawCorrectJoint(hipRight);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (leftKneeAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(leftKneeAngle, currentPose.getLeftKneeAngle());
			if (isJointCorrect) {
				drawCorrectJoint(kneeLeft);
			}
			isCorrect = isCorrect && isJointCorrect;
		} else {
			return false;
		}
		
		if (rightKneeAngle > 0) {
			boolean isJointCorrect = checkCorrectJoint(rightKneeAngle, currentPose.getRightKneeAngle());
			if (isJointCorrect) {
				drawCorrectJoint(kneeRight);
			}
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
	
	private void drawCorrectJoint (PVector v) {
		if (v != null) {
			fill(0, 0, 255);
			ellipse(v.x, v.y, .05f,.05f);
		}
	}
	
	private void drawConnection (PVector v1, PVector v2) {
		if (v1 != null && v2 != null) {
			stroke(0, 0,0);
			strokeWeight(.05f);
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
	
	private boolean checkTouchStartButton(PVector hand) {
		return (hand.x > -.2f && hand.x < .2f && hand.y > -.6f && hand.y < -.4f);
	}
	
	public static void main(String[] args) {
		PApplet.main(PosingGame.class.getName());
	}
}