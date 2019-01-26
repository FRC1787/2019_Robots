package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hatch 
{
    private static final int HATCH_GRABBING_MOTOR_ID = 8;
    private static final int HATCH_ARTICULATING_MOTOR_ID = 9;

    private final WPI_VictorSPX hatchGrabber = new WPI_VictorSPX(HATCH_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX hatchArticulating = new WPI_VictorSPX(HATCH_ARTICULATING_MOTOR_ID);

    private static final Hatch instance = new Hatch();
    
    public static Hatch getInstance()
    {
        return instance;
    }

    public void articulateHatch(double speed)
    {
        hatchArticulating.set(speed);
    }

    public void grabHatch(double speed)
    {
        hatchGrabber.set(speed);
    }
}
