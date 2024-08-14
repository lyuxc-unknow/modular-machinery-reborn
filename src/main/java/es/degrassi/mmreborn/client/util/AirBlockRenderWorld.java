package es.degrassi.mmreborn.client.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class AirBlockRenderWorld implements BlockGetter {
  private final Biome globalBiome;
  private final ResourceKey<Level> globalType;

  public AirBlockRenderWorld(Biome globalBiome, ResourceKey<Level> globalType) {
    this.globalBiome = globalBiome;
    this.globalType = globalType;
  }

  @Nullable
  public BlockEntity getBlockEntity(BlockPos pos) {
    return null;
  }

  public int getCombinedLight(BlockPos pos, int lightValue) {
    return 0;
  }

  public BlockState getBlockState(BlockPos pos) {
    return Blocks.AIR.defaultBlockState();
  }

  @Override
  public FluidState getFluidState(BlockPos blockPos) {
    return null;
  }

  public boolean isAirBlock(BlockPos pos) {
    return true;
  }

  public Biome getBiome(BlockPos pos) {
    return globalBiome;
  }

  public int getStrongPower(BlockPos pos, Direction direction) {
    return 0;
  }

  public ResourceKey<Level> getWorldType() {
    return globalType;
  }

  public boolean isSideSolid(BlockPos pos, Direction side, boolean _default) {
    return _default;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public int getMinBuildHeight() {
    return 0;
  }
}
