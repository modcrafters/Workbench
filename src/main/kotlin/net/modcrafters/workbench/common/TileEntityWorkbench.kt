package net.modcrafters.workbench.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.ItemHandlerHelper.insertItem




class TileEntityWorkbench : TileEntity(), ITickable {
    override fun update() {
        tryCraft()
    }

    private inline fun createInventory(size: Int, crossinline onChange: ((s: Int) -> Unit)) = object: ItemStackHandler(size) {
        override fun onContentsChanged(slot: Int) {
            this@TileEntityWorkbench.sync()
            onChange(slot)
        }
    }

    val inputItems = createInventory(9, {})
    val outputItem = createInventory(1, {})
    val toolItem = createInventory(1, {})
    val outputItems = createInventory(4, {})
    val extraItems = createInventory(9, {})

    private fun sync() {
        this.markDirty()
        if (!this.world.isRemote) {
            val cp = this.world.getChunkFromBlockCoords(getPos()).pos
            val entry = (this.world as WorldServer).playerChunkMap.getEntry(cp.x, cp.z)
            entry?.sendPacket(this.updatePacket!!)
        }
    }


    private fun tryCraft(): Boolean {

        val crafting = InventoryCrafting(object : Container() {
            override fun canInteractWith(playerIn: EntityPlayer?) = true
        }, 3, 3)
        (0 until this.inputItems.slots).forEach {
            crafting.setInventorySlotContents(it, this.inputItems.getStackInSlot(it))
        }
        val recipe = CraftingManager.findMatchingRecipe(crafting, this.world)
        this.outputItem.setStackInSlot(0,recipe?.recipeOutput ?: ItemStack.EMPTY)
        if(recipe != null){
            return true
        }
        return false

    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        arrayOf(
            this.inputItems to "inv_input",
            this.toolItem to "inv_tool",
            this.outputItem to "inv_output",
            this.outputItems to "inv_outputs",
            this.extraItems to "inv_extra"
        ).forEach {
            if (compound.hasKey(it.second, Constants.NBT.TAG_COMPOUND)) {
                it.first.deserializeNBT(compound.getCompoundTag(it.second))
            }
            else {
                (0 until it.first.slots).forEach { slot -> it.first.setStackInSlot(slot, ItemStack.EMPTY) }
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound {
        val nbt = super.writeToNBT(compound ?: NBTTagCompound())

        arrayOf(
            this.inputItems to "inv_input",
            this.toolItem to "inv_tool",
            this.outputItem to "inv_output",
            this.outputItems to "inv_outputs",
            this.extraItems to "inv_extra"
        ).forEach {
            nbt.setTag(it.second, it.first.serializeNBT())
        }

        return nbt
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        return SPacketUpdateTileEntity(this.pos, 42, this.updateTag)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        this.handleUpdateTag(pkt.nbtCompound)
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        super.handleUpdateTag(tag)
    }
}
