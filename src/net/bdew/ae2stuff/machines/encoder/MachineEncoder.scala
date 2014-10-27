package net.bdew.ae2stuff.machines.encoder

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

object MachineEncoder extends Machine("Encoder", BlockEncoder) with GuiProvider {
  override def guiId = 1
  override type TEClass = TileEncoder

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiEncoder(new ContainerEncoder(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerEncoder(te, player)

  NetHandler.regServerHandler {
    case (MsgSetRecipe(tag), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerEncoder]).map { cont =>
        val recipe = (Misc.iterNbtCompoundList(tag, "recipe") map {
          x => x.getInteger("slot") -> ItemStack.loadItemStackFromNBT(x)
        }).toMap
        for ((slotNum, recIdx) <- cont.te.slots.recipe.zipWithIndex) {
          cont.te.setInventorySlotContents(slotNum, recipe.get(recIdx).orNull)
        }
        cont.updateRecipe()
      }
  }
}
