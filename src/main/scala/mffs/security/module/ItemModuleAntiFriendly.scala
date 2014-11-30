package mffs.security.module

import java.util.Set

import mffs.ModularForceFieldSystem
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLivingBase, INpc}
import resonant.api.mffs.machine.IProjector
import resonant.lib.transform.vector.Vector3

class ItemModuleAntiFriendly extends ItemModuleDefense
{
  override def onProject(projector: IProjector, fields: Set[Vector3]): Boolean =
  {
    val entities = getEntitiesInField(projector)

    entities.view
      .filter(entity => entity.isInstanceOf[EntityLivingBase] && !(entity.isInstanceOf[IMob] && !entity.isInstanceOf[INpc]) && !entity.isInstanceOf[EntityPlayer])
      .map(_.asInstanceOf[EntityLivingBase])
      .foreach(entity =>
               {
                 entity.setHealth(1)
                 entity.attackEntityFrom(ModularForceFieldSystem.damageFieldShock, 100)
               })

    return false
  }
}