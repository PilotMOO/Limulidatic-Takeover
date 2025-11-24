package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;


public class GreedyNodeBuilder {
    public static GreedyNode constructGroundedNode(int x, int y, int z,
                                                   @Nullable final GreedyMap optionalMap){
        GreedyMap gMap;
        if (optionalMap == null){
            GreedyChunk gChunk = GreedyWorld.retrieveOrCreateGreedyChunk(
                    GreedyChunk.computeCoordinatesToID(x, z));
            gMap = gChunk.locateClosest(x, y, z, GreedyChunk.SearchType.MapExtension);
        } else gMap = optionalMap;
    }
    public static GreedyNode constructGroundedNode(int x, int y, int z,
                                                   @Nullable final GreedyChunk optionalChunk) {
        GreedyChunk gChunk;
        if (optionalChunk == null){
            gChunk = GreedyWorld.retrieveOrCreateGreedyChunk(GreedyChunk.computeCoordinatesToID(x, z));
        } else gChunk = optionalChunk;
    }

    //This'll create or update a (few) Greedy Chunk(s)
    // so maybe keeping it void is best, not certain
    public static void evaluateSection(Level level, QuadSpace section,
                                       GreedyNodeEvaluator evaluator,
                                       boolean assumeWithinBounds){
        if (assumeWithinBounds) evaluateContainedSection(level, section, evaluator);
        else for (QuadSpace qSpace : sectionByChunkBoundaries(section, true)) {
            evaluateContainedSection(level, qSpace, evaluator);
        }
    }
    private static void evaluateContainedSection(Level level, QuadSpace section,
                                                 GreedyNodeEvaluator evaluator){
        Vector3i minor = section.minor;
        int chunkX = minor.x >> 4, chunkZ = minor.z >> 4;
        LevelChunk chunk = (LevelChunk)level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        if (chunk == null) System.err.printf("[NODE EVALUATOR] WARNING! Failed to evaluate a chunk-sectioned QuadSpace of %s due to it failing to retrieve Chunk[%d, %d]%n", section, chunkX, chunkZ);
        else{
            LevelChunkSection chunkSlice = chunk.getSection(chunk.getSectionIndex(minor.y));
            if (chunkSlice.hasOnlyAir() && !evaluator.evaluateEvenIfOnlyAir()) return;

            QuadSpace[] cleared = new QuadSpace[1];
            QuadSpace loopBounds = cleared[0] = new QuadSpace(section.minor);
            GreedyNode.Blueprint[] blueprints = new GreedyNode.Blueprint[0];

            BlockPos.MutableBlockPos mBPos =
                    new BlockPos.MutableBlockPos(minor.x, minor.y, minor.z);
            //Ordered Y, Z, X because that's the priority of GNode growth
            // X expands first, then Z, then Y last
            // Y is last because most grounded nodes won't extend upwards
            for (int y = 0; y <= section.sizeY; y++) {
                for (int z = 0; z <= section.sizeZ; z++) {
                    for (int x = 0; x <= section.sizeY; x++){
                        boolean alreadyChecked = false;
                        for (int i = 1; i < cleared.length; i++){
                            QuadSpace qSpace = cleared[i];
                            if (qSpace != null && qSpace.contains(mBPos)) {
                                alreadyChecked = true;
                                break;
                            }
                        }
                        if (alreadyChecked) continue;

                        boolean nodeFlag = evaluator.evaluateSoloInstance(level, chunk, mBPos,
                                chunkSlice.getBlockState(mBPos.getX() % 16,
                                        mBPos.getY() % 16,
                                        mBPos.getZ() % 16));
                        if (nodeFlag){
                            Vector3i pos = DataHelper.ForVector3i.from(mBPos);
                            byte one = 1; //Fuckin hate casting bytes fuck you
                            GreedyNode.Blueprint blueprint =
                                    new GreedyNode.Blueprint(pos, one, one, one);

                            //It's already mutable, but this just clones it
                            BlockPos.MutableBlockPos stepper = mBPos.mutable();
                            //These values don't change in the first while loop
                            // so no need to call the method each time
                            int cY = stepper.getY(), cZ = stepper.getZ();
                            do{
                                blueprint.stepX();
                                stepper.move(1, 0, 0);
                            }
                            while(evaluator.evaluateSoloInstance(level, chunk, stepper,
                                    chunkSlice.getBlockState(stepper.getX() % 16,
                                            cY % 16, cZ % 16)));
                            //Expanding in the Z axis...
                            boolean cont = true; //Flag to stop stepping Z (and Y later)
                            while(cont){
                                cZ++; //Inc the Z position
                                stepper.set(pos.x - 1, stepper.getY(), cZ); //Update the stepper position
                                //set the X position back one because of the desync in the for loop,
                                // 'i' starts at 0 but the stepper steps forward 1 anyway

                                //Iterate over all X positions for this Z position
                                for (int i = 0; i < blueprint.x; i++){
                                    stepper.move(1, 0, 0); //Moving the stepper
                                    //Evaluating the new node
                                    boolean flag = evaluator.evaluateSoloInstance(level, chunk, stepper,
                                            chunkSlice.getBlockState(stepper.getX() % 16,
                                                    cY % 16, cZ % 16));
                                    if (!flag) {
                                        //Oops! This node is invalid, break out of everything.
                                        cont = false;
                                        break;
                                    }
                                }
                                //If we're okie for going forward, step the blueprint
                                if (cont) blueprint.stepZ();
                            }
                            //Finally, check the Y axis
                            // (should stop on the first checked position if it's a ground node)
                            cont = true;
                            while (cont){
                                cY++; //Inc the Y position
                                stepper.set(pos.x - 1, cY, pos.z); //Update/reset the stepper position
                                //See Z position iterator for the reason why 1 is subtracted from X

                                //Iterate over all X and Z positions for this Y position
                                for (int i = 0; i < blueprint.x; i++){
                                    //Move the stepper 1 on the X axis and reset the Z position
                                    stepper.set(stepper.getX() + 1, stepper.getY(), pos.z); //Moving the stepper
                                    for (int j = 0; j < blueprint.z; j++) {
                                        stepper.move(0, 0, 1);
                                        //Evaluating the new node
                                        boolean flag = evaluator.evaluateSoloInstance(level, chunk, stepper,
                                                chunkSlice.getBlockState(stepper.getX() % 16,
                                                        cY % 16, stepper.getZ() % 16));
                                        if (!flag) {
                                            //Oops! This position is invalid, break out of everything.
                                            cont = false;
                                            break;
                                        }
                                    }
                                }
                                //If we're okie for going forward, step the blueprint
                                if (cont) blueprint.stepY();
                            }

                            //Once it's constructed, add it to the array of blueprints
                            blueprints = DataHelper.Arrays.expandAndAdd(blueprints, blueprint);
                            //Don't forget to grab the QuadSpace and add it to the checked positions!
                            cleared = DataHelper.Arrays.putInNextValidSlot(cleared, blueprint.quadSpace());
                        }

                        mBPos.move(1, 0, 0);
                        loopBounds.stepX();
                    }
                    mBPos.move(0, 0, 1);
                    mBPos.setX(minor.x);
                    loopBounds.stepZ();
                }
                mBPos.move(0, 1, 0);
                mBPos.setZ(minor.z); mBPos.setX(minor.x);
                loopBounds.stepY();

                if (cleared.length != 1){
                    for (int i = 1; i < cleared.length; i++){
                        if (loopBounds.contains(cleared[i])) cleared[i] = null;
                    }
                }
            }

            //After all of that bullshit that probably doesn't even work
            // we need to turn the blueprints into GreedyNodes and assign them a map
            // BUT that sounds like a lot of effort so I'll just do it later blehh
            //TODO: Finish the node evaluation and assign blueprints to their respective maps
            // or just make a new map idfk
        }
    }

    /**
     * Takes a QuadSpace that (might) extend over (Greedy) Chunk boundaries and slices
     * it into multiple QuadSpaces along the boundaries.
     * No QuadSpaces should overlap, and it should cover the same area as the initial section
     * @param section The initial QuadSpace to divide across (Greedy) Chunk boundaries
     * @param GreedyChunk Are we slicing across GreedyChunk boundaries rather than MC chunks?
     *                    (GreedyChunks are 64x64 blocks wide rather than 16x16)
     * @param sectionY Whether to also slice the QuadSpaces across the Y axis too,
     *                for use in managing Chunk Sections
     *                 (not intended to be used while GreedyChunk argument is true, but does work)
     * @return an array of QuadSpaces that make up the same area as the initial QuadSpace but cut along the chunk boundaries
     * (or just an array of size 1 containing the initial QuadSpace argument
     * if it does not cross any chunk boundaries)
     */
    public static QuadSpace[] sectionByChunkBoundaries(QuadSpace section, boolean GreedyChunk, boolean sectionY){
        int sectionSize = 16;
        if (GreedyChunk) sectionSize *= 4;

        Vector3i minor = section.minor;
        QuadSpace[] divide = new QuadSpace[1];
        boolean xSpill = (minor.x % sectionSize) + section.sizeX > sectionSize,
                ySpill = sectionY && ((minor.y % sectionSize) + section.sizeY > sectionSize),
                zSpill = (minor.z % sectionSize) + section.sizeZ > sectionSize;
        if (xSpill || ySpill || zSpill) {
            //Take the distance between the minor edge of the Chunk
            // and the minor of the QuadSpace, subtract that from 16
            //This is the distance from the major edge for the given axis
            // to the QuadSpace's minor point
            //Math.min ensures that it won't make a QSpace with dimensions larger than the
            // initial QSpace if it does extend beyond a Chunk Boundary
            int xMax = Math.min(section.sizeX, sectionSize - (minor.x % sectionSize)); //X-axis...
            //If we want to section by the Y axis too, we'll need to ensure within section range
            // Same as the other two axis
            //Otherwise, we can set the max Y for a QSpace to just the initial Y size
            int yMax = sectionY ?
                    Math.min(section.sizeY,  sectionSize - (minor.y % sectionSize)) :
                    section.sizeY;
            int zMax = Math.min(section.sizeZ, sectionSize - (minor.z % sectionSize)); //Z-axis...
            //That's the max size we can get away with for the first QuadSpace, so expand it.
            divide[0] = new QuadSpace(minor, xMax, yMax, zMax); //Add it to the array

            //First, let's see if we need to construct more QuadSpaces along the X-axis
            //You could probably isolate these nested while loops into
            // Multiple methods so you don't need to have multiple instances of
            // (almost) identical while loops, but I don't want to deal with that so--
            if (xSpill) {
                int xCycle = xMax; //Cycle tracker, the total amount of X-space we have covered
                int nXMax = xMax; //new Max size we can get away with for the next QuadSpace
                Vector3i nextXMinor = new Vector3i(minor);
                while (xCycle < section.sizeX) {
                    //Make the new minor coordinate, it will be the same as the initial
                    // minor, but offset by the X distance covered by the last QuadSpace
                    nextXMinor.add(nXMax, 0, 0);
                    //Make sure to check that the next "max size" won't exceed the bounds of the
                    // initial QuadSpace
                    nXMax = Math.min(section.sizeX - xCycle, sectionSize - (nextXMinor.x % sectionSize));
                    //Add the new QuadSpace to the array
                    divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextXMinor, nXMax, yMax, zMax));

                    //If we are also expecting more than 1 QuadSpace for the Y axis too,
                    // Then compute that
                    if (ySpill){
                        int yCycle = yMax;
                        int nYMax = yMax;
                        Vector3i nextYMinor = new Vector3i(nextXMinor);
                        while (yCycle < section.sizeY){
                            nextYMinor.add(0, nYMax, 0);
                            nYMax = Math.min(section.sizeY - yCycle, sectionSize - (nextYMinor.y % sectionSize));
                            divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextYMinor, nXMax, nYMax, zMax));
                            //Finally, if we ALSO cross the Z axis too, compute another QSpace for that too
                            if (zSpill){
                                int zCycle = zMax;
                                int nZMax = zMax;
                                Vector3i nextZMinor = new Vector3i(nextYMinor);
                                while (zCycle < section.sizeZ){
                                    nextZMinor.add(0, 0, nZMax);
                                    nZMax = Math.min(section.sizeZ - zCycle, sectionSize - (nextZMinor.z % sectionSize));
                                    divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextZMinor, nXMax, nYMax, nZMax));

                                    zCycle += nZMax;
                                }
                            }
                            yCycle += nYMax;
                        }
                    }
                    //If not, see if we need to compute the Z axis
                    else if (zSpill){
                        //Same as the 3rd loop in the X-Y loop
                        //But using the default Y position and max value
                        int zCycle = zMax;
                        int nZMax = zMax;
                        Vector3i nextZMinor = new Vector3i(nextXMinor);
                        while (zCycle < section.sizeZ){
                            nextZMinor.add(0, 0, nZMax);
                            nZMax = Math.min(section.sizeZ - zCycle, sectionSize - (nextZMinor.z % sectionSize));
                            divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextZMinor, nXMax, yMax, nZMax));
                            zCycle += nZMax;
                        }
                    }
                    xCycle += nXMax; //...and update the X-axis tracker for next cycle
                }
            }
            //We still need to check the other axis
            else if (ySpill){
                int yCycle = yMax;
                int nYMax = yMax;
                Vector3i nextYMinor = new Vector3i(minor);
                while (yCycle < section.sizeY){
                    nextYMinor.add(0, nYMax, 0);
                    nYMax = Math.min(section.sizeY - yCycle, sectionSize - (nextYMinor.y % sectionSize));
                    divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextYMinor, xMax, nYMax, zMax));
                    if (zSpill){
                        int zCycle = zMax;
                        int nZMax = zMax;
                        Vector3i nextZMinor = new Vector3i(nextYMinor);
                        while (zCycle < section.sizeZ){
                            nextZMinor.add(0, 0, nZMax);
                            nZMax = Math.min(section.sizeZ - zCycle, sectionSize - (nextZMinor.z % sectionSize));
                            divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextZMinor, xMax, nYMax, nZMax));
                            zCycle += nZMax;
                        }
                    }
                    yCycle += nYMax;
                }
            }
            else {
                //Only the Z axis has spilling, so compute the missing ones...
                int zCycle = zMax;
                int nZMax = zMax;
                Vector3i nextZMinor = new Vector3i(minor);
                while (zCycle < section.sizeZ){
                    nextZMinor.add(0, 0, nZMax);
                    nZMax = Math.min(section.sizeZ - zCycle, sectionSize - (nextZMinor.z % sectionSize));
                    divide = DataHelper.Arrays.expandAndAdd(divide, new QuadSpace(nextZMinor, xMax, yMax, nZMax));
                    zCycle += nZMax;
                }
            }
            return divide;
        }
        else divide[0] = section;
        return divide;
    }
}

