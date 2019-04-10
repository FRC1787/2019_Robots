package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;

public class Climb {
    // Climb motor id
    private static final int CLIMBING_MOTOR_ONE_ID = 6;
    private static final int CLIMBING_MOTOR_TWO_ID = 4;

    private final int CLIMB_PIN_CHANNEL = 0;

     private final WPI_TalonSRX climbMotorOne = new WPI_TalonSRX(CLIMBING_MOTOR_ONE_ID);
    private final WPI_VictorSPX climbMotorTwo = new WPI_VictorSPX(CLIMBING_MOTOR_TWO_ID);

    // Create climb servo object
    private final Servo climbPin = new Servo(CLIMB_PIN_CHANNEL);

    // Singleton instance
    private static final Climb instance = new Climb();

    public Climb() {
        // Put the climb motor into brake mode
        climbMotorOne.setNeutralMode(NeutralMode.Coast);
        climbMotorTwo.setNeutralMode(NeutralMode.Coast);
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
    public void setActuators(double position) {
        if (position >= 0.2 && position <= 1.0)
            climbPin.set(position);
        else if (position < 0.2)
            climbPin.set(0.2);
        else if (position > 1.0)
            climbPin.set(1); 
            
    }

    public double sliderCorrection(Joystick joystick) 
    {
        return -(joystick.getRawAxis(3) - 1) / 2;
    }

    // Moves the robot using climb motor
    public void moveClimber(double speed) 
    {
        climbMotorOne.set(speed);
        climbMotorTwo.set(-speed);
    }

    public double getClimbSpeed() {
        return climbMotorOne.get();
    }

}
