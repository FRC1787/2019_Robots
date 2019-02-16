package frc.robot;

//Imports
  //Automatic import
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
  //Java lang imports
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementScanner6;
  //WPI first imports
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
  //IDK why but they are here
  protected int farfar37;
  protected double internetSpeed = 2.0;

  //Class instances
  private final Cargo cargo = Cargo.getInstance();
  private final Climb climb = Climb.getInstance();
  private final DriveTrain driveTrain = DriveTrain.getInstance();
  private final Hatch hatch = Hatch.getInstance();
  //private final Vision vision = Vision.getInstance();

  //Joystick 
    //Joystick IDs
  private static final int RIGHT_JOYSTICK_ID = 0;
  private static final int LEFT_JOYSTICK_ID = 1;
    //Create joystick objects
  private final Joystick rightJoyStick = new Joystick(RIGHT_JOYSTICK_ID);
  private final Joystick leftJoyStick = new Joystick(LEFT_JOYSTICK_ID);
    //Joystick axis 
  private static final int JOYSTICK_ROTATION_AXIS = 2;
  private static final int JOYSTICK_SLIDER_AXIS = 3;
  
  //Button IDs
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

  //Working Values
  //Hatch speeds
  private final double F_HATCH_DEPLOY_SPEED = -0.9;
  private final double F_HATCH_STOW_SPEED = 0.9;
  private final double F_HATCH_INTAKE_SPEED = -1;
  private final double F_HATCH_DELIVER_SPEED = 1;
    //Cargo speeds
  private final double F_CARGO_MECHANISM_DEPLOY_SPEED = 0.25;
  private final double F_CARGO_MECHANISM_STOW_SPEED = -0.20;
  private final double F_CARGO_INTAKE_SPEED = 0.75;
  private final double F_CARGO_SHOOT_SPEED = 0.5;


    //Hatch speeds
  private double HATCH_DEPLOY_SPEED = -0.9;
  private double HATCH_STOW_SPEED = 0.9;
  private double HATCH_INTAKE_SPEED = -1;
  private double HATCH_DELIVER_SPEED = 1;
    //Cargo speeds
  private double CARGO_MECHANISM_DEPLOY_SPEED = 0.25;
  private double CARGO_MECHANISM_STOW_SPEED = -0.20;
  private double CARGO_INTAKE_SPEED = 0.75;
  private double CARGO_SHOOT_SPEED = 0.5;
    //Climb speed
  private final double CLIMB_SPEED = 1;

  //Other varibales
  private boolean deployCargoIntakeState = false;
  private int hatchDecelerationCounter = 0;
  private boolean engageShooterBelt = false;
  private int shooterTimer = 0;

  

  public void updateDashboard() 
  {
    
    SmartDashboard.putNumber("Hatch Deploy Speed", HATCH_DEPLOY_SPEED);
    SmartDashboard.putNumber("Hatch Stow Speed", HATCH_STOW_SPEED);
    SmartDashboard.putNumber("Hatch Intake Speed", HATCH_INTAKE_SPEED);
    SmartDashboard.putNumber("Hatch Deliver Speed", HATCH_DELIVER_SPEED);

    SmartDashboard.putNumber("Cargo Deploy Speed", CARGO_MECHANISM_DEPLOY_SPEED);
    SmartDashboard.putNumber("Cargo Stow Speed", CARGO_MECHANISM_STOW_SPEED);
    SmartDashboard.putNumber("Cargo Intake Speed", CARGO_INTAKE_SPEED);
    SmartDashboard.putNumber("Cargo Shoot Speed", CARGO_SHOOT_SPEED);
  }

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

    updateDashboard();

    /*************************/
    /*************************/
    /*************************/
    /*************************/

    //Check for dead zones on right and left joystick
    //If right joyStick is in the dead zone, drive with left stick

    driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());

    /*
    if (driveTrain.joyStickInDeadZone(rightJoyStick) && !driveTrain.joyStickInDeadZone(leftJoyStick))
    {
      driveTrain.arcadeDrive(leftJoyStick.getX(), leftJoyStick.getY());
    }

    //If left joyStick is in dead zone, drive with right, but invert Y-axis
    else if (!driveTrain.joyStickInDeadZone(rightJoyStick) && driveTrain.joyStickInDeadZone(leftJoyStick))
    {
      driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());
    }

    //If both are in dead zone, stop motors

    else
    {
      driveTrain.arcadeDrive(0, 0);
    }
    */
    

    //Listen to limit switches
    if (rightJoyStick.getRawAxis(JOYSTICK_SLIDER_AXIS) < 0)
    {

    /*************************/
    /*************************/
    /*Right Joystick controls*/
    /*************************/
    /*************************/



    //Hatch mechanism controls
      //Deploy hatch mechanism
    if (rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID) && !hatch.getHatchMechanismDeploeyedSwitchState())
    {
      hatch.articulateHatch(HATCH_DEPLOY_SPEED);
    }

    //Stop deploy once the deploy limit switch is pressed
    if (!rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID)  && hatch.getHatchMechanismDeploeyedSwitchState() )
    {
      hatch.articulateHatch(0);
    }



    //Stow hatch mechanism
    if (rightJoyStick.getRawButton(HATCH_MECHANISM_STOW_BTN_ID) && !hatch.getHatchMechanismStowedSwitchState())
    {
      hatch.articulateHatch(HATCH_STOW_SPEED);
    }

    //Stop stow once the stow limit switch is pressed
    if (!rightJoyStick.getRawButton(HATCH_MECHANISM_DEPLOY_BTN_ID) && hatch.getHatchMechanismStowedSwitchState())
    {
      hatch.articulateHatch(0);
    }

    //If hatch is on the mechanism don't move anything
    if(hatch.getHatchIntakedSwitchState())
    {
      hatch.articulateHatch(0);
    }



    //Hatch intake and delivery
    //Intake hatch
    if(rightJoyStick.getRawButton(HATCH_INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
    {
      hatch.grabHatch(HATCH_INTAKE_SPEED);
    }

    //Stop once hatch is on and limit switch is pressed
    if(!rightJoyStick.getRawButton(HATCH_DELIVER_BTN_ID) && hatch.getHatchIntakedSwitchState())
    {
      hatch.grabHatch(0);
    }



    //Deliver hatch
    if(rightJoyStick.getRawButton(HATCH_DELIVER_BTN_ID))
    {
      hatch.grabHatch(HATCH_DELIVER_SPEED);
    }

    //Stop delivering once no button is being pressed
    if(!rightJoyStick.getRawButton(HATCH_DELIVER_BTN_ID) && !rightJoyStick.getRawButton(HATCH_INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
    {
      hatch.grabHatch(0);
    }



    /************************/
    /************************/
    /*Left joystick controls*/
    /************************/
    /************************/



    //Cargo controls
    //Deploy cargo intake mechanism
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismDeployedSwitchState())
    {
      cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
    }

    //Stop deploy once the deploy limit switch is pressed
    if(!leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID) && cargo.getCargoIntakeMechanismDeployedSwitchState())
    {
      cargo.articulateCargoIntake(0);
    }



    //Stow cargo intake mechanism 
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
    {
      cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
    }

    //Stop stow once hte stow limit switch is hit
    if(!leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID) && cargo.getCargoIntakeMechanismStowedSwitchState())
    {
      cargo.articulateCargoIntake(0);
    }



    //Stop while no button is being pressed
    if(!leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID) && !leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID))
    {
      cargo.articulateCargoIntake(0);
    }

    //Intake cargo: deploy the intake, then spin the wheels
    if(leftJoyStick.getRawButton(CARGO_INTAKE_BTN_ID))
    {
      cargo.intakeCargo(CARGO_INTAKE_SPEED);
      cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
    }

    //Stop intake wheels, 
    if(!leftJoyStick.getRawButton(CARGO_INTAKE_BTN_ID) && !leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
    {
      cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
      cargo.intakeCargo(0);
    }

    //Shoot cargo
    //Spin intake wheels as long as button is pressed
    if(leftJoyStick.getRawButton(CARGO_SHOOT_BTN_ID))
    {
      cargo.intakeCargo(CARGO_INTAKE_SPEED);
    }

    
    if(leftJoyStick.getRawButtonReleased(CARGO_SHOOT_BTN_ID))
    {
      engageShooterBelt = true;
    }

    //Engage shooting belt, once the button is released
    if(engageShooterBelt)
    {
      cargo.intakeCargo(0);
      if(shooterTimer <= 100)
      {
        cargo.shootCargo(CARGO_SHOOT_SPEED);
      }
      else
      {
        cargo.shootCargo(0);
        engageShooterBelt = false;
        shooterTimer = 0;
      }
      shooterTimer++;
    }

    if(!leftJoyStick.getRawButton(CARGO_SHOOT_BTN_ID) && !engageShooterBelt)
    {
      cargo.shootCargo(0);
    }
  }

  


  //test code without limit switches///////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////
  if(rightJoyStick.getRawAxis(JOYSTICK_SLIDER_AXIS) > 0)
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
    if(leftJoyStick.getRawButton(CARGO_INTAKE_MECHANISM_STOW_BTN_ID))
    {
      cargo.articulateCargoIntake(CARGO_MECHANISM_STOW_SPEED);
    }
    if(leftJoyStick.getRawButton(CARGO_INTAKE_BTN_ID))
    {
      cargo.intakeCargo(CARGO_INTAKE_SPEED);
      cargo.articulateCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
    }
    if(leftJoyStick.getRawButton(CARGO_SHOOT_BTN_ID))
    {
      cargo.articulateCargoIntake(0);
      cargo.shootCargo(CARGO_SHOOT_SPEED);
    }
    else
    {
      cargo.shootCargo(0);
      cargo.intakeCargo(0);
      cargo.articulateCargoIntake(0);
    }
    
  }

    
  }

  public void testPeriodic() 
  {
    
  }
}
