package net.modcrafters.workbench.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class ContainerWorkbench(invPlayer: InventoryPlayer, // Stores a reference to the tile entity instance for later use
                         private val tileEntityWorkbench: TileEntityWorkbench) : Container() {

    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory secondary ingredient slots, which map to our TileEntity slot numbers 0 - 8)
    //  36 - 44 = TileInventory secondary ingredient slots, which map to our TileEntity slot numbers 0 - 8)

    private val HOTBAR_SLOT_COUNT = 9
    private val PLAYER_INVENTORY_ROW_COUNT = 3
    private val PLAYER_INVENTORY_COLUMN_COUNT = 9
    private val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
    private val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT

    private val VANILLA_FIRST_SLOT_INDEX = 0
    private val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT
    private val TE_INVENTORY_SLOT_COUNT = 25

    init {

        val SLOT_X_SPACING = 18
        val SLOT_Y_SPACING = 18
        val HOTBAR_XPOS = 8
        val HOTBAR_YPOS = 173
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (x in 0 until HOTBAR_SLOT_COUNT) {
            addSlotToContainer(Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS))
        }

        val PLAYER_INVENTORY_XPOS = 8
        val PLAYER_INVENTORY_YPOS = 115
        // Add the rest of the players inventory to the gui
        for (y in 0 until PLAYER_INVENTORY_ROW_COUNT) {
            for (x in 0 until PLAYER_INVENTORY_COLUMN_COUNT) {
                val slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x
                val xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING
                val ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING
                addSlotToContainer(Slot(invPlayer, slotNumber, xpos, ypos))
            }
        }

        if (TE_INVENTORY_SLOT_COUNT != tileEntityWorkbench.getSizeInventory()) {
            System.err.println("Mismatched slot count in ContainerWorkbench(" + TE_INVENTORY_SLOT_COUNT +
                    ") and TileInventory (" + tileEntityWorkbench.getSizeInventory() + ")")
        }

        for (x in 0 until TE_INVENTORY_SLOT_COUNT) {
// Add the secondary ingredients to the gui
            if (x < 9) {
                addSlotToContainer(Slot(tileEntityWorkbench, x, 8 + SLOT_X_SPACING * x, 84))
            } else
            // Add the crafting grid to the gui
                if (x < 18) {
                    addSlotToContainer(Slot(tileEntityWorkbench, x, 17 + SLOT_X_SPACING * ((x - 9) % 3), 17 + SLOT_Y_SPACING * ((x - 9) / 3)))
                } else
                // Add the output grid to the gui
                    if (x < 24) {
                        //System.out.println(((x-18)%3));
                        addSlotToContainer(SlotOutput(tileEntityWorkbench, x, 125 + SLOT_X_SPACING * ((x - 18) % 2), 17 + SLOT_Y_SPACING * ((x - 18) % 3)))
                    } else
                    // Add the tool slot to the gui
                        if (x == 24) {
                            addSlotToContainer(Slot(tileEntityWorkbench, x, 89, 45))
                        }
        }
    }


    // Vanilla calls this method every tick to make sure the player is still able to access the inventory, and if not closes the gui
    override fun canInteractWith(player: EntityPlayer): Boolean {
        return tileEntityWorkbench.isUsableByPlayer(player)
    }

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory)
    // At the very least you must override this and return EMPTY_ITEM or the game will crash when the player shift clicks a slot
    // returns EMPTY_ITEM if the source slot is empty, or if none of the the source slot items could be moved
    //   otherwise, returns a copy of the source stack
    override fun transferStackInSlot(player: EntityPlayer?, sourceSlotIndex: Int): ItemStack {
        val sourceSlot = inventorySlots[sourceSlotIndex] as Slot
        if ( !sourceSlot.hasStack) return ItemStack.EMPTY  //EMPTY_ITEM
        val sourceStack = sourceSlot.stack
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + 9, false)) {
                return ItemStack.EMPTY  // EMPTY_ITEM
            }
        } else if (sourceSlotIndex >= TE_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY   // EMPTY_ITEM
            }
        } else {
            System.err.print("Invalid slotIndex:" + sourceSlotIndex)
            return ItemStack.EMPTY   // EMPTY_ITEM
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {  // getStackSize
            sourceSlot.putStack(ItemStack.EMPTY)  // EMPTY_ITEM
        } else {
            sourceSlot.onSlotChanged()
        }

        sourceSlot.onTake(player, sourceStack)  //onPickupFromSlot()
        return copyOfSourceStack
    }

    // SlotOutput is a slot that will not accept any items
    inner class SlotOutput(inventoryIn: IInventory, index: Int, xPosition: Int, yPosition: Int) : Slot(inventoryIn, index, xPosition, yPosition) {

        // if this function returns false, the player won't be able to insert the given item into this slot
        override fun isItemValid(stack: ItemStack?): Boolean {
            return tileEntityWorkbench.isItemValidForOutputSlot(stack!!)
        }
    }

    // SlotTool is a slot for tool items
    inner class SlotTool(inventoryIn: IInventory, index: Int, xPosition: Int, yPosition: Int) : Slot(inventoryIn, index, xPosition, yPosition) {

        // if this function returns false, the player won't be able to insert the given item into this slot
        override fun isItemValid(stack: ItemStack?): Boolean {
            return tileEntityWorkbench.isItemValidForToolSlot(stack!!)
        }
    }

    // pass the close container message to the tileEntityInventory (not strictly needed for this example)
    //  see ContainerChest and TileEntityChest
    override fun onContainerClosed(playerIn: EntityPlayer) {
        super.onContainerClosed(playerIn)
        this.tileEntityWorkbench.closeInventory(playerIn)
    }

}
