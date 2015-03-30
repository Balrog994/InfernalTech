package com.balrog.InfernalTech;

import com.balrog.InfernalTech.blocks.BlockEnergyAccumulator;
import com.balrog.InfernalTech.blocks.BlockEnergyChannel;
import com.balrog.InfernalTech.blocks.BlockMolecularSeparator;
import com.balrog.InfernalTech.energy.EnergyNetworkHandler;
import com.balrog.InfernalTech.materials.ItemCoalPowder;
import com.balrog.InfernalTech.network.PacketHandler;
import com.balrog.InfernalTech.network.PacketPowerStorage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;


@Mod(modid = InfernalTech.MODID, version = InfernalTech.VERSION, name = "Infernal Tech")
public class InfernalTech {
	public static final String MODID = "infernaltech";
    public static final String VERSION = "0.0.1";
    
    /**
     * Gets created by FML to specialize client vs. server calls.
     */
    @SidedProxy(
        clientSide = "com.balrog.InfernalTech.ClientProxy",
        serverSide = "com.balrog.InfernalTech.ServerProxy")
    public static CommonProxy proxy;
    
    @Instance(InfernalTech.MODID)
    public static InfernalTech instance = null;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	boolean isServerSide = event.getSide() == Side.SERVER;
    	
    	PacketHandler.INSTANCE.registerMessage(new PacketPowerStorage(), PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    	MinecraftForge.EVENT_BUS.register(EnergyNetworkHandler.instance);
        FMLCommonHandler.instance().bus().register(EnergyNetworkHandler.instance);
    	
    	BlockMolecularSeparator.init(proxy, isServerSide);
    	BlockEnergyAccumulator.init(proxy, isServerSide);
    	BlockEnergyChannel.init(proxy, isServerSide);
    	ItemCoalPowder.init(proxy, isServerSide);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
}
