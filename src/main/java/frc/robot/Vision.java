package frc.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;

public class Vision {

    // Immutable static instance of Vision subsystem
    private static final Vision instance = new Vision();

    // Top and bottom cameras
    private final UsbCamera topCamera, bottomCamera;

    // Camera server
    private final CameraServer cameraServer;

    // OpenCV instances for cameras
    private final CvSink top, bottom;


    private Vision() {
        // Create UsbCamera instances
        this.topCamera = new UsbCamera("Top Camera", 0);
        this.bottomCamera = new UsbCamera("Bottom Camera", 1);

        // Get camera server instance
        this.cameraServer = CameraServer.getInstance();

        // Add cameras to camera server
        this.cameraServer.addCamera(topCamera);
        this.cameraServer.addCamera(bottomCamera);

        // Get OpenCV sources for cameras
        this.top = this.cameraServer.getVideo("Top Camera");
        this.bottom = this.cameraServer.getVideo("Bottom Camera");
    }

    public void pollCameras() {
        // Create new mat object
        Mat topMat = new Mat();
        Mat bottomMat = new Mat();

        // Grab frames from the cameras
        top.grabFrame(topMat);
        bottom.grabFrame(bottomMat);

        System.out.println(String.format("Width: %s, Height: %s", topMat.width(), topMat.height()));
    }

    public static synchronized Vision getInstance() {
        return instance;
    }
}
