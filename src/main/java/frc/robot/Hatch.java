package frc.robot;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

public class Hatch 
{
    //Hatch motor controller variables
    private static final int HATCH_GRABBING_MOTOR_ID = 7;
    private static final int HATCH_ARTICULATING_MOTOR_ID = 12;
    
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

    //Singelton instance
    private static final Hatch instance = new Hatch();

    //Default contructor 
    public Hatch()
    {
        hatchArticulator.setNeutralMode(NeutralMode.Brake);
        hatchGrabber.setNeutralMode(NeutralMode.Brake);
    }
    
    //Return method for singelton instance
    public static Hatch getInstance()
    {
        return instance;
    }

    //Rotates hatch grabbing mechanism 
    public void articulateHatch(double speed)
    {
        hatchArticulator.set(speed);
    }

    //Controls hatch grabber wheels
    public void grabHatch(double speed)
    {
        hatchGrabber.set(speed);
    }

    //Accessor methods for getting limit switch states
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
