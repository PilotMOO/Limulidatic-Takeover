package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

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
        ChunkArray2d = new LevelChunk[16];

        logger = new StatusLogger(String.format("GreedyChunk[%d] Node Evaluator", greedyChunk.chunkID));
    }

    public StatusLogger logger;
    public void Oops(boolean crash, int count){
        if (count == -1) logger.printAll(); else logger.printLast(count);
        if (crash){
            throw new RuntimeException(String.format("GreedyNodeEvaluator[%s] had a little fucky wucky", this));
        }
    }

    public Level level;
    public GreedyChunk greedyChunk;
    public LevelChunk[] ChunkArray2d;

    protected LevelChunk curChunk;
    protected LevelChunkSection curSection;

    //This wont work because if x and/or z is negative, it wont return the right value
    // unused, so i dont feel like fixing it
    /*public LevelChunk getChunkByWorldCoordinates(int x, int z){
        return getChunk(toGreedyChunkContext(x / 16), toGreedyChunkContext(z / 16));
    }*/
    //this goes unused, it's probably broken
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
        //this can have an out of bounds error for some reason, I'm confused
        int arrayIndex = (relativeX * 4) + relativeZ;
        System.out.println("[GET CHUNK] Trying to get chunk index [" + relativeX + ", " + relativeZ + "]");
        System.out.println("Index computed to [" + arrayIndex + "]");
        LevelChunk chunk = ChunkArray2d[arrayIndex];
        if (chunk == null){
            System.out.println("Chunk array was empty, locating from world...");
            int chunkX = (greedyChunk.relative.x >> 4) + relativeX,
                    chunkZ = (greedyChunk.relative.y >> 4) + relativeZ;
            System.out.println("chunk coords computed to [" + chunkX + ", " + chunkZ +"]");
            chunk = ChunkArray2d[arrayIndex] =
                    (LevelChunk)level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
            System.out.println("Level returned " + chunk);
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
        boolean negative = coordinate < 0;
        byte remainder = (byte)(coordinate % GreedyChunk.GreedyChunkXZDimensions);
        if (negative) {
            remainder += GreedyChunk.GreedyChunkXZDimensions;
            remainder--;
        }
        System.out.println("Computing coordinate value[" + coordinate + "] to GChunk contexts, result[" + remainder + "]");
        return remainder;
    }
    public static byte toMCChunkContext(int coordinate){
        return (byte)((coordinate % 16)
                + (coordinate < 0 ? 16:0));
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

    /*public void evaluateSection(QuadSpace section){

    }*/

    protected int MinWorld, MaxWorld;
    protected GreedyNode curGNode;
    public GreedyNode buildNode(int worldX, int worldY, int worldZ, boolean reassignGChunk, boolean ignoreChecks){
        System.out.println("BUILDING NODE");
        //ToDo:
        // Remove debugging println invokes
        // and:
        // Check for other nodes within other GreedyMaps in the same GChunk
        // to ensure that any given position isn't already covered

        //X axis seems to be fucked, it overshoots a bunch when generating an uncapped
        // node...

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
                    //TODO: UPDATE THIS TO USE THE MULTIWORLD FEATURE
                    // ONCE THAT IS ADDED
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

        int cX = chunkContextX, cZ = chunkContextZ,
                sectY = worldY % 16;
        if (worldY < 0) sectY += 16;

        if (evaluatePosition(cX % 16, sectY, cZ % 16)){
            System.out.println("INITIAL CHECK WORKED");
            curGNode = GreedyNode.buildSkeleton(chunkContextX, worldY, chunkContextZ);

            /*------------------
            Negative X
            ------------------*/
            boolean changedChunks = false;
            while(--cX > -1){
                //Step back is handled in the while check
                //If we are out of the bounds of the current MC chunk...
                if (cX / 16 < chunkRelativeX) {
                    System.out.println("reassigning X chunk from "
                            + chunkRelativeX + " to " + (chunkRelativeX - 1));
                    changedChunks = true; //Set the flag so we can correct this later
                    //reassign the current MC chunk and section
                    updatedViewedMCChunk(--chunkRelativeX, chunkRelativeZ, worldY);
                }
                //Check the next coordinate, and step the GNode if valid
                // % 16 to ensure it's within MC chunk contexts
                // don't worry about referenced chunk misalignment,
                // the evaluator-contained 'curChunk' and 'curChunkSection'
                // are already updated to reflect the currently desired chunk
                // [see updateViewedMCChunk(args...)]
                if (evaluatePosition(
                        cX % 16,
                        sectY,
                        cZ % 16)) {
                    //System.out.println("NEGATIVE X STEP VALID");
                    curGNode.stepX(-1);
                }
                else break; //Otherwise, stop checking backwards X values
            }
            //move the current X position back to the front of the QuadSpace
            // (the initial starting point)
            System.out.println("Neg X done, cX is " + cX +
                    ", being changed to " + chunkContextX);
            cX = chunkContextX;
            if (changedChunks){
                //If the chunks changed last loop, update the current chunk
                System.out.println("NEG X CHANGED CHUNK, FIXING FROM "
                        + chunkRelativeX + " to " + (cX / 16));
                chunkRelativeX = cX / 16;
                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                changedChunks = false; //reset the flag for later use
            }
            /*------------------*/

            System.out.println("POSITIVE X");
            /*------------------
            Positive X
            ------------------*/
            while(++cX < 64){ //Step forward
                if (cX / 16 > chunkRelativeX) {
                    changedChunks = true;
                    updatedViewedMCChunk(++chunkRelativeX, chunkRelativeZ, worldY);
                }
                if (evaluatePosition(
                        cX % 16,
                        sectY,
                        cZ % 16)) {
                    //System.out.println("POS X STEP");
                    curGNode.stepX();
                }
                else break;
            }
            //Set the current X to the minor of the GreedyNode (lowest valid X value)
            cX = curGNode.minorX;
            if (changedChunks){
                System.out.println("POS X CHANGED CHUNK, FIXING FROM "
                        + chunkRelativeX + " to " + cX / 16);
                chunkRelativeX = cX / 16;
                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                changedChunks = false;
            }
            /*------------------*/

            //In theory, this should tell if the X axis crosses any chunk boundaries
            // remainder 16 isolates cX to the current MC chunk context
            // and if adding the node size
            // (how many x places the following loops will check)
            // makes the value exceed 15, then that means it would have crossed over
            // at LEAST 1 chunk
            boolean xCrossesChunk = (cX % 16) + curGNode.sizeX > 15;
            System.out.println("Does the X axis cross any chunks? " + xCrossesChunk);

            System.out.println("NEG Z");
            /*------------------
            Negative Z
            ------------------*/
            //Step back
            while(--cZ > -1){
                //IF the minecraft chunk index relative within the GreedyChunk changes
                if (cZ / 16 < chunkRelativeZ) {
                    //Update the viewed chunk
                    changedChunks = true;
                    updatedViewedMCChunk(chunkRelativeX, --chunkRelativeZ, worldY);
                }
                //Cycle through all X positions
                boolean valid = true; //Flag for if any blocks are invalid
                for (byte i = 0; i < curGNode.sizeX; i++){
                    //^^ Cycle through all X positions

                    //Add offset to cX, which is equal to the minor of the
                    // current node, so the smallest valid X coordinate
                    byte checkX = (byte)(cX + i);
                    int upCRX = checkX / 16;
                    //System.out.println("har har har har: checkX " + checkX);
                    if (chunkRelativeX < upCRX /*(chunkRelativeX = checkX / 16)*/){
                        chunkRelativeX = upCRX;
                        //In theory, we don't need to set this flag
                        // because the end of the Z for loop already fixes it if this
                        // would have even changed
                        //changedChunks = true;
                        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                    }
                    if (!evaluatePosition(checkX % 16, sectY, cZ % 16)) {
                        System.out.println("Negative Z didnt like those coords");
                        valid = false;
                        break;
                    }
                }
                System.out.println("NegZ cycle of " + cZ + " resulted in ["
                        + valid + "] with cRelative " + chunkRelativeX
                        + " fixing to " +( cX / 16));
                if (xCrossesChunk){
                    System.out.println("X Cross chunk FIX");
                    chunkRelativeX = cX / 16; //in THEORY this should fix it...
                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
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

            System.out.println("POS Z");
            /*------------------
            Positive Z
            ------------------*/
            while(++cZ < 64){
                if (cZ / 16 > chunkRelativeZ) {
                    changedChunks = true;
                    updatedViewedMCChunk(chunkRelativeX, ++chunkRelativeZ, worldY);
                }
                //Cycle through all X positions
                boolean valid = true;
                for (byte i = 0; i < curGNode.sizeX; i++){
                    byte checkX = (byte)(cX + i);
                    int upCRX = checkX / 16;
                    if (chunkRelativeX < upCRX){
                        chunkRelativeX = upCRX;
                        //changedChunks = true;
                        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                    }
                    if (!evaluatePosition(
                            checkX % 16,
                            sectY,
                            cZ % 16)) {
                        valid = false;
                        break;
                    }
                }
                if (xCrossesChunk){
                    chunkRelativeX = cX / 16;
                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                }
                if (valid) curGNode.stepZ();
                else break;
            }

            /*------------------*/

            //CHECKING Y AXIS//

            //Optionally, you can set the evaluator to assume that any Y values other than
            // the given will be invalid (say, for grounded node evaluation)
            // and not waste computational power on that
            System.out.println("Might check Y: " + checkY());
            if (checkY()) {
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
                //See xCrossesChunk for explanation
                boolean zCrossesChunk = (cZ % 16) + curGNode.sizeZ > 15;

                int cWorldY = worldY;
                int curChunkIndex = curChunk.getSectionIndex(worldY);
                /*------------------
                Negative Y
                ------------------*/
                if (checkNegativeY()) {
                    //Remember! We are checking Y level, so this time it's capped by the
                    // Dimension's Min|Max Y level and not the GreedyChunk's x64
                    while (--cWorldY > MinWorld) {//Step back
                        //Delta = change
                        int chunkIndexDelta = curChunk.getSectionIndex(cWorldY);
                        if (curChunkIndex != chunkIndexDelta) {
                            curChunkIndex = chunkIndexDelta;
                            //We don't need to change the viewed chunk, just the section
                            curSection = getSection(curChunk, curChunkIndex);
                        }
                        //Cycle through all X & Z positions
                        boolean valid = true;
                        for (byte i = 0; i < curGNode.sizeX; i++) {
                            for (byte j = 0; j < curGNode.sizeZ; j++) {
                                byte checkX = (byte) (cX + i),
                                        checkZ = (byte) (cZ + j);
                                if (chunkRelativeX < (chunkRelativeX = checkX / 16) ||
                                        chunkRelativeZ < (chunkRelativeZ = checkZ / 16)) {
                                    System.out.println("Chunk X|Z changed during neg Y loop, values: "+
                                            "checkX[" + checkX + "] from cX " +cX +
                                            "checkZ[" + checkZ + "] from cZ " +  cZ +
                                            "x, z [" + chunkRelativeX + ", " + chunkRelativeZ + "]");
                                    //changedChunks = true;
                                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                                }
                                if (!evaluatePosition(checkX % 16,
                                        toMCChunkContext(cWorldY),
                                        checkZ % 16)) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (zCrossesChunk){
                                chunkRelativeZ = cZ / 16;
                                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ,
                                        cWorldY);
                            }
                            if (!valid) break;
                        }
                        if (xCrossesChunk){
                            chunkRelativeX = cX / 16;
                            updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ,
                                    cWorldY);
                        }
                        if (valid) curGNode.stepY(-1);
                        else break;
                    }
                }
                /*------------------*/

                /*------------------
                Positive Y
                ------------------*/
                if (checkPositiveY()) {
                    //Only update this stuff if we need to
                    // (repairing damages caused by the check Negative Y block)
                    if (checkNegativeY()){
                        //There isn't any chunk updating because
                        // it should already be located in the lowest chunk index
                        // because of the cycle index repairing done after each
                        // X & Z loop in the negative Y axis
                        curSection = getSection(curChunk,
                                cWorldY = curGNode.minorY);
                    }
                    while (++cWorldY < MaxWorld) {
                        //Delta = change
                        int chunkIndexDelta = curChunk.getSectionIndex(cWorldY);
                        if (curChunkIndex != chunkIndexDelta) {
                            curChunkIndex = chunkIndexDelta;
                            curSection = getSection(curChunk, curChunkIndex);
                        }
                        //Cycle through all X & Z positions
                        boolean valid = true;
                        for (byte i = 0; i < curGNode.sizeX; i++) {
                            for (byte j = 0; j < curGNode.sizeZ; j++) {
                                byte checkX = (byte) (cX + i), checkZ = (byte) (cZ + j);
                                if (chunkRelativeX < (chunkRelativeX = checkX / 16) ||
                                        chunkRelativeZ < (chunkRelativeZ = checkZ / 16)) {
                                    System.out.println("Chunk X|Z changed during pos Y loop, values: "+
                                            "checkX[" + checkX + "] from cX " +cX +
                                            " checkZ[" + checkZ + "] from cZ " +  cZ +
                                            " x, z [" + chunkRelativeX + ", " + chunkRelativeZ + "] " +
                                            "current node " + curGNode);
                                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                                }
                                if (!evaluatePosition(checkX % 16,
                                        toMCChunkContext(cWorldY),
                                        checkZ % 16)) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (zCrossesChunk){
                                chunkRelativeZ = cZ / 16;
                                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ,
                                        cWorldY);
                            }
                            if (!valid) break;
                        }
                        if (xCrossesChunk){
                            chunkRelativeX = cX / 16;
                            updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ,
                                    cWorldY);
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
            System.out.println("YIPPEE!!!");
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
