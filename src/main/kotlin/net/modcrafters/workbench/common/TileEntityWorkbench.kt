package net.modcrafters.workbench.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.ItemStackHandler

class TileEntityWorkbench : TileEntity() {
    private inline fun createInventory(size: Int, crossinline onChange: ((s: Int) -> Unit)) = object: ItemStackHandler(size) {
        override fun onContentsChanged(slot: Int) {
            this@TileEntityWorkbench.markDirty()
            onChange(slot)
        }
    }

    val inputItems = createInventory(9, { this.testRecipe() })
    val outputItem = createInventory(1, { this.onRecipeOutputTaken() })
    val toolItem = createInventory(1, { this.testRecipe() })
    val outputItems = createInventory(4, {})
    val extraItems = createInventory(9, {})

    private fun testRecipe() {
        val crafting = InventoryCrafting(object : Container() {
            override fun canInteractWith(playerIn: EntityPlayer?) = true
        }, 3, 3)
        (0 until this.inputItems.slots).forEach {
            crafting.setInventorySlotContents(it, this.inputItems.getStackInSlot(it))
        }
        val recipe = CraftingManager.findMatchingRecipe(crafting, this.world)
        this.outputItem.setStackInSlot(0,recipe?.recipeOutput ?: ItemStack.EMPTY)
    }

    private fun onRecipeOutputTaken() {
        // TODO: apply recipe if slot is empty
//        if (this.outputItem.getStackInSlot(0).isEmpty) {
//            (0 until this.inputItems.slots).forEach {
//                val stack = this.inputItems.getStackInSlot(it)
//                if (!stack.isEmpty) stack.shrink(1)
//            }
//        }
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
}
