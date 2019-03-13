package frc.robot;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public final class PDP
{
    PowerDistributionPanel pdp = new PowerDistributionPanel(0);

    public double getPortAmps(int channel)
    {
        return pdp.getCurrent(channel);
    }

    
}