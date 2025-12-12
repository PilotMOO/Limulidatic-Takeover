package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
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

import java.util.ArrayList;

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
        if (count == -1) logger.printAll(); else logger.printLast(count);
        if (crash){
            throw new RuntimeException(String.format("GreedyNodeEvaluator[%s] had a little fucky wucky", this));
        }
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
    public void updatedViewedMCChunk(int relativeX, int relativeZ, int worldY){
        this.curChunk = getChunk(relativeX, relativeZ);
        this.curSection = getSection(curChunk, worldY);
    }
    public LevelChunk getChunk(int relativeX, int relativeZ){
        //ToDo: see why this is broken and isnt working and is cringe
        int arrayIndex = (relativeX * 4) + relativeZ;
        System.out.println("Trying to get chunk index ");
        LevelChunk chunk = ChunkArray2d[arrayIndex];
        if (chunk == null){
            int chunkX = greedyChunk.relative.x + (relativeX * 16),
                    chunkZ = greedyChunk.relative.y + (relativeZ * 16);
            chunk = ChunkArray2d[arrayIndex] =
                    (LevelChunk)level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
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
        //Fix this, it doesn't return the right values if the coordinate is negative
        // if the value is negative, I think you just need to flip it?
        // like, if it was -16 (AFTER remainer), then the right value would be 47 (63 - 16 = 47)
        // but if it's along the X or Z axis (within 32 of 0)
        // then the offset would be... weird. For both positive and negative values...
        //ToDo: FIX CHUNK CONTEXTUALIZER; SEE ABOVE
        return (byte)Math.abs(coordinate % GreedyChunk.GreedyChunkXZDimensions);
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
    protected GreedyNode curGNode;
    public GreedyNode buildNode(int worldX, int worldY, int worldZ, boolean reassignGChunk, boolean ignoreChecks){
        System.out.println("BUILDING NODE");

        if (!ignoreChecks){
            //Y position out of the bounds of the dimension?
            if (worldY <= MinWorld || worldY >= MaxWorld) {
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
                    GreedyChunk newGChunk = GreedyWorld.WORLD_DEFAULT().retrieveFromWorldCoordinates(worldX, worldZ);
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
        int chunkRelativeX = chunkContextX / 16, chunkRelativeZ = chunkContextZ / 16;

        System.out.println("chunk xz context is [" + chunkContextX + ", " + chunkContextZ + "]");

        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);

        int cX = chunkContextX, cZ = chunkContextZ;

        if (evaluatePosition(cX % 16, worldY % 16, cZ % 16)){
            curGNode = GreedyNode.buildSkeleton(chunkContextX, worldY, chunkContextZ);

            /*------------------
            Negative X
            ------------------*/
            boolean changedChunks = false;
            while(cX > -1){
                cX--; //Step back
                //If we are out of the bounds of the current MC chunk...
                if (cX / 16 < chunkRelativeX) {
                    changedChunks = true; //Set the flag so we can correct this later
                    //reassign the current MC chunk and section
                    updatedViewedMCChunk(--chunkRelativeX, chunkRelativeZ, worldY);
                }
                //Check the next coordinate, and step the GNode if valid
                // % 16 to ensure it's within MC chunk contexts
                // don't worry about losing chunk accuracy, the evaluator-contained
                // 'curChunk' and 'curChunkSection' is already updated to reflect
                // the currently desired chunk [see updateViewedMCChunk(args...)]
                if (evaluatePosition(cX % 16,
                        worldY % 16,
                        cZ % 16)) {
                    curGNode.stepX(-1);
                }
                else break; //Otherwise, stop checking backwards X values
            }
            //move the current X position back to the front of the QuadSpace
            // (the initial starting point)
            cX = chunkContextX;
            if (changedChunks){
                //If the chunks changed last loop, update the current chunk
                chunkRelativeX = cX / 16;
                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                changedChunks = false; //reset the flag for later use
            }
            /*------------------*/

            /*------------------
            Positive X
            ------------------*/
            while(cX < 64){
                cX++; //Step forward
                if (cX / 16 > chunkRelativeX) {
                    changedChunks = true;
                    updatedViewedMCChunk(++chunkRelativeX, chunkRelativeZ, worldY);
                } else break;
                if (evaluatePosition(cX % 16,
                        worldY % 16,
                        cZ % 16)) {
                    curGNode.stepX();
                }
                else break;
            }
            //Set the current X to the minor of the GreedyNode (lowest valid X value)
            cX = curGNode.minorX;
            if (changedChunks){
                chunkRelativeX = cX / 16;
                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                changedChunks = false;
            }
            /*------------------*/

            /*------------------
            Negative Z
            ------------------*/
            while(cZ > -1){
                cZ--; //Step back
                if (cZ / 16 < chunkRelativeZ) {
                    changedChunks = true;
                    updatedViewedMCChunk(chunkRelativeX, --chunkRelativeZ, worldY);
                }
                //Cycle through all X positions
                boolean valid = true;
                for (byte i = 0; i < curGNode.sizeX; i++){
                    byte checkX = (byte)(cX + i);
                    if (chunkRelativeX < (chunkRelativeX = checkX / 16)){
                        //Psst! I dunno if the above if check... actually works???
                        // So... if shit is broken, might be because of this
                        // (this kind of reassignment comparison check is used elsewhere
                        // within this method, so keep that in mind :/)
                        changedChunks = true;
                        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                    }
                    if (!evaluatePosition(
                            checkX % 16,
                            worldY % 16,
                            cZ % 16)) {
                        valid = false;
                    }
                }
                if (valid) curGNode.stepZ(-1);
                else break;
            }
            if (changedChunks) {
                chunkRelativeX = cX / 16;
                chunkRelativeZ = cZ / 16;
                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                changedChunks = false;
            }
            /*------------------*/

            /*------------------
            Positive Z
            ------------------*/
            while(cZ < 64){
                cZ--; //Step back
                if (cZ / 16 > chunkRelativeZ) {
                    changedChunks = true;
                    updatedViewedMCChunk(chunkRelativeX, ++chunkRelativeZ, worldY);
                }
                //Cycle through all X positions
                boolean valid = true;
                for (byte i = 0; i < curGNode.sizeX; i++){
                    byte checkX = (byte)(cX + i);
                    if (chunkRelativeX < (chunkRelativeX = checkX / 16)){
                        changedChunks = true;
                        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                    }
                    if (!evaluatePosition(checkX, cZ, worldY)) valid = false;
                }
                if (valid) curGNode.stepZ();
                else break;
            }
            /*Fixing chunk view changes is moved into the checkY() if statement because
            * if we aren't going to look at the Y axis then don't bother wasting our time
            * fixing the viewed chunk*/
            /*------------------*/

            //CHECKING Y AXIS//

            //Optionally, you can set the evaluator to assume that any Y values other than
            // the given will be invalid (say, for grounded node evaluation)
            // and not waste computational power on that
            if (checkY()) {
                    /*See Positive Z axis post-comment on why this is here*/
                cZ = curGNode.minorZ; //Put it to the lowest valid Z point...
                //No need to update cX because it should still be curGNode.minorX
                // since checking the Z axis didn't modify the value, only read
                if (changedChunks){
                    //...but chunkRelativeX would have been modified if the viewed chunk changed
                    // so make sure to fix that
                    chunkRelativeX = cX / 16;
                    chunkRelativeZ = cZ / 16;
                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                    changedChunks = false;
                }

                int cWorldY = worldY;
                int curChunkIndex = curChunk.getSectionIndex(worldY);
                /*------------------
                Negative Y
                ------------------*/
                if (checkNegativeY()) {
                    //Remember! We are checking Y level, so this time it's capped by the
                    // Dimension's Min|Max Y level and not the GreedyChunk's x64
                    while (cWorldY > MinWorld) {
                        cWorldY--; //Step back
                        if (curChunkIndex != (curChunkIndex = curChunk.getSectionIndex(cWorldY))) {
                            //Console printing to ensure that the above logic does work
                            // and doesn't always default to false
                            System.out.println("Negative Y section jump worked!");
                            //We don't need to change the viewed chunk, just the section
                            curSection = getSection(curChunk, curChunkIndex);
                        }
                        //Cycle through all X & Z positions
                        boolean valid = true;
                        for (byte i = 0; i < curGNode.sizeX; i++) {
                            for (byte j = 0; j < curGNode.sizeZ; j++) {
                                byte checkX = (byte) (cX + i), checkZ = (byte) (cZ + j);
                                if (chunkRelativeX < (chunkRelativeX = checkX / 16) ||
                                        chunkRelativeZ < (chunkRelativeZ = checkZ / 16)) {
                                    changedChunks = true;
                                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                                }
                                if (!evaluatePosition(checkX % 16,
                                        cWorldY % 16,
                                        checkZ % 16)) {
                                    valid = false;
                                }
                            }
                        }
                        if (valid) curGNode.stepY(-1);
                        else break;
                    }

                    //Fixing Negative Y chunk damage is moved into the Positive Y logic block
                    // because we don't need to fix anything if we aren't going to check anything else
                }
                /*------------------*/

                /*------------------
                Positive Y
                ------------------*/
                if (checkPositiveY()) {
                    //Only update this stuff if we need to
                    // (repairing damages caused by the check Negative Y block)
                    if (checkNegativeY()){
                        cWorldY = curGNode.minorY;
                        //Fix the chunk relatives and viewed chunks if they changed.
                        // If the chunk didn't change, to be safe assume the section needs to be updated
                        // and manually reassign it with the modified (current) Y value
                        // (updateViewedMCChunk already updates the section so only manually reassign
                        // the section if we don't want to waste computational power on reassigning the chunk)
                        if (changedChunks) {
                            chunkRelativeX = cX / 16;
                            chunkRelativeZ = cZ / 16;
                            updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                            //No need to use this flag anymore, don't bother
                            //changedChunks = false;
                        } else curSection = getSection(curChunk, cWorldY);
                    }
                    while (cWorldY < MaxWorld) {
                        cWorldY++;
                        if (curChunkIndex != (curChunkIndex = curChunk.getSectionIndex(cWorldY))) {
                            System.out.println("Positive Y section jump worked!");
                            curSection = getSection(curChunk, curChunkIndex);
                        }
                        //Cycle through all X & Z positions
                        boolean valid = true;
                        for (byte i = 0; i < curGNode.sizeX; i++) {
                            for (byte j = 0; j < curGNode.sizeZ; j++) {
                                byte checkX = (byte) (cX + i), checkZ = (byte) (cZ + j);
                                if (chunkRelativeX < (chunkRelativeX = checkX / 16) ||
                                        chunkRelativeZ < (chunkRelativeZ = checkZ / 16)) {
                                    //We don't need to flag the changed chunks anymore
                                    // because this is the last axis to check
                                    //changedChunks = true;
                                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                                }
                                if (!evaluatePosition(checkX % 16,
                                        cWorldY % 16,
                                        checkZ % 16)) {
                                    valid = false;
                                }
                            }
                        }
                        if (valid) curGNode.stepY();
                        else break;
                    }
                    //There is no more chunk modification checks because the Positive Y axis
                    // is the last one checked
                }
                /*------------------*/
            }

            //Finally, we have expanded the node to all valid places
            // let's build it and slot it into its own GreedyMap then return it
            GreedyMap gMap = greedyChunk.locateClosest(curGNode, GreedyChunk.SearchType.MapExtension);
            if (gMap == null) gMap = greedyChunk.buildNewMap();
            gMap.addNode(curGNode);
            logger.log(String.format("Successfully evaluated position [%d, %d, %d] with resulting GreedyNode of %s with GlobalID[%d]%n)", worldX, worldY, worldZ, curGNode.toString(), greedyChunk.computeGlobalID(gMap.computeMapLevelID(curGNode.nodeID))), false);
            return curGNode;
        }
        else {
            logger.log(String.format("Evaluator returned false for world position [%d, %d, %d]%n", worldX, worldY, worldZ), false);
            return null;
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
     *
     * <p>TO DO: ADD ARGUMENTS TO ALLOW THE METHOD TO KNOW WHERE curSection
     * IS IN CONTEXT TO THE REST OF THE CHUNK'S SECTIONS
     * SO TRANS-SECTION BLOCKSTATE EVALUATION CAN OCCUR</p>
     * (E.G. for checking the block below the position in case it overspills to the below section)
     * @param contextX The Contextual X position
     * @param contextY The Contextual Y position
     * @param contextZ The Contextual Z position
     * @return If the node is valid, as defined by the evaluator
     */
    protected abstract boolean evaluatePosition(final int contextX, final int contextY, final int contextZ);


    public abstract boolean evaluateSoloInstance(final Level level,
                                                 final LevelChunk chunk,
                                                 /*final LevelChunkSection chunkSection,*/
                                                 final BlockPos.MutableBlockPos bPos,
                                                 final BlockState bState);
    public abstract boolean evaluateEvenIfOnlyAir();
    public abstract boolean checkNegativeY();
    public abstract boolean checkPositiveY();
    public boolean checkY(){return checkNegativeY() || checkPositiveY();}


    public static class StatusLogger{
        public StatusLogger(String name){
            loggerName = name;
            loggerAwake = System.nanoTime();
            logs = new ArrayList<>();
        }
        private final String loggerName;
        private final long loggerAwake;
        public ArrayList<LogInstance> logs;
        public int logCount = 0;

        public void log(String msg, boolean err){
            logs.add(new LogInstance(System.nanoTime(), err, msg));
            logCount++;
        }
        public void printLog(LogInstance log){
            System.out.println("PRINTING LOGS");
            String print = String.format("[%s : %s, %s] ", loggerAwake, log.logDateNano, log.logDateNano - loggerAwake);
            print = loggerName + print + log.msg;
            if (log.err) classLogger.error(print); else classLogger.info(print);
        }
        public void printAll(){
            System.out.println("Printing logs total : " + logCount);
            for (LogInstance log : logs) printLog(log);
        }
        public void printLast(){
            printLog(logs.get(logCount - 1));
        }
        public void printLast(int count){
            int index = Math.min(logCount - (count + 1), 0);
            for (; index < logCount; index++) printLog(logs.get(index));
        }

        public record LogInstance(long logDateNano, boolean err, String msg){}
    }
}
