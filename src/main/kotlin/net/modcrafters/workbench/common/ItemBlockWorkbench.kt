package net.modcrafters.workbench.common

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.modcrafters.workbench.Workbench
import java.util.*

class ItemBlockWorkbench(block: Block) : ItemBlock(block) {


    override fun placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
        if (!world.setBlockState(pos, newState, 11)) return false

        val state = world.getBlockState(pos)
        if (state.block === this.block) {
            ItemBlock.setTileEntityNBT(world, player, pos, stack)
            this.block.onBlockPlacedBy(world, pos, state, player, stack)

            if (player is EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, stack)
        }

        return true
    }

    init {
        setHasSubtypes(true)
        maxDamage = 0
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(itemIn: CreativeTabs, list: NonNullList<ItemStack>) {
        if (itemIn === Workbench.BENCH_TAB) {
            for (i in 0..15) {
                if (!Arrays.asList(0, 7, 8, 15).contains(i)) {
                    list.add(ItemStack(this, 1, i))
                }
            }
        }
    }
}
