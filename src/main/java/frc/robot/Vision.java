package frc.robot;

//Imports
	//Java imports
import java.util.ArrayList;

	//OpenCV imports
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

//WPI imports
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.wpilibj.CameraServer;


public class Vision
{
	private final GRIP grip = GRIP.getInstance();

	//Decalre cameras
	private UsbCamera cargoCamera;
	private UsbCamera hatchCamera;
	
	private CvSource outputStream;
	private CvSink cargoFrameGrabber;
	private CvSink hatchFrameGrabber;
	private static final int STANDARD_IMG_WIDTH = 160;
	private static final int STANDARD_IMG_HEIGHT = 120;
	private Mat originalFrame = new Mat();
	private Mat processedFrame = new Mat();

	//Final HSV thresholds for cargo(orange ball)
	private final Scalar HSV_THRESHOLD_LOWER = new Scalar(0.0, 162.8, 240.7);
	private final Scalar HSV_THRESHOLD_UPPER = new Scalar(29.5, 224.5, 255.0);

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

		processOutput = grip.process(processedFrame);

		if(!processOutput.isEmpty())
		{
			this.outputFrame(this.drawContoursOnFrame(processOutput));
			System.out.println("Full");
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



	/**Sets camera settings for either driving or vision processing
	 * 
	 * @param camera, camera object that will be configured
	 * @param targetingCamera, boolean indicating wether the camera is going to be used for image processing
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

		camera.setBrightness(50);
		camera.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
	}




































	/*Apply an HSV filter, filters the image based on hue, saturation and value(brightness sort of)
	* @param: lowerHSVBounds, the minimum values for the filtration
	* @param: upperHSVBounds, the maximum values for the filtration
	*
	* @return: processedFrame, binary image
	*/
	public Mat  getHSVFitlteredImage(Scalar lowerHSVBounds, Scalar upperHSVBounds)
	{
		//Grab frames from cargo camera to be processed
		cargoFrameGrabber.grabFrame(originalFrame);

		//Covert the BGR image to a HSV image
		Imgproc.cvtColor(originalFrame, processedFrame, Imgproc.COLOR_BGR2HSV);

		//Apply hsv filter
		Core.inRange(originalFrame, HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER, processedFrame);

		//Return processed frame
		return processedFrame;
	}

	/**Gets a list of contorus from a binary image, stores them in an array list
	 * 
	 * @param image, binary frame
	 * @return contoursList
	 */
	public ArrayList<MatOfPoint> findExternalContours(Mat image)
	{
		//Empty arraylist of mat points to store contours in 
		ArrayList <MatOfPoint> contoursList = new ArrayList<MatOfPoint>();

		//Mode and method variables, only find external contours
		int mode = Imgproc.RETR_EXTERNAL;
		//Again I don't knwo what this means but Simon did so it should work
		int method = Imgproc.CHAIN_APPROX_SIMPLE;

		//Simon did it for overlapping contours i don't know what is does!!
		Mat hierarchy = new Mat();

		return contoursList;
	}

	/**Finds the centroid (center point) of an array list of contours
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

	

	/**Draws the contours on the original frame
	 * 
	 * @param contourList, an array list of mat point with the contours stored inside
	 * @return originalFrame
	 */
	public Mat drawContoursOnFrame(ArrayList<MatOfPoint> contourList)
	{
		for(int contourIndex = contourList.size() -1; contourIndex >= 0; contourIndex--)
		{
			Imgproc.drawContours(originalFrame, contourList, contourIndex, COLORS[contourIndex % COLORS.length]);
		}

		return originalFrame;

	}

	public void processing()
	{
		outputStream.putFrame(drawContoursOnFrame(findExternalContours(getHSVFitlteredImage(HSV_THRESHOLD_LOWER, HSV_THRESHOLD_UPPER))));
	}

	//Return method for the singelton instance
	public static Vision getInstance()
	{
		return instance;
	}
}
