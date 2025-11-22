package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.function.BiFunction;

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

    public static class GroundedNodeEvaluator{
        public static boolean evaluateNode(Level level, BlockPos bPos){
            level.getBlockState(bPos);
        }
        public static boolean evaluateSection(Level level, QuadSpace section,
                                              BiFunction<Level, BlockPos, Boolean> evaluator,
                                              boolean assumeWithinBounds){
            Vector3i minor = section.minor;
            //If we aren't assuming everything is okie dokie
            //Then we need to see if the QuadSpace would extend beyond chunk boundaries
            if (!assumeWithinBounds){
                boolean xSpill = (minor.x % 16) + section.sizeX > 16,
                        ySpill = (minor.y % 16) + section.sizeY > 16,
                        zSpill = (minor.z % 16) + section.sizeZ > 16;
                if (xSpill || ySpill || zSpill) {
                    QuadSpace[] divide = new QuadSpace[1];
                    //If so, set up the Array of all divided QuadSpaces

                    //Make the first QuadSpace at the same minor as the original QuadSpace
                    //Take the distance between the minor edge of the Chunk
                    // and the minor of the QuadSpace, subtract that from 16
                    //This is the distance from the major edge for the given axis
                    //to the QuadSpace's minor point
                    int xMax = 16 - (minor.x % 16); //X-axis...
                    int yMax = 16 - (minor.y % 16); //Y-axis...
                    int zMax = 16 - (minor.z % 16); //Z-axis...
                    //That's the max size we can get away with for the first QuadSpace, so expand it.
                    divide[0] = new QuadSpace(minor); //Add it to the array
                    /**/

                    //First, let's see if we need to construct more QuadSpaces along the X-axis
                    if (xSpill) {
                        int xCycle = xMax; //Cycle tracker, the total amount of X-space we have covered
                        int nXMax = xMax; //new Max size we can get away with for the next QuadSpace
                        Vector3i nextMinor = new Vector3i(minor);
                        while (xCycle < section.sizeX) {
                            //Make the new minor coordinate, it will be the same as the initial
                            // minor, but offset by the X distance covered by the last QuadSpace
                            nextMinor = nextMinor.add(nXMax, 0, 0, new Vector3i());
                            //new Max size we can get away with...
                            //Make sure to check that the next "max size" won't exceed the bounds of the
                            // initial QuadSpace
                            nXMax = Math.min(section.sizeX - xCycle, 16 - (nextMinor.x % 16));
                            //Add the new QuadSpace to the array
                            divide = expandAndAdd(divide, new QuadSpace(nextMinor, nXMax, yMax, zMax));
                            //If we are also expecting more than 1 QuadSpace for the Z axis too
                            if (zSpill){
                                //Go ahead and compute an additional one, with the next Z offset
                                //ToDo: FINISH SPLITTING LOOP
                                //God this shit is complex blehhhh
                                //I hope it'll be easy enough to pick back up... :/
                                int zCycle = zMax;
                                while (zCycle < section.sizeZ){
                                    Vector3i nextZMinor = nextMinor.add(0, 0, )

                                }
                            }
                            xCycle += nXMax; //...and update the X-axis tracker for next cycle
                        }
                    }

                }
            }


            int chunkMinorX = minor.x >> 4, chunkMinorZ = minor.z >> 4;

            LevelChunk chunk = level.getChunk()
        }
    }

    @SuppressWarnings("unchecked")
    private static <I> I[] expandAndAdd(I[] array, I object){
        int newSize = array.length + 1;
        I[] newArray = (I[]) new Object[newSize];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[newSize - 1] = object;
        return newArray;
    }
}
