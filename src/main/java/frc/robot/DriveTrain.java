package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Joystick;


public final class DriveTrain {

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

    //PID tweak variables
    private static double proportionalTweak = 0.0065; //0.0065
    private static double integralTweak = 0.0; //.000007
    private static double DerivativeTweak = 0.0;
    private static double okErrorRange = 0.7;

    //PID init variables 
    private static double error = 0;
    private static double proportional = 0;
    private static double derivative = 0;
    private static double integral = 0; 
    private static double previousError = 0;
    private static double pIDMotorVoltage = 0;

    public double PLACEHOLDER = 0;

    private DriveTrain() {
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

    public static DriveTrain getInstance() {
        return instance;
    }

    public double rangeCorrection(double num) {
        if (num > -DEAD_ZONE_VALUE && num < DEAD_ZONE_VALUE)
            return 0;
        else if (num > 1)
            return 1;
        else if (num < -1)
            return -1;
        else
            return num;
    }

    public double sqrtSignPreserved(double axis) {
        if (axis < 0)
            return -Math.sqrt(-axis);
        else
            return Math.sqrt(axis);
    }


    public boolean joyStickInDeadZone(Joystick joystick) {
        return (joystick.getX() > DEAD_ZONE_VALUE || joystick.getX() < -DEAD_ZONE_VALUE)
                || (joystick.getY() > DEAD_ZONE_VALUE || joystick.getY() < -DEAD_ZONE_VALUE);

    }

    public void squareRootDrive(double xAxis, double yAxis) {
        xAxis = rangeCorrection(sqrtSignPreserved(xAxis));
        yAxis = rangeCorrection(sqrtSignPreserved(yAxis));

        leftDriveVoltage = xAxis + xAxis;
        rightDriveVoltage = xAxis - yAxis;
    }

    public void arcadeDrive(double xAxis, double yAxis) {
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

    public void linearDrive(double xAxis, double yAxis) {
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

    public void tankDrive(double leftSide, double rightSide) {
        leftMaster.set(leftSide);
        rightMaster.set(rightSide);
        leftFollower.set(leftSide);
        rightFollower.set(rightSide);
    }

    public void seekDrive(double destination, String feedBackSensor)
    {
        if (feedBackSensor == "navX")
        {
            tankDrive(pIDDrive(destination, Gyro.navXAngull(), feedBackSensor), pIDDrive(destination, Gyro.navXAngull(), feedBackSensor));
        }
        else if (feedBackSensor == "encoder")
        {
            tankDrive(pIDDrive(destination, PLACEHOLDER, feedBackSensor), -(pIDDrive(destination, PLACEHOLDER, feedBackSensor)));
        }

    }

    public double pIDDrive(double targetDisatance, double actualValue, String feedBackSensor) // enter target distance in feet
	{ 

        if (feedBackSensor == "navX")
        {
         proportionalTweak = 0.0047; //0.0065 0.0047
         integralTweak = 0.0; //.000007
         DerivativeTweak = 0.00001;
         okErrorRange = 0.0;
        }
        else if (feedBackSensor == "encoder")
        {
         proportionalTweak = 0.0065; //placeholers until ideal values for linear drive are found
         integralTweak = 0.0;
         DerivativeTweak = 0.0;
         okErrorRange = 0.0; 
        }
        else
        {
         proportionalTweak = 0; //these just stay zero
         integralTweak = 0;
         DerivativeTweak = 0;
         okErrorRange = 0; 
        }
		error = Math.abs(targetDisatance - (actualValue));
		proportional = error;
		derivative = (previousError - error)/ 0.02;
		integral += previousError;
		previousError = error;

		if (error > okErrorRange || error < -okErrorRange )
		{
			pIDMotorVoltage = truncateMotorOutput((proportionalTweak * proportional) + (DerivativeTweak * derivative) + (integralTweak * integral), feedBackSensor);
			return pIDMotorVoltage;
		}
		else
		{
			proportional = 0;
			integral = 0;
			derivative = 0;
			previousError = 0;
			return 0;
		}
		
    }
    
    private static double truncateMotorOutput(double motorOutput, String feedBackSensor) //Whatever the heck Jake and Van did
    {
        if (feedBackSensor == "encoder")
        {
          if (motorOutput > 1) {
              return 0.5;
          } else if (motorOutput < -1) {
              return -0.5;
          } else {
              return motorOutput;
          }
        }
        if (feedBackSensor == "navX")
        {
            if (motorOutput > .4) {
                return 0.4;
            } else if (motorOutput < -.4) {
                return -0.4;
            } else {
                return motorOutput;
            }
          }
          else
          return 0;
    }

}
