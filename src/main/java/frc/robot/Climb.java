package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Servo;

public class Climb 
{
    private static final int CLIMBING_MOTOR_ID = 10;

    private WPI_TalonSRX climber = new WPI_TalonSRX(CLIMBING_MOTOR_ID);
    private Servo leftActuator = new Servo(0);
    private Servo rightActuator = new Servo(1);

    private static final Climb instance = new Climb();


    
    public static Climb getInstance()
    {
        return instance;
    }

    public void setActuators(double angle)
    {
        leftActuator.set(angle);
        rightActuator.set(angle);
    }

    public void moveClimber(double speed)
    {
        climber.set(speed);
    }
}
