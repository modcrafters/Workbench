package net.modcrafters.workbench.common

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import java.util.*
import kotlin.experimental.and


class TileEntityWorkbench_Old : TileEntity(), IInventory, ITickable {

    // Create and initialize the items variable that will store store the items
    private val NUMBER_OF_SLOTS = 25
    private val itemStacks = arrayOfNulls<ItemStack>(NUMBER_OF_SLOTS)

    init {
        clear()
    }

    /* The following are some IInventory methods you are required to override */

    // Gets the number of slots in the inventory
    override fun getSizeInventory(): Int {
        return itemStacks.size
    }

    // returns true if all of the slots in the inventory are empty
    override fun isEmpty(): Boolean {
        for (itemstack in itemStacks) {
            if (itemstack!!.isEmpty) {  // isEmpty()
                return false
            }
        }

        return true
    }

    // Gets the stack in the given slot
    override fun getStackInSlot(slotIndex: Int): ItemStack {
        return itemStacks[slotIndex]!!
    }

    /**
     * Removes some of the units from itemstack in the given slot, and returns as a separate itemstack
     * @param slotIndex the slot number to remove the items from
     * @param count the number of units to remove
     * @return a new itemstack containing the units removed from the slot
     */
    override fun decrStackSize(slotIndex: Int, count: Int): ItemStack {
        val itemStackInSlot = getStackInSlot(slotIndex)
        if (itemStackInSlot.isEmpty) return ItemStack.EMPTY  // isEmpt();   EMPTY_ITEM

        val itemStackRemoved: ItemStack
        if (itemStackInSlot.count <= count) {  // getStackSize()
            itemStackRemoved = itemStackInSlot
            setInventorySlotContents(slotIndex, ItemStack.EMPTY)   // EMPTY_ITEM
        } else {
            itemStackRemoved = itemStackInSlot.splitStack(count)
            if (itemStackInSlot.count == 0) { // getStackSize
                setInventorySlotContents(slotIndex, ItemStack.EMPTY)   // EMPTY_ITEM
            }
        }
        markDirty()
        return itemStackRemoved
    }

    // overwrites the stack in the given slotIndex with the given stack
    override fun setInventorySlotContents(slotIndex: Int, itemstack: ItemStack) {
        itemStacks[slotIndex] = itemstack
        if (itemstack.isEmpty && itemstack.count > inventoryStackLimit) { //  isEmpty(); getStackSize()
            itemstack.count = inventoryStackLimit  //setStackSize
        }

        if (slotIndex in (9..17)) {
            val crafting = InventoryCrafting(object : Container() {
                override fun canInteractWith(playerIn: EntityPlayer?) = true
            }, 3, 3)
            (9 until 17).forEach { crafting.setInventorySlotContents(it - 9, this.itemStacks[it] ?: ItemStack.EMPTY) }
            val recipe = CraftingManager.findMatchingRecipe(crafting, this.world)
            this.itemStacks[18] = recipe?.recipeOutput ?: ItemStack.EMPTY
            this.markDirty()
        }

        markDirty()
    }

    // This is the maximum number if items allowed in each slot
    // This only affects things such as hoppers trying to insert items you need to use the container to enforce this for players
    // inserting items via the gui
    override fun getInventoryStackLimit(): Int {
        return 64
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        if (this.world.getTileEntity(this.pos) !== this) return false
        val X_CENTRE_OFFSET = 0.5
        val Y_CENTRE_OFFSET = 0.5
        val Z_CENTRE_OFFSET = 0.5
        val MAXIMUM_DISTANCE_SQ = 8.0 * 8.0
        return player.getDistanceSq(pos.x + X_CENTRE_OFFSET, pos.y + Y_CENTRE_OFFSET, pos.z + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ
    }

    // Return true if the given stack is allowed to go in the given slot.  In this case, we can insert anything.
    // This only affects things such as hoppers trying to insert items you need to use the container to enforce this for players
    // inserting items via the gui
    override fun isItemValidForSlot(slotIndex: Int, itemstack: ItemStack): Boolean {
        return true
    }

    // This is where you save any data that you don't want to lose when the tile entity unloads
    // In this case, it saves the itemstacks stored in the container
    override fun writeToNBT(parentNBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(parentNBTTagCompound) // The super call is required to save and load the tileEntity's location

        // to use an analogy with Java, this code generates an array of hashmaps
        // The itemStack in each slot is converted to an NBTTagCompound, which is effectively a hashmap of key->value pairs such
        //   as slot=1, id=2353, count=1, etc
        // Each of these NBTTagCompound are then inserted into NBTTagList, which is similar to an array.
        val dataForAllSlots = NBTTagList()
        for (i in this.itemStacks.indices) {
            if (!this.itemStacks[i]!!.isEmpty) { //isEmpty()
                val dataForThisSlot = NBTTagCompound()
                dataForThisSlot.setByte("Slot", i.toByte())
                this.itemStacks[i]!!.writeToNBT(dataForThisSlot)
                dataForAllSlots.appendTag(dataForThisSlot)
            }
        }
        // the array of hashmaps is then inserted into the parent hashmap for the container
        parentNBTTagCompound.setTag("Items", dataForAllSlots)
        // return the NBT Tag Compound
        return parentNBTTagCompound
    }

    // This is where you load the data that you saved in writeToNBT
    override fun readFromNBT(parentNBTTagCompound: NBTTagCompound) {
        super.readFromNBT(parentNBTTagCompound) // The super call is required to save and load the tiles location
        val NBT_TYPE_COMPOUND: Byte = 10       // See NBTBase.createNewByType() for a listing
        val dataForAllSlots = parentNBTTagCompound.getTagList("Items", NBT_TYPE_COMPOUND.toInt())

        Arrays.fill(itemStacks, ItemStack.EMPTY)           // set all slots to empty EMPTY_ITEM
        for (i in 0 until dataForAllSlots.tagCount()) {
            val dataForOneSlot = dataForAllSlots.getCompoundTagAt(i)
            val slotIndex = dataForOneSlot.getByte("Slot") and 255.toByte()

            if (slotIndex >= 0 && slotIndex < this.itemStacks.size) {
                this.itemStacks[slotIndex.toInt()] = ItemStack(dataForOneSlot)
            }
        }
    }

    override fun update() {
        //CraftingManager.findMatchingResult(craftMatrix, worldIn)
        //if (canCraft()) {
        //	craftItem(true);
        //}
    }

    //private boolean canCraft() {return craftItem(false);}

    /*public ItemStack getCraftingResultForItems() {
			//ForgeRegistries.RECIPES
			InventoryCrafting craftMatrix = new InventoryCrafting(null, 3, 3);

			for(int i =0; i<9; i++){
				craftMatrix.setInventorySlotContents(i, itemStacks[i+9]);
			}

			ItemStack out = CraftingManager.findMatchingRecipe(craftMatrix, this.getWorld()).getRecipeOutput();
			return out;
		}

		private boolean craftItem(boolean performSmelt)
		{
			Integer firstSuitableInputSlot = null;
			Integer firstSuitableOutputSlot = null;
			ItemStack result = ItemStack.EMPTY;

			boolean flag = false;
			for (int inputSlot = 0; inputSlot < 25; inputSlot++)	{
				if (!itemStacks[inputSlot].isEmpty()) {
					flag = true;
					break;
				}
			}
			if(!flag){
				return false;
			}
			result = getCraftingResultForItems();


			if (!performSmelt) return true;

			// alter input and output
			//itemStacks[firstSuitableInputSlot].shrink(1);

			if (itemStacks[18].isEmpty()) {
	      itemStacks[18] = ItemStack.EMPTY;
	    }
			if (itemStacks[18].isEmpty()) {
				itemStacks[18] = result.copy(); // Use deep .copy() to avoid altering the recipe
			} else {
				int newStackSize = itemStacks[18].getCount() + result.getCount();
				itemStacks[18].setCount(newStackSize) ;  //setStackSize(), getStackSize()
			}
			markDirty();
			return true;
		}*/


    // set all slots to empty
    override fun clear() {
        Arrays.fill(itemStacks, ItemStack.EMPTY)  //empty item
    }

    // will add a key for this container to the lang file so we can name it in the GUI
    override fun getName(): String {
        return "container.workbench.name"
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState, newState: IBlockState): Boolean {
        return if (oldState.block == newState.block) {
            false
        } else super.shouldRefresh(world, pos, oldState, newState)
    }

    // standard code to look up what the human-readable name is
    override fun getDisplayName(): ITextComponent? {
        return if (this.hasCustomName()) TextComponentString(this.name) else TextComponentTranslation(this.name)
    }


    override fun removeStackFromSlot(slotIndex: Int): ItemStack {
        val itemStack = getStackInSlot(slotIndex)
        if (!itemStack.isEmpty) setInventorySlotContents(slotIndex, ItemStack.EMPTY)  //isEmpty(), EMPTY_ITEM
        return itemStack
    }

    override fun openInventory(player: EntityPlayer) {}

    override fun closeInventory(player: EntityPlayer) {}

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {}

    override fun getFieldCount(): Int {
        return 0
    }

    fun isItemValidForToolSlot(stack: ItemStack): Boolean {
        return true
    }

    fun isItemValidForOutputSlot(itemStack: ItemStack): Boolean {
        return false
    }
}
