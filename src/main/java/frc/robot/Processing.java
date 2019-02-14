package frc.robot;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
// import org.usfirst.frc.team1787.subsystems.Autonomous;
// import org.usfirst.frc.team1787.subsystems.DriveTrain;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Processing
{

	private static final Processing instance = new Processing();
	
	public Processing()
	{
	
	}

	public ArrayList<MatOfPoint> findContours(Mat currentFrame)
	{
		ArrayList<MatOfPoint> listOfContours = new ArrayList<MatOfPoint>();
		
		//Literally no fucking clue what these three lines do
	    Mat hierarchy = new Mat();
	    int mode = Imgproc.RETR_EXTERNAL;
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		
	    //Imgproc.findContours(frame, listOfContours, hierarchy, mode, method);
	    
	    return listOfContours;
	}

	// public MatOfPoint bestContour(ArrayList<MatOfPoint> contours)
	// {

	// }


	public static Processing getInstance()
	{
		return instance;
	}
}
