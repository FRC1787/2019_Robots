package frc.robot;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.*;
import frc.robot.DriveTrain;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.objdetect.*;


public class Vision
{
	private static final Vision instance = new Vision();
	private final GRIP grip = GRIP.getInstance();

	private UsbCamera cargoCam, hatchCam;
	
	private CvSource outputStream;
	private CvSink cargoFrameGrabber, hatchFrameGrabber;
	private static final int STANDARD_IMG_WIDTH = 160;
	private static final int STANDARD_IMG_HEIGHT = 120;

	private Mat originalFrame = new Mat();
	private Mat processedFrame = new Mat();

	private final Scalar HSV_THRESHOLD_LOWER = new Scalar(0.0, 162.8, 240.7);
	private final Scalar HSV_THRESHOLD_UPPER = new Scalar(29.5, 224.5, 255.0);

	private CameraServer server = CameraServer.getInstance();
	//private CameraServer server2 = CameraServer.getInstance();

	private void configureCamera(UsbCamera inputCam, int exposureValue)
	{
		inputCam.setResolution(STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
		inputCam.setFPS(20);
		//inputCam.setExposureManual(exposureValue);
		inputCam.setExposureAuto();
		inputCam.setBrightness(50);
		inputCam.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
	}

	public Vision()
	{

		cargoCam = server.startAutomaticCapture(1);
		hatchCam = server.startAutomaticCapture(0);

		cargoFrameGrabber = server.getVideo(cargoCam);
		hatchFrameGrabber = server.getVideo(hatchCam);

		//Exposure value for vision is 0, for regular sight it was 5 but idk what the best value is
		configureCamera(cargoCam, 5);
		configureCamera(hatchCam, 5);
		
		outputStream = server.putVideo("Processed Video Stream", STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
	}


	public boolean ballInFrame() {

		//Grab the original frame and run it through the predefined GRIP process
		cargoFrameGrabber.grabFrame(originalFrame);
		grip.process(originalFrame);

		if(!grip.filterContoursOutput().isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}

		
	}

	public static Vision getInstance()
	{
		return instance;
	}
	
}