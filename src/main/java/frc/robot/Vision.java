package frc.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Vision {

    // Immutable static instance of Vision subsystem
    private static final Vision instance = new Vision();

    // Top and bottom cameras
    private final UsbCamera topCamera, bottomCamera;

    // Camera server
    private final CameraServer cameraServer;

    // OpenCV instances for cameras
    private final CvSink top, bottom;

    // Mat instances
    private Mat topMat = new Mat(), bottomMat = new Mat();
    private Mat topProcessedMat = new Mat(), bottomProcessedMat = new Mat();

    private boolean pressed = false;


    private Vision() {
        // Get camera server instance
        this.cameraServer = CameraServer.getInstance();

        // Create UsbCamera instances
        this.topCamera = cameraServer.startAutomaticCapture(0);
        this.bottomCamera = cameraServer.startAutomaticCapture(1);

        // Set camera settings
        topCamera.setResolution(160, 120);
        topCamera.setFPS(20);
        topCamera.setExposureManual(0);
        topCamera.setBrightness(100);
        topCamera.setWhiteBalanceManual(VideoCamera.WhiteBalance.kFixedIndoor);

        bottomCamera.setResolution(160, 120);
        bottomCamera.setFPS(20);
        bottomCamera.setExposureManual(0);
        bottomCamera.setBrightness(100);
        bottomCamera.setWhiteBalanceManual(VideoCamera.WhiteBalance.kFixedIndoor);

        // Add cameras to camera server
        this.cameraServer.addCamera(topCamera);
        this.cameraServer.addCamera(bottomCamera);

        // Get OpenCV sources for cameras
        this.top = this.cameraServer.getVideo(topCamera);
        this.bottom = this.cameraServer.getVideo(bottomCamera);
    }

    public synchronized void pollCameras() {

        // Grab frames from the cameras
        top.grabFrame(topMat);

        // Convert color spaces
        Imgproc.cvtColor(topMat, topProcessedMat, Imgproc.COLOR_BGR2HSV);

        // Make sure the data is within acceptable nugget range
        Core.inRange(topProcessedMat, new Scalar(83, 55, 34), new Scalar(222, 153, 90), topProcessedMat);

        // Find the nuggetly contours
        List<MatOfPoint> points = findContours(topProcessedMat);

        // Make sure there are some points
        if (!points.isEmpty()) {

        }


    }

    public synchronized List<MatOfPoint> findContours(Mat frame) {
        List<MatOfPoint> points = new ArrayList<>();

        // Heirarchy mat
        Mat hierarchy = new Mat();

        // Find image contours
        Imgproc.findContours(topMat, points, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        return points;
    }

    public static synchronized Vision getInstance() {
        return instance;
    }
}
