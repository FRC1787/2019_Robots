package frc.robot;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

public class Hatch 
{
    //Hatch motor controller variables
    private static final int HATCH_GRABBING_MOTOR_ID = 11;
    private static final int HATCH_ARTICULATING_MOTOR_ID = 3;
    
    //Construct motor controller objects
    private final WPI_VictorSPX hatchGrabber = new WPI_VictorSPX(HATCH_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX hatchArticulator = new WPI_VictorSPX(HATCH_ARTICULATING_MOTOR_ID);

    //Hatch limit switche variables
    private static final int HATCH_MECHANISM_DEPLOYED_LIMITSWITCH_CHANNEL = 0;
    private static final int HATCH_MECHANISM_STOWED_LIMITSWITCH_CHANNEL = 1;
    private static final int HATCH_INTAKE_LIMITSWITCH_CHANNEL = 2;
    
    //Construct hatch limit switch objects
    private final DigitalInput hatchMechanismDeployedSwitch = new DigitalInput(HATCH_MECHANISM_DEPLOYED_LIMITSWITCH_CHANNEL);
    private final DigitalInput hatchMechanismStowedSwitch = new DigitalInput(HATCH_MECHANISM_STOWED_LIMITSWITCH_CHANNEL);
    private final DigitalInput hatchIntakedSiwtch = new DigitalInput(HATCH_INTAKE_LIMITSWITCH_CHANNEL);

    //singelton instance
    private static final Hatch instance = new Hatch();
    
    public static Hatch getInstance()
    {
        return instance;
    }

    //rotates hatch grabbing mechanism 
    public void articulateHatch(double speed)
    {
        hatchArticulator.setNeutralMode(NeutralMode.Brake);
        hatchArticulator.set(speed);
    }

    //controls hatch grabber wheels
    public void grabHatch(double speed)
    {
        hatchGrabber.setNeutralMode(NeutralMode.Brake);
        hatchGrabber.set(speed);
    }

    //accessor methods for getting limit switch states
    public boolean getHatchMechanismDeploeyedSwitchState()
    {
        return hatchMechanismDeployedSwitch.get();
    }
    public boolean getHatchMechanismStowedSwitchState()
    {
        return hatchMechanismStowedSwitch.get();
    }
    public boolean getHatchIntakedSwitchState()
    {
        return hatchIntakedSiwtch.get();
    }

}
