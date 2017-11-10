package net.modcrafters.workbench.common

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.modcrafters.workbench.Workbench

open class CommonProxy {
    open fun registerMessageHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Workbench, GuiHandler())
    }
}
