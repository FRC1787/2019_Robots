package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


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
    private final double F_HATCH_INTAKE_SPEED = -1;
    private final double F_HATCH_DELIVER_SPEED = 1;

    /* Cargo Speeds */
    private final double F_CARGO_MECHANISM_DEPLOY_SPEED = 0.25;
    private final double F_CARGO_MECHANISM_STOW_SPEED = -0.20;
    private final double F_CARGO_INTAKE_SPEED = -1;
    private final double F_CARGO_SHOOT_SPEED = -0.5;


    /* Hatch Speeds */
    private double HATCH_DEPLOY_SPEED = -0.9;
    private double HATCH_STOW_SPEED = 0.9;
    private double HATCH_INTAKE_SPEED = 1;
    private double HATCH_DELIVER_SPEED = -1;

    /* Cargo Speeds */
    private double CARGO_MECHANISM_DEPLOY_SPEED = 0.25;
    private double CARGO_MECHANISM_STOW_SPEED = -0.20;
    private double CARGO_INTAKE_SPEED = 1;
    private double CARGO_SHOOT_SPEED = 0.75;

    /* Other Variables */
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
    private boolean joyStickCargoMode = true;
    private boolean climbDirectionExtend = true;


    public void primusPeriodic() {
        //vision.processing();

        /* *********************************** */
        /* TOGGLE RIGHT JOYSTICK CONTROL MODES */
        /* *********************************** */

        // Switches joystick to cargo mode
        if (leftJoyStick.getRawButtonPressed(JOYSTICK_CARGO_MODE_BTN_ID)) {
            joyStickHatchMode = false;
            joyStickCargoMode = true;
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

            // If hatch is on the mechanism don't move anything
            if (hatch.getHatchIntakedSwitchState())
                hatch.articulateHatch(0);


            /* ************************* */
            /* HATCH INTAKE AND DELIVERY */
            /* ************************* */

            // Intake hatch
            if (rightJoyStick.getRawButton(INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
                hatch.grabHatch(HATCH_INTAKE_SPEED);

            // Stop once hatch is on and limit switch is pressed
            if (!rightJoyStick.getRawButton(DELIVER_BTN_ID) && hatch.getHatchIntakedSwitchState())
                hatch.grabHatch(0);


            // Deliver hatch
            if (rightJoyStick.getRawButton(DELIVER_BTN_ID))
                hatch.grabHatch(HATCH_DELIVER_SPEED);

            // Stop delivering once no button is being pressed
            if (!rightJoyStick.getRawButton(DELIVER_BTN_ID) && !rightJoyStick.getRawButton(INTAKE_BTN_ID) && !hatch.getHatchIntakedSwitchState())
                hatch.grabHatch(0);

        }


        /* ************************************* */
        /* CARGO JOYSTICK & CARGO ORIENTED DRIVE */
        /* ************************************* */

        if (joyStickCargoMode && !joyStickHatchMode) {
            // Switches the orientation of the drive so that the front of the robot is the cargo
            driveTrain.arcadeDrive(rightJoyStick.getX(), -rightJoyStick.getY());

            /* ************** */
            /* CARGO CONTROLS */
            /* ************** */

            // Deploy cargo intake mechanism
            if (rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismDeployedSwitchState())
                cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);

            // Stop deploy once the deploy limit switch is pressed
            if (!rightJoyStick.getRawButton(STOW_BTN_ID) && cargo.getCargoIntakeMechanismDeployedSwitchState() && !readyForIntake)
                cargo.articulateCargoIntake(0);

            // Stow cargo intake mechanism
            if (rightJoyStick.getRawButton(STOW_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState())
                cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);

            // Stop stow once hte stow limit switch is hit
            if (!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && cargo.getCargoIntakeMechanismStowedSwitchState() && !readyForIntake)
                cargo.articulateCargoIntake(0);


            // Stop while no button is being pressed
            if (!rightJoyStick.getRawButton(DEPLOY_BTN_ID) && !leftJoyStick.getRawButton(STOW_BTN_ID) && !readyForIntake)
                cargo.articulateCargoIntake(0);

            // Intake cargo: deploy the intake, then spin the wheels
            if (rightJoyStick.getRawButton(INTAKE_BTN_ID)) {
                cargo.intakeCargo(CARGO_INTAKE_SPEED);
                cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
            }

            //Stop intake wheels,
            if (!rightJoyStick.getRawButton(INTAKE_BTN_ID) && !leftJoyStick.getRawButton(DEPLOY_BTN_ID) && !cargo.getCargoIntakeMechanismStowedSwitchState() && !readyForIntake) {
                cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
                cargo.intakeCargo(0);
            }

            /* ******************* */
            /* CARGO INTAKE VISION */
            /* ******************* */

            if (rightJoyStick.getRawButton(CARGO_AUTO_INTAKE_BTN_ID)) {
                System.out.format("Cargo Auto Count: %d%n", ++cargoAutoCount);
                if (cargoAutoCount <= CARGO_AUTO_COUNT_MAX) {
                    driveTrain.tankDrive(CARGO_AUTO_SPEED, -CARGO_AUTO_SPEED);
                } else if (vision.ballInFrame()) {
                    readyForIntake = true;
                }

                if (readyForIntake) {
                    cargo.intakeCargo(CARGO_INTAKE_SPEED);
                    cargo.deployCargoIntake(CARGO_MECHANISM_DEPLOY_SPEED);
                }

                if (!(cargoAutoCount <= CARGO_AUTO_COUNT_MAX)) {
                    driveTrain.tankDrive(0, 0);
                }
            } else if (rightJoyStick.getRawButtonReleased(INTAKE_BTN_ID)) {
                cargoAutoCount = 0;
                readyForIntake = false;
                driveTrain.tankDrive(0, 0);
                cargo.stowCargoIntake(CARGO_MECHANISM_STOW_SPEED);
                cargo.intakeCargo(0);
            }

            if (rightJoyStick.getRawButton(13)) {
                driveTrain.tankDrive(0, 0);
                cargoAutoCount = 0;
            }


            /* *********** */
            /* SHOOT CARGO */
            /* *********** */

            // Spin intake wheels as long as button is pressed
            if (rightJoyStick.getRawButton(DELIVER_BTN_ID))
                cargo.intakeCargo(CARGO_SHOOT_SPEED);

            engageShooterBelt = rightJoyStick.getRawButtonReleased(DELIVER_BTN_ID);

            // Engage shooting belt, once the button is released
            if (engageShooterBelt) {
                cargo.stowCargoIntake(-0.5);
                cargo.intakeCargo(0);
                if (shooterTimer <= 100) {
                    cargo.shootCargo(CARGO_SHOOT_SPEED);
                } else {
                    cargo.shootCargo(0);
                    engageShooterBelt = false;
                    shooterTimer = 0;
                }
                shooterTimer++;
            }

            if (!rightJoyStick.getRawButton(DELIVER_BTN_ID) && !engageShooterBelt) {
                //cargo.stowCargoIntake(0);
                cargo.shootCargo(0);
            }
        }

        // Climb controls just for a commit



    // Climb direction switch trigger & back button
    if(leftJoyStick.getRawButtonPressed(CLIMB_DIRECTION_EXTEND_BTN_ID))
        climbDirectionExtend = true;
    //Back button
    else if(leftJoyStick.getRawButtonPressed(CLIMB_DIRECTION_RETRACT_BTN_ID))
        climbDirectionExtend = false;



        // Climb action controls
        climbInitiated = !joyStickHatchMode && rightJoyStick.getRawButton(CLIMB_INITIATION_BTN);


        if (climbInitiated) {
            // If statement determining the direction of the climb
            if (climbDirectionExtend)
                climb.moveClimber(climb.sliderCorrection(leftJoyStick));
            else
                climb.moveClimber(-climb.sliderCorrection(leftJoyStick));
        }

        if (!joyStickHatchMode && rightJoyStick.getRawButton(STOP_CLIMB_BTN))
            climbInitiated = false;

        if (!climbInitiated) climb.moveClimber(0);
    }

    public void setDashboard() {
        SmartDashboard.putNumber("Hatch Deploy Speed", F_HATCH_DEPLOY_SPEED);
        SmartDashboard.putNumber("Hatch Stow Speed", F_HATCH_STOW_SPEED);
        SmartDashboard.putNumber("Hatch Intake Speed", F_HATCH_INTAKE_SPEED);
        SmartDashboard.putNumber("Hatch Deliver Speed", F_HATCH_DELIVER_SPEED);

        SmartDashboard.putNumber("Cargo Deploy Speed", F_CARGO_MECHANISM_DEPLOY_SPEED);
        SmartDashboard.putNumber("Cargo Stow Speed", F_CARGO_MECHANISM_STOW_SPEED);
        SmartDashboard.putNumber("Cargo Intake Speed", F_CARGO_INTAKE_SPEED);
        SmartDashboard.putNumber("Cargo Shoot Speed", F_CARGO_SHOOT_SPEED);

        SmartDashboard.putNumber("Shooter Belt Timer Max", F_SHOOTER_TIMER_MAX);

        SmartDashboard.putBoolean("Cargo Mode", joyStickCargoMode);
        SmartDashboard.putBoolean("Hatch Mode", joyStickHatchMode);


        SmartDashboard.putNumber("H Upper", vision.HSV_THRESHOLD_UPPER.val[0]);
        SmartDashboard.putNumber("S Upper", vision.HSV_THRESHOLD_UPPER.val[1]);
        SmartDashboard.putNumber("V Upper", vision.HSV_THRESHOLD_UPPER.val[2]);
        SmartDashboard.putNumber("H Lower", vision.HSV_THRESHOLD_LOWER.val[0]);
        SmartDashboard.putNumber("S Lower", vision.HSV_THRESHOLD_LOWER.val[1]);
        SmartDashboard.putNumber("V Lower", vision.HSV_THRESHOLD_LOWER.val[2]);

    }

    public void updateDashboard() {
        HATCH_DEPLOY_SPEED = SmartDashboard.getNumber("Hatch Deploy Speed", F_HATCH_DEPLOY_SPEED);
        HATCH_STOW_SPEED = SmartDashboard.getNumber("Hatch Stow Speed", F_HATCH_STOW_SPEED);
        HATCH_INTAKE_SPEED = SmartDashboard.getNumber("Hatch Intake Speed", F_HATCH_INTAKE_SPEED);
        HATCH_DELIVER_SPEED = SmartDashboard.getNumber("Hatch Deliver Speed", F_HATCH_DELIVER_SPEED);

        CARGO_MECHANISM_DEPLOY_SPEED = SmartDashboard.getNumber("Cargo Deploy Speed", F_CARGO_MECHANISM_DEPLOY_SPEED);
        CARGO_MECHANISM_STOW_SPEED = SmartDashboard.getNumber("Cargo Stow Speed", F_CARGO_MECHANISM_STOW_SPEED);
        CARGO_INTAKE_SPEED = SmartDashboard.getNumber("Cargo Intake Speed", F_CARGO_INTAKE_SPEED);
        CARGO_SHOOT_SPEED = SmartDashboard.getNumber("Cargo Shoot Speed", F_CARGO_SHOOT_SPEED);

        shooterTimerMaxValue = (int) SmartDashboard.getNumber("Shooter Belt Timer Max", F_SHOOTER_TIMER_MAX);

        vision.setColor(true, 1, SmartDashboard.getNumber("H Upper", 360));
        vision.setColor(true, 2, SmartDashboard.getNumber("S Upper", 255));
        vision.setColor(true, 3, SmartDashboard.getNumber("V Upper", 255));
        vision.setColor(false, 1, SmartDashboard.getNumber("H Lower", 0));
        vision.setColor(false, 2, SmartDashboard.getNumber("S Lower", 0));
        vision.setColor(false, 3, SmartDashboard.getNumber("V Lower", 0));
    }

    public void robotInit() {
        this.setDashboard();
    }

    public void robotPeriodic() {
        this.updateDashboard();
        //vision.outputFrame(vision.getCurrentFrame());
        //vision.processing();
        //vision.ballInFrame();

    }

    public void autonomousInit() {

    }

    public void autonomousPeriodic() {
        this.primusPeriodic();
    }

    public void teleopInit() {

    }

    public void teleopPeriodic() {
        this.primusPeriodic();
    }

    public void testPeriodic() {
        this.teleopPeriodic();
    }
}
