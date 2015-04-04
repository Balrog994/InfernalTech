package com.balrog.InfernalTech.world;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.*;

import java.util.Random;

import com.balrog.InfernalTech.blocks.BlockCustomOre;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InfernalTechWorldGenHandler {
	private WorldGenMinableCustom copperGen;
	private WorldGenMinableCustom leadGen;
	private WorldGenMinableCustom nickelGen;
	private WorldGenMinableCustom silverGen;
	private WorldGenMinableCustom tinGen;
	private ChunkProviderSettings chunkProviderSettings;
	
	private static int copperSize = 9;
	private static int copperCount = 20;
	private static int copperMinHeight = 40;
	private static int copperMaxHeight = 75;
	
	private static int leadSize = 9;
	private static int leadCount = 2;
	private static int leadMinHeight = 10;
	private static int leadMaxHeight = 35;
	
	private static int nickelSize = 8;
	private static int nickelCount = 2;
	private static int nickelMinHeight = 5;
	private static int nickelMaxHeight = 20;
	
	private static int silverSize = 9;
	private static int silverCount = 2;
	private static int silverMinHeight = 5;
	private static int silverMaxHeight = 30;
	
	private static int tinSize = 9;
	private static int tinCount = 20;
	private static int tinMinHeight = 20;
	private static int tinMaxHeight = 55;
	
	public static final InfernalTechWorldGenHandler instance = new InfernalTechWorldGenHandler();
	
	private InfernalTechWorldGenHandler() {
		this.chunkProviderSettings = ChunkProviderSettings.Factory.func_177865_a("").func_177864_b();
		this.copperGen = new WorldGenMinableCustom(BlockCustomOre.copper_ore.getDefaultState(), this.copperSize);
		this.leadGen = new WorldGenMinableCustom(BlockCustomOre.lead_ore.getDefaultState(), this.leadSize);
		this.nickelGen = new WorldGenMinableCustom(BlockCustomOre.nickel_ore.getDefaultState(), this.nickelSize);
		this.silverGen = new WorldGenMinableCustom(BlockCustomOre.silver_ore.getDefaultState(), this.silverSize);
		this.tinGen = new WorldGenMinableCustom(BlockCustomOre.tin_ore.getDefaultState(), this.tinSize);
	}
	
	@SubscribeEvent
	public void postOreGen(OreGenEvent.Post event) {
		if (TerrainGen.generateOre(event.world, event.rand, this.copperGen, event.pos, CUSTOM))
	        this.genStandardOre(event.rand, event.pos, event.world, this.copperCount, this.copperGen, this.copperMinHeight, this.copperMaxHeight);
		if (TerrainGen.generateOre(event.world, event.rand, this.leadGen, event.pos, CUSTOM))
	        this.genStandardOre(event.rand, event.pos, event.world, this.leadCount, this.leadGen, this.leadMinHeight, this.leadMaxHeight);
		if (TerrainGen.generateOre(event.world, event.rand, this.nickelGen, event.pos, CUSTOM))
	        this.genStandardOre(event.rand, event.pos, event.world, this.nickelCount, this.nickelGen, this.nickelMinHeight, this.nickelMaxHeight);
		if (TerrainGen.generateOre(event.world, event.rand, this.silverGen, event.pos, CUSTOM))
	        this.genStandardOre(event.rand, event.pos, event.world, this.silverCount, this.silverGen, this.silverMinHeight, this.silverMaxHeight);
		if (TerrainGen.generateOre(event.world, event.rand, this.tinGen, event.pos, CUSTOM))
	        this.genStandardOre(event.rand, event.pos, event.world, this.tinCount, this.tinGen, this.tinMinHeight, this.tinMaxHeight);
	}
	
	private void genStandardOre(Random rand, BlockPos pos, World world, int count, WorldGenerator generator, int minHeight, int maxHeight) {
		int l;

        if (maxHeight < minHeight)
        {
            l = minHeight;
            minHeight = maxHeight;
            maxHeight = l;
        }
        else if (maxHeight == minHeight)
        {
            if (minHeight < 255)
            {
                ++maxHeight;
            }
            else
            {
                --minHeight;
            }
        }

        for (l = 0; l < count; ++l)
        {
            BlockPos blockpos = pos.add(rand.nextInt(16), rand.nextInt(maxHeight - minHeight) + minHeight, rand.nextInt(16));
            generator.generate(world, rand, blockpos);
        }
	}
}
