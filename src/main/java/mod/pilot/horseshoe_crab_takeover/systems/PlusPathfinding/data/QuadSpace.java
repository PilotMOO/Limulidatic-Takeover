package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import org.joml.Vector3i;

/**
 * A 3d quad in space, expressed as a "minor" coordinate
 * (the corner with the lowest X, Y, and Z coordinate values)
 * with 3 respective sizes for the X, Y, and Z coordinates. Each size coordinate expands
 * the quad from the minor coordinate towards the positive of their value
 * @param minor The "minor" coordinate of the quad, the corner of the quad with the lowest
 *              XYZ value
 * @param sizeX The size of the quad along the X axis
 * @param sizeY The size of the quad along the Y axis
 * @param sizeZ The size of the quad along the Z axis
 */
public record QuadSpace(Vector3i minor, int sizeX, int sizeY, int sizeZ) {
    public static QuadSpace INVALID = new QuadSpace(new Vector3i(), -1, -1, -1);
    public boolean invalid(){return this.equals(INVALID);}
    /**
     * Returns a "major" coordinate along any of the 3 axis (interchangeable)
     * @param x Whether the returned coordinate is a major along the X axis
     * @param y Whether the returned coordinate is a major along the Y axis
     * @param z Whether the returned coordinate is a major along the Z axis
     * @return A "major" coordinate with the selected axis
     */
    public Vector3i getMajor(boolean x, boolean y, boolean z){
        return new Vector3i(
                minor.x + (x?sizeX:0),
                minor.y + (y?sizeY:0),
                minor.z + (z?sizeZ:0));
    }

    /**
     * Creates and returns the true "Major" of this quad, the corner complete opposite of the minor
     * with the largest XYZ coordinate value.
     * @return The true major of the quad
     */
    public Vector3i getMajor(){
        return new Vector3i(minor.x + sizeX, minor.y + sizeY, minor.z + sizeZ);
    }

    /**
     * Checks to see if the supplied coordinate falls within the bound of the quad
     * @param point The coordinate to check
     * @return {@code true} if the coordinate falls within the quad
     */
    public boolean contains(Vector3i point){return contains(point.x, point.y, point.z);}
    public boolean contains(int x, int y, int z){
        if (invalid()) return false;
        Vector3i major = getMajor();
        return x >= minor.x && x <= major.x
                && y >= minor.y && y <= major.y
                && z >= minor.z && z <= major.z;
    }

    public boolean containsLargePoint(int x, int y, int z, double pointSize){
        if (invalid()) return false;
        double pointHalf = pointSize / 2;
        Vector3i major = getMajor();
        return x >= minor.x - pointHalf && x <= major.x + pointHalf
                && y >= minor.y - pointHalf && y <= major.y + pointHalf
                && z >= minor.z - pointHalf && z <= major.z + pointHalf;
    }
}
