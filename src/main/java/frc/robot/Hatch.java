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
    
    private boolean hatchMechanismDeployed = false;
    private boolean hatchMechanismStowed = false;
    private boolean hatchGrabbed = false;

    

    //construct motor controller objects
    private final WPI_VictorSPX hatchGrabber = new WPI_VictorSPX(HATCH_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX hatchArticulator = new WPI_VictorSPX(HATCH_ARTICULATING_MOTOR_ID);

    //singelton instance
    private static final Hatch instance = new Hatch();
    
    public static Hatch getInstance()
    {
        return instance;
    }

    //rotates hatch grabbing mechanism 
    public void articulateHatch(double speed)
    {
        hatchArticulator.set(speed);
    }

    //controls hatch grabber wheels
    public void grabHatch(double speed)
    {
        hatchGrabber.set(speed);
    }

    //deploy hatch mechanism
    // public void deployHatchMechanism(double deploySpeed)
    // {
    //     if (!HATCH_MECHANISM_DEPLOYED_SWITCH. get() && !hatchMechanismDeployed)
    //     {
    //         articulateHatch(deploySpeed);
    //     }
    //     else
    //     {
    //         articulateHatch(0);
    //         hatchMechanismDeployed = true;
    //         hatchMechanismStowed = false;
    //     }
    // }

    // //stow hatch mechanism
    // public void stowHatchMechanism (double stowSpeed)
    // {
    //     if(!HATCH_MEHANISM_STOWED_SWITCH.get() && !hatchMechanismStowed)
    //     {
    //         articulateHatch(-stowSpeed);
    //     }
    //     else
    //     {
    //         articulateHatch(0);
    //         hatchMechanismStowed = true;
    //         hatchMechanismDeployed = false;
    //     }

    // }

    // //intake hatch until limit switch is hit
    // public void intakeHatch(double intakeSpeed)
    // {
    //     if (!HATCH_INTAKE_LIMITSWITCH.get() && !hatchGrabbed)
    //     {
    //         grabHatch(intakeSpeed);
    //     }
    //     else
    //     {
    //         grabHatch(0);
    //         hatchGrabbed = true;
    //     }
    // }

    // public boolean getSwitchValue()
    // {
    //     return HATCH_MECHANISM_DEPLOYED_SWITCH.get();
    // }

    //deliver hatch
    public void deliverHatch (double deploySpeed)
    {
        grabHatch(deploySpeed);
    }
}
