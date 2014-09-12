package resonantinduction.mechanical.turbine;

import resonantinduction.mechanical.energy.grid.MechanicalNode;
import net.minecraftforge.common.util.ForgeDirection;
import resonantinduction.core.interfaces.IMechanicalNode;

/**
 * Turbine's Mechanical node
 * Turbines always face forward and connect from behind.
 *
 * @author Calclavia, Darkguardsman
 */
public class TurbineNode extends MechanicalNode
{
	public TurbineNode(TileTurbine tileTurbineBase)
	{
		super(tileTurbineBase);
	}

	public TileTurbine turbine()
	{
		return (TileTurbine) getParent();
	}

	@Override
	public boolean canConnect(ForgeDirection from, Object source)
	{
		return turbine().getMultiBlock().isPrimary() && source instanceof MechanicalNode && !(source instanceof TurbineNode) && from == turbine().getDirection();
	}

	@Override
	public boolean inverseRotation(ForgeDirection dir)
	{
		return dir == turbine().getDirection().getOpposite();
	}
}