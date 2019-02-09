package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Cargo 
{

    private static final int CARGO_GRABBING_MOTOR_ID = 3;
    private static final int INTAKE_ARTICULATING_MOTOR_ID = 11;
    private static final int SHOOTER_MOTOR_ID = 6;

    private final WPI_TalonSRX cargoGrabberMotor = new WPI_TalonSRX(CARGO_GRABBING_MOTOR_ID);
    private final WPI_VictorSPX intakeArticulator = new WPI_VictorSPX(INTAKE_ARTICULATING_MOTOR_ID);
    private final WPI_TalonSRX shooter = new WPI_TalonSRX(SHOOTER_MOTOR_ID);

    private static final Cargo instance = new Cargo();


    
    public static Cargo getInstance()
    {
        return instance;
    }
    
    public void cargoIntake(double speed)
    {
        cargoGrabberMotor.set(speed);
    }

    public void cargoGrabber(double speed)
    {
        intakeArticulator.set(speed);
    }

    public void cargoShooter(double speed)
    {
        shooter.set(speed);
    }
}
