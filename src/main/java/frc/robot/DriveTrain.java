package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain
{ 

    private static final int LEFT_MASTER_TALON_ID = 9;
    private static final int LEFT_FOLLOWER_VICTOR_ID = 10;
    private static final int RIGHT_MASTER_TALON_ID = 1;
    private static final int RIGHT_FOLLOWER_VICTOR_ID = 2;

    private final WPI_TalonSRX leftMaster = new WPI_TalonSRX(LEFT_MASTER_TALON_ID);
    private final WPI_TalonSRX leftFollower = new WPI_TalonSRX(LEFT_FOLLOWER_VICTOR_ID);
    private final WPI_TalonSRX rightMaster = new WPI_TalonSRX(RIGHT_MASTER_TALON_ID);
    private final WPI_TalonSRX rightFollower = new WPI_TalonSRX(RIGHT_FOLLOWER_VICTOR_ID);

    public final boolean LEFT_MASTER_INVERTED = false;
    public final boolean LEFT_FOLLOWER_INVERTED = false;
    public final boolean RIGHT_MASTER_INVERTED = false;
    public final boolean RIGHT_FOLLOWER_INVERTED = false;

    

    private static final DriveTrain instance = new DriveTrain();
    
    private volatile double leftDriveTrainMotorsVoltage;
    private volatile double rightDriveTrainMotorsVoltage;
    
    private DriveTrain() 
    {
        leftMaster.setInverted(LEFT_MASTER_INVERTED);
        leftFollower.setInverted(LEFT_FOLLOWER_INVERTED);
        rightMaster.setInverted(RIGHT_MASTER_INVERTED);
        rightFollower.setInverted(RIGHT_FOLLOWER_INVERTED); 

        leftMaster.configVoltageCompSaturation(12, 10);
        leftFollower.configVoltageCompSaturation(12, 10);
        rightMaster.configVoltageCompSaturation(12, 10);
        rightFollower.configVoltageCompSaturation(12, 10);

        leftMaster.enableVoltageCompensation(true);
        leftFollower.enableVoltageCompensation(true);
        rightMaster.enableVoltageCompensation(true);
        rightFollower.enableVoltageCompensation(true);

        leftMaster.setNeutralMode(NeutralMode.Brake);
        rightMaster.setNeutralMode(NeutralMode.Brake);
        leftFollower.setNeutralMode(NeutralMode.Brake);
        rightFollower.setNeutralMode(NeutralMode.Brake);
    }

    public static DriveTrain getInstance()
    {
        return instance;
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

    public void arcadeDrive(double xAxis, double yAxis)
    {
        yAxis = yAxis * Math.abs(yAxis);
        xAxis = xAxis * Math.abs(xAxis);

        //checks the range to see if the value is greater than 1 or less than -1 and if so corrects the value to be in that range
        yAxis = rangeCorrection(yAxis);
        xAxis = rangeCorrection(xAxis);

        leftDriveTrainMotorsVoltage = xAxis + yAxis;
        rightDriveTrainMotorsVoltage = xAxis - yAxis;

        leftMaster.set(leftDriveTrainMotorsVoltage);
        rightMaster.set(rightDriveTrainMotorsVoltage);
        leftFollower.set(leftDriveTrainMotorsVoltage);
        rightFollower.set(rightDriveTrainMotorsVoltage);
    }
}
