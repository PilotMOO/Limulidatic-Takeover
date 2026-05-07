package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.Iterator;

/**
 * A 3d quad in space, expressed as a "minor" coordinate
 * (the corner with the lowest X, Y, and Z coordinate values)
 * with 3 respective sizes for the X, Y, and Z coordinates. Each size coordinate expands
 * the quad from the minor coordinate towards the positive of their value.
 * <p>A QuadSpace with negative X, Y, and/or Z sizes is assumed to be invalid</p>
 */
public class QuadSpace{
    public static QuadSpace empty(){
        return new QuadSpace(0, 0, 0, -1, -1, -1);
    }

    public QuadSpace(int minorX, int minorY, int minorZ, int sizeX, int sizeY, int sizeZ){
        this.minorX = minorX; this.minorY = minorY; this.minorZ = minorZ;
        this.sizeX = sizeX; this.sizeY = sizeY; this.sizeZ = sizeZ;
    }
    public QuadSpace(int minorX, int minorY, int minorZ){
        this(minorX, minorY, minorZ, 0, 0, 0);
    }
    public QuadSpace(Vector3i minor, int x, int y, int z) {
        this(minor.x, minor.y, minor.z, x, y, z);
    }

    /**
     * The X|Y|Z component of the minor corner of this QuadSpace.
     * <p>'Minor' corner is the corner of the QuadSpace with the lowest X, Y, and Z values</p>
     */
    public int minorX, minorY, minorZ;
    /**The size of the quad along the X|Y|Z axis*/
    public int sizeX, sizeY, sizeZ;

    /**The "minor" coordinate of the quad, the corner of the quad with the lowest X, Y, and Z values*/
    public Vector3i minor(){return new Vector3i(minorX, minorY, minorZ);}

    /**
     * "Steps" the QuadSpace {@code count} spaces (absolute) in the positive|negative
     * direction along the X axis when the argument {@code count} is positive|negative
     * <p>TL;DR expands the QuadSpace along the X axis. If the argument is negative, it
     * expands the QuadSpace along the negative X axis, positive if positive.</p>
     * <b>NEGATIVE VALUES DO NOT SHRINK THE QUADSPACE</b>
     * @param count How much to "step" the QuadSpace along the positive|negative X axis,
     *              depending on if the value is positive or negative.
     *              Feeding in a negative value expands the QuadSpace along the negative axis,
     *              it does not shrink the QuadSpace
     */
    public void stepX(int count){
        if (count < 0){
            System.out.println("STEPPING NEGATIVE X FROM " + minorX + ", size " + sizeX);
            minorX += count;
            sizeX -= count;
            System.out.println("TO " + minorX + ", size " + sizeX);
        } else sizeX += count;
    }
    /**
     * "Steps" the QuadSpace {@code count} spaces (absolute) in the positive|negative
     * direction along the Y axis when the argument {@code count} is positive|negative
     * <p>TL;DR expands the QuadSpace along the Y axis. If the argument is negative, it
     * expands the QuadSpace along the negative Y axis, positive if positive.</p>
     * <b>NEGATIVE VALUES DO NOT SHRINK THE QUADSPACE</b>
     * @param count How much to "step" the QuadSpace along the positive|negative Y axis,
     *              depending on if the value is positive or negative.
     *              Feeding in a negative value expands the QuadSpace along the negative axis,
     *              it does not shrink the QuadSpace
     */
    public void stepY(int count){
        if (count < 0) {
            minorY += count;
            sizeY -= count;
        } else sizeY += count;
    }
    /**
     * "Steps" the QuadSpace {@code count} spaces (absolute) in the positive|negative
     * direction along the Z axis when the argument {@code count} is positive|negative
     * <p>TL;DR expands the QuadSpace along the Z axis. If the argument is negative, it
     * expands the QuadSpace along the negative Z axis, positive if positive.</p>
     * <b>NEGATIVE VALUES DO NOT SHRINK THE QUADSPACE</b>
     * @param count How much to "step" the QuadSpace along the positive|negative Z axis,
     *              depending on if the value is positive or negative.
     *              Feeding in a negative value expands the QuadSpace along the negative axis,
     *              it does not shrink the QuadSpace
     */
    public void stepZ(int count){
        if (count < 0){
            minorZ += count;
            sizeZ -= count;
        } else sizeZ += count;
    }

    /**Expands the QuadSpace 1 unit along the positive X axis.<p>See {@link QuadSpace#stepX(int)}</p>*/
    public void stepX(){sizeX++;}
    /**Expands the QuadSpace 1 unit along the positive Y axis.<p>See {@link QuadSpace#stepY(int)}</p>*/
    public void stepY(){sizeY++;}
    /**Expands the QuadSpace 1 unit along the positive Z axis.<p>See {@link QuadSpace#stepZ(int)}</p>*/
    public void stepZ(){sizeZ++;}
    public void shiftMinor(int x, int y, int z){
        minorX += x; minorY += y; minorZ += z;
    }

    public boolean invalid(){
        return (sizeX | sizeY | sizeZ) < 0;
    }
    public boolean has3dSpace(){
        //If any of the sizes are <= 0, the QuadSpace does not contain any 3d space
        return sizeX > 0 && sizeY > 0 && sizeZ > 0;
    }
    /**
     * Returns a "major" coordinate along any of the 3 axis (interchangeable)
     * @param x Whether the returned coordinate is a major along the X axis
     * @param y Whether the returned coordinate is a major along the Y axis
     * @param z Whether the returned coordinate is a major along the Z axis
     * @return A "major" coordinate with the selected axis
     */
    public Vector3i major(boolean x, boolean y, boolean z){
        return new Vector3i(
                minorX + (x?sizeX:0),
                minorY + (y?sizeY:0),
                minorZ + (z?sizeZ:0));
    }

    /**
     * Creates and returns the true "major" of this quad, the corner complete opposite of the minor
     * with the largest XYZ coordinate value.
     * @return The true major of the quad
     */
    public Vector3i major(){
        return new Vector3i(minorX + sizeX, minorY + sizeY, minorZ + sizeZ);
    }

    /**
     * Checks to see if the supplied coordinate falls within the bound of the quad
     * @param point The coordinate to check
     * @return {@code true} if the coordinate falls within the quad
     */
    public boolean contains(Vector3i point){return contains(point.x, point.y, point.z);}
    public boolean contains(BlockPos point){return contains(point.getX(), point.getY(), point.getZ());}
    public boolean contains(int x, int y, int z){
        if (invalid()) return false;
        return x >= minorX && (x - minorX) < sizeX
                && y >= minorY && (y - minorY) < sizeY
                && z >= minorZ && (z - minorZ) < sizeZ;
    }
    public boolean contains(double x, double y, double z){
        if (invalid()) return false;
        return x >= minorX && (x - minorX) < sizeX
                && y >= minorY && (y - minorY) < sizeY
                && z >= minorZ && (z - minorZ) < sizeZ;
    }
    public boolean contains(QuadSpace qSpace){
        if (this.invalid() || qSpace.invalid()) return false;
        return this.contains(qSpace.minorX, qSpace.minorY, qSpace.minorZ)
                && this.contains(qSpace.major());
    }

    public boolean intersects(QuadSpace qSpace){
        if (invalid() || qSpace.invalid()) return false;
        //these variables could be inlined but that makes it less legible so... eh.
        int otherMajorX = qSpace.minorX + qSpace.sizeX,
                otherMajorY = qSpace.minorY + qSpace.sizeY,
                otherMajorZ = qSpace.minorZ + qSpace.sizeZ;
        int majorX = minorX + sizeX,
                majorY = minorY + sizeY,
                majorZ = minorZ + sizeZ;
        //Check if the minor overshoots the other major
        // or if the major undershoots the other minor for each axis
        if (minorX >= otherMajorX || majorX <= qSpace.minorX) return false;
        if (minorY >= otherMajorY || majorY <= qSpace.minorY) return false;
        return minorZ < otherMajorZ && majorZ > qSpace.minorZ;
    }
    public boolean intersects(int x, int y, int z, int sizeX, int sizeY, int sizeZ){
        if (invalid()) return false;
        //these variables could be inlined but that makes it less legible so... eh.
        int otherMajorX = x + sizeX,
                otherMajorY = y + sizeY,
                otherMajorZ = z + sizeZ;
        int majorX = minorX + this.sizeX,
                majorY = minorY + this.sizeY,
                majorZ = minorZ + this.sizeZ;
        //Check if the minor overshoots the other major
        // or if the major undershoots the other minor for each axis
        if (minorX >= otherMajorX || majorX <= x) return false;
        if (minorY >= otherMajorY || majorY <= y) return false;
        return minorZ < otherMajorZ && majorZ > z;
    }
    public boolean intersectInflated(QuadSpace qSpace, double inflation){
        if (invalid()) return false;
        double half = inflation / 2d;
        int otherMajorX = qSpace.minorX + qSpace.sizeX,
                otherMajorY = qSpace.minorY + qSpace.sizeY,
                otherMajorZ = qSpace.minorZ + qSpace.sizeZ;
        double majorX = minorX + sizeX + inflation,
                majorY = minorY + sizeY + inflation,
                majorZ = minorZ + sizeZ + inflation;
        if ((minorX - half) >= otherMajorX || majorX <= (qSpace.minorX - half)) return false;
        if ((minorY - half) >= otherMajorY || majorY <= (qSpace.minorY - half)) return false;
        return (minorZ - half) < otherMajorZ && majorZ > (qSpace.minorZ - half);
    }

    public boolean containsLargePoint(int x, int y, int z, double pointSize){
        if (invalid() || pointSize <= 0) return false;
        double pointHalf = pointSize / 2;
        Vector3i major = major();
        return x >= minorX - pointHalf && x < major.x + pointHalf
                && y >= minorY - pointHalf && y < major.y + pointHalf
                && z >= minorZ - pointHalf && z < major.z + pointHalf;
    }

    /**
     * Shorthand, computes {@link QuadSpace#distance(double, double, double, double, double, double)}
     * with the first coordinate being the minor of this QuadSpace
     * @param x X value of the second coordinate
     * @param y Y value of the second coordinate
     * @param z Z value of the second coordinate
     * @return The distance from the coordinate to the minor of this QuadSpace
     */
    public double distanceMinor(double x, double y, double z){
        return distance(minorX, minorY, minorZ, x, y, z);
    }
    /**
     * Shorthand, computes {@link QuadSpace#distance(double, double, double, double, double, double)}
     * with the first coordinate being the major of this QuadSpace
     * @param x X value of the second coordinate
     * @param y Y value of the second coordinate
     * @param z Z value of the second coordinate
     * @return The distance from the coordinate to the major of this QuadSpace
     */
    public double distanceMajor(double x, double y, double z){
        return distance(minorX + sizeX, minorY + sizeY, minorZ + sizeZ, x, y, z);
    }

    /**
     * Returns the distance of the given coordinate to the closest
     * edge or face of the QuadSpace.
     * If the coordinate is within the QuadSpace
     * (so that {@link QuadSpace#contains(double, double, double)} returns true)
     * then the distance is 0.
     * @param x the X coordinate to evaluate
     * @param y the Y coordinate to evaluate
     * @param z the Z coordinate to evaluate
     * @return the distance to the closest coordinate within the QuadSpace, NOT the center.
     * Coordinates within the bounds of the QuadSpace
     * (satisfies {@link QuadSpace#contains(double, double, double)})
     * will return 0. If the QuadSpace is invalid, distance is -1
     */
    public double distanceToEdge(double x, double y, double z){
        //Invalid QuadSpaces aren't, well... valid.
        if (invalid()) return -1;
        //If the point is within the bounds of the QuadSpace
        // the distance is zero
        if (contains(x, y, z)) return 0;

        //Is the X|Y|Z position below the minor?
        boolean subX = x < minorX, subY = y < minorY, subZ = z < minorZ;
        //If they are all below, return the distance to the minor
        if (subX && subY && subZ) return distanceMinor(x, y, z);
        //Computing major coordinates...
        int majorX = minorX + sizeX,
                majorY = minorY + sizeY,
                majorZ = minorZ + sizeZ;
        //Is the X|Y|Z position above the major?
        boolean abvX = x > majorX, abvY = y > majorY, abvZ = x > majorZ;
        //If they are all above, return the distance to the major
        if (abvX && abvY && abvZ) return distanceMajor(x, y, z);
        //Is the X|Y|Z coordinate in between the minor and major?
        // (At least one of these will be false, because if they are all true then
        // contains(x, y, z) would have returned true)
        boolean btwnX = !(subX || abvX),
                btwnY = !(subY || abvY),
                btwnZ = !(subZ || abvZ);

        //the square distance from the X coordinate
        // to the most relevant QuadSpace coordinate
        double distX, distY, distZ;
        //If the X value is between the minor and major, then the distance here is zero
        if (btwnX) distX = 0;
        else{
            //Otherwise, decide if the difference is between the minor or major
            // depending on which extreme it is on (assume abv is true if sub is false)
            // (intermediate step)
            double interX = x - (subX ? minorX : majorX);
            distX = interX * interX; //Square it, that's our distance
        }
        //Repeat above but for Y and Z
        if (btwnY) distY = 0;
        else{
            double interY = y - (subY ? minorY : majorY);
            distY = interY * interY;
        }
        if (btwnZ) distZ = 0;
        else{
            double interZ = z - (subZ ? minorZ : majorZ);
            distZ = interZ * interZ;
        }
        //Return the square root of all 3 squares added
        return Math.sqrt(distX + distY + distZ);
    }
    /**
     * Returns the distance of the closest edge or face of the argument
     * to the closest edge or face of the QuadSpace.
     * If the two QuadSpaces intersect is within the QuadSpace
     * (so that {@link QuadSpace#intersects(QuadSpace)} returns true)
     * then the distance is 0.
     * @param quad the other QuadSpace to compare edge distance
     * @return the distance to the two closest coordinates between the QuadSpaces,
     * NOT the center. Intersecting QuadSpaces
     * (satisfies {@link QuadSpace#intersects(QuadSpace)})
     * will return 0. If either QuadSpace is invalid, distance is -1
     */
    public double distanceEdgeToEdge(QuadSpace quad){
        //If either are invalid, no need to compare
        if (invalid() || quad.invalid()) return -1;
        //If they clip each other, then the distance is 0
        if (intersects(quad)) return 0;
        //The major coordinates of this QSpace
        int majorX = minorX + sizeX,
                majorY = minorY + sizeY,
                majorZ = minorZ + sizeZ;
        //The major coordinates of the argument
        int oMajorX = quad.minorX + quad.sizeX,
                oMajorY = quad.minorY + quad.sizeY,
                oMajorZ = quad.minorZ + quad.sizeZ;
        //The coordinates from this QSpace to compare
        int compareX, compareY, compareZ;
        //The coordinates from the argument QSpace to compare
        int o_compareX, o_compareY, o_compareZ;
        //If the argument major is BELOW this QSpace's minor,
        // then we want to compare these two points.
        if (oMajorX < minorX){
            compareX = minorX;
            o_compareX = oMajorX;
        } else if (quad.minorX > majorX) {
            //Else, if the argument minor is ABOVE this major,
            // then compare those two
            compareX = majorX;
            o_compareX = quad.minorX;
        }
        //Otherwise, there has to be some overlap, so we default the comparisons to 0
        else compareX = o_compareX = 0;
        
        //Y...
        if (oMajorY < minorY){
            compareY = minorY;
            o_compareY = oMajorY;
        } else if (quad.minorY > majorY) {
            compareY = majorY;
            o_compareY = quad.minorY;
        }
        else compareY = o_compareY = 0;
        
        //Z...
        if (oMajorZ < minorZ){
            compareZ = minorZ;
            o_compareZ = oMajorZ;
        } else if (quad.minorZ > majorZ) {
            compareZ = majorZ;
            o_compareZ = quad.minorZ;
        }
        else compareZ = o_compareZ = 0;

        return distance(compareX, compareY, compareZ, o_compareX, o_compareY, o_compareZ);
    }

    /**
     * Returns the distance between two coordinates.
     * @param x1 X value of the first coordinate
     * @param y1 Y value of the first coordinate
     * @param z1 Z value of the first coordinate
     * @param x2 X value of the second coordinate
     * @param y2 Y value of the second coordinate
     * @param z2 Z value of the second coordinate
     * @return the distance between the two points
     */
    public static double distance(double x1, double y1, double z1,
                                  double x2, double y2, double z2){
        double xD = x2 - x1, yD = y2 - y1, zD = z2 - z1;
        return Math.sqrt((xD * xD) +
                        (yD * yD) +
                        (zD * zD));
    }

    public Vector3iSpaceIterator getVector3iIterator(){
        return new Vector3iSpaceIterator();
    }
    public Vec3SpaceIterator getVec3Iterator(boolean centerPositions){
        return new Vec3SpaceIterator(centerPositions);
    }
    public BlockPosSpaceIterator getBlockPosIterator(boolean mutable){
        return mutable ? new MutableBlockPosSpaceIterator() : new BlockPosSpaceIterator();
    }

    /**
     * A base class for all Space Iterators available for the QuadSpace.
     * <p>Space Iterators return a new 3d vector object
     * (defined by the implementation and name) for each integer position between its bounds,
     * equivalent to using 3 nested for-loops with [x,y,z] bounds defined by the QuadSpace's
     * [x,y,z] size bounds.</p>
     * I.E:
     * <pre>
     * {@code
     * for (int x = 0; x < sizeX; x++){
     *     for (int y = 0; y < sizeY; y++){
     *         for (int z = 0; z < sizeZ; z++){
     *             return new YourVector(minorX + x, minorY + y, minorZ + z);
     *         }
     *     }
     * }
     * }
     * </pre>
     * @param <I> What type of vector this SpaceIterator will return
     *           (see {@code YourVector} in equivalent code)
     */
    private abstract class SpaceIterator<I> implements Iterable<I>{
        private SpaceIterator(){valid = has3dSpace();}
        public int positionX = 0, positionY = 0, positionZ = 0;
        protected boolean minorFlag = true;
        protected final boolean valid;

        protected abstract I getNext();
        public I step(){
            if (!valid) return null;
            boolean xThreshold = positionX >= (sizeX - 1),
                    yThreshold = positionY >= (sizeY - 1),
                    zThreshold = positionZ >= (sizeZ - 1);

            if (minorFlag && !invalid()){
                minorFlag = false;
                return getNext();
            }
            if (xThreshold && yThreshold && zThreshold) return null;

            if (!zThreshold) positionZ++;
            else if (!yThreshold) {positionZ = 0; positionY++;}
            else {positionZ = positionY = 0; positionX++;}
            return getNext();
        }

        @Override
        public @NotNull Iterator<I> iterator() {
            return new InnerIterator();
        }
        protected class InnerIterator implements Iterator<I>{
            I cur;
            @Override public boolean hasNext() {return (cur = step()) != null;}
            @Override public I next() {return cur;}
        }
    }

    public class Vector3iSpaceIterator extends SpaceIterator<Vector3i> {
        private Vector3iSpaceIterator(){super();}
        @Override
        protected Vector3i getNext() {
            return new Vector3i(minorX + positionX,
                    minorY + positionY,
                    minorZ + positionZ);
        }
    }
    public class Vec3SpaceIterator extends SpaceIterator<Vec3> {
        private Vec3SpaceIterator(boolean centerPosition){
            super();
            this.center = centerPosition;}
        public boolean center;
        @Override protected Vec3 getNext() {
            double localization = center ? .5 : 0;
            return new Vec3(minorX + positionX + localization,
                    minorY + positionY + localization,
                    minorZ + positionZ + localization);
        }
    }

    public class BlockPosSpaceIterator extends SpaceIterator<BlockPos> {
        private BlockPosSpaceIterator(){super();}
        @Override protected BlockPos getNext() {
            return new BlockPos(minorX + positionX,
                    minorY + positionY,
                    minorZ + positionZ);
        }
    }
    public class MutableBlockPosSpaceIterator extends BlockPosSpaceIterator{
        private MutableBlockPosSpaceIterator(){
            super();
            mBPos = new BlockPos.MutableBlockPos(minorX, minorY, minorZ);
        }
        public final BlockPos.MutableBlockPos mBPos;

        @Override
        protected BlockPos getNext() {
            return mBPos;
        }

        @Override
        public BlockPos step() {
            if (!valid){
                System.out.println("INVALID QUADSPACE, FUCK YOU");
                return null;
            }
            if (minorFlag){
                minorFlag = false;
                return getNext();
            }

            boolean xThreshold = positionX >= (sizeX - 1),
                    yThreshold = positionY >= (sizeY - 1),
                    zThreshold = positionZ >= (sizeZ - 1);

            if (xThreshold && yThreshold && zThreshold) return null;

            if (!zThreshold){
                mBPos.move(0, 0, 1);
                positionZ++;
            }
            else if (!yThreshold){
                mBPos.move(0, 1, -(sizeZ - 1));
                positionZ = 0;
                positionY++;
            }
            else{
                mBPos.move(1, -(sizeY - 1), -(sizeZ - 1));
                positionZ = positionY = 0;
                positionX++;
            }
            return getNext();
        }
    }

    @Override
    public String toString() {
        return "QuadSpace{" +
                "minor=[" + minorX + ", " + minorY + ", " + minorZ +
                "], sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", sizeZ=" + sizeZ +
                '}';
    }
}
