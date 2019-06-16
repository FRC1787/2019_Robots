package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.Encoder;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SPI;

import edu.wpi.first.wpilibj.Servo;


public class Robot extends TimedRobot {

    //IDK why but they are here
    protected int farfar37;
    protected double internetSpeed = 2.0;

    /* Subsystem Instances */
    private final Cargo cargo = Cargo.getInstance();
    private final Climb climb = Climb.getInstance();
    private final DriveTrain driveTrain = DriveTrain.getInstance();
    private final Hatch hatch = Hatch.getInstance();
    private final Vision vision = Vision.getInstance();
    private final Sensor sensor = Sensor.getInstance();

    /* NavX Object Setup */
    public static AHRS navX = new AHRS(SPI.Port.kMXP);

    /* Joystick IDs */
    private static final int RIGHT_JOYSTICK_ID = 0;
    private static final int LEFT_JOYSTICK_ID = 1;

    /* Joystick Instances */
    private final Joystick rightJoyStick = new Joystick(RIGHT_JOYSTICK_ID);
    private final Joystick leftJoyStick = new Joystick(LEFT_JOYSTICK_ID);

    /* Joystick Axis */
    private static final int JOYSTICK_ROTATION_AXIS = 2;
    private static final int JOYSTICK_SLIDER_AXIS = 3;

    /* Right Joystick Button IDs */
    private final int INTAKE_BTN_ID = 1;
    private final int DELIVER_BTN_ID = 2;
    private final int STOW_BTN_ID = 3;
    private final int DEPLOY_BTN_ID = 4;

    /* Left Joystick Button IDs */
    private final int CLIMB_DIRECTION_EXTEND_BTN_ID = 1;
    private final int CLIMB_DIRECTION_RETRACT_BTN_ID = 2;
    private final int JOYSTICK_CARGO_MODE_BTN_ID = 3;
    private final int JOYSTICK_HATCH_MODE_BTN_ID = 4;
    private final int CARGO_AUTO_INTAKE_BTN_ID = 14;
    private final int CLIMB_INITIATION_BTN = 5;
    private final int STOP_CLIMB_BTN = 10;
    private final int Toggle = 11;

    private boolean togglePressed = false;
    

    /* WORKING VALUES */

    /* Hatch Speeds */
    private final double F_HATCH_DEPLOY_SPEED = -0.9;
    private final double F_HATCH_STOW_SPEED = 0.9;
    private final double F_HATCH_INTAKE_SPEED = 1; //-.6
    private final double F_HATCH_DELIVER_SPEED = 1;

    /* Cargo Speeds */
    private final double F_CARGO_MECHANISM_DEPLOY_SPEED = 0.4;
    private final double F_CARGO_MECHANISM_STOW_SPEED = -0.40;
    private final double F_CARGO_INTAKE_SPEED = -.9 ;
    private final double F_CARGO_SHOOT_BELT_SPEED = -0.5;

    /* Hatch Speeds */
    private double HATCH_DEPLOY_SPEED = -0.9;
    private double HATCH_STOW_SPEED = 0.9;
    private double HATCH_INTAKE_SPEED = .6;
    private double HATCH_DELIVER_SPEED = -1.0;
    private double HATCH_BACK_DRIVE_SPEED = 0.125;

    /* Cargo Speeds */
    private double CARGO_MECHANISM_DEPLOY_SPEED = 0.4;
    private double CARGO_MECHANISM_STOW_SPEED = -0.4;
    private double CARGO_INTAKE_SPEED = .9;
    private double CARGO_SHOOT_BELT_SPEED = 1;
    private double CARGO_SHOOT_DRUM_SPEED = 0.6;


    /* Hatch Amp Limit Variables */
    private final double F_HATCH_AMP_LIMIT = 15;
    private final double F_HATCH_TIMEOUT = 20.0;

    private final double hatchAmpLimit = 20;
    private int hatchAmpCounter = 0;
    private boolean allowedToIntake = true;

    private final double CLIMB_MOTOR_AMP_LIMIT = 10;

    /* Joystick Swap */
    private boolean joyStickHatchMode = true;
    private boolean joyStickCargoMode = false;

    /* Other Variables */
    private boolean engageShooterBelt = false;
    private int shooterTimer = 0;
    private double climbSliderValue = 0;
    private int cargoIntakeTimer = 0;
    private int hatchIntakeTimer = 0;
    private int Pin_Value = 1;
    private int climbRetractCounter = 0; 
    private boolean climbDone = false;
    private boolean runMotorTest = false;
    private boolean backDriveHatch = true;
    private boolean retractClimber = false;
    public boolean movingthething = true;
    public double angleAdd;
    public double testAngle;


    public void robotInit() 
    {
        this.setDashboard();
    }

    public void robotPeriodic() 
    {
        this.updateDashboard();
        this.setDashboard();
        
    }

    public void autonomousInit() {}

    public void autonomousPeriodic() 
    {
        
        //this.teleopPeriodic();
    }

    public void teleopInit()
    {
        climbDone = false;
        //allowedToIntake = true;
    }

    public void teleopPeriodic() 
    {
        this.primusPeriodic();

        climb.setBrakeMode();
    }


    public void testInit()
    {
        runMotorTest = false;
    }


    public void testPeriodic() 
    {   



        if (rightJoyStick.getRawButton(1))
        {
            driveTrain.seekDrive(angull(), "navX");   
        }
        else if (rightJoyStick.getRawButton(2))
        {
            angleAdd++;
            if (angleAdd == 1)
            {
                testAngle = navX.getYaw() + 90;
            }
            driveTrain.seekDrive(testAngle, "navX");
        }
        else
        {
            angleAdd = 0;
            driveTrain.tankDrive(0,0);
        }
       /* if (leftJoyStick.getRawButton(1))
        {
            runMotorTest = true;
        }

        else if (leftJoyStick.getRawButton(2))
        {
            runMotorTest = false;
        }


        if (runMotorTest)
        {
            if (hatch.getHatchGrabberCurrent() >= 3.6)
            {
                runMotorTest = false;
            }
            hatch.grabHatch(1);
        }

        else if (!runMotorTest)
        {
            hatch.grabHatch(0.00);
        }


       /* if (leftJoyStick.getRawButton(CLIMB_DIRECTION_EXTEND_BTN_ID) && !leftJoyStick.getRawButton(CLIMB_DIRECTION_RETRACT_BTN_ID))
        { 
            cargo.intakeCargo(-.45);

            if(climbSliderValue < 1)
            {
                climbSliderValue += 0.025;
            }
            else
            {
                climbSliderValue = 1;
            }

            if(climb.getClimberMotorCurrent() < CLIMB_MOTOR_AMP_LIMIT)
            {
                if(!climb.climberFullyExtended())
                {
                    climb.moveClimber(climbSliderValue);
                }
                else
                {
                    climb.moveClimber(0);
                }
            }
            else
            {
                climb.moveClimber(0);   
            }
        }

        if(leftJoyStick.getRawButtonReleased(CLIMB_DIRECTION_EXTEND_BTN_ID))
        {
            retractClimber = true;
        }

        if(retractClimber)
        {
            if(climb.getClimberMotorCurrent() < CLIMB_MOTOR_AMP_LIMIT)
            {
                climb.moveClimber(-0.5);
            }
            else
            {
                climb.moveClimber(0);
                retractClimber = false;
            }
        } */


        //Retract Climber
        /* if (leftJoyStick.getRawButton(CLIMB_DIRECTION_RETRACT_BTN_ID) && !leftJoyStick.getRawButton(CLIMB_DIRECTION_EXTEND_BTN_ID))
        {
            if(climb.getClimberMotorCurrent() < CLIMB_MOTOR_AMP_LIMIT)
            {
                if(!climb.climberFullyRetracted())
                {
                    climb.moveClimber(-0.5);
                }

                else
                {
                    climb.moveClimber(0);
                }
                cargo.intakeCargo(0);
            }

            else
            {
                climb.moveClimber(0);
            }
        } 



        if(!leftJoyStick.getRawButton(CLIMB_DIRECTION_EXTEND_BTN_ID) && !leftJoyStick.getRawButton(CLIMB_DIRECTION_RETRACT_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID) && !rightJoyStick.getRawButton(DELIVER_BTN_ID) )
        {
            climb.moveClimber(0);
            cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
            cargo.intakeCargo(0);
        } */


    }  



    public void primusPeriodic() 
    {

        /* *********************************** */
        /* TOGGLE RIGHT JOYSTICK CONTROL MODES */
        /* *********************************** */

        // Switches joystick to cargo mode
        if (leftJoyStick.getRawButtonPressed(JOYSTICK_CARGO_MODE_BTN_ID)) {
            joyStickHatchMode = false;
            joyStickCargoMode = true;
            
           
            hatch.grabHatch(0);
        }

        // Switches joystick to hatch mode
        else if (leftJoyStick.getRawButtonPressed(JOYSTICK_HATCH_MODE_BTN_ID)) {
            joyStickHatchMode = true;
            joyStickCargoMode = false;
        }



        /* ************************************* */
        /* HATCH JOYSTICK & HATCH ORIENTED DRIVE */
        /* ************************************* */

        if (joyStickHatchMode && !joyStickCargoMode) {
            // Switches the orientation of the drive so that the front of the robot is the hatch
            driveTrain.arcadeDrive(rightJoyStick.getX(), rightJoyStick.getY());



            /* ************************ */
            /* HATCH MECHANISM CONTROLS */
            /* ************************ */

            // Deploy hatch mechanism
            if (rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !hatch.getHatchMechanismDeployedSwitchState())
                hatch.articulateHatch(HATCH_DEPLOY_SPEED);

            // Stop deploy once the deploy limit switch is pressed
            if (!rightJoyStick.getRawButton(STOW_BTN_ID) && hatch.getHatchMechanismDeployedSwitchState())
                hatch.articulateHatch(0);


            // Stow hatch mechanism
            if (rightJoyStick.getRawButton(STOW_BTN_ID) && !hatch.getHatchMechanismStowedSwitchState())
                hatch.articulateHatch(HATCH_STOW_SPEED);

            // Stop stow once the stow limit switch is pressed
            if (!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && hatch.getHatchMechanismStowedSwitchState())
                hatch.articulateHatch(0);


            /* ************************* */
            /* HATCH INTAKE AND DELIVERY */
            /* ************************* */

            //System.out.println(hatch.getHatchGrabberCurrent());


            if(rightJoyStick.getRawButton(INTAKE_BTN_ID) && !rightJoyStick.getRawButton(DELIVER_BTN_ID) )
            {

                /* Hatch amp limit enabled code */
                ////////////////////////////////////////////////////////////////////////////
                // if(hatchAmpCounter < 5)                                                 
                // {
                //     if(hatch.getHatchGrabberCurrent() < hatchAmpLimit)
                //     {
                //         hatch.grabHatch(-HATCH_INTAKE_SPEED);
                //     }

                //     else
                //     {
                //         hatchAmpCounter ++;
                //     }
                // }
                // else
                // {
                //     hatch.grabHatch(HATCH_BACK_DRIVE_SPEED);
                // }
                /////////////////////////////////////////////////////////////////////////////

                hatch.grabHatch(.45);
            }
            
            if(rightJoyStick.getRawButton(DELIVER_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID))
            {
                hatch.grabHatch(-HATCH_DELIVER_SPEED);
            }

            if(!rightJoyStick.getRawButton(DELIVER_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID))
            {
                hatch.grabHatch(HATCH_BACK_DRIVE_SPEED);
            }
            
            /* **************** */
            /*   CARGO INTAKE   */
            /* **************** */

            // Stow cargo intake mechanism
            if (!cargo.getCargoIntakeMechanismStowedSwitchState())
            {
                cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
            }

            // Stop stow once the stow limit switch is hit
            if (cargo.getCargoIntakeMechanismStowedSwitchState())
                cargo.articulateCargoIntake(0);


        }



        /* ************************************* */
        /* CARGO JOYSTICK & CARGO ORIENTED DRIVE */
        /* ************************************* */

        if (joyStickCargoMode && !joyStickHatchMode) 
        {
            // Switches the orientation of the drive so that the front of the robot is the cargo
            driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());



            /* ************** */
            /* CARGO CONTROLS */
            /* ************** */

            // Deploy cargo intake mechanism
            if (rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismDeployedSwitchState())
                cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);

            // Stop deploy once the deploy limit switch is pressed
            if (!rightJoyStick.getRawButton(STOW_BTN_ID) && cargo.getCargoIntakeMechanismDeployedSwitchState())
                cargo.articulateCargoIntake(0);

            // Stow cargo intake mechanism
            if (rightJoyStick.getRawButton(STOW_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
                cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);

            // Stop stow once the stow limit switch is hit
            if (!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && cargo.getCargoIntakeMechanismStowedSwitchState())
                cargo.articulateCargoIntake(0);

            // Stop while no button is being pressed
            if (!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !leftJoyStick.getRawButton(STOW_BTN_ID))
                cargo.articulateCargoIntake(0);

            // Intake cargo: deploy the intake, then spin the wheels
            if (rightJoyStick.getRawButton(INTAKE_BTN_ID)) 
            {
                // if(cargoIntakeTimer % 25 >= 4)
                // {
                cargo.intakeCargo(CARGO_INTAKE_SPEED);
                // }
                // else
                // {
                //     cargo.intakeCargo(.1);
                // }
                // cargoIntakeTimer ++;
                cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
            }

            //Stop intake wheels,
            if (!rightJoyStick.getRawButton(INTAKE_BTN_ID) && !leftJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState()) 
            {
                cargoIntakeTimer = 0;
                cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
                cargo.intakeCargo(0);
            }



            /* *********** */
            /* SHOOT CARGO */
            /* *********** */

            // Spin intake wheels as long as button is pressed
            if (rightJoyStick.getRawButton(DELIVER_BTN_ID))
            {
                cargo.intakeCargo(-CARGO_SHOOT_DRUM_SPEED);
            }
            
            if (rightJoyStick.getRawButtonReleased(DELIVER_BTN_ID))
            {
              engageShooterBelt = true;
            }

            // Engage shooting belt, once the button is released
            if (engageShooterBelt) 
            {
                cargo.stowCargoIntake(-0.5);
                cargo.intakeCargo(0);

                if (shooterTimer <= 50) 
                {
                  cargo.shootCargo(-CARGO_SHOOT_BELT_SPEED);
                  shooterTimer++;
                }

                else 
                {
                  cargo.shootCargo(0);
                  engageShooterBelt = false;
                  shooterTimer = 0;
                }

            }

            if (!rightJoyStick.getRawButton(DELIVER_BTN_ID) && !engageShooterBelt) {
                //cargo.stowCargoIntake(0);
                cargo.shootCargo(0);
            }
        }



        /* ************** */
        /* CLIMB CONTROLS */
        /* ************** */

        /*
        // Climb direction switch trigger & back button
        if (leftJoyStick.getRawButtonPressed(CLIMB_DIRECTION_EXTEND_BTN_ID))
            climbDirectionExtend = true;
            //Back button
        else if (leftJoyStick.getRawButtonPressed(CLIMB_DIRECTION_RETRACT_BTN_ID))
            climbDirectionExtend = false;


        // Climb action controls
        climbInitiated = rightJoyStick.getRawButton(CLIMB_INITIATION_BTN);
        */


        //Extend climber
        if (leftJoyStick.getRawButton(CLIMB_DIRECTION_EXTEND_BTN_ID) && !leftJoyStick.getRawButton(CLIMB_DIRECTION_RETRACT_BTN_ID))
        { 
            cargo.intakeCargo(-.45);

            /* if(climbSliderValue < 1)
            {
                climbSliderValue += 0.025;
            }
            else
            {
                climbSliderValue = 1;
            } */

            if(!climb.climberFullyExtended())
            {
                climb.moveClimber(1);
            }
            else
            {
                climb.moveClimber(0);
            }
        }


        //Retract Climber
        if (leftJoyStick.getRawButton(CLIMB_DIRECTION_RETRACT_BTN_ID) && !leftJoyStick.getRawButton(CLIMB_DIRECTION_EXTEND_BTN_ID))
        {
            if(!climb.climberFullyRetracted())
            {
            climb.moveClimber(-1);
            }
            else
            {
                climb.moveClimber(0);
            }
            cargo.intakeCargo(0);
        }

        if(!leftJoyStick.getRawButton(CLIMB_DIRECTION_EXTEND_BTN_ID) && !leftJoyStick.getRawButton(CLIMB_DIRECTION_RETRACT_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID) && !rightJoyStick.getRawButton(DELIVER_BTN_ID))
        {
            climb.moveClimber(0);
            cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
            cargo.intakeCargo(0);
        }

        if(leftJoyStick.getRawButton(10))
        {
            angleAdd ++;
            if (angleAdd == 1)
            {
                testAngle = navX.getYaw() + 50;
            }

        }
        else
        {
            angleAdd = 0;
        }
    }

    public double fixInput(double joyNum) //Adds deadzone to the center of the Joystick
    {
      if (joyNum > .15 || joyNum < .15)
      return joyNum;
      else
      return 0;
    }
  
    public double angull() //changes Joystick input to Encoder ticks
    {
      if (fixInput(rightJoyStick.getX()) == 0 && fixInput(rightJoyStick.getY()) == 0)
      return 0;
      else if (fixInput(rightJoyStick.getX()) == 0 && fixInput(rightJoyStick.getY()) == 0 && rightJoyStick.getDirectionDegrees() > 0)
      return rightJoyStick.getDirectionDegrees();
      else
      return
      rightJoyStick.getDirectionDegrees() + 360;
    }

    public void setDashboard() {
        SmartDashboard.putNumber("Hatch Deploy Speed", F_HATCH_DEPLOY_SPEED);
        SmartDashboard.putNumber("Hatch Stow Speed", F_HATCH_STOW_SPEED);
        SmartDashboard.putNumber("Hatch Intake Speed", F_HATCH_INTAKE_SPEED);
        SmartDashboard.putNumber("Hatch Deliver Speed", F_HATCH_DELIVER_SPEED);

        SmartDashboard.putNumber("Cargo Deploy Speed", F_CARGO_MECHANISM_DEPLOY_SPEED);
        SmartDashboard.putNumber("Cargo Stow Speed", F_CARGO_MECHANISM_STOW_SPEED);
        SmartDashboard.putNumber("Cargo Intake Speed", F_CARGO_INTAKE_SPEED);
        SmartDashboard.putNumber("Cargo Shoot Speed", F_CARGO_SHOOT_BELT_SPEED);

        SmartDashboard.putBoolean("Drive Mode", joyStickCargoMode);

        SmartDashboard.putNumber("H Upper", vision.HSV_THRESHOLD_UPPER.val[0]);
        SmartDashboard.putNumber("S Upper", vision.HSV_THRESHOLD_UPPER.val[1]);
        SmartDashboard.putNumber("V Upper", vision.HSV_THRESHOLD_UPPER.val[2]);
        SmartDashboard.putNumber("H Lower", vision.HSV_THRESHOLD_LOWER.val[0]);
        SmartDashboard.putNumber("S Lower", vision.HSV_THRESHOLD_LOWER.val[1]);
        SmartDashboard.putNumber("V Lower", vision.HSV_THRESHOLD_LOWER.val[2]);

        SmartDashboard.putNumber("Climb Motor Speed", climbSliderValue);

        SmartDashboard.putNumber("Hatch Amp Limit", F_HATCH_AMP_LIMIT);
        SmartDashboard.putNumber("Hatch Amp Timeout", F_HATCH_TIMEOUT);
        
        SmartDashboard.putNumber("Inches Output", sensor.getSense());

        SmartDashboard.putBoolean(  "IMU_Connected",        navX.isConnected());
        SmartDashboard.putBoolean(  "IMU_IsCalibrating",    navX.isCalibrating());
        SmartDashboard.putNumber(   "IMU_Yaw",              navX.getYaw());
        SmartDashboard.putNumber(   "IMU_Pitch",            navX.getPitch());
        SmartDashboard.putNumber(   "IMU_Roll",             navX.getRoll());

        SmartDashboard.putNumber("motorpwr", driveTrain.pIDDrive(40, navX.getYaw(), "navX"));
        SmartDashboard.putNumber("Counter", angleAdd);
        SmartDashboard.putNumber("setPoint", testAngle);
        SmartDashboard.putNumber("angull", angull());

    }

    public void updateDashboard() {
        HATCH_DEPLOY_SPEED = SmartDashboard.getNumber("Hatch Deploy Speed", F_HATCH_DEPLOY_SPEED);
        HATCH_STOW_SPEED = SmartDashboard.getNumber("Hatch Stow Speed", F_HATCH_STOW_SPEED);
        HATCH_INTAKE_SPEED = SmartDashboard.getNumber("Hatch Intake Speed", F_HATCH_INTAKE_SPEED);
        HATCH_DELIVER_SPEED = SmartDashboard.getNumber("Hatch Deliver Speed", F_HATCH_DELIVER_SPEED);

        CARGO_MECHANISM_DEPLOY_SPEED = SmartDashboard.getNumber("Cargo Deploy Speed", F_CARGO_MECHANISM_DEPLOY_SPEED);
        CARGO_MECHANISM_STOW_SPEED = SmartDashboard.getNumber("Cargo Stow Speed", F_CARGO_MECHANISM_STOW_SPEED);
        CARGO_INTAKE_SPEED = SmartDashboard.getNumber("Cargo Intake Speed", F_CARGO_INTAKE_SPEED);
        CARGO_SHOOT_BELT_SPEED = SmartDashboard.getNumber("Cargo Shoot Speed", F_CARGO_SHOOT_BELT_SPEED);

        vision.setColor(true, 1, SmartDashboard.getNumber("H Upper", 360));
        vision.setColor(true, 2, SmartDashboard.getNumber("S Upper", 255));
        vision.setColor(true, 3, SmartDashboard.getNumber("V Upper", 255));
        vision.setColor(false, 1, SmartDashboard.getNumber("H Lower", 0));
        vision.setColor(false, 2, SmartDashboard.getNumber("S Lower", 0));
        vision.setColor(false, 3, SmartDashboard.getNumber("V Lower", 0));

        

    }
}
