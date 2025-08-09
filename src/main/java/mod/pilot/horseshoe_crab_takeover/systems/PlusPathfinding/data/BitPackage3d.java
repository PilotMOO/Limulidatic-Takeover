package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import java.util.function.Function;

/**
 * A class for storing objects in 3d space compactly in a small bit array, assigning only as few bits as required/defined per object--
 * allowing for clean, small, and swift computation and storage.
 * <p>Note! "Coordinate Index" refers to the human-legible {@code [x,y,z]} coordinates within the array.</p>
 * "Contextual Index" refers to the ACTUAL index within the flat array,
 * as the bits/objects are stored within a flat 1d array as supposed to a multidimensional array.
 * <p>(Bits are stored in a boolean array, as solo bits are not available in Java)</p>
 * @param <T> The type of object this BitPackage stores
 */
public class BitPackage3d<T> {
    /**
     * Creates a new BitPackage3d with a defined amount of bits per object, the size of the X/Y/Z axes, and the to/from bit computation methods
     * @param bitsPerObject The amount of bits reserved per-object. MUST be the same as the length of the array returned by the toBits function!
     * @param sizeX The size of the X axis in the flat-3d array
     * @param sizeY The size of the Y axis in the flat-3d array
     * @param sizeZ The size of the Z axis in the flat-3d array
     * @param to A function that packs the defined object type to a bit array (boolean array)
     * @param from A function that unpacks a bit array (boolean array) to the defined object type
     */
    public BitPackage3d(int bitsPerObject, int sizeX, int sizeY, int sizeZ, Function<T, boolean[]> to, Function<boolean[], T> from){
        bits = new boolean[
                (this.sizeX = sizeX) *
                (this.sizeY = sizeY) *
                (this.sizeZ = sizeZ) *
                (this.bitsPerObject = bitsPerObject)];
        yStep = sizeZ * bitsPerObject;
        xStep = sizeY * sizeZ * bitsPerObject;
        toBits = to; fromBits = from;
    }

    /**
     * Defines how many bits are reserved for an object within the array.
     * MUST be consistent with the array length for the to/from functions.
     */
    public final int bitsPerObject;
    /**The size of the X|Y|Z axis within the flat-3d bit array*/
    public final int sizeX, sizeY, sizeZ;
    /**The amount of bit indexes to skip when moving +1 on the X|Y axis. No step value for the Z axis as it is identical to {@link BitPackage3d#bitsPerObject}*/
    public final int xStep, yStep;
    /**The stored bits of this BitPackage.
     * <p>Do NOT edit directly unless you are CERTAIN you know what you are doing-- use any of the read/write methods to modify this array instead</p>
     * Stored as a boolean array as solo bits are not available in Java
     * */
    protected final boolean[] bits;

    /**
     * Computes the contextual index of the first bit of a given object within the BitPackage with a coordinate index of {@code [x,y,z]}.
     * <p>WILL throw an error if the supplied coordinate index is out of the range of the BitPackage</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The index of the first bit of the object stored at the coordinate index of {@code [x,y,z]} (contextual index),
     * for use in reading or writing a given object from/to the bit array
     */
    public int computeIndex(int x, int y, int z){
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) throw new IndexOutOfBoundsException(
                "INVALID Coordinates ["+x+", "+y+", "+z+"] for BitPackage3d of size ["+sizeX+", "+sizeY+", "+sizeZ+"]"
        );
        //zStep does not exist because it is the same as bitsPerObject
        return (xStep * x) + (yStep * y) + (bitsPerObject * z);
    }
    /**
     * Same as {@link BitPackage3d#computeIndex(int, int, int)}
     * BUT it does NOT validate the range of the coordinates and will not throw an error if the coordinates are invalid.
     * <p>Faster than its guarded variant, but may modify/corrupt other bits if used in cases where the safety of the index bounds are uncertain</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The index of the first bit of the object stored at the coordinate index of {@code [x,y,z]} (contextual index),
     * for use in reading or writing a given object from/to the bit array
     */
    public int computeIndexUNSAFE(int x, int y, int z){
        return (xStep * x) + (yStep * y) + (bitsPerObject * z);
    }

    /**
     * Reads and returns the bits of a given object at the given coordinate index as a bit array (boolean array)
     * as used in {@link BitPackage3d#readObject(int, int, int)}
     * <p>Shorthand, computes the contextual index then invokes {@link BitPackage3d#readBits(int)}</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return A bit array (boolean array) of the bits related to the object stored at the supplied coordinate index
     */
    public boolean[] readBits(int x, int y, int z){
        return readBits(computeIndex(x, y, z));
    }
    /**
     * Reads and returns the bits of a given object at the given contextual index as a bit array (boolean array)
     * as used in {@link BitPackage3d#readObject(int)}
     * @param index the contextual index of the object to read the bits from
     * @return A bit array (boolean array) of all the bits pertaining to a given object at the supplied contextual index
     */
    public boolean[] readBits(int index){
        boolean[] objBits = new boolean[bitsPerObject];
        System.arraycopy(bits, index, objBits, 0, bitsPerObject);
        return objBits;
    }

    /**
     * Same as {@link BitPackage3d#readBits(int, int, int)}, but uses the faster, UNSAFE method to compute the contextual index.
     * <p>Faster, but not recommended unless you are CERTAIN that the supplied {@code [x,y,z]} coordinate index is within the bounds of the BitPackage</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return A bit array (boolean array) of the bits related to the object stored at the supplied coordinate index
     */
    public boolean[] readBitsUNSAFE(int x, int y, int z){
        return readBits(computeIndexUNSAFE(x, y, z));
    }

    /**
     * Modifies (or "writes") the bits of the object at the supplied coordinate index with the supplied bits (or "ink")
     * <p>Shorthand, computes the contextual index then invokes {@link BitPackage3d#writeBits(int, boolean[])}</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @param ink The bit array (boolean array) of bits to "write" in the position of the old bits at the supplied coordinate index
     */
    public void writeBits(int x, int y, int z, boolean[] ink){
        writeBits(computeIndex(x, y, z), ink);
    }
    /**
     * Modifies (or "writes") the bits of the object at the supplied coordinate index with the supplied bits (or "ink")
     * @param index The contextual index of the object to write over
     * @param ink The bit array (boolean array) of bits to "write" in the position of the old bits at the supplied contextual index
     */
    public void writeBits(int index, boolean[] ink){
        System.arraycopy(ink, 0, bits, index, bitsPerObject);
    }

    /**
     * An unsafe variant of {@link BitPackage3d#writeBits(int, int, int, boolean[])} that writes ALL the bits to the BitPackage
     * and will not truncate the copying to the expected {@link BitPackage3d#bitsPerObject}.
     * <p>Only use if you want to modify more than 1 object at once, and ONLY if you KNOW what you are doing.
     * May modify/corrupt other bits if used in cases where the safety of the index bounds are uncertain</p>
     * Shorthand, computes the contextual index then invokes {@link BitPackage3d#writeBitsUNSAFE(int, boolean[])}
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @param ink The bit array (boolean array) of bits to "write" in the position of the old bits at the supplied coordinate index
     */
    public void writeBitsUNSAFE(int x, int y, int z, boolean[] ink){
        writeBitsUNSAFE(computeIndex(x, y, z), ink);
    }
    /**
     * An unsafe variant of {@link BitPackage3d#writeBits(int, boolean[])} that writes ALL the bits to the BitPackage
     * and will not truncate the copying to the expected {@link BitPackage3d#bitsPerObject}.
     * <p>Only use if you want to modify more than 1 object at once, and ONLY if you KNOW what you are doing.
     * May modify/corrupt other bits if used in cases where the safety of the index bounds are uncertain</p>
     * @param index The contextual index of the object to write over
     * @param ink The bit array (boolean array) of bits to "write" in the position of the old bits at the supplied contextual index
     */
    public void writeBitsUNSAFE(int index, boolean[] ink){
        System.arraycopy(ink, 0, bits, index, ink.length);
    }

    /**
     * Reads the bits at the supplied coordinate index and computes the object from the resulting bits
     * as defined by the {@link BitPackage3d#fromBits} function
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The object computed from the bits at the supplied coordinate index
     */
    public T readObject(int x, int y, int z){ return fromBits.apply(readBits(x, y, z)); }
    /**
     * Reads the bits at the supplied contextual index and computes the object from the resulting bits
     * as defined by the {@link BitPackage3d#fromBits} function
     * @param index The contextual index of the object
     * @return The object computed from the bits at the supplied contextual index
     */
    public T readObject(int index){ return fromBits.apply(readBits(index)); }

    /**
     * Writes an object into the BitPackage at the supplied coordinate index.
     * <p>MAKE SURE the supplied toBits function's array is of the same length as the BitPackage's {@link BitPackage3d#bitsPerObject}!</p>
     * @param x The X coordinate to write the object to
     * @param y The Y coordinate to write the object to
     * @param z The Z coordinate to write the object to
     * @param obj The object to write into the BitPackage at the supplied coordinate index
     */
    public void writeObject(int x, int y, int z, T obj){ writeBits(computeIndex(x, y, z), toBits.apply(obj)); }
    /**
     * Writes an object into the BitPackage at the supplied contextual index.
     * <p>MAKE SURE the supplied toBits function's array is of the same length as the BitPackage's {@link BitPackage3d#bitsPerObject}!</p>
     * @param index The contextual index to write the object to
     * @param obj The object to write into the BitPackage at the supplied contextual index
     */
    public void writeObject(int index, T obj){ writeBits(index, toBits.apply(obj)); }

    /**
     * An unsafe variant of {@link BitPackage3d#writeObject(int, int, int, Object)} that uses the unsafe methods for better performance.
     * <p>ONLY USE THIS IF YOU ARE CERTAIN YOU KNOW WHAT YOU ARE DOING. It may modify/corrupt bits outside the expected range</p>
     * <p>MAKE SURE the supplied toBits function's array is of the same length as the BitPackage's {@link BitPackage3d#bitsPerObject}!</p>
     * @param x The X coordinate to write the object to
     * @param y The Y coordinate to write the object to
     * @param z The Z coordinate to write the object to
     * @param obj The object to write into the BitPackage at the supplied coordinate index
     */
    public void writeObjectUNSAFE(int x, int y, int z, T obj){
        writeBits(computeIndexUNSAFE(x, y, z), toBits.apply(obj));
    }

    /**
     * A function that takes a given object type and packs it into a bit array (boolean array).
     * <p>MAKE SURE the resulting bit array has the same length as the {@link BitPackage3d#bitsPerObject} within the related BitPackage</p>
     */
    protected final Function<T, boolean[]> toBits;
    /**
     * A function that takes a bit array (boolean array) and unpacks it into a given object type.
     * <p>The supplied bit array should have the same length as the {@link BitPackage3d#bitsPerObject} within the related BitPackage</p>
     */
    protected final Function<boolean[], T> fromBits;
}
