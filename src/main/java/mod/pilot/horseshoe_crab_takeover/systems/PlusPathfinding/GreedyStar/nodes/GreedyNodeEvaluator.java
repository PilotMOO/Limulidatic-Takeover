package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract class GreedyNodeEvaluator {
    public abstract boolean evaluate(final Level level, final LevelChunk chunk,
                                     final BlockPos.MutableBlockPos bPos, final BlockState bState);
    public abstract boolean evaluateEvenIfOnlyAir();
}
