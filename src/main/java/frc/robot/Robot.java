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
  private final Vision vision = Vision.getInstance();


  //Joystick 
  private static final int RIGHT_JOYSTICK_ID = 0;
  private static final int LEFT_JOYSTICK_ID = 1;

  private final Joystick rightJoyStick = new Joystick(RIGHT_JOYSTICK_ID);
  private final Joystick leftJoyStick = new Joystick(LEFT_JOYSTICK_ID);

  private static final int JOYSTICK_ROTATION_AXIS = 2;
  private static final int JOYSTICK_SLIDER_AXIS = 3;
  
  //Right Stick Button IDs
  private final int HATCH_INTAKE_BTN_ID = 1;
  private final int HATCH_DELIVER_BTN_ID = 2;
  private final int HATCH_MECHANISM_STOW_BTN_ID = 3;
  private final int HATCH_MECHANISM_DEPLOY_BTN_ID = 4;

  //Left Stick Button IDs
  private final int CARGO_INTAKE_BTN_ID = 1;
  private final int CARGO_SHOOT_BTN_ID = 2;
  private final int CARGO_INTAKE_MECHANISM_STOW_BTN_ID = 3;
  private final int CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID = 4;

  //Motor Voltages
    //hatch speeds
  private final double HATCH_DEPLOY_SPEED = -0.9;
  private final double HATCH_STOW_SPEED = 0.9;
  private final double HATCH_INTAKE_SPEED = 1;
  private final double HATCH_DELIVER_SPEED = -1;
    //cargo speeds
  private final double CARGO_MECHANISM_DEPLOY_SPEED = 0.1;
  private final double CARGO_MEHCANISM_STOW_SPEED = 0.1;
  private final double CARGO_INTAKE_SPEED = 0.5;
  private final double CARGO_SHOOT_SPEED = 0.5;
  

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

    if (rightJoyStick.getRawAxis(JOYSTICK_SLIDER_AXIS) > 0)
    {
    /*************************/
    /*************************/
    /*Right Joystick controls*/
    /*************************/
    /*************************/

    //hatch mechanism controls
      //deploy hatch mechanism
    if (rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID) && !hatch.getHatchMechanismDeploeyedSwitchState())
    {
      hatch.articulateHatch(HATCH_DEPLOY_SPEED);
    }
    if (!rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID)  && hatch.getHatchMechanismDeploeyedSwitchState())
    {
      hatch.articulateHatch(0);
    }
      //stow hatch mechanism
    if (rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID) && !hatch.getHatchMechanismStowedSwitchState())
    {
      hatch.articulateHatch(HATCH_STOW_SPEED);
    }
    if (!rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID) && hatch.getHatchMechanismStowedSwitchState())
    {
      hatch.articulateHatch(0);
    }
      //if hatch is on the mechanism don't move anything
    if(hatch.getHatchIntakedSwitchState())
    {
      hatch.articulateHatch(0);
    }

    //Hatch intake and delivery
      //intake hatch
    if(rightJoyStick.getRawButton(HATCH_INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
    {
      hatch.grabHatch(HATCH_INTAKE_SPEED);
    }
      //stop once hatch is on
    if(!rightJoyStick.getRawButton(HATCH_DELIVER_BTN_ID) && hatch.getHatchIntakedSwitchState())
    {
      hatch.grabHatch(0);
    }
      //deliver hatch
    if(rightJoyStick.getRawButton(HATCH_DELIVER_BTN_ID))
    {
      hatch.grabHatch(HATCH_DELIVER_SPEED);
    }

    /************************/
    /************************/
    /*Left joystick controls*/
    /************************/
    /************************/

    //Cargo controls
      //deploy cargo intake mechanism
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismDployedSwitchState())
    {
      cargo.articulateCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
    }
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID) && cargo.getCargoIntakeMechanismDployedSwitchState())
    {
      cargo.articulateCargoIntake(0);
    }
      //stow cargo intake mechanism 
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
    {
      cargo.articulateCargoIntake(CARGO_MEHCANISM_STOW_SPEED);
    }
    if(!leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID) && cargo.getCargoIntakeMechanismStowedSwitchState())
    {
      cargo.articulateCargoIntake(0);
    }
      //if cagro is intaked 
    if (cargo.getIntakedSwitchState() && !cargo.getCargoIntakeMechanismStowedSwitchState())
    {
      cargo.articulateCargoIntake(CARGO_MEHCANISM_STOW_SPEED);
    }

    //intake cargo
    if(leftJoyStick.getRawButton(CARGO_INTAKE_BTN_ID) && !cargo.getIntakedSwitchState())
    {
      cargo.intakeCargo(CARGO_INTAKE_SPEED);
    }
    else 
    {
      cargo.intakeCargo(0);
    }
    //shoot cargo
    if(leftJoyStick.getRawButton(CARGO_SHOOT_BTN_ID))
    {
      cargo.shootCargo(CARGO_SHOOT_SPEED);
    }
  }


  //test code without limit switches///////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  if(rightJoyStick.getRawAxis(JOYSTICK_SLIDER_AXIS) < 0)
  {

    //right joystick test controls
    if(rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID))
    {
      hatch.articulateHatch(HATCH_DEPLOY_SPEED);
    }
    else if(rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID))
    {
      hatch.articulateHatch(HATCH_STOW_SPEED);
    }
    else if(rightJoyStick.getRawButton(HATCH_INTAKE_BTN_ID))
    {
      hatch.articulateHatch(0);
      hatch.grabHatch(HATCH_INTAKE_SPEED);
    }
    else if(rightJoyStick.getRawButton(HATCH_DELIVER_BTN_ID))
    {
      hatch.articulateHatch(0);
      hatch.grabHatch(HATCH_DELIVER_SPEED);
    }
    else
    {
      hatch.articulateHatch(0);
      hatch.grabHatch(0);
    }


    //left joystick test controls
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID))
    {
      cargo.articulateCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
    }
    else if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID))
    {
      cargo.articulateCargoIntake(CARGO_MEHCANISM_STOW_SPEED);
    }
    else if(leftJoyStick.getRawButton(CARGO_INTAKE_BTN_ID))
    {
      cargo.articulateCargoIntake(0);
      cargo.intakeCargo(CARGO_INTAKE_SPEED);
    }
    else if(leftJoyStick.getRawButton(CARGO_SHOOT_BTN_ID))
    {
      cargo.shootCargo(CARGO_SHOOT_SPEED);
    }
    else
    {
      cargo.shootCargo(0);
      cargo.intakeCargo(0);
      cargo.articulateCargoIntake(0);
    }
    
  }

    driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());
  }

  public void testPeriodic() 
  {

  }
}
