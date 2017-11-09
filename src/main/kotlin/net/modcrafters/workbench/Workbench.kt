package net.modcrafters.workbench

import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.ResourceLocation
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.modcrafters.workbench.common.BlockWorkbench

import net.modcrafters.workbench.common.GuiHandler
import net.modcrafters.workbench.common.ItemBlockWorkbench
import net.modcrafters.workbench.common.TileEntityWorkbench
import org.apache.logging.log4j.Logger

@Mod(modid = Workbench.MODID, version = Workbench.VERSION,useMetadata = true,name = Workbench.NAME,dependencies ="required-after:forgelin@[1.5.1,)", modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")

object Workbench{

     const val MODID = "workbench"
     const val NAME = "Workbench"
     const val VERSION = "1.0"

    @SidedProxy(clientSide = "net.modcrafters.workbench.client.ClientProxy", serverSide = "net.modcrafters.workbench.server.ServerProxy")

    lateinit var logger: Logger

	var BlockBench: BlockWorkbench = BlockWorkbench()


    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {

        logger.error("Hello from Kotlin!")
        addBlock(BlockBench,"workbench")

        if (event.side == Side.CLIENT) {
            for( i in 0..15){
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockBench), i,
                        ModelResourceLocation(ResourceLocation(MODID, "workbench"), "color="+ EnumDyeColor.byMetadata(i).getName()))
            }
        }


        NetworkRegistry.INSTANCE.registerGuiHandler(Workbench, GuiHandler())

        GameRegistry.registerTileEntity(TileEntityWorkbench::class.java,"workbench:workbench") //TileEntityWorkbench.class,"workbench:workbench")
    }




	private fun addBlock(block : Block, name : String): Block {
		block.setRegistryName(name)
		block.unlocalizedName = block.registryName?.resourceDomain + "." + name
		ForgeRegistries.BLOCKS.register(block)
		val itemBlock = ItemBlockWorkbench(block)
		itemBlock.setRegistryName(name)
		itemBlock.unlocalizedName = block.registryName?.resourceDomain + "." + name
		ForgeRegistries.ITEMS.register(itemBlock)

		block.setCreativeTab(BenchTab)
		return block
	}

     val BenchTab: CreativeTabs = object : CreativeTabs(MODID) {
		override fun getTabIconItem() = ItemStack(ForgeRegistries.BLOCKS.getValue(ResourceLocation("workbench:workbench")))
	}

}
