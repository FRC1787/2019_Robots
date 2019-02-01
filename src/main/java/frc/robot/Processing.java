package frc.robot;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team1787.subsystems.Autonomous;
import org.usfirst.frc.team1787.subsystems.DriveTrain;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Processing {
	private UsbCamera topCam;
	private UsbCamera bottomCam;
	
	private static final Vision instance = new Vision();
	
	private CvSource outputStream;
	private CvSink topFrameGrabber;
	private CvSink bottomFrameGrabber;
	private static final int STANDARD_IMG_WIDTH = 160;
	private static final int STANDARD_IMG_HEIGHT = 120;
	private Mat originalFrame = new Mat();
	private Mat processedFrame = new Mat();
	private MatOfPoint optimalContour;
	private Point centerOfBestContour;
	private double errorX;
	private final double ACCEPTABLE_CENTER_RANGE = 5.0;
	private final double VISION_CORRECTION_SPEED = 0.3;
	private ArrayList<MatOfPoint> allContours;
	private final Scalar FILTER_UPPER_BOUND = new Scalar(100, 255, 128);
	private final Scalar FILTER_LOWER_BOUND = new Scalar(60, 224, 20);
	private static final double CENTER_X = (STANDARD_IMG_WIDTH - 1) / 2.0;
	private static final double CENTER_Y = (STANDARD_IMG_HEIGHT - 1) / 2.0;
	private Targeting targeting = Targeting.getInstance();
	private DriveTrain driveTrain = DriveTrain.getInstance();
	
	private Vision() {
		CameraServer server = CameraServer.getInstance();
		  
		topCam = server.startAutomaticCapture(1);
		bottomCam = server.startAutomaticCapture(0);
		
		topCam.setResolution(STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
		topCam.setFPS(20);
		topCam.setExposureManual(5);//vision targeting optimal value is 0
		topCam.setBrightness(100);//changed by Jordan, usually 100
		topCam.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
		
		bottomCam.setResolution(STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
		bottomCam.setFPS(20);
		bottomCam.setExposureManual(5);
		bottomCam.setBrightness(100);//was 100
		bottomCam.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
		
		
		topFrameGrabber = server.getVideo(topCam);
		bottomFrameGrabber = server.getVideo(bottomCam);
		
		outputStream = server.putVideo("Processed Video Stream", STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
	}
	
	
	public void pushFrameToOutput(Mat currentFrame) {
		if (!currentFrame.empty()) {
			outputStream.putFrame(currentFrame);
		}
	}
	
	
	public void visionProcessing() {
		
		//processedFrame is initially empty, and it is then populated by the top camera's frame
		//because grabFrame takes a frame location as input and then writes to that location
		
		topFrameGrabber.grabFrame(originalFrame, 1.0);
		
		if (!originalFrame.empty()) {

			Imgproc.cvtColor(originalFrame, processedFrame, Imgproc.COLOR_BGR2HSV);
		    Core.inRange(processedFrame, FILTER_LOWER_BOUND, FILTER_UPPER_BOUND, processedFrame);
			pushFrameToOutput(processedFrame);
		}
	}
	
	
	public void visionTargeting() {
		
		//processedFrame is initially empty, and it is then populated by the top camera's frame
		//because grabFrame takes a frame location as input and then writes to that location
		topFrameGrabber.grabFrame(originalFrame, 1.0);
				
		Imgproc.cvtColor(originalFrame, processedFrame, Imgproc.COLOR_BGR2HSV);
		Core.inRange(processedFrame, FILTER_LOWER_BOUND, FILTER_UPPER_BOUND, processedFrame);
		pushFrameToOutput(processedFrame);
		
		
				
		if (!targeting.findContours(processedFrame).isEmpty()) {
			
			allContours = targeting.findContours(processedFrame);
			
			optimalContour = targeting.bestContour(allContours);
			
			centerOfBestContour = targeting.centerOfContour(optimalContour);
			
			errorX = targeting.xOfCenterContour(optimalContour) - ((STANDARD_IMG_WIDTH - 1)/2);
			
			if (errorX < ACCEPTABLE_CENTER_RANGE && errorX > -ACCEPTABLE_CENTER_RANGE ) {
				driveTrain.tankDrive(0, 0);
			}
			else if (errorX >= ACCEPTABLE_CENTER_RANGE) {
				driveTrain.tankDrive(VISION_CORRECTION_SPEED, -VISION_CORRECTION_SPEED);
			}
			else if (errorX <= -ACCEPTABLE_CENTER_RANGE) {
				driveTrain.tankDrive(-VISION_CORRECTION_SPEED, VISION_CORRECTION_SPEED);
			}
		}
		else {
			
		}
		
	}
	
	
	
	
	

	public static Vision getInstance() {
		return instance;
	}
}
