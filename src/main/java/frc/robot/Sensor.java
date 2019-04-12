/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogOutput;

public final class Sensor {

//Sensor Channel Objects
    //private DigitalOutput Sensor_Output_Channel = new DigitalOutput(6);
    //private DigitalInput Sensor_Input_Channel = new DigitalInput(6);
    private static final AnalogInput succitvan = new AnalogInput(0);

//Sensor Setup
    private static final Sensor instance = new Sensor();
    //private final Ultrasonic ok = new Ultrasonic(0,0);
    //public double distance = ok.getRangeInches();

    public Sensor()
    {
      //ok.setAutomaticMode(true);
    } 

    public static Sensor getInstance()
    {
        return instance;
    }

//Sensor Methods
    
public double SensorRaw = succitvan.getVoltage();

public double getSense()
    {
       return succitvan.getVoltage()*40;
    }

    


    

   /* public double sensorValue()
    {
        return distance;
    } */
}
