package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;

public class Climb {
    // Climb motor id
    private static final int CLIMBING_MOTOR_ONE_ID = 4;
    
    //Limit switch channels
    private static final int CLIMB_FULLY_RETRACTED_LIMITSWITCH = 2;
    private static final int CLIMB_FULLY_EXTENDED_LIMITSWITCH = 5;

    //Limit switch objects
    private static final DigitalInput climbFullyExtendedLimitSwitch = new DigitalInput (CLIMB_FULLY_EXTENDED_LIMITSWITCH);
    private static final DigitalInput climbFullyRetractedLimitSwitch = new DigitalInput(CLIMB_FULLY_RETRACTED_LIMITSWITCH);

    //Motor objects
    private final WPI_TalonSRX climbMotorOne = new WPI_TalonSRX(CLIMBING_MOTOR_ONE_ID);

    // Singleton instance
    private static final Climb instance = new Climb();

    public Climb() 
    {
        // Put the climb motor into brake mode
        climbMotorOne.setNeutralMode(NeutralMode.Brake);
    }

    // Return method for the singleton instance
    public static Climb getInstance() {
        return instance;
    }

    /**
     * Extend or retract the linear servo for climb
     *
     * @param position between 0.2 (completely retracted) and 1.0 (fully extended)
     */
    // public void setActuators(double position) {
    //     if (position >= 0.2 && position <= 1.0)
    //         climbPin.set(position);
    //     else if (position < 0.2)
    //         climbPin.set(0.2);
    //     else if (position > 1.0)
    //         climbPin.set(1); 
            
    // }

    public double sliderCorrection(Joystick joystick) 
    {
        return -(joystick.getRawAxis(3) - 1) / 2;
    }

    // Moves the robot using climb motor
    public void moveClimber(double speed) 
    {
        climbMotorOne.set(speed);
    }

    public double getClimbSpeed() {
        return climbMotorOne.get();
    }

    public void setBrakeMode() 
    {
        climbMotorOne.setNeutralMode(NeutralMode.Brake);
    }

    public boolean climberFullyExtended ()
    {
        return climbFullyExtendedLimitSwitch.get();
    }

    public boolean climberFullyRetracted ()
    {
        return climbFullyRetractedLimitSwitch.get();
    }

    public double getClimberMotorCurrent()
    {
        return climbMotorOne.getOutputCurrent();
    }
    
}
