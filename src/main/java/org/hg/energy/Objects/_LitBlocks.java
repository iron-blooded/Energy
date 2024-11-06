package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class _LitBlocks {
    public static void lit(List<Location> list, boolean lit){
        for (Location location: list){
            Block block = location.getBlock();
            BlockData blockData = block.getBlockData();
            try {
                blockData.getClass().getMethod("setLit", boolean.class).invoke(blockData, lit);
                block.setBlockData(blockData);
            } catch (Exception ignored) {
            }
        }
    }
}
