package net.modcrafters.workbench.common

import java.util.Arrays

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary
import net.modcrafters.workbench.Workbench

class BlockWorkbench : Block(Material.ROCK, MapColor.STONE) {
    init {
        this.soundType = SoundType.STONE
        this.defaultState = this.blockState.baseState.withProperty(COLOR, EnumDyeColor.YELLOW)
    }

    override fun hasTileEntity(state: IBlockState?): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntityWorkbench? {
        return TileEntityWorkbench()
    }

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn!!.isRemote || hand == EnumHand.OFF_HAND) {
            return true
        }

        val heldItem = playerIn!!.getHeldItem(hand)
        if (!heldItem.isEmpty) {
            if (heldItem.item === Items.DYE) {
                worldIn.setBlockState(pos!!, state!!.withProperty(COLOR, EnumDyeColor.byDyeDamage(heldItem.metadata)))
                return false
            } else {
                for (i in 0..15) {
                    if (!Arrays.asList(0, 7, 8, 15).contains(i)) {
                        continue
                    }

                    if (OreDictionary.getOres("dye${EnumDyeColor.byMetadata(i).unlocalizedName}").contains(heldItem)) {
                        worldIn.setBlockState(pos!!, state!!.withProperty(COLOR, EnumDyeColor.byMetadata(i)))
                        return false
                    }
                }
            }
        }
        playerIn.openGui(Workbench, 0, worldIn, pos!!.x, pos.y, pos.z)
        playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION)
        return true
    }

    /**
     * Called server side after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {

        val tileEntity = worldIn.getTileEntity(pos)



        if (tileEntity is IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (tileEntity as IInventory?)!!)
        }
        // Super MUST be called last because it removes the tile entity
        super.breakBlock(worldIn, pos, state)
    }


    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack {
        return ItemStack(this, 1, state.getValue(COLOR).metadata)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(COLOR, EnumDyeColor.byMetadata(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(COLOR).metadata
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, COLOR)
    }

    companion object {
        val COLOR = PropertyEnum.create("color", EnumDyeColor::class.java)
    }

}

