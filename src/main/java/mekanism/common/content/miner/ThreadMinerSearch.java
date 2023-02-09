package mekanism.common.content.miner;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import mekanism.api.Chunk3D;
import mekanism.api.Coord4D;
import mekanism.api.util.BlockInfo;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.fluids.IFluidBlock;

public class ThreadMinerSearch extends Thread {

    private TileEntityDigitalMiner tileEntity;

    public State state = State.IDLE;

    private Map<Chunk3D, BitSet> oresToMine = new HashMap<>();
    private Map<Integer, MinerFilter> replaceMap = new HashMap<>();
    private Map<BlockInfo, MinerFilter> acceptedItems = new HashMap<>();
    private ChunkCache chunkCache;

    public int found = 0;

    public ThreadMinerSearch(TileEntityDigitalMiner tile) {
        tileEntity = tile;
    }

    public void setChunkCache(ChunkCache chunkCache) {
        this.chunkCache = chunkCache;
    }

    @Override
    public void run() {
        state = State.SEARCHING;
        if (!tileEntity.inverse && tileEntity.filters.isEmpty()) {
            state = State.FINISHED;
            return;
        }
        Coord4D coord = tileEntity.getStartingCoord();
        int diameter = tileEntity.getDiameter();
        int size = tileEntity.getTotalSize();
        BlockInfo info = new BlockInfo(null, 0);
        BlockPos minerPos = tileEntity.getPos();

        for (int i = 0; i < size; i++) {
            if (tileEntity.isInvalid()) {
                //Make sure the miner is still valid and something hasn't gone wrong
                return;
            }
            int x = coord.x + i % diameter;
            int z = coord.z + (i / diameter) % diameter;
            int y = coord.y + (i / diameter / diameter);
            if (minerPos.getX() == x && minerPos.getY() == y && minerPos.getZ() == z) {
                //Skip the miner itself
                continue;
            }

            BlockPos testPos = new BlockPos(x, y, z);
            if (chunkCache.getTileEntity(testPos) instanceof TileEntityBoundingBlock) {
                //If it is not loaded or it is a bounding block skip it
                continue;
            }

            IBlockState state = chunkCache.getBlockState(testPos);
            info.block = state.getBlock();
            info.meta = state.getBlock().getMetaFromState(state);

            if (info.block == null || info.block instanceof BlockLiquid || info.block instanceof IFluidBlock || info.block.isAir(state, chunkCache, testPos)) {
                //Skip air and liquids
                continue;
            }

            MinerFilter filterFound = null;
            if (acceptedItems.containsKey(info)) {
                filterFound = acceptedItems.get(info);
            } else {
                ItemStack stack = new ItemStack(info.block, 1, info.meta);
                if (tileEntity.isReplaceStack(stack)) {
                    continue;
                }
                for (MinerFilter filter : tileEntity.filters) {
                    if (filter.canFilter(stack)) {
                        filterFound = filter;
                        break;
                    }
                }
                acceptedItems.put(info, filterFound);
            }
            if (tileEntity.inverse == (filterFound == null)) {
                set(i, new Coord4D(x, y, z, tileEntity.getWorld().provider.getDimension()));//I Really hope this is threadsafe..
                replaceMap.put(i, filterFound);
                found++;
            }
        }


        state = State.FINISHED;
        tileEntity.oresToMine = oresToMine;
        tileEntity.replaceMap = replaceMap;
        chunkCache = null;
        MekanismUtils.saveChunk(tileEntity);
    }

    public void set(int i, Coord4D location) {
        Chunk3D chunk = new Chunk3D(location);
        oresToMine.computeIfAbsent(chunk, k -> new BitSet());
        oresToMine.get(chunk).set(i);
    }

    public void reset() {
        state = State.IDLE;
        chunkCache = null;
    }

    public enum State {
        IDLE("gui.teleporter.notReady"),
        SEARCHING("gui.teleporter.Searching"),
        PAUSED("gui.teleporter.Paused"),
        FINISHED("gui.teleporter.ready");

        public String desc;

        State(String s) {
            desc = s;
        }

        public String localize() {
            return I18n.translateToLocal(getTranslationKey());
        }

        public String getTranslationKey() {
            return desc;
        }
    }

}