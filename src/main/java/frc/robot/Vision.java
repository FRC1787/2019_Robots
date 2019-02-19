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

	private UsbCamera cargoCam, hatchCam;
	
	private CvSource outputStream;
	private CvSink cargoFrameGrabber, hatchFrameGrabber;
	private static final int STANDARD_IMG_WIDTH = 160;
	private static final int STANDARD_IMG_HEIGHT = 120;
	private Mat originalFrame = new Mat();

	private Mat processedFrame = new Mat();
	private final Scalar HSV_THRESHOLD_LOWER = new Scalar(0.0, 162.8, 240.7);
	private final Scalar HSV_THRESHOLD_UPPER = new Scalar(29.5, 224.5, 255.0);

	private DriveTrain driveTrain;
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
	}



	private void filterContours(ArrayList<MatOfPoint> inputContours, double minArea, double minPerimeter, double minWidth, double maxWidth, double minHeight, double maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double minRatio, double maxRatio, ArrayList<MatOfPoint> output) 
	{

	final MatOfInt hull = new MatOfInt();
	output.clear();

	for (int i = 0; i < inputContours.size(); i++) 
	{
		final MatOfPoint contour = inputContours.get(i);
		final Rect bb = Imgproc.boundingRect(contour);
		if (bb.width < minWidth || bb.width > maxWidth) 
		continue;
		if (bb.height < minHeight || bb.height > maxHeight) 
		continue;
		final double area = Imgproc.contourArea(contour);
		if (area < minArea) 
		continue;
		if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) 
		continue;
		Imgproc.convexHull(contour, hull);
		MatOfPoint mopHull = new MatOfPoint();
		mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
		for (int j = 0; j < hull.size().height; j++) 
		{
			int index = (int)hull.get(j, 0)[0];
			double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
			mopHull.put(j, 0, point);
		}
		final double solid = 100 * area / Imgproc.contourArea(mopHull);
		if (solid < solidity[0] || solid > solidity[1]) 
		continue;
		if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	
		continue;
		final double ratio = bb.width / (double)bb.height;
		if (ratio < minRatio || ratio > maxRatio) 
		continue;
		output.add(contour);
	}
}


	public int giveUp(Mat inputFrame) 
	{
		int totalPassedPixels = 0;
		double[] goodPixel = {1, 1, 1};

		for(int i = 0; i < inputFrame.rows(); i++)
		{
			for(int j = 0; j < inputFrame.cols(); j++)
			{
				if(inputFrame.get(i, j).equals(goodPixel))
				{
					totalPassedPixels++;
				}
			}
		}

		return totalPassedPixels;
	}



	public boolean bigBallsInFrame()
	{
		Mat inputFrame = new Mat();

		cargoFrameGrabber.grabFrame(inputFrame);

		Core.inRange(inputFrame, HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER, processedFrame);

		
		//outputStream.putFrame(processedFrame);

		if (this.giveUp(processedFrame) < 10000)
		{
			return false;
		}
			return true;
		
	}

	public static Vision getInstance()
	{
		return instance;
	}
	
}