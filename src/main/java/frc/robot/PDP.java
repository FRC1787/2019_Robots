package frc.robot;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public final class PDP
{
    PowerDistributionPanel pdp = new PowerDistributionPanel(0);

    private static final PDP instance = new PDP();

    public static PDP getInstance ()
    {
        return instance; 
    }

    public double getPortAmps(int channel)
    {
        return pdp.getCurrent(channel);
    }

    
}