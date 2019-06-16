/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.lang.Math;

public class Gyro {

    

    // Singleton instance
    private static final Gyro instance = new Gyro();

    public Gyro() 
    {
    }
 
    // Return method for the singleton instance
    public static Gyro getInstance() {
    return instance;
    }

    public static double navXAngull()
    {
        if (Robot.navX.getYaw() > 0)
        return Robot.navX.getYaw();
        else
        return Robot.navX.getYaw() + 360;
    }
    
    
    //old setup
    public double thing()
    {
        if (Robot.navX.getYaw() < 0)
        return Robot.navX.getYaw();
        else
        return Robot.navX.getYaw() + 360;
    }
    public double navXAngle()
    {
        if (Robot.navX.getYaw() > 0 && thing() <= 180)
        return Robot.navX.getYaw();
        else if (thing() + 90 > 360)
        return thing() - 360;
        else
        return thing(); 
    } 
}
