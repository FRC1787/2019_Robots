package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Servo;

public class Climb 
{
    //Climb motor id
    private static final int CLIMBING_MOTOR_ID = 10;
    private final int CLIMB_PIN_CHANNEL = 0;

    //Create climb motorcontroller object
    private final WPI_TalonSRX climbMotor = new WPI_TalonSRX(CLIMBING_MOTOR_ID);
    
    //Create climb servo object
    private final Servo climbPin = new Servo(CLIMB_PIN_CHANNEL);

    //Singelton instance
    private static final Climb instance = new Climb();

    //Default constructor
    public Climb()
    {
        //Put the climb motor into brake mode
        climbMotor.setNeutralMode(NeutralMode.Brake);
    }
    
    //Return method for the singolten instance
    public static Climb getInstance()
    {
        return instance;
    }
    
    /*Extend or retract the linear servo for climb
    *@param: double betweeen 0.2 (completly retracted) and 1.0 (fully extended)
    */
    public void setActuators(double position) 
    {
        if(position >= 0.2 && position <= 1.0)
        {
            climbPin.set(position);
        }

        else if(position < 0.2)
        {
            climbPin.set(0.2);
        }

        else if(position > 1.0)
        {
            climbPin.set(1);
        }
    }

    //Moves the robot using climb motor
    public void moveClimber(double speed)
    {
        climbMotor.set(speed);
    }

}
