package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.hg.scorchingsun.ScorchingSun;

import static org.hg.energy.Energy.getScorchingSun;

public class _LitBlocks {
    public static void lit(Structure structure, boolean lit) {
        ScorchingSun scorchingSun = getScorchingSun();
        for (Location location : structure.getLocations()) {
            Block block = location.getBlock();
            if (scorchingSun != null) {
                if (lit) {
                    scorchingSun.addFromListCustomTempLocations(block, structure.getTemperature());
                } else {
                    scorchingSun.removeFromListCustomTempLocations(block);
                }
            }
            BlockData blockData = block.getBlockData();
            try {
                blockData.getClass().getMethod("setLit", boolean.class).invoke(blockData, lit);
                block.setBlockData(blockData);
            } catch (Exception ignored) {
            }
        }
    }


}
