import java.io.IOException;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * The user has 2 minutes to make as many poses as possible.
 * For each pose, the user has 5 points.
 * Instruction is shown at the beginning of the game
 * @author Natalie Truong and Carla Gonzalez-Vazquez
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
	private PImage scoreImg;
	
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
		// Load images
		instructionImg = loadImage("data/instruction.png");
		gameOverImg = loadImage("data/gameover.png");
		startButtonImg = loadImage("data/startbutton.png");
		timeLabel = loadImage("data/time.png");
		scoreImg = loadImage("data/score.png");
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
		poses.addAllPoses(this);
		currentPose = poses.getCurrentPose();
	}
	
	public void draw(){
		setScale(.5f);
		noStroke();
		background(255,255,255);
		
		// Draw time bar and score
		drawTimeBar();
		drawScore();
		
		//draw person
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		if (!gameStart) {
			drawInstruction();
		}
		
		Body person = getClosestBody(bodyData);
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
			
			// Fill in the torso
			if (spine != null && shoulderLeft != null && shoulderRight != null && hipLeft != null && hipRight != null) {
				beginShape();
				vertex(shoulderLeft.x, shoulderLeft.y);
				vertex(spine.x, spine.y);
				vertex(shoulderRight.x, shoulderRight.y);
				vertex(hipRight.x, hipRight.y);
				vertex(hipLeft.x, hipLeft.y);
				endShape();
			}
			
			// Check start button is touched and start the game
			if (handRight!=null && !gameStart) {
				gameStart = checkTouchStartButton(handRight);
				if (gameStart) {
					startTime = millis();
					score = 0;
					poses.addAllPoses(this);
					currentPose = poses.getCurrentPose();
				}
			}
			
			if (gameStart) {
				poses.drawPose(this);
				updateTime();
				
				leftShoulderAngle = calculateAngle(elbowLeft, shoulderLeft, hipLeft, currentPose.getLeftShoulderAngle());
				rightShoulderAngle = calculateAngle(elbowRight, shoulderRight, hipRight, currentPose.getRightShoulderAngle());
				leftElbowAngle = calculateAngle(handLeft, elbowLeft, shoulderLeft, currentPose.getLeftElbowAngle());
				rightElbowAngle = calculateAngle(handRight, elbowRight, shoulderRight, currentPose.getRightElbowAngle());
				leftHipAngle = calculateAngle(shoulderLeft, hipLeft, kneeLeft, currentPose.getLeftHipAngle());
				rightHipAngle = calculateAngle(shoulderRight, hipRight, kneeRight, currentPose.getRightHipAngle());
				leftKneeAngle = calculateAngle(hipLeft, kneeLeft, footLeft, currentPose.getLeftKneeAngle());
				rightKneeAngle = calculateAngle(hipRight, kneeRight, footRight, currentPose.getRightKneeAngle());
				
				boolean isCorrectPose = checkCorrectPose(shoulderLeft, shoulderRight, elbowLeft, elbowRight, hipLeft, hipRight, kneeLeft, kneeRight);
				
				// Change pose if a pose is matched and set game over when running out of poses
				if (isCorrectPose) {
					poses.removePose();
					currentPose = poses.getCurrentPose();
					score += 5;
					if (poses.isEmpty()) {
						gameStart = false;
						currentTime = -1;
					}
				}
			}
		}
	}
	
	public Body getClosestBody(KinectBodyData bodyData) {
		int personCnt = bodyData.getPersonCount();
		
		if (personCnt <= 0) return null;
		
		Body closestBody = null;
		float dist = Float.MAX_VALUE;
		
		for(int i = 0; i < personCnt; i++) {
			Body b = bodyData.getPerson(i);
			if(b != null) {
				PVector head = b.getJoint(Body.HEAD);
				if(head != null) {
					float d = head.mag();
					if(d < dist) {
						dist = d;
						closestBody = b;
					}
				}
			}
			
		}
		return closestBody;
		
	}
	
	/**
	 * Draw the time bar
	 */
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
	
	/**
	 * Draw the score
	 */
	private void drawScore() {
		fill(255,0,0);
		image(scoreImg,.5f, 1.2f, 0.4f, 0.4f);
		pushMatrix();
		scale(.003f);
		scale(1,-1);
		textSize(100);
		text(""+score, 300, -400);
		popMatrix();
	}
	
	/**
	 * Draw instruction
	 */
	private void drawInstruction() {
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
	
	/**
	 * Update time and check if time is up
	 */
	private void updateTime() {
		currentTime = TIME - (millis() - startTime)/1000;
		if (currentTime <= 0) {
			gameStart = false;
		}
	}
	
	/**
	 * Check if all joints get the correct angle
	 * @param shoulderLeft
	 * @param shoulderRight
	 * @param elbowLeft
	 * @param elbowRight
	 * @param hipLeft
	 * @param hipRight
	 * @param kneeLeft
	 * @param kneeRight
	 * @return true if all joints are correct
	 */
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
	
	/**
	 * Draw the arc around joint
	 * @param startJoint
	 * @param midJoint
	 * @param endJoint
	 * @param angle
	 * @param rightAngle
	 */
	private void drawJointArc(PVector startJoint, PVector midJoint, PVector endJoint, float angle, float rightAngle) {
		// Check correct joint to choose the color for the arc
		boolean isJointCorrect = checkCorrectJoint(angle, rightAngle);
		noStroke();
		if (isJointCorrect) {
			fill(0, 255, 255);
		} else {
			fill(255,0,0);
		}
		// Draw the arc
		PVector startCurve = PVector.add(startJoint, midJoint).div(2);
		PVector endCurve = PVector.add(midJoint, endJoint).div(2);
		// Fill in the arc
		beginShape();
		vertex(midJoint.x, midJoint.y);
		vertex(startCurve.x, startCurve.y);
		vertex(endCurve.x, endCurve.y);
		vertex(midJoint.x, midJoint.y);
		endShape();
		strokeWeight(.3f);
		curve (midJoint.x, midJoint.y, startCurve.x, startCurve.y, endCurve.x, endCurve.y, midJoint.x, midJoint.y);
	}
	
	/**
	 * Check if the angle of the user's joint is below 10 degree from the correct angle
	 * @param currentAngle the user's joint angle
	 * @param targetAngle the correct coded angle
	 * @return true if the angle of the user's joint is below 10 degree from the correct angle
	 */
	private boolean checkCorrectJoint(float currentAngle, float targetAngle) {
		if (Math.abs(currentAngle - targetAngle) < 10) {
			return true;
		}
		return false;
	}
	
	/**
	 * Draw the joint in black
	 * @param v the joint location
	 */
	private void drawJoint (PVector v) {
		if (v != null) {
			fill(0, 0, 0);
			ellipse(v.x, v.y, .05f,.05f);
		}
	}
	
	/**
	 * Draw correct joint in green
	 * @param v
	 */
	private void drawCorrectJoint (PVector v) {
		if (v != null) {
			fill(0, 255, 0);
			ellipse(v.x, v.y, .05f,.05f);
		}
	}
	
	/**
	 * Draw connections between 2 joints
	 * @param v1 joint1
	 * @param v2 joint2
	 */
	private void drawConnection (PVector v1, PVector v2) {
		if (v1 != null && v2 != null) {
			stroke(0, 0,0);
			strokeWeight(.05f);
			line(v1.x,v1.y, v2.x, v2.y);
		}
	}
	
	/**
	 * Calculate the angle and draw the arc
	 * @param startJoint
	 * @param midJoint
	 * @param endJoint
	 * @param rightAngle the correct coded angle
	 * @return the angle if none of the start, mid and end is null, else return -1
	 */
	private float calculateAngle(PVector startJoint, PVector midJoint, PVector endJoint, float rightAngle) {
		if (startJoint != null && midJoint != null && endJoint != null) {
			PVector orientation = PVector.sub(midJoint, endJoint);
			float angle = Math.abs(angleOf(startJoint, midJoint, orientation));
			drawJointArc(startJoint, midJoint, endJoint, angle, rightAngle);
			return angle;
		}
		return -1;
	}
	
	/**
	 * Calculate angle of 2 vectors and an axis
	 * @param v1
	 * @param v2
	 * @param axis
	 * @return degree of the angle
	 */
	private float angleOf(PVector v1, PVector v2, PVector axis) {
		PVector limb = PVector.sub(v2, v1);
		return degrees(PVector.angleBetween(limb, axis));
	}
	
	/**
	 * Check if the start button has been touched
	 * @param hand
	 * @return true if the right hand is inside the button
	 */
	private boolean checkTouchStartButton(PVector hand) {
		return (hand.x > -.2f && hand.x < .2f && hand.y > -.6f && hand.y < -.4f);
	}
	
	public static void main(String[] args) {
		PApplet.main(PosingGame.class.getName());
	}
}