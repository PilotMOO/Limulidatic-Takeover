package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        //MaxWorld has +MinWorld because dimT.height is zero-context
        // so if the minY is a negative value, it is equal to maxY - minY
        // (E.G. maxY for overworld is 320 but dimT.height() = 384
        // because minY = -64, so height + minY = 384 + -64 = 320)
        MinWorld = dimT.minY();
        MaxWorld = dimT.height() + MinWorld;
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

    public void updatedViewedMCChunk(int relativeX, int relativeZ, int worldY){
        this.curChunk = getChunk(relativeX, relativeZ);
        this.curSection = getSection(curChunk, worldY);
    }
    public LevelChunk getChunk(int relativeX, int relativeZ){
        int arrayIndex = (relativeX * 4) + relativeZ;
        //System.out.println("[GET CHUNK] Trying to get chunk index [" + relativeX + ", " + relativeZ + "]");
        //System.out.println("Index computed to [" + arrayIndex + "]");
        LevelChunk chunk = ChunkArray2d[arrayIndex];
        if (chunk == null){
            //System.out.println("Chunk array was empty, locating from world...");
            int chunkX = (greedyChunk.relative.x >> 4) + relativeX,
                    chunkZ = (greedyChunk.relative.y >> 4) + relativeZ;
            //System.out.println("chunk coords computed to [" + chunkX + ", " + chunkZ +"]");
            chunk = ChunkArray2d[arrayIndex] =
                    (LevelChunk)level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
            //System.out.println("Level returned " + chunk);
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
            //Subtracting one because
            // positive GreedyChunks go from 0 to 63
            // but negative ones go from -64 to -1
            remainder--;
        }
        //System.out.println("Computing coordinate value[" + coordinate + "] to GChunk contexts, result[" + remainder + "]");
        return remainder;
    }
    public static byte toMCChunkContext(int coordinate){
        int remainder = coordinate % 16;
        return (byte)(remainder + (remainder < 0 ? 16:0));
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

        final byte chunkContextX = toGreedyChunkContext(worldX < 0 ? worldX+1 : worldX),
                chunkContextZ = toGreedyChunkContext(worldZ < 0 ? worldZ+1 : worldZ);
        int chunkRelativeX = chunkContextX / 16,
                chunkRelativeZ = chunkContextZ / 16;

        //System.out.println("chunk xz context is [" + chunkContextX + ", " + chunkContextZ + "]");

        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);

        int cX = chunkContextX, cZ = chunkContextZ,
                sectY = toMCChunkContext(worldY);

        //Grabbing the gMap early so we can access preexisting GNodes to avoid overlap
        GreedyMap gMap = greedyChunk.locateClosest(chunkContextX, worldY, chunkContextZ,
                GreedyChunk.SearchType.MapExtension);
        int mapCount = -1;
        boolean checkGMap = gMap != null && (mapCount = gMap.count()) != 0;
        System.out.println("gMap is " + gMap);
        GreedyNode[] sisters = null; byte[] sisterRelative = null;
        if (checkGMap) {
            sisters = new GreedyNode[mapCount];
            sisterRelative = new byte[mapCount];
            int i = 0;
            //idk if it's reading nodes yet smh
            System.out.println("Shitting out this many GNodes in this map: " + mapCount);
            for (GreedyNode gNode : gMap.nodes){
                sisters[i] = gNode;
                System.out.println("gMap contains gNode " + gNode + " at index " + ++i + " of " + (gMap.count() - 1));
                if (gNode.contains(chunkContextX, worldY, chunkContextZ)){
                    logger.log(String.format("Position[%d, %d, %d] is already contained in a preexisting GreedyNode %s",
                                    worldX, worldY, worldZ, gNode),
                            false);
                    return null;
                }
            }
        }

        if (evaluatePosition(cX % 16, sectY, cZ % 16)){
            //System.out.println("INITIAL CHECK WORKED");
            curGNode = GreedyNode.buildSkeleton(chunkContextX, worldY, chunkContextZ);
            System.out.println("BEFORE ANYTHING: " + curGNode);
            System.out.println("cX vs. chunkContextX[" + cX + ", " + chunkContextX + "]");
            System.out.println("cZ vs. chunkContextZ[" + cZ + ", " + chunkContextZ + "]");
            /*------------------
            Negative X
            ------------------*/
            boolean changedChunks = false;
            while(--cX > -1){
                if (checkGMap) {
                    boolean overlap = false;
                    for (int i = 0; i < mapCount; i++) {
                        GreedyNode gNode = sisters[i];
                        System.out.println("cX=" + cX + ", checking node " + gNode);
                        if (gNode.contains(cX, worldY, cZ)){
                            System.out.println("Another node contains pos");
                            overlap = true;
                            sisterRelative[i] = (byte)(gNode.nodeID | GreedyMap.MapContext.ID_WEST);
                            break;
                        }
                    }
                    if (overlap) {
                        System.out.println("NEGATIVE X BROKE EARLY BECAUSE OF NODE OVERLAP");
                        break;
                    }
                }
                //Step back is handled in the while check
                //If we are out of the bounds of the current MC chunk...
                if (cX / 16 < chunkRelativeX) {
                    /*System.out.println("reassigning X chunk from "
                            + chunkRelativeX + " to " + (chunkRelativeX - 1));*/
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
            System.out.println("negX done, result: " + curGNode);
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
            while(++cX < 64){ //Step forward
                if (checkGMap) {
                    boolean overlap = false;
                    for (int i = 0; i < mapCount; i++) {
                        //No need to evaluate this one if we've already evaluated it
                        // for a different axis. It can't be adjacent on more than 1
                        // axis without also overlapping the initial position
                        if (sisterRelative[i] != 0) continue;
                        GreedyNode gNode = sisters[i];
                        System.out.println("cX=" + cX + ", checking node " + gNode);
                        if (gNode.contains(cX, worldY, cZ)){
                            System.out.println("Another node contains pos");
                            overlap = true;
                            sisterRelative[i] = (byte)(gNode.nodeID | GreedyMap.MapContext.ID_EAST);
                            break;
                        }
                    }
                    if (overlap) {
                        System.out.println("POSITIVE X BROKE EARLY BECAUSE OF NODE OVERLAP");
                        break;
                    }
                }

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
            System.out.println("posX done, result: " + curGNode);
            //Set the current X to the minor of the GreedyNode (lowest valid X value)
            cX = curGNode.minorX;
            if (changedChunks){
                /*System.out.println("POS X CHANGED CHUNK, FIXING FROM "
                        + chunkRelativeX + " to " + cX / 16);*/
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
                if (checkGMap) {
                    boolean overlap = false;
                    for (int i = 0; i < mapCount; i++) {
                        if (sisterRelative[i] != 0) continue;
                        GreedyNode gNode = sisters[i];
                        System.out.println("cZ=" + cZ + ", checking node " + gNode);
                        if (gNode.intersects(cX, worldY, cZ, curGNode.sizeX, 1, 1)){
                            System.out.println("Another node contains pos");
                            overlap = true;
                            sisterRelative[i] = (byte)(gNode.nodeID | GreedyMap.MapContext.ID_NORTH);
                            break;
                        }
                    }
                    if (overlap) {
                        System.out.println("NEGATIVE Z BROKE EARLY BECAUSE OF NODE OVERLAP");
                        break;
                    }
                }

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
                        //System.out.println("Negative Z didnt like those coords");
                        valid = false;
                        break;
                    }
                }
                /*System.out.println("NegZ cycle of " + cZ + " resulted in ["
                        + valid + "] with cRelative " + chunkRelativeX
                        + " fixing to " +( cX / 16));*/
                if (xCrossesChunk){
                    //System.out.println("X Cross chunk FIX");
                    chunkRelativeX = cX / 16; //in THEORY this should fix it...
                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                }

                if (valid) curGNode.stepZ(-1);
                else break;
            }
            System.out.println("negZ done, result: " + curGNode);
            cZ = chunkContextZ;
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
                if (checkGMap) {
                    boolean overlap = false;
                    for (int i = 0; i < mapCount; i++) {
                        if (sisterRelative[i] != 0) continue;
                        GreedyNode gNode = sisters[i];
                        System.out.println("cZ=" + cZ + ", checking node " + gNode);
                        if (gNode.intersects(cX, worldY, cZ, curGNode.sizeX, 1, 1)){
                            System.out.println("Another node contains pos");
                            overlap = true;
                            sisterRelative[i] = (byte)(gNode.nodeID | GreedyMap.MapContext.ID_SOUTH);
                            break;
                        }
                    }
                    if (overlap) {
                        System.out.println("POSITIVE Z BROKE EARLY BECAUSE OF NODE OVERLAP");
                        break;
                    }
                }

                //IF the minecraft chunk index relative within the GreedyChunk changes
                if (cZ / 16 > chunkRelativeZ) {
                    //Update the viewed chunk
                    changedChunks = true;
                    updatedViewedMCChunk(chunkRelativeX, ++chunkRelativeZ, worldY);
                }
                //Cycle through all X positions
                boolean valid = true; //Flag for if any blocks are invalid
                for (byte i = 0; i < curGNode.sizeX; i++){
                    byte checkX = (byte)(cX + i);
                    int upCRX = checkX / 16;
                    //System.out.println("har har har har: checkX " + checkX);
                    if (chunkRelativeX < upCRX){
                        chunkRelativeX = upCRX;
                        //changedChunks = true;
                        updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                    }
                    if (!evaluatePosition(checkX % 16, sectY, cZ % 16)) {
                        //System.out.println("positive Z didnt like those coords");
                        valid = false;
                        break;
                    }
                }
                /*System.out.println("PosZ cycle of " + cZ + " resulted in ["
                        + valid + "] with cRelative " + chunkRelativeX
                        + " fixing to " +( cX / 16));*/
                if (xCrossesChunk){
                    //System.out.println("X Cross chunk FIX");
                    chunkRelativeX = cX / 16;
                    updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, worldY);
                }

                if (valid) curGNode.stepZ();
                else break;
            }
            System.out.println("posZ done, result: " + curGNode);
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
                    while (--cWorldY > MinWorld) {
                        if (checkGMap) {
                            boolean overlap = false;
                            for (int i = 0; i < mapCount; i++) {
                                if (sisterRelative[i] != 0) continue;
                                GreedyNode gNode = sisters[i];
                                System.out.println("cWorldY=" + cWorldY + ", checking node " + gNode);
                                if (gNode.intersects(cX, cWorldY, cZ, curGNode.sizeX, 1, curGNode.sizeZ)){
                                    System.out.println("Another node contains pos");
                                    overlap = true;
                                    sisterRelative[i] = (byte)(gNode.nodeID | GreedyMap.MapContext.ID_DOWN);
                                    break;
                                }
                            }
                            if (overlap) {
                                System.out.println("NEGATIVE Y BROKE EARLY BECAUSE OF NODE OVERLAP");
                                break;
                            }
                        }

                        //Delta = change
                        int chunkIndexDelta = curChunk.getSectionIndex(cWorldY);
                        if (curChunkIndex != chunkIndexDelta) {
                            curChunkIndex = chunkIndexDelta;
                            //We don't need to change the viewed chunk, just the section
                            curSection = curChunk.getSection(curChunkIndex);
                        }
                        //Cycle through all X & Z positions
                        boolean valid = true;
                        for (byte i = 0; i < curGNode.sizeX; i++) {
                            for (byte j = 0; j < curGNode.sizeZ; j++) {
                                byte checkX = (byte) (cX + i),
                                        checkZ = (byte) (cZ + j);
                                if (chunkRelativeX < (chunkRelativeX = checkX / 16) ||
                                        chunkRelativeZ < (chunkRelativeZ = checkZ / 16)) {
                                    /*System.out.println("Chunk X|Z changed during neg Y loop, values: "+
                                            "checkX[" + checkX + "] from cX " +cX +
                                            "checkZ[" + checkZ + "] from cZ " +  cZ +
                                            "x, z [" + chunkRelativeX + ", " + chunkRelativeZ + "]");*/
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
                                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                            }
                            if (!valid) break;
                        }
                        if (xCrossesChunk){
                            chunkRelativeX = cX / 16;
                            updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
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
                        curSection = getSection(curChunk, cWorldY = worldY);
                    }
                    while (++cWorldY < MaxWorld) {
                        if (checkGMap) {
                            boolean overlap = false;
                            for (int i = 0; i < mapCount; i++) {
                                if (sisterRelative[i] != 0) continue;
                                GreedyNode gNode = sisters[i];
                                System.out.println("cWorldY=" + cWorldY + ", checking node " + gNode);
                                if (gNode.intersects(cX, cWorldY, cZ, curGNode.sizeX, 1, curGNode.sizeZ)){
                                    System.out.println("Another node contains pos");
                                    overlap = true;
                                    sisterRelative[i] = (byte)(gNode.nodeID | GreedyMap.MapContext.ID_UP);
                                    break;
                                }
                            }
                            if (overlap) {
                                System.out.println("POSITIVE Y BROKE EARLY BECAUSE OF NODE OVERLAP");
                                break;
                            }
                        }

                        //Delta = change
                        System.out.println("[POS Y] cWorldY is " + cWorldY);
                        int chunkIndexDelta = curChunk.getSectionIndex(cWorldY);
                        if (curChunkIndex != chunkIndexDelta) {
                            curChunkIndex = chunkIndexDelta;
                            curSection = curChunk.getSection(curChunkIndex);
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
                                updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                            }
                            if (!valid) break;
                        }
                        if (xCrossesChunk){
                            chunkRelativeX = cX / 16;
                            updatedViewedMCChunk(chunkRelativeX, chunkRelativeZ, cWorldY);
                        }
                        if (valid) curGNode.stepY();
                        else break;
                    }
                    //There is no more chunk modification checks because
                    // the positive Y axis is the last one checked
                }
                /*------------------*/
            }

            if (gMap == null) gMap = greedyChunk.buildNewMap();
            else if (checkGMap){
                for (int i = 0; i < mapCount; i++) {
                    byte id = sisterRelative[i];
                    if (id != 0) continue;
                    GreedyNode gNode = sisters[i];
                    double dist = gNode.distanceEdgeToEdge(curGNode);
                    if (dist != 0) {
                        System.out.println("DISTANCE WASN'T ZERO: " + dist);
                        sisterRelative[i] = -1;
                        continue;
                    }
                    int majorX = curGNode.minorX + curGNode.sizeX,
                            majorY = curGNode.minorY + curGNode.sizeY,
                            majorZ = curGNode.minorZ + curGNode.sizeZ;
                    int oMajorX = gNode.minorX + gNode.sizeX,
                            oMajorY = gNode.minorY + gNode.sizeY,
                            oMajorZ = gNode.minorZ + gNode.sizeZ;
                    boolean eval = false;
                    boolean corner = false;
                    id = gNode.nodeID;
                    //West, negative X
                    if (curGNode.minorX >= oMajorX){
                        eval = true;
                        id |= GreedyMap.MapContext.ID_WEST;
                    }
                    //East, positive X
                    else if (majorX <= gNode.minorX){
                        eval = true;
                        id |= GreedyMap.MapContext.ID_EAST;
                    }
                    //Down, negative Y
                    if (curGNode.minorY >= oMajorY) {
                        if (eval) corner = true;
                        else {
                            eval = true;
                            id |= GreedyMap.MapContext.ID_DOWN;
                        }
                    }
                    //Up, positive Y
                    else if (majorY <= gNode.minorY) {
                        if (eval) corner = true;
                        else {
                            eval = true;
                            id |= GreedyMap.MapContext.ID_UP;
                        }
                    }
                    //North, negative Z //why the fuck are these two flipped
                    if (curGNode.minorZ >= oMajorZ) {
                        if (eval) corner = true;
                        else {
                            eval = true;
                            id |= GreedyMap.MapContext.ID_NORTH;
                        }
                    }
                    //South, positive Z /**/
                    else if (majorZ <= gNode.minorZ) {
                        if (eval) corner = true;
                        else {
                            eval = true;
                            id |= GreedyMap.MapContext.ID_SOUTH;
                        }
                    }

                    if (corner) {
                        System.out.println("GNODE " + gNode + " IS CORNER RELATIVE TO CURGNODE");
                        sisterRelative[i] = -1;
                        //I dont have a way to deal with corners rn
                    }
                    else {
                        if (!eval) System.out.println("node id [" + BitwiseDataHelper.parseByteToBinary(id) + "] is not very skibiti");
                        sisterRelative[i] = eval ? id : -1;
                    }
                }
            }
            GreedyMap.MapContext context = gMap.addNode(curGNode);
            if (checkGMap){
                byte curID = curGNode.nodeID;
                for (int i = 0; i < mapCount; i++) {
                    byte id = sisterRelative[i];
                    System.out.println("Cycling through all id's, current is " + id + ", [" + BitwiseDataHelper.parseByteToBinary(id) + "]");
                    if (id != -1){
                        GreedyMap.MapContext otherContext = gMap.contextFromID(id);
                        if (otherContext == null){
                            logger.log(String.format("OOPS! attempted to evaluate relative direction for a new gNode %s for the node ID[%d] but it seems like the supplied GreedyMap does NOT have a node that matches that ID! Something must have gone horribly wrong...", curGNode, id), true);
                            continue;
                        }
                        System.out.println("ADDING NODE CONTEXT " + BitwiseDataHelper.parseByteToBinary(id) + " TO CURGNODE");
                        context.addElementByComputedID(id);
                        otherContext.addElementByID(curID,
                                GreedyMap.MapContext.oppositeDirection(
                                        GreedyMap.MapContext.isolateDirection(id)));
                        System.out.println("OTHER context after adding new thinghdfaskhfsfs " + otherContext);
                    }
                }
                System.out.println("CURGNODE NOW HAS CONTEXTS OF " + context);
            }
            logger.log(String.format("Successfully evaluated position [%d, %d, %d] with resulting GreedyNode of %s with GlobalID[%d]%n)", worldX, worldY, worldZ, curGNode.toString(), greedyChunk.computeGlobalID(gMap.computeMapLevelID(curGNode.nodeID))), false);
            System.out.println("YIPPEE!!!");
            System.out.println("GChunk is " + greedyChunk);
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
