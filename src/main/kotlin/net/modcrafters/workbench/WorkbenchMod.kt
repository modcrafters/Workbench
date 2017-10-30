package net.modcrafters.workbench

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.ndrei.teslacorelib.config.ModConfigHandler
import org.apache.logging.log4j.Logger

@Mod(modid = MOD_ID, version = MOD_VERSION, name = MOD_NAME,
    dependencies = MOD_DEPENDENCIES, acceptedMinecraftVersions = MOD_MC_VERSION,
    useMetadata = true, modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object WorkbenchMod {
    @SidedProxy(clientSide = "net.modcrafters.workbench.ClientProxy", serverSide = "net.modcrafters.workbench.ServerProxy")
    lateinit var proxy: CommonProxy
    lateinit var logger: Logger

    lateinit var config: ModConfigHandler

    val creativeTab: CreativeTabs = object : CreativeTabs(MOD_NAME) {
        override fun getTabIconItem() = ItemStack(Blocks.WOOL)
    }

    @Mod.EventHandler
    fun construction(event: FMLConstructionEvent) {
//        arrayOf(
//            TeslaCoreLibConfig.REGISTER_GEARS,
//            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.WOOD.material}",
//            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.STONE.material}",
//            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.IRON.material}"
//        ).forEach {
//            TeslaCoreLibConfig.setDefaultFlag(it, true)
//        }

        this.proxy.construction(event)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        this.logger = event.modLog
        this.config = ModConfigHandler(MOD_ID, this.javaClass, logger, event.modConfigurationDirectory)

        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        proxy.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        proxy.postInit(e)
    }
}
