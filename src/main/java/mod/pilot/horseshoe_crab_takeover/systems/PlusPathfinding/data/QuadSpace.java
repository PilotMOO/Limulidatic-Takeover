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
        this.sizeX = sizeX; this.sizeZ = sizeZ; this.sizeY = sizeY;
    }
    public QuadSpace(int minorX, int minorY, int minorZ){
        this(minorX, minorY, minorZ, 0, 0, 0);
    }
    public QuadSpace(Vector3i minor, int x, int y, int z) {
        this(minor.x, minor.y, minor.z, x, y, x);
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
            minorX += count;
            sizeX -= count;
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
        return x >= minorX && (x - minorX) <= sizeX
                && y >= minorY && (y - minorY) <= sizeY
                && z >= minorZ && (z - minorZ) <= sizeZ;
    }
    public boolean contains(QuadSpace qSpace){
        if(this.invalid() || qSpace.invalid()) return false;
        return this.contains(qSpace.minorX, qSpace.minorY, qSpace.minorZ)
                && this.contains(qSpace.major());
    }
    //Idk if this works, but I'm not super great at geometry and that stuff so...
    // Not my problem?
    // If it doesn't work I'll rework it later...
    public boolean intersects(QuadSpace qSpace){
        if (invalid()) return false;
        //these variables could be inlined but that makes it less legible so... eh.
        int otherMajorX = qSpace.minorX + qSpace.sizeX,
                otherMajorY = qSpace.minorY + qSpace.sizeY,
                otherMajorZ = qSpace.minorZ + qSpace.sizeZ;
        int majorX = minorX + sizeX,
                majorY = minorY + sizeY,
                majorZ = minorZ + sizeZ;
        //If all the minor points overshoot the majors, it can't intercept
        if (qSpace.minorX > majorX && qSpace.minorY > majorY && qSpace.minorZ > majorZ) return false;
        //Do the same check again but reversed
        else return minorX <= otherMajorX || minorY <= otherMajorY || minorZ <= otherMajorZ;
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
        if ((qSpace.minorX - half) > majorX &&
                (qSpace.minorY - half) > majorY &&
                (qSpace.minorZ - half) > majorZ) return false;
            //Do the same check again but reversed
        else return (minorX - half) <= otherMajorX ||
                (minorY - half) <= otherMajorY ||
                (minorZ - half) <= otherMajorZ;
    }

    public boolean containsLargePoint(int x, int y, int z, double pointSize){
        if (invalid() || pointSize <= 0) return false;
        double pointHalf = pointSize / 2;
        Vector3i major = major();
        return x >= minorX - pointHalf && x <= major.x + pointHalf
                && y >= minorY - pointHalf && y <= major.y + pointHalf
                && z >= minorZ - pointHalf && z <= major.z + pointHalf;
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
     * for (int x = 0; x <= sizeX; x++){
     *     for (int y = 0; y <= sizeY; y++){
     *         for (int z = 0; z <= sizeZ; z++){
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
        public int positionX = 0, positionY = 0, positionZ = 0;

        protected abstract I getNext();
        public I step(){
            boolean xThreshold = positionX > sizeX,
                    yThreshold = positionY > sizeY,
                    zThreshold = positionZ > sizeZ;

            if (xThreshold || yThreshold || zThreshold){
                if (!zThreshold) positionZ++;
                else if (!yThreshold) {positionZ = 0; positionY++;}
                else {positionZ = positionY = 0; positionX++;}
                return getNext();
            }
            return null;
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
        private Vector3iSpaceIterator(){}
        @Override
        protected Vector3i getNext() {
            return new Vector3i(minorX + positionX,
                    minorY + positionY,
                    minorZ + positionZ);
        }
    }
    public class Vec3SpaceIterator extends SpaceIterator<Vec3> {
        private Vec3SpaceIterator(boolean centerPosition){this.center = centerPosition;}
        public boolean center;
        @Override protected Vec3 getNext() {
            double localization = center ? .5 : 0;
            return new Vec3(minorX + positionX + localization,
                    minorY + positionY + localization,
                    minorZ + positionZ + localization);
        }
    }

    public class BlockPosSpaceIterator extends SpaceIterator<BlockPos> {
        @Override protected BlockPos getNext() {
            return new BlockPos(minorX + positionX,
                    minorY + positionY,
                    minorZ + positionZ);
        }
    }
    public class MutableBlockPosSpaceIterator extends BlockPosSpaceIterator{
        private MutableBlockPosSpaceIterator(){
            mBPos = new BlockPos.MutableBlockPos(minorX, minorY, minorZ);
        }
        public final BlockPos.MutableBlockPos mBPos;

        @Override
        protected BlockPos getNext() {
            return mBPos;
        }

        @Override
        public BlockPos step() {
            boolean xThreshold = positionX > sizeX,
                    yThreshold = positionY > sizeY,
                    zThreshold = positionZ > sizeZ;

            if (xThreshold || yThreshold || zThreshold){
                if (!zThreshold){
                    mBPos.move(0, 0, 1);
                    positionZ++;
                }
                else if (!yThreshold){
                    mBPos.move(0, 1, -sizeZ);
                    positionZ = 0; positionY++;
                }
                else{
                    mBPos.move(1, -sizeY, -sizeZ);
                    positionZ = positionY = 0;
                    positionX++;
                }
                return getNext();
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "QuadSpace[" +
                "minor=[" + minorX + ", " + minorY + ", " + minorZ +
                "], sizeX=" + sizeX +
                ", sizeY=" + sizeY +
                ", sizeZ=" + sizeZ +
                ']';
    }
}
