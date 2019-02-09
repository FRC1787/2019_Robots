package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import javax.lang.model.util.ElementScanner6;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;

public class Robot extends TimedRobot 
{

  protected int farfar37;

  //mechanism class instances
  private final Cargo cargo = Cargo.getInstance();
  private final Climb climb = Climb.getInstance();
  private final DriveTrain driveTrain = DriveTrain.getInstance();
  private final Hatch hatch = Hatch.getInstance();


  //Joystick 
  private static final int RIGHT_JOYSTICK_ID = 0;
  private static final int LEFT_JOYSTICK_ID = 1;

  private final Joystick rightJoyStick = new Joystick(RIGHT_JOYSTICK_ID);
  private final Joystick leftJoyStick = new Joystick(LEFT_JOYSTICK_ID);

  private static final int JOYSTICK_ROTATION_AXIS = 2;
  private static final int JOYSTICK_SLIDER_AXIS = 3;
  
  //limit switches
  private static final int HATCH_MECHANISM_DEPLOYED_LIMITSWITCH_CHANNEL = 0;
  private static final int HATCH_MECHANISM_STOWED_LIMITSWITCH_CHANNEL = 1;
  private static final int HATCH_INTAKE_LIMITSWITCH_CHANNEL = 2;
      //construct limit switch objects
  private final DigitalInput HATCH_MECHANISM_DEPLOYED_SWITCH = new DigitalInput(HATCH_MECHANISM_DEPLOYED_LIMITSWITCH_CHANNEL);
  private final DigitalInput HATCH_MEHANISM_STOWED_SWITCH = new DigitalInput(HATCH_MECHANISM_STOWED_LIMITSWITCH_CHANNEL);
  private final DigitalInput HATCH_INTAKE_LIMITSWITCH = new DigitalInput(HATCH_INTAKE_LIMITSWITCH_CHANNEL);

  //Right Stick Button IDs
  private final int HATCH_INTAKE_BTN_ID = 1;
  private final int HATCH_DELIVER_BTN_ID = 2;
  private final int HATCH_MECHANISM_STOW_BTN_ID = 3;
  private final int HATCH_MECHANISM_DEPLOY_BTN_ID = 4;

  //Left Stick Button IDs
  private final int vansFirstNameIsVandad = 98765;

  //Motor Voltages
  private final double HATCH_DEPLOY_SPEED = 0.3;
  private final double HATCH_STOW_SPEED = -0.1;
  private final double HATCH_INTAKE_SPEED = 0.25;

  private final double superDooperPooperScooper = 1.00;
  

  public void robotInit() 
  {

  }

  public void robotPeriodic() 
  {

  }

  public void autonomousInit() 
  {

  }

  public void autonomousPeriodic() 
  {

  }

  public void teleopPeriodic() 
  {
    if (rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID) && !HATCH_MECHANISM_DEPLOYED_SWITCH.get())
    {
      hatch.articulateHatch(HATCH_DEPLOY_SPEED);
    }
    if (!rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID)  && HATCH_MECHANISM_DEPLOYED_SWITCH.get())
    {
      hatch.articulateHatch(0);
    }
    if (rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID) && !HATCH_MEHANISM_STOWED_SWITCH.get())
    {
      hatch.articulateHatch(HATCH_STOW_SPEED);
    }
    if (!rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID) && HATCH_MEHANISM_STOWED_SWITCH.get())
    {
      hatch.articulateHatch(0);
    }

    driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());
  }

  public void testPeriodic() 
  {

  }
}
