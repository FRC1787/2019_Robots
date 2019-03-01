package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;


public class Cargo 
{
    // Cargo motor controller variables
    private static final int CARGO_GRABBING_MOTOR_ID = 3;
    private static final int INTAKE_ARTICULATING_MOTOR_ID = 11;
    private static final int SHOOTER_MOTOR_ID = 8;

    // Construct cargo motor controller objects
    private final WPI_VictorSPX cargoGrabberMotor = new WPI_VictorSPX(CARGO_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX intakeArticulator = new WPI_VictorSPX(INTAKE_ARTICULATING_MOTOR_ID);
    private final WPI_VictorSPX shooter = new WPI_VictorSPX(SHOOTER_MOTOR_ID);

    // Cargo limit switch variables
    private final int CARGO_INTAKE_MECHANISM_DEPLOYED_LIMIT_SWITCH_CHANNEL = 4;
    private final int CARGO_INTAKE_MECHANISM_STOWED_LIMIT_SWITCH_CHANNEL = 3;

    // Construct cargo limit switch objects
    private final DigitalInput cargoIntakeMechanismDeployedSwitch = new DigitalInput(CARGO_INTAKE_MECHANISM_DEPLOYED_LIMIT_SWITCH_CHANNEL);
    private final DigitalInput cargoIntakeMechanismStowedSwitch = new DigitalInput(CARGO_INTAKE_MECHANISM_STOWED_LIMIT_SWITCH_CHANNEL);

    // Singleton instance
    private static final Cargo instance = new Cargo();

    // Default constructor
    public Cargo()
    {
        intakeArticulator.setNeutralMode(NeutralMode.Brake);
    }

    // Return method for the singleton instance
    public static Cargo getInstance()
    {
        return instance;
    }

    // Articulates the intake mechanism between deployed and stowed
    public void articulateCargoIntake(double articulationSpeed)
    {
        intakeArticulator.set(articulationSpeed);
    }
    
    // Intake cargo
    public void intakeCargo(double speed)
    {
        cargoGrabberMotor.set(speed);
    }

    // Shoot cargo
    public void shootCargo(double speed)
    {
        shooter.set(speed);
    }

    // Deploy cargo intake mechanism
    public void deployCargoIntake(double deploySpeed)
    {
        // Fold cargo intake out until limit switch is pressed
        if(!cargoIntakeMechanismDeployedSwitch.get())
        {
            articulateCargoIntake(deploySpeed);
        }


        else // Stop the motor once the limit switch is pressed
        {
            articulateCargoIntake(0);
        }
    }

    // Stow cargo intake mechanism
    public void stowCargoIntake(double stowSpeed)
    {
        // Fold cargo intake in until limit switch is pressed
        if(!cargoIntakeMechanismStowedSwitch.get())
        {
            articulateCargoIntake(stowSpeed);
        }

        // Stop the motor once the limit switch is pressed
        else
        {
            articulateCargoIntake(0);
        }
    }

    // Accessor methods for limit switch states
    public boolean getCargoIntakeMechanismDeployedSwitchState()
    {
        return cargoIntakeMechanismDeployedSwitch.get();
    }

    public boolean getCargoIntakeMechanismStowedSwitchState()
    {
        return cargoIntakeMechanismStowedSwitch.get();
    }
}
