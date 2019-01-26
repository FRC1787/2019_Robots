package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Cargo 
{

    private final int CARGO_GRABBING_MOTOR_ID = 5;
    private final int INTAKE_ARTICULATING_MOTOR_ID = 6;
    private final int SHOOTER_MOTOR_ID = 7;

    private WPI_TalonSRX cargoGrabber = new WPI_TalonSRX(CARGO_GRABBING_MOTOR_ID);
    private WPI_VictorSPX articulatingIntake = new WPI_VictorSPX(INTAKE_ARTICULATING_MOTOR_ID);
    private WPI_TalonSRX shooter = new WPI_TalonSRX(SHOOTER_MOTOR_ID);

    private static final Cargo instance = new Cargo();


    
    public static Cargo getInstance()
    {
        return instance;
    }
}
