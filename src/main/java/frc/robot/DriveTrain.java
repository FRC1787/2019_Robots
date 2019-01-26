package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain
{

    private final int LEFT_MASTER_TALON_ID = 1;
    private final int LEFT_FOLLOWER_VICTOR_ID = 2;
    private final int RIGHT_MASTER_TALON_ID = 3;
    private final int RIGHT_FOLLOWER_VICTOR_ID = 4;

    private WPI_TalonSRX leftMaster = new WPI_TalonSRX(LEFT_MASTER_TALON_ID);
    private WPI_VictorSPX leftFollower = new WPI_VictorSPX(LEFT_FOLLOWER_VICTOR_ID);
    private WPI_TalonSRX rightMaster = new WPI_TalonSRX(RIGHT_MASTER_TALON_ID);
    private WPI_VictorSPX rightFollower = new WPI_VictorSPX(RIGHT_FOLLOWER_VICTOR_ID);

    private static final DriveTrain instance = new DriveTrain();
    
    private static double leftSideRobot;
    private static double rightSideRobot;


    
    public static DriveTrain getInstance()
    {
        return instance;
    }

    public void arcadeDrive(double xAxis, double yAxis)
    {
        yAxis = yAxis * Math.abs(yAxis);
        xAxis = xAxis * Math.abs(xAxis);

        //checks the range to see if the value is greater than 1 or less than -1 and if so corrects the value to be in that range
        yAxis = rangeCorrection(yAxis);
        xAxis = rangeCorrection(xAxis);

        leftSideRobot = yAxis + xAxis;
        rightSideRobot = yAxis - xAxis;

        leftMaster.set(leftSideRobot);
        rightMaster.set(rightSideRobot);

        leftFollower.set(leftSideRobot);
        rightFollower.set(rightSideRobot);
    }

    public double rangeCorrection(double num)
    {
        if(num > 1)
        {
            return 1;
        }
        else if(num < -1)
        {
            return -1;
        }
        else
        {
            return num;
        }
    }
}
