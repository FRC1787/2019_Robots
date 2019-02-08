package frc.robot;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

public class Hatch 
{
    //Satic variables, IDs, channels
    private static final int HATCH_GRABBING_MOTOR_ID = 11;
    private static final int HATCH_ARTICULATING_MOTOR_ID = 3;
    private static final int HATCH_ARTICULATION_OUT_LIMITSWITCH_CHANNEL = 0;
    private static final int HATCH_ARTICULATION_IN_LIMITSWITCH_CHANNEL = 1;
    private static final int HATCH_INTAKE_LIMITSWITCH_CHANNEL = 2;
    
    private static boolean hatchGrabberOut = false;
    private static boolean hatchGrabbed = false;

    //construct limit switch objects
    private final DigitalInput HATCH_ATICULATION_OUT_LIMITSWITCH = new DigitalInput(HATCH_ARTICULATION_OUT_LIMITSWITCH_CHANNEL);
    private final DigitalInput HATCH_ARTICULATION_IN_LIMIT_SWITCH = new DigitalInput(HATCH_ARTICULATION_IN_LIMITSWITCH_CHANNEL);
    private final DigitalInput HATCH_INTAKE_LIMITSWITCH = new DigitalInput(HATCH_INTAKE_LIMITSWITCH_CHANNEL);

    //construct motor controller objects
    private final WPI_VictorSPX hatchGrabber = new WPI_VictorSPX(HATCH_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX hatchArticulating = new WPI_VictorSPX(HATCH_ARTICULATING_MOTOR_ID);

    //singelton instance
    private static final Hatch instance = new Hatch();
    
    public static Hatch getInstance()
    {
        return instance;
    }

    //rotates hatch grabbing mechanism 
    public void articulateHatch(double speed)
    {
        hatchArticulating.set(speed);
    }

    //controls hatch grabber wheels
    public void grabHatch(double speed)
    {
        hatchGrabber.set(speed);
    }

    public void intakeHatch(double intakeSpeed, double articulateSpeed )
    {
        
        //rotates hatch mechanism out for intaking
        if (HATCH_ATICULATION_OUT_LIMITSWITCH.get() == false)
        {
            articulateHatch(articulateSpeed);
        }
        else if (HATCH_INTAKE_LIMITSWITCH.get() == false && hatchGrabberOut == true)
        {
            grabHatch(intakeSpeed);
        }
    }

    public String limitTesting()
        {
            return ("Limit switch 0:" + HATCH_ATICULATION_OUT_LIMITSWITCH.get() + "\nLimit switch 2: " + HATCH_INTAKE_LIMITSWITCH.get());

        }
}
