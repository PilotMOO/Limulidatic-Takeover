package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import net.minecraft.core.BlockPos;
import org.joml.Vector3i;

import javax.annotation.Nullable;

public interface INode {
    @Nullable INode getParent();
    Vector3i getWithOffset(Vector3i offset);
    default BlockPos getBlockPosFromOffset(Vector3i offset){
        Vector3i vi = getWithOffset(offset);
        return new BlockPos(vi.x, vi.y, vi.z);
    }
}
