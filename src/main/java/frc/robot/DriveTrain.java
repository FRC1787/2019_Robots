package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;

public final class DriveTrain
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

    // Drivetrain variables
    private final double DEAD_ZONE_VALUE = 0.02;

    private static final DriveTrain instance = new DriveTrain();
    
    private double leftDriveVoltage;
    private double rightDriveVoltage;
    
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
        if(num > -DEAD_ZONE_VALUE && num < DEAD_ZONE_VALUE)
            return 0;
        else if(num > 1)
            return 1;
        else if(num < -1)
            return -1;
        else
            return num;
    } 

    public double sqrtSignPreserved(double axis)
    {
        if(axis < 0)
            return -Math.sqrt(-axis);
        else
            return Math.sqrt(axis);
    }

    
    public boolean joyStickInDeadZone(Joystick joystick)
    {
        return (joystick.getX() > DEAD_ZONE_VALUE || joystick.getX() < -DEAD_ZONE_VALUE)
                || (joystick.getY() > DEAD_ZONE_VALUE || joystick.getY() < -DEAD_ZONE_VALUE);

    }

    public void squareRootDrive(double xAxis, double yAxis)
    {
        xAxis = rangeCorrection(sqrtSignPreserved(xAxis));
        yAxis = rangeCorrection(sqrtSignPreserved(yAxis));

        leftDriveVoltage = xAxis + xAxis;
        rightDriveVoltage = xAxis - yAxis;
    }

    public void arcadeDrive(double xAxis, double yAxis)
    {
        yAxis = yAxis * Math.abs(yAxis);
        xAxis = xAxis * Math.abs(xAxis);

        //checks the range to see if the value is greater than 1 or less than -1 and if so corrects the value to be in that range
        yAxis = rangeCorrection(yAxis);
        xAxis = rangeCorrection(xAxis);

        leftDriveVoltage = xAxis + yAxis;
        rightDriveVoltage = xAxis - yAxis;

        leftMaster.set(leftDriveVoltage);
        rightMaster.set(rightDriveVoltage);
        leftFollower.set(leftDriveVoltage);
        rightFollower.set(rightDriveVoltage);
    }

    public void linearDrive(double xAxis, double yAxis)
    {
        //checks the range to see if the value is greater than 1 or less than -1 and if so corrects the value to be in that range
        yAxis = rangeCorrection(yAxis);
        xAxis = rangeCorrection(xAxis);

        leftDriveVoltage = xAxis + yAxis;
        rightDriveVoltage = xAxis - yAxis;

        leftMaster.set(leftDriveVoltage);
        rightMaster.set(rightDriveVoltage);
        leftFollower.set(leftDriveVoltage);
        rightFollower.set(rightDriveVoltage);
    }

    public void tankDrive(double leftSide, double rightSide)
    {
        leftMaster.set(leftSide);
        rightMaster.set(rightSide);
        leftFollower.set(leftSide);
        rightFollower.set(rightSide);
    }
}
