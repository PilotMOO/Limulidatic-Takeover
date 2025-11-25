package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GreedyNodeEvaluator {
    private static final Logger classLogger = LoggerFactory.getLogger(GreedyNodeEvaluator.class);

    public void setupGChunkEvaluation(final Level level, final GreedyChunk gChunk){
        this.level = level;
        this.greedyChunk = gChunk;
        DimensionType dimT = level.dimensionType();
        MinWorld = dimT.minY(); MaxWorld = dimT.height();

        logger = new StatusLogger(String.format("GreedyChunk[%d] Node Evaluator", greedyChunk.chunkID));
    }

    public StatusLogger logger;
    public void Oops(boolean crash, int count){
        if (crash){
            throw new RuntimeException(String.format("GreedyNodeEvaluator[%s] had a little fucky wucky", this));
        }
        if (count == -1) logger.printAll(); else logger.printLast(count);
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

    protected LevelChunk curChunk;
    protected LevelChunkSection curSection;

    public LevelChunk getChunkByWorldCoordinates(int x, int z){
        return getChunk(toGreedyChunkContext(x / 16), toGreedyChunkContext(z / 16));
    }
    public byte coordinateToChunkArrayIndex(int x, int z){
        //Both world and context coordinates work
        // if they are world coords, compute to contextual
        if ((x & z) > GreedyChunk.GreedyChunkXZDimensions){
            x = toGreedyChunkContext(x);
            z = toGreedyChunkContext(z);
        }
        return (byte)(((x / 16) << 2) | (z / 16));
    }
    public LevelChunk getChunk(int relativeX, int relativeZ){
        int arrayIndex = (relativeX * 4) + relativeZ;
        LevelChunk chunk = ChunkArray2d[arrayIndex];
        if (chunk == null){
            int chunkX = greedyChunk.relative.x + (relativeX * 16),
                    chunkZ = greedyChunk.relative.y + (relativeZ * 16);
            chunk = ChunkArray2d[arrayIndex] =
                    (LevelChunk)level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        }
        return chunk;
    }

    protected static LevelChunkSection getSection(LevelChunk chunk, int worldY){
        return chunk.getSection(chunk.getSectionIndex(worldY));
    }
    protected BlockState blockStateFromSectionContext(byte sliceX, byte sliceY, byte sliceZ){
        return curSection.getBlockState(sliceX, sliceY, sliceZ);
    }

    public static byte toGreedyChunkContext(int coordinate){
        return (byte)(coordinate % GreedyChunk.GreedyChunkXZDimensions);
    }
    public static byte toMCChunkContext(int coordinate){
        return (byte)(coordinate % 16);
    }
    public Vector3i toWorldCoordinates(int x, int z){
        return toWorldCoordinates(x, 0, z);
    }
    public Vector3i toWorldCoordinates(int x, int y, int z){
        return new Vector3i(greedyChunk.relative.x + x, y, greedyChunk.relative.y + z);
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

    protected int MinWorld, MaxWorld;
    protected QuadSpace curQSpace;
    public GreedyNode buildNode(int worldX, int worldY, int worldZ, boolean reassignGChunk, boolean ignoreChecks){
        if (!ignoreChecks){
            //Y position out of the bounds of the dimension?
            if (worldY <= MinWorld || MinWorld >= MaxWorld) {
                logger.log(String.format("WARNING! Attempted to evaluate a node at [%d, %d, %d] but the Y value were out of the bounds of the dimension's min|max block height[%d|%d]%n", worldX, worldY, worldZ, MinWorld, MaxWorld),
                        true);
                return null;
            }
        }
        if (reassignGChunk || !ignoreChecks){
            //Getting the bounds of the GreedyChunk
            int minX = greedyChunk.relative.x, minZ = greedyChunk.relative.y;
            int maxX = minX + 64, maxZ = minZ + 64;
            if (worldX < minX || worldX >= maxX || worldZ < minZ || worldZ >= maxZ){
                if (reassignGChunk) {
                    GreedyChunk newGChunk = GreedyWorld.retrieveFromWorldCoordinates(worldX, worldZ);
                    logger.log(String.format("Reassigning current GreedyChunk to [%d]...%n", newGChunk.chunkID),
                            false);
                    setupGChunkEvaluation(level, newGChunk);
                } else {
                    logger.log(String.format("WARNING! Attempted to evaluate a node at [%d, %d, %d] but either the X or Z value were out of the bounds of the current GreedyChunk's bounds! [Min/Max X: %d, %d; Min/Max Z: %d, %d]%n", worldX, worldY, worldZ, minX, maxX, minZ, maxZ),
                            true);
                    return null;
                }
            }
        }

        byte chunkContextX = toGreedyChunkContext(worldX),
                chunkContextZ = toGreedyChunkContext(worldZ);
        curChunk = getChunk(chunkContextX / 16, chunkContextZ / 16);
        curSection = getSection(curChunk, worldY);

        cX = toMCChunkContext(worldX);
        cY = toMCChunkContext(worldY);
        cZ = toMCChunkContext(worldZ);

        if (evaluatePosition(cX, cY, cZ)){
            curQSpace = new QuadSpace(
                    toGreedyChunkContext(worldX),
                    toGreedyChunkContext(worldY),
                    toGreedyChunkContext(worldZ));
            while (stepNodeX(negOne)){}

        } else {
            logger.log(String.format("Evaluator returned false for world position [%d, %d, %d]%n", worldX, worldY, worldZ), false);
            return null;
        }
    }

    /**
     * The current X|Y|Z contextual coordinate the evaluator is viewing.
     * <p>Usually fed into the arguments of {@link GreedyNodeEvaluator#evaluatePosition(int, int, int)}
     * and modified in methods that repeatedly invoke that method, E.G. {@link GreedyNodeEvaluator#buildNode(int, int, int, boolean, boolean)}</p>
     * Should always be {@code >= 0} and {@code < 16}
     */
    byte cX, cY, cZ;

    /**I fucking hate bytes in Java and having to byte cast all the time fuck you*/
    protected static byte one = 1, negOne = -1;

    protected boolean stepNodeX(){return stepNodeX(one);}
    protected boolean stepNodeY(){return stepNodeY(one);}
    protected boolean stepNodeZ(){return stepNodeZ(one);}

    protected boolean stepNodeX(byte stepValue){
        //This isn't a very helpful exception message but whatever.
        // Don't put in 0 for the step value, don't waste our fucking time
        if (stepValue == 0) throw new RuntimeException("Fuck you");

        int absStep = Math.abs(stepValue);
        if (absStep == 1){
            boolean flag = evaluateBetween(cX, cY, cZ, stepValue, 1, 1);
            /*evaluatePosition(cX + stepValue, cY, cZ);*/
            if (flag) {
                cX += stepValue;
                curQSpace.stepX(stepValue);
            }
            return flag;
        }
        else {
            byte singleStep = (byte)((stepValue < 0) ? -1 : 1);
            for (int i = 0; i != stepValue; i += singleStep){
                if (evaluatePosition(cX + i, cY, cZ)){
                    cX += singleStep;
                    curQSpace.stepX(singleStep);
                } else return false;
            }
            return true;
        }
    }
    protected boolean stepNodeY(byte stepValue){
        //This isn't a very helpful exception message but whatever.
        // Don't put in 0 for the step value, don't waste our fucking time
        if (stepValue == 0) throw new RuntimeException("Fuck you");

        int absStep = Math.abs(stepValue);
        if (absStep == 1){
            boolean flag = evaluatePosition(cX, cY + stepValue, cZ);
            if (flag) {
                cY += stepValue;
                curQSpace.stepY(stepValue);
            }
            return flag;
        }
        else {
            byte singleStep = (byte)((stepValue < 0) ? -1 : 1);
            for (int i = 0; i != stepValue; i += singleStep){
                if (evaluatePosition(cX, cY + i, cZ)){
                    cY += singleStep;
                    curQSpace.stepY(singleStep);
                } else return false;
            }
            return true;
        }
    }
    protected boolean stepNodeZ(byte stepValue){
        //This isn't a very helpful exception message but whatever.
        // Don't put in 0 for the step value, don't waste our fucking time
        if (stepValue == 0) throw new RuntimeException("Fuck you");

        int absStep = Math.abs(stepValue);
        if (absStep == 1){
            boolean flag = evaluatePosition(cX, cY, cZ + stepValue);
            if (flag) {
                cZ += stepValue;
                curQSpace.stepZ(stepValue);
            }
            return flag;
        }
        else {
            byte singleStep = (byte)((stepValue < 0) ? -1 : 1);
            for (int i = 0; i != stepValue; i += singleStep){
                if (evaluatePosition(cX, cY, cZ + i)){
                    cZ += singleStep;
                    curQSpace.stepZ(singleStep);
                } else return false;
            }
            return true;
        }
    }


    /**
     * Evaluates a block position with the CONTEXTUAL [X,Y,Z] coordinate.
     * <p>This method expects the [X,Y,Z] value to be LESS THAN 16 as it expects the values
     * to be contextual to the current ChunkSection
     * (see {@link GreedyNodeEvaluator#curSection} and {@link GreedyNodeEvaluator#blockStateFromSectionContext(byte, byte, byte)})</p>
     * Tip! {@link GreedyNodeEvaluator#curChunk} and {@link GreedyNodeEvaluator#curSection}
     * will have the relevant values already preassigned when this method is invoked by
     * {@link GreedyNodeEvaluator#buildNode(int, int, int, boolean, boolean)}
     * @param contextX The Contextual X position
     * @param contextY The Contextual Y position
     * @param contextZ The Contextual Z position
     * @return If the node is valid, as defined by the evaluator
     */
    protected abstract boolean evaluatePosition(final int contextX, final int contextY, final int contextZ);
    /**
     * Shorthand, feeds in {@link GreedyNodeEvaluator#cX}, {@link GreedyNodeEvaluator#cY}, {@link GreedyNodeEvaluator#cZ} in as the arguments
     * @return {@link GreedyNodeEvaluator#evaluatePosition(int, int, int)}
     */
    protected boolean evaluatePosition(){return evaluatePosition(cX, cY, cZ);}
    protected boolean evaluateBetween(int startX, int startY, int startZ,
                                      int xSize, int ySize, int zSize){
        //We don't want all the xyz values to be 0
        if ((xSize | ySize | zSize) == 0)
            throw new RuntimeException("Cannot evaluate a section that lacks dimensions!");

        byte stepX = (byte)(xSize < 0 ? -1 : 1),
                stepY = (byte)(ySize < 0 ? -1 : 1),
                stepZ = (byte)(zSize < 0 ? -1 : 1);

        for (byte y = 0; y != ySize; y += stepY){
            for (byte z = 0; z != zSize; z += stepZ){
                for (byte x = 0; x != xSize; x += stepX){
                    if (!evaluatePosition(startX + x, startY + y, startZ + z)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    protected boolean evaluateAxisX(int startX, int startY, int startZ,
                                    int ySize, int zSize){

    }


    public abstract boolean evaluateSoloInstance(final Level level,
                                                 final LevelChunk chunk,
                                                 /*final LevelChunkSection chunkSection,*/
                                                 final BlockPos.MutableBlockPos bPos,
                                                 final BlockState bState);
    public abstract boolean evaluateEvenIfOnlyAir();


    public static class StatusLogger{
        public StatusLogger(String name){
            loggerName = name;
            loggerAwake = System.nanoTime();
            logs = new LogInstance[0];
        }
        private final String loggerName;
        private final long loggerAwake;
        public LogInstance[] logs;
        public int logCount = 0;

        public void log(String msg, boolean err){
            DataHelper.Arrays.expandAndAdd(logs, new LogInstance(System.nanoTime(), err, msg));
            logCount++;
        }
        public void printLog(LogInstance log){
            String print = String.format("[%s : %s, %s] ", loggerAwake, log.logDateNano, log.logDateNano - loggerAwake);
            print = loggerName + print + log.msg;
            if (log.err) classLogger.error(print); else classLogger.info(print);
        }
        public void printAll(){
            for (LogInstance log : logs) printLog(log);
        }
        public void printLast(){
            printLog(logs[logCount - 1]);
        }
        public void printLast(int count){
            int index = logCount - (count + 1);
            for (; index < logCount; index++) printLog(logs[index]);
        }

        public record LogInstance(long logDateNano, boolean err, String msg){}
    }
}
