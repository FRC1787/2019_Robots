// package frc.robot;

// import java.util.ArrayList;

// import org.opencv.core.Core;
// import org.opencv.core.Mat;
// import org.opencv.core.MatOfPoint;
// import org.opencv.core.Point;
// import org.opencv.core.Scalar;
// import org.opencv.*;
// import frc.robot.DriveTrain;
// import edu.wpi.cscore.CvSink;
// import edu.wpi.cscore.CvSource;
// import edu.wpi.cscore.UsbCamera;
// import edu.wpi.cscore.VideoCamera.WhiteBalance;
// import edu.wpi.first.wpilibj.CameraServer;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// public class Vision
// {
// 	private static final Vision instance = new Vision();

// 	private UsbCamera topCam;
// 	private UsbCamera bottomCam;
	
// 	private CvSource outputStream;
// 	private CvSink topFrameGrabber;
// 	private CvSink bottomFrameGrabber;
// 	private static final int STANDARD_IMG_WIDTH = 160;
// 	private static final int STANDARD_IMG_HEIGHT = 120;
// 	private Mat originalFrame = new Mat();
// 	private Mat processedFrame = new Mat();

// 	private Targeting targeting = Targeting.getInstance();
// 	private Processing processing = Processing.getInstance();
// 	private DriveTrain driveTrain = DriveTrain.getInstance();
// 	private CameraServer server = CameraServer.getInstance();

// 	 private void configureCamera(UsbCamera inputCam, int exposureValue)
// 	{
// 		inputCam.setResolution(STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
// 		inputCam.setFPS(20);
// 		inputCam.setExposureAuto();
// 		inputCam.setBrightness(100);
// 		inputCam.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
// 	}

// 	public Vision()
// 	{
// 		topCam = server.startAutomaticCapture(1);
// 		bottomCam = server.startAutomaticCapture(0);

// 		topFrameGrabber = server.getVideo(topCam);
// 		bottomFrameGrabber = server.getVideo(bottomCam);

// 		//Exposure value for vision is 0, for regular sight it was 5 but idk what the best value is
// 		configureCamera(topCam, 5);
// 		configureCamera(bottomCam, 0);
		
// 		outputStream = server.putVideo("Processed Video Stream", STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);

		
// 	}


// 	public static Vision getInstance()
// 	{
// 		return instance;
// 	}
	
// }
