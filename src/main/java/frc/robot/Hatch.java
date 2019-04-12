package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.DigitalInput;

public final class Hatch
{
    // Hatch motor controller variables
    private static final int HATCH_GRABBING_MOTOR_ID = 6;
    private static final int HATCH_ARTICULATING_MOTOR_ID = 12;


    /////////////////////////////////
    private final int HATCH_TEST_MOTOR_ID = 4;
    private final WPI_VictorSPX hatchTestMotor = new WPI_VictorSPX(HATCH_TEST_MOTOR_ID);
    
    // Construct motor controller objects
    //private final WPI_VictorSPX hatchGrabber = new WPI_VictorSPX(HATCH_GRABBING_MOTOR_ID);
    private final WPI_TalonSRX hatchGrabber = new WPI_TalonSRX(HATCH_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX hatchArticulator = new WPI_VictorSPX(HATCH_ARTICULATING_MOTOR_ID);

    // Hatch limit switch variables
    private static final int HATCH_MECHANISM_DEPLOYED_LIMITSWITCH_CHANNEL = 0;
    private static final int HATCH_MECHANISM_STOWED_LIMITSWITCH_CHANNEL = 1;
    
    // private static final int HATCH_INTAKE_LIMITSWITCH_ONE_CHANNEL = 2;
    // private static final int HATCH_INAKE_LIMITSWITCH_TWO_CHANNRL = 5;
    
    // Construct hatch limit switch objects
        //When your looking at front of the hatch one is right, two is left
    private final DigitalInput hatchMechanismDeployedSwitch = new DigitalInput(HATCH_MECHANISM_DEPLOYED_LIMITSWITCH_CHANNEL);
    private final DigitalInput hatchMechanismStowedSwitch = new DigitalInput(HATCH_MECHANISM_STOWED_LIMITSWITCH_CHANNEL);
        

    private boolean hatchIntaked = false;
    private boolean hatchLost = false;

    // Singleton instance
    private static final Hatch instance = new Hatch();

    // Default constructor
    public Hatch()
    {
        hatchGrabber.configFactoryDefault();
        hatchArticulator.setNeutralMode(NeutralMode.Brake);
        hatchGrabber.setNeutralMode(NeutralMode.Brake);
        hatchGrabber.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0 , 20);
    }
    
    // Return method for singleton instance
    public static Hatch getInstance()
    {
        return instance;
    }


    // Rotates hatch grabbing mechanism
    public void articulateHatch(double speed)
    {
        hatchArticulator.set(speed);
    }

    // Controls hatch grabber wheels
    public void grabHatch(double speed)
    {
        hatchGrabber.set(speed);
    }

    // Accessor methods for getting limit switch states
    public boolean getHatchMechanismDeployedSwitchState()
    {
        return hatchMechanismDeployedSwitch.get();
    }

    public boolean getHatchMechanismStowedSwitchState()
    {
        return hatchMechanismStowedSwitch.get();
    }

    public double encoderPosition()
    {
        return hatchGrabber.getSensorCollection().getPulseWidthPosition();
    }
    
    // public boolean getHatchIntakedSwitchOneState()
    // {
    //     return hatchIntakedSwitchOne.get();
    // }

    // public boolean getHatchIntakedSwitchTwoState()
    // {
    //     return hatchItnakedSwitchTwo.get();
    // }

    // public boolean isHatchOn()
    // {
    //     if(hatchIntakedSwitchOne.get() && hatchItnakedSwitchTwo.get())
    //     {
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean isHatchReleased()
    // { 
    //     if(isHatchOn())
    //     {
    //         hatchIntaked = true;
    //     }
    //     if(hatchIntaked)
    //     {
    //         if((getHatchIntakedSwitchOneState() && !getHatchIntakedSwitchTwoState()) || (!getHatchIntakedSwitchOneState() && getHatchIntakedSwitchTwoState()))
    //         {
    //             hatchLost = true;
    //         }
    //     }
        
    //     if(hatchIntaked && hatchLost)
    //     {
    //         return true;
    //     }
    //     return false;
    // }

    public double getHatchGrabberCurrent()
    {
        return hatchGrabber.getOutputCurrent();
    }

    public double getHatchGrabberSpeed()
    {
        return hatchGrabber.getMotorOutputPercent();
    }

}
