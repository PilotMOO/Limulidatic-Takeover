package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3i;

public abstract class GreedyNodeEvaluator {
    public void setupGChunkEvaluation(final Level level, final GreedyChunk gChunk){
        this.level = level;
        this.greedyChunk = gChunk;
    }

    //TODO: Read this shit and pick it back up when you feel like it
    /*
    * Im currently like, transferring the entirety of GreedyNodeBuilder into this class
    * because there's no real reason to have them as separate thingies
    *
    * It's kind of a mess because I'm only halfway paying attention to what I need
    * And the logic I need to build is fucking insane
    *
    * I could probably clean up the code a lot too, the QuadSpace chunk slicer uses
    *  like 6 (?) repeating while loops and I could easily compress it into like 1 or 2
    *  methods and just use method calls rather than copy/pasting 6 (almost) identical loops
    *
    * But that's like. a lot of work and I dont wanna do that rn even though I almost just
    *  started. I'm writing this to remind me when I go to work on this again (maybe tomorrow?)
    * */

    public Level level;
    public GreedyChunk greedyChunk;
    public LevelChunk[] ChunkArray2d = new LevelChunk[16];
    private static final byte zBitmask = 12;

    public LevelChunk getChunk(int x, int z){
        return getChunk(coordinateToChunkArrayIndex(x, z));
    }
    public byte coordinateToChunkArrayIndex(int x, int z){
        //Both world and context coordinates work
        // if they are world coords, compute to contextual
        if ((x & z) > GreedyChunk.GreedyChunkXZDimensions){
            x = toContextCoordinate(x);
            z = toContextCoordinate(z);
        }
        return (byte)(((x / 16) << 2) | (z / 16));
    }
    public LevelChunk getChunk(byte formatID){
        byte idX = (byte)(formatID >> 2), idZ = (byte)((formatID & zBitmask) >>> 2);
        int arrayIndex = (idX * 4) + idZ;
        LevelChunk chunk = ChunkArray2d[arrayIndex];
        if (chunk == null){
            int chunkX = greedyChunk.relative.x + (idX * 16),
                    chunkZ = greedyChunk.relative.y + (idZ * 16);
            chunk = ChunkArray2d[arrayIndex] =
                    (LevelChunk)level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        }
        return chunk;
    }

    protected LevelChunkSection getSlice(LevelChunk chunk, int y){
        return chunk.getSection(chunk.getSectionIndex(y));
    }

    public static int toContextCoordinate(int coordinate){
        return coordinate % GreedyChunk.GreedyChunkXZDimensions;
    }
    public Vector3i toWorldCoordinates(int x, int z){
        return toWorldCoordinates(x, 0, z);
    }
    public Vector3i toWorldCoordinates(int x, int y, int z){
        return new Vector3i(greedyChunk.relative, y).add(x, 0, z);
    }
    public Vec3 toWorldCoordinatesVec3(double x, double z){
        return toWorldCoordinatesVec3(x, 0, z);
    }
    public Vec3 toWorldCoordinatesVec3(double x, double y, double z){
        //GreedyChunk.relative stores the Z coordinate in the Y value
        // because Vector2i stores values as x & y, but we are storing x & z world coordinates
        return new Vec3(greedyChunk.relative.x + x, y, greedyChunk.relative.y + z);
    }

    public void evaluateSection(QuadSpace section){

    }

    /**
     * Evaluates a block position with the CONTEXTUAL [X,Y,Z] coordinate.
     * <p>This method expects the [X,Y,Z] value to be LESS THAN 64 as it expects the vales
     * to be contextual</p>
     * @param x The Contextual X position
     * @param y The Contextual Y position
     * @param z The Contextual Z position
     * @return If the node is valid, as defined by the evaluator
     */
    protected abstract boolean evaluate(final byte x, final byte y, final byte z);


    public abstract boolean evaluateSoloInstance(final Level level,
                                                 final LevelChunk chunk,
                                                 /*final LevelChunkSection chunkSection,*/
                                                 final BlockPos.MutableBlockPos bPos,
                                                 final BlockState bState);
    public abstract boolean evaluateEvenIfOnlyAir();
}
