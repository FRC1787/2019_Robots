package frc.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;


public class Vision
{
	private final GRIP grip = GRIP.getInstance();

	// Declare cameras
	private UsbCamera cargoCamera;
	private UsbCamera hatchCamera;

	private CvSource outputStream;

	private CvSink cargoFrameGrabber;
	private CvSink hatchFrameGrabber;

	private static final int STANDARD_IMG_WIDTH = 160;
	private static final int STANDARD_IMG_HEIGHT = 120;

	private Mat originalFrame = new Mat();
	private Mat processedFrame = new Mat();
	private Mat finalFrame = new Mat();

	//Final HSV thresholds for cargo(orange ball)
	public Scalar HSV_THRESHOLD_LOWER = new Scalar(100, 200, 238);   //(0.0, 76.8, 240.7);
	public Scalar HSV_THRESHOLD_UPPER = new Scalar(200, 255, 255);   //(29.5, 192.5, 255.0);

	double filterContoursMinArea = 10000.0;
	double filterContoursMinPerimeter = 0;
	double filterContoursMinWidth = 0;
	double filterContoursMaxWidth = 1000;
	double filterContoursMinHeight = 0;
	double filterContoursMaxHeight = 1000;
	double[] filterContoursSolidity = {0, 100};
	double filterContoursMaxVertices = 1000000;
	double filterContoursMinVertices = 0;
	double filterContoursMinRatio = 0;
	double filterContoursMaxRatio = 1000;

	List<MatOfPoint> filteredContours;

	// Colors used to draw contours..........new Scalar(B, G, R);
	public static final Scalar COLOR_BLACK = new Scalar(0, 0, 0);
	public static final Scalar COLOR_WHITE = new Scalar(255, 255, 255);
	public static final Scalar COLOR_BLUE = new Scalar(255, 0, 0);
	public static final Scalar COLOR_GREEN = new Scalar(0, 255, 0);
	public static final Scalar COLOR_RED = new Scalar(0, 0, 255);
	public static final Scalar COLOR_YELLOW = new Scalar(0, 255, 255);
	public static final Scalar COLOR_PURPLE = new Scalar(255, 0, 255);
	public static final Scalar COLOR_CYAN = new Scalar(255, 255, 0);
	private final Scalar[] COLORS = {COLOR_RED, COLOR_YELLOW, COLOR_CYAN, 
								   COLOR_GREEN, COLOR_PURPLE, COLOR_BLUE};

	private ArrayList<MatOfPoint> processOutput = new ArrayList<MatOfPoint>();

	//Singelton instance
	private static final Vision instance = new Vision();


	//Default constructor for the vision class
	public Vision()
	{
		CameraServer cameraServer = CameraServer.getInstance();

		//Initialize each camera with a channel and name, pushes non-processed images
		cargoCamera = cameraServer.startAutomaticCapture("Cargo Camera", 1);
		hatchCamera = cameraServer.startAutomaticCapture("Hatch Camera", 0);

		//Configure resoltuion, FPS, exposure, brightness and white-balance
		configureCamera(cargoCamera, false);
		configureCamera(hatchCamera, false);

		//Initialize frame grabber used to grab individual frames from video stream to be processed later
		cargoFrameGrabber = cameraServer.getVideo(cargoCamera);
		hatchFrameGrabber = cameraServer.getVideo(hatchCamera);

		//Push processed or unprocessed frames
		outputStream = cameraServer.putVideo("Processed Video", STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);

		
				
	}



//5, 50, 50
//15, 255, 255


	public void setColor(boolean isUpperBounds, int pos, double newValue)
	{
		Scalar update;
		Scalar bounds;
		if (isUpperBounds) bounds = HSV_THRESHOLD_UPPER;
		else bounds = HSV_THRESHOLD_LOWER;

		switch (pos) {
			case 1:
				update = new Scalar(newValue, bounds.val[1], bounds.val[2]);
				break;
			case 2:
				update = new Scalar(bounds.val[0], newValue, bounds.val[2]);
				break;
			case 3:
				update = new Scalar(bounds.val[0], bounds.val[1], newValue);
				break;
			default:
				throw new UnsupportedOperationException();
		}

		// Update threshold
		if (isUpperBounds) HSV_THRESHOLD_UPPER = update;
		else HSV_THRESHOLD_LOWER = update;
	}



	public void outputFrame(Mat currentFrame)
	{
		if (!currentFrame.empty()) {
			outputStream.putFrame(currentFrame);
		}
	}


	public boolean ballInFrame()
	{

		
		cargoFrameGrabber.grabFrame(originalFrame, 1.0);

		Imgproc.cvtColor(originalFrame, processedFrame, Imgproc.COLOR_BGR2HSV);

		finalFrame = processing();

		if(!finalFrame.empty())
		{
			outputStream.putFrame(finalFrame);
			System.out.println("YES");
      /*
		processOutput = grip.process(processedFrame);

		if(!processOutput.isEmpty())
		{
			this.outputFrame(this.drawContoursOnFrame(processOutput));
			System.out.println("Full");
      */
			return true;
		}
		else
		{

			this.outputFrame(originalFrame);
			System.out.println("Empty");

			return false;
		}



	}



	

	public Mat getCurrentFrame()
	{
		cargoFrameGrabber.grabFrame(originalFrame, 1.0);
		return originalFrame;
	}


	/**
	 * Sets camera settings for either driving or vision processing
	 *
	 * @param camera,          camera object that will be configured
	 * @param targetingCamera, boolean indicating whether the camera is going to be used for image processing
	 */
	public void configureCamera(UsbCamera camera, boolean targetingCamera)
	{
		camera.setResolution(STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT);
		camera.setFPS(10);
		if(targetingCamera)
		{
			camera.setExposureManual(5);
		}
		else
		{
			camera.setExposureAuto();
		}

		camera.setBrightness(40);
		camera.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
	}


	/**
	 * Apply an HSV filter, filters the image based on hue, saturation and value(brightness sort of)
	 *
	 * @param: lowerHSVBounds, the minimum values for the filtration
	 * @param: upperHSVBounds, the maximum values for the filtration
	 * @return: processedFrame, binary image
	 */
	public Mat getHSVFilteredImage(Scalar lowerHSVBounds, Scalar upperHSVBounds)
	{
		//Grab frames from cargo camera to be processed
		cargoFrameGrabber.grabFrame(originalFrame, 2);

		//Covert the BGR image to a HSV image
		Imgproc.cvtColor(originalFrame, processedFrame, Imgproc.COLOR_BGR2HSV);

		//Apply hsv filter
		Core.inRange(originalFrame, HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER, processedFrame);

		//Return processed frame
		return processedFrame;
	}

	/**
	 * Gets a list of contorus from a binary image, stores them in an array list
	 *
	 * @param image, binary frame
	 * @return contoursList
	 */
	public List<MatOfPoint> findExternalContours(Mat image)
	{
		// Empty arraylist of mat points to store contours in
		ArrayList <MatOfPoint> contoursList = new ArrayList<MatOfPoint>();

		// Mode and method variables, only find external contours
		int mode = Imgproc.RETR_EXTERNAL;
		// Again I don't knwo what this means but Simon did so it should work
		int method = Imgproc.CHAIN_APPROX_SIMPLE;

		// Simon did it for overlapping contours i don't know what is does!!
		Mat hierarchy = new Mat();

		return contoursList;
	}

	/**
	 * Finds the centroid (center point) of an array list of contours
	 *
	 * @param foundContours,
	 * @return centerPoint,
	 */
	public Point findContourCenter(MatOfPoint foundContours)
	{
		//Decalre the center point
		Point centerPoint = new Point();
		//Simon did it, i don't know what is does but it's here
		Moments moments = Imgproc.moments(foundContours);

		//Find the x position of the center
		double xCenter = moments.get_m10()/ moments.get_m00();

		//Find the y position of the center
		double yCenter = moments.get_m01()/ moments.get_m00();

		//Set the X and Y values of the center
		centerPoint.x = xCenter;
		centerPoint.y = yCenter;

		return centerPoint;
	}


	/**
	 * Draws the contours on the original frame
	 *
	 * @param contourList, an array list of mat point with the contours stored inside
	 * @return processedFrame
	 */
	public Mat drawContoursOnFrame(List<MatOfPoint> contourList)
	{
		for(int contourIndex = contourList.size() -1; contourIndex >= 0; contourIndex--)
		{
			Imgproc.drawContours(processedFrame, contourList, contourIndex, COLOR_BLUE);
		}

		return processedFrame;

	}

	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximimum height
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	private void filterContours(List<MatOfPoint> inputContours, double minArea,
								double minPerimeter, double minWidth, double maxWidth, double minHeight, double
										maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
										minRatio, double maxRatio, List<MatOfPoint> output) {
		final MatOfInt hull = new MatOfInt();
		output.clear();
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int) hull.get(j, 0)[0];
				double[] point = new double[]{contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount) continue;
			final double ratio = bb.width / (double) bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
	}

	public void pushProcessing()
	{	
		outputStream.putFrame(drawContoursOnFrame(findExternalContours(getHSVFilteredImage(HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER))));
	}

	public Mat processing()
	{	
		

		if(!findExternalContours(getHSVFilteredImage(HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER)).isEmpty())
		{
			filterContours(findExternalContours(getHSVFilteredImage(HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER)), filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filteredContours);
			return drawContoursOnFrame(filteredContours);
		}

		else
		{
			return originalFrame;
		}
				
		
	}

	// Return method for the singleton instance
	public static Vision getInstance()
	{
		return instance;
	}
}
