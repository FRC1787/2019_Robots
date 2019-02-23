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
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;

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
  private final Vision vision = Vision.getInstance();
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
  private final int INTAKE_BTN_ID = 1;
  private final int DELIVER_BTN_ID = 2;
  private final int STOW_BTN_ID = 3;
  private final int DEPLOY_BTN_ID = 4;

    //Left Stick Button IDs
  private final int JOYSTICK_CARGO_MODE_BTN_ID = 3;
  private final int JOYSTICK_HATCH_MODE_BTN_ID = 4;
  // private final int CARGO_AUTO_INTAKE_BTN_ID = 14;
  private final int CLIMB_INITIATION_BTN = 5;
  private final int STOP_CLIMB_BTN = 10;
  private final int Toggle = 11;

  private boolean togglePressed = false;

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
  private final double F_CARGO_INTAKE_SPEED = -1;
  private final double F_CARGO_SHOOT_SPEED = -0.5;


    //Hatch speeds
  private double HATCH_DEPLOY_SPEED = -0.9;
  private double HATCH_STOW_SPEED = 0.9;
  private double HATCH_INTAKE_SPEED = 1;
  private double HATCH_DELIVER_SPEED = -1;
    //Cargo speeds
  private double CARGO_MECHANISM_DEPLOY_SPEED = 0.25;
  private double CARGO_MECHANISM_STOW_SPEED = -0.20;
  private double CARGO_INTAKE_SPEED = 1;
  private double CARGO_SHOOT_SPEED = 0.75;

  //Other varibales
  private boolean deployCargoIntakeState = false;
  private int hatchDecelerationCounter = 0;
  private boolean engageShooterBelt = false;
  private int shooterTimer = 0;
  private final int F_SHOOTER_TIMER_MAX = 100;
  private int shooterTimerMaxValue;
  private final double F_JOYSTICK_SWITCH_THRESHOLD = 0.25;
  private double joyStickSwitchThreshold;
  private boolean joyStickSwap = true;
  private int cargoAutoCount = 0;
  private final int CARGO_AUTO_COUNT_MAX = 5;
  private final double CARGO_AUTO_SPEED = -0.25;
  private boolean readyForIntake = false;
  private boolean climbInitiated = false;
  private boolean joyStickHatchMode = false;
  private boolean joyStickCargoMode = false;


  public void setDashboard()
  {
    SmartDashboard.putNumber("Hatch Deploy Speed", F_HATCH_DEPLOY_SPEED);
    SmartDashboard.putNumber("Hatch Stow Speed", F_HATCH_STOW_SPEED);
    SmartDashboard.putNumber("Hatch Intake Speed", F_HATCH_INTAKE_SPEED);
    SmartDashboard.putNumber("Hatch Deliver Speed", F_HATCH_DELIVER_SPEED);

    SmartDashboard.putNumber("Cargo Deploy Speed", F_CARGO_MECHANISM_DEPLOY_SPEED);
    SmartDashboard.putNumber("Cargo Stow Speed", F_CARGO_MECHANISM_STOW_SPEED);
    SmartDashboard.putNumber("Cargo Intake Speed", F_CARGO_INTAKE_SPEED);
    SmartDashboard.putNumber("Cargo Shoot Speed", F_CARGO_SHOOT_SPEED);

    SmartDashboard.putNumber("Shooter Belt Timer Max", F_SHOOTER_TIMER_MAX);
  }

  public void updateDashboard()
  {
    HATCH_DEPLOY_SPEED = SmartDashboard.getNumber("Hatch Deploy Speed", F_HATCH_DEPLOY_SPEED);
    HATCH_STOW_SPEED = SmartDashboard.getNumber("Hatch Stow Speed", F_HATCH_STOW_SPEED);
    HATCH_INTAKE_SPEED = SmartDashboard.getNumber("Hatch Intake Speed", F_HATCH_INTAKE_SPEED);
    HATCH_DELIVER_SPEED = SmartDashboard.getNumber("Hatch Deliver Speed", F_HATCH_DELIVER_SPEED);

    CARGO_MECHANISM_DEPLOY_SPEED = SmartDashboard.getNumber("Cargo Deploy Speed", F_CARGO_MECHANISM_DEPLOY_SPEED);
    CARGO_MECHANISM_STOW_SPEED = SmartDashboard.getNumber("Cargo Stow Speed", F_CARGO_MECHANISM_STOW_SPEED);
    CARGO_INTAKE_SPEED = SmartDashboard.getNumber("Cargo Intake Speed", F_CARGO_INTAKE_SPEED);
    CARGO_SHOOT_SPEED = SmartDashboard.getNumber("Cargo Shoot Speed", F_CARGO_SHOOT_SPEED);

    shooterTimerMaxValue = (int) SmartDashboard.getNumber("Shooter Belt Timer Max", F_SHOOTER_TIMER_MAX);
  }

  public void robotInit() 
  {
    this.setDashboard();
  }

  public void robotPeriodic() 
  {
    this.updateDashboard();
  }

  public void autonomousInit() 
  {

  }

  public void autonomousPeriodic() 
  {

  }

  public void teleopInit()
  {
    
  }

  public void teleopPeriodic() 
  {

    vision.processing();

    /*
     * **************************************
     * **************************************
     * **************************************
     * Toggle right joystick control modes***
     * **************************************
     * **************************************
     * **************************************
     */

    //Switches joystick to cargo mode
    if (leftJoyStick.getRawButtonPressed(JOYSTICK_CARGO_MODE_BTN_ID))
    {
      joyStickHatchMode = false;
      joyStickCargoMode = true;
    }

    //Switches joystick to hatch mode
    else if (leftJoyStick.getRawButtonPressed(JOYSTICK_HATCH_MODE_BTN_ID))
    {
      joyStickHatchMode = true;
      joyStickCargoMode = false;
    }




    
    /**
     * ******************************************
     * ******************************************
     * ******************************************
     * Hatch joystick and hatch oriented drive***
     * ******************************************
     * ******************************************
     */
    if (joyStickHatchMode && !joyStickCargoMode)
    {
      //Switches the orientation of the drive so that the front of the robot is the hatch
      driveTrain.arcadeDrive(rightJoyStick.getX(), rightJoyStick.getY());



      /*
       * *************************
       * Hatch mechanism controls*
       * ************************* 
       */

      //Deploy hatch mechanism
      if (rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !hatch.getHatchMechanismDeploeyedSwitchState())
      {
        hatch.articulateHatch(HATCH_DEPLOY_SPEED);
      }

      //Stop deploy once the deploy limit switch is pressed
      if (!rightJoyStick.getRawButton(STOW_BTN_ID)  && hatch.getHatchMechanismDeploeyedSwitchState() )
      {
        hatch.articulateHatch(0);
      }



      //Stow hatch mechanism
      if (rightJoyStick.getRawButton(STOW_BTN_ID) && !hatch.getHatchMechanismStowedSwitchState())
      {
        hatch.articulateHatch(HATCH_STOW_SPEED);
      }

      //Stop stow once the stow limit switch is pressed
      if (!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && hatch.getHatchMechanismStowedSwitchState())
      {
        hatch.articulateHatch(0);
      }

      //If hatch is on the mechanism don't move anything
      if(hatch.getHatchIntakedSwitchState())
      {
        hatch.articulateHatch(0);
      }



      /*
       * **************************
       * Hatch intake and delivery*
       * **************************  
       */

      //Intake hatch
      if(rightJoyStick.getRawButton(INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
      {
        hatch.grabHatch(HATCH_INTAKE_SPEED);
      }

      //Stop once hatch is on and limit switch is pressed
      if(!rightJoyStick.getRawButton(DELIVER_BTN_ID) && hatch.getHatchIntakedSwitchState())
      {
        hatch.grabHatch(0);
      }



      //Deliver hatch
      if(rightJoyStick.getRawButton(DELIVER_BTN_ID))
      {
        hatch.grabHatch(HATCH_DELIVER_SPEED);
      }

      //Stop delivering once no button is being pressed
      if(!rightJoyStick.getRawButton(DELIVER_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
      {
        hatch.grabHatch(0);
      }

    }



    /*
    ********************************************
    ********************************************
    * ******************************************
    * Cargo joystick and cargo oriented drive***
    ********************************************
    ********************************************
    ********************************************
    */
    if(joyStickCargoMode && !joyStickHatchMode)
    {
      //Swithces the orientation of the drive so that the front of the robot is the cargo
      driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());

      

      /*
       * ***************
       * Cargo controls*
       * ***************
       */
      //Deploy cargo intake mechanism
      if(rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismDeployedSwitchState())
      {
       cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
      }

      //Stop deploy once the deploy limit switch is pressed
      if(!rightJoyStick.getRawButton(STOW_BTN_ID) && cargo.getCargoIntakeMechanismDeployedSwitchState() && !readyForIntake)
      {
        cargo.articulateCargoIntake(0);
      }




      //Stow cargo intake mechanism 
      if(rightJoyStick.getRawButton(STOW_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
      {
        cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
      }

      //Stop stow once hte stow limit switch is hit
      if(!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && cargo.getCargoIntakeMechanismStowedSwitchState() && !readyForIntake)
      {
        cargo.articulateCargoIntake(0);
      }



      //Stop while no button is being pressed
      if(!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !leftJoyStick.getRawButton(STOW_BTN_ID) && !readyForIntake)
      {
        cargo.articulateCargoIntake(0);
      }

      //Intake cargo: deploy the intake, then spin the wheels
      if(rightJoyStick.getRawButton(INTAKE_BTN_ID))
      {
        cargo.intakeCargo(CARGO_INTAKE_SPEED);
        cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
      }

      //Stop intake wheels, 
      if(!rightJoyStick.getRawButton(INTAKE_BTN_ID) && !leftJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState() && !readyForIntake)
      {
        cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
        cargo.intakeCargo(0);
      }



      /*
      * **************************
      * CARGO INTAKE VISION STUFF*
      * **************************
      */
      if(rightJoyStick.getRawButton(INTAKE_BTN_ID))
      {
        cargoAutoCount++;
        System.out.println(cargoAutoCount);
        if(cargoAutoCount <= CARGO_AUTO_COUNT_MAX) 
        {
          driveTrain.tankDrive(CARGO_AUTO_SPEED, -CARGO_AUTO_SPEED);
        }
      
        // else if(vision.ballInFrame())
        // {
        //   readyForIntake = true;
        // }
        
        if(readyForIntake)
        {
          cargo.intakeCargo(CARGO_INTAKE_SPEED);
          cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
        }

        if(!(cargoAutoCount <= CARGO_AUTO_COUNT_MAX))
        {
          driveTrain.tankDrive(0, 0);
        }
      }
    
      else if(rightJoyStick.getRawButtonReleased(INTAKE_BTN_ID))
      {
        cargoAutoCount = 0;
        readyForIntake = false;
        driveTrain.tankDrive(0, 0);
        cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
        cargo.intakeCargo(0);
      }

      if(rightJoyStick.getRawButton(13))
      {
        driveTrain.tankDrive(0, 0);
        cargoAutoCount = 0;
      }

      

      /*
       * ************
       * Shoot cargo* 
       * ************
       */
      //Spin intake wheels as long as button is pressed
      if(rightJoyStick.getRawButton(DELIVER_BTN_ID))
      {
        cargo.intakeCargo(CARGO_SHOOT_SPEED);
      }

    
      if(rightJoyStick.getRawButtonReleased(DELIVER_BTN_ID))
      {
        engageShooterBelt = true;
      }

      //Engage shooting belt, once the button is released
      if(engageShooterBelt)
      {
        cargo.stowCargoIntake(-0.5);
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

      if(!rightJoyStick.getRawButton(DELIVER_BTN_ID) && !engageShooterBelt)
      {
        //cargo.stowCargoIntake(0);
        cargo.shootCargo(0);
      }
    }





/*********************************************************************************************************************/
  //   //Listen to limit switches
  //   if (rightJoyStick.getRawAxis(JOYSTICK_SLIDER_AXIS) < 0)
  //   {

  //   /*************************/
  //   /*************************/
  //   /*Right Joystick controls*/
  //   /*************************/
  //   /*************************/


  //   //Hatch mechanism controls
  //     //Deploy hatch mechanism
  //   if (joyStickHatchMode && rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !hatch.getHatchMechanismDeploeyedSwitchState())
  //   {
  //     hatch.articulateHatch(HATCH_DEPLOY_SPEED);
  //   }

  //   //Stop deploy once the deploy limit switch is pressed
  //   if (joyStickHatchMode && !rightJoyStick.getRawButton(STOW_BTN_ID)  && hatch.getHatchMechanismDeploeyedSwitchState() )
  //   {
  //     hatch.articulateHatch(0);
  //   }



  //   //Stow hatch mechanism
  //   if (joyStickHatchMode && rightJoyStick.getRawButton(STOW_BTN_ID) && !hatch.getHatchMechanismStowedSwitchState())
  //   {
  //     hatch.articulateHatch(HATCH_STOW_SPEED);
  //   }

  //   //Stop stow once the stow limit switch is pressed
  //   if (joyStickHatchMode && !rightJoyStick.getRawButton(DEPLOY_BTN_ID) && hatch.getHatchMechanismStowedSwitchState())
  //   {
  //     hatch.articulateHatch(0);
  //   }

  //   //If hatch is on the mechanism don't move anything
  //   if(joyStickHatchMode && hatch.getHatchIntakedSwitchState())
  //   {
  //     hatch.articulateHatch(0);
  //   }



  //   //Hatch intake and delivery
  //   //Intake hatch
  //   if(joyStickHatchMode && rightJoyStick.getRawButton(INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
  //   {
  //     hatch.grabHatch(HATCH_INTAKE_SPEED);
  //   }

  //   //Stop once hatch is on and limit switch is pressed
  //   if(joyStickHatchMode && !rightJoyStick.getRawButton(DELIVER_BTN_ID) && hatch.getHatchIntakedSwitchState())
  //   {
  //     hatch.grabHatch(0);
  //   }



  //   //Deliver hatch
  //   if(joyStickHatchMode && rightJoyStick.getRawButton(DELIVER_BTN_ID))
  //   {
  //     hatch.grabHatch(HATCH_DELIVER_SPEED);
  //   }

  //   //Stop delivering once no button is being pressed
  //   if(joyStickHatchMode && !rightJoyStick.getRawButton(DELIVER_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
  //   {
  //     hatch.grabHatch(0);
  //   }

  


  //   /************************/
  //   /************************/
  //   /*Left joystick controls*/
  //   /************************/
  //   /************************/


    



  //   //Cargo controls
  //   //Deploy cargo intake mechanism
  //   if(!joyStickHatchMode && rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismDeployedSwitchState())
  //   {
  //     cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
  //   }

  //   //Stop deploy once the deploy limit switch is pressed
  //   if(!joyStickHatchMode && !rightJoyStick.getRawButton(STOW_BTN_ID) && cargo.getCargoIntakeMechanismDeployedSwitchState() && !readyForIntake)
  //   {
  //     cargo.articulateCargoIntake(0);
  //   }




  //   //Stow cargo intake mechanism 
  //   if(!joyStickHatchMode && rightJoyStick.getRawButton(STOW_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
  //   {
  //     cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
  //   }

  //   //Stop stow once hte stow limit switch is hit
  //   if(!joyStickHatchMode && !rightJoyStick.getRawButton(DEPLOY_BTN_ID) && cargo.getCargoIntakeMechanismStowedSwitchState() && !readyForIntake)
  //   {
  //     cargo.articulateCargoIntake(0);
  //   }



  //   //Stop while no button is being pressed
  //   if(!joyStickHatchMode && !rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !leftJoyStick.getRawButton(STOW_BTN_ID) && !readyForIntake)
  //   {
  //     cargo.articulateCargoIntake(0);
  //   }

  //   //Intake cargo: deploy the intake, then spin the wheels
  //   if(!joyStickHatchMode && rightJoyStick.getRawButton(INTAKE_BTN_ID))
  //   {
  //     cargo.intakeCargo(CARGO_INTAKE_SPEED);
  //     cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
  //   }

  //   //Stop intake wheels, 
  //   if(!joyStickHatchMode && !rightJoyStick.getRawButton(INTAKE_BTN_ID) && !leftJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState() && !readyForIntake)
  //   {
  //     cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
  //     cargo.intakeCargo(0);
  //   }
  //   /*
  //   * CARGO INTAKE VISION STUFF
  //   */
  //   if(!joyStickHatchMode && rightJoyStick.getRawButton(INTAKE_BTN_ID))
  //   {
  //     cargoAutoCount++;
  //     System.out.println(cargoAutoCount);
  //     if(cargoAutoCount <= CARGO_AUTO_COUNT_MAX) 
  //     {
  //       driveTrain.tankDrive(CARGO_AUTO_SPEED, -CARGO_AUTO_SPEED);
  //     }
      
  //     // else if(vision.ballInFrame())
  //     // {
  //     //   readyForIntake = true;
  //     // }
        
  //     if(readyForIntake)
  //     {
  //       cargo.intakeCargo(CARGO_INTAKE_SPEED);
  //       cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
  //     }

  //     if(!(cargoAutoCount <= CARGO_AUTO_COUNT_MAX))
  //     {
  //       driveTrain.tankDrive(0, 0);
  //     }
  //   }
    
  //   else if(!joyStickHatchMode && rightJoyStick.getRawButtonReleased(INTAKE_BTN_ID))
  //   {
  //     cargoAutoCount = 0;
  //     readyForIntake = false;
  //     driveTrain.tankDrive(0, 0);
  //     cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
  //     cargo.intakeCargo(0);
  //   }

  //   if(!joyStickHatchMode && rightJoyStick.getRawButton(13))
  //   {
  //     driveTrain.tankDrive(0, 0);
  //     cargoAutoCount = 0;
  //   }

  //   //Shoot cargo
  //   //Spin intake wheels as long as button is pressed
  //   if(!joyStickHatchMode && rightJoyStick.getRawButton(DELIVER_BTN_ID))
  //   {
  //     cargo.intakeCargo(CARGO_SHOOT_SPEED);
  //   }

    
  //   if(!joyStickHatchMode && rightJoyStick.getRawButtonReleased(DELIVER_BTN_ID))
  //   {
  //     engageShooterBelt = true;
  //   }

  //   //Engage shooting belt, once the button is released
  //   if(engageShooterBelt)
  //   {
  //     cargo.stowCargoIntake(-0.5);
  //     cargo.intakeCargo(0);
  //     if(shooterTimer <= 100)
  //     {
  //       cargo.shootCargo(CARGO_SHOOT_SPEED);
  //     }

  //     else
  //     {
  //       cargo.shootCargo(0);
  //       engageShooterBelt = false;
  //       shooterTimer = 0;
  //     }
  //     shooterTimer++;
  //   }

  //   if(!joyStickHatchMode && !rightJoyStick.getRawButton(DELIVER_BTN_ID) && !engageShooterBelt)
  //   {
  //     //cargo.stowCargoIntake(0);
  //     cargo.shootCargo(0);
  //   }
  // }
  /*******************************************************************************************************************/


    //Climb controls just for a commit
    if(!joyStickHatchMode && rightJoyStick.getRawButton(CLIMB_INITIATION_BTN))
    {
      climbInitiated = true;
    }
    if(climbInitiated)
    {
      climb.moveClimber(climb.sliderCorrection(leftJoyStick));
    }
    if(!joyStickHatchMode && leftJoyStick.getRawButton(STOP_CLIMB_BTN))
    {
      climbInitiated = false;
    }
    if(!climbInitiated)
    {
      climb.moveClimber(0);
    }
  



  
  //test code without limit switches///////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////
  // if(rightJoyStick.getRawAxis(JOYSTICK_SLIDER_AXIS) > 0)
  // {

  //   //right joystick test controls
  //   if(rightJoyStick.getRawButton(DEPLOY_BTN_ID))
  //   {
  //     hatch.articulateHatch(HATCH_DEPLOY_SPEED);
  //   }
  //   else if(rightJoyStick.getRawButton(STOW_BTN_ID))
  //   {
  //     hatch.articulateHatch(HATCH_STOW_SPEED);
  //   }
  //   else if(rightJoyStick.getRawButton(INTAKE_BTN_ID))
  //   {
  //     hatch.articulateHatch(0);
  //     hatch.grabHatch(HATCH_INTAKE_SPEED);
  //   }
  //   else if(rightJoyStick.getRawButton(DELIVER_BTN_ID))
  //   {
  //     hatch.articulateHatch(0);
  //     hatch.grabHatch(HATCH_DELIVER_SPEED);
  //   }
  //   else
  //   {
  //     hatch.articulateHatch(0);
  //     hatch.grabHatch(0);
  //   }


  //   //left joystick test controls
  //   if(leftJoyStick.getRawButton(DEPLOY_BTN_ID))
  //   {
  //     cargo.articulateCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
  //   }
  //   if(leftJoyStick.getRawButton(STOW_BTN_ID))
  //   {
  //     cargo.articulateCargoIntake(CARGO_MECHANISM_STOW_SPEED);
  //   }
  //   if(leftJoyStick.getRawButton(INTAKE_BTN_ID))
  //   {
  //     cargo.intakeCargo(CARGO_INTAKE_SPEED);
  //     cargo.articulateCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
  //   }
  //   if(leftJoyStick.getRawButton(DELIVER_BTN_ID))
  //   {
  //     cargo.articulateCargoIntake(0);
  //     cargo.shootCargo(CARGO_SHOOT_SPEED);
  //   }
  //   else
  //   {
  //     cargo.shootCargo(0);
  //     cargo.intakeCargo(0);
  //     cargo.articulateCargoIntake(0);
  //   }
    
  // }
}

  public void testPeriodic() 
  {
    this.teleopPeriodic(); 
  }
}
