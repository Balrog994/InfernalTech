package com.balrog.InfernalTech.world;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.*;

import java.util.Random;

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
	public static final InfernalTechWorldGenHandler instance = new InfernalTechWorldGenHandler();
	private WorldGenMinable copperGen;
	private ChunkProviderSettings chunkProviderSettings;
	
	private InfernalTechWorldGenHandler() {
		this.chunkProviderSettings = ChunkProviderSettings.Factory.func_177865_a("").func_177864_b();
		this.copperGen = new WorldGenMinable(Blocks.diamond_block.getDefaultState(), this.chunkProviderSettings.ironSize);
	}
	
	@SubscribeEvent
	public void postOreGen(OreGenEvent.Post event) {
		if (TerrainGen.generateOre(event.world, event.rand, this.copperGen, event.pos, CUSTOM))
	        this.genStandardOre(event.rand, event.pos, event.world, this.chunkProviderSettings.ironCount, this.copperGen, this.chunkProviderSettings.ironMinHeight, this.chunkProviderSettings.ironMaxHeight);
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
