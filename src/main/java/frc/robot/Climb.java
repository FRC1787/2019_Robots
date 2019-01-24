package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climb 
{
    private static final int CLIMBING_MOTOR_ID = 10;

    private WPI_TalonSRX climber = new WPI_TalonSRX(CLIMBING_MOTOR_ID);

    private static final Climb instance = new Climb();


    
    public static Climb getInstance()
    {
        return instance;
    }
}
