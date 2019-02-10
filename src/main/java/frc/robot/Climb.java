package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Servo;

public class Climb 
{
    private static final int CLIMBING_MOTOR_ID = 10;

    private final WPI_TalonSRX climbMotor = new WPI_TalonSRX(CLIMBING_MOTOR_ID);
    private final Servo leftActuator = new Servo(0);
    private final Servo rightActuator = new Servo(1);

    private static final Climb instance = new Climb();
    
    public static Climb getInstance()
    {
        return instance;
    }

    public void setActuators(double position)  //Actuanix linear servo range: 0.2 (completely retracted) 1.0 (completely extended)
    {
        leftActuator.set(position);
        rightActuator.set(position);
    }

    public void moveClimber(double speed)
    {
        climbMotor.set(speed);
    }
}
