package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

/**
 * A class for storing objects in 3d space compactly in a small bit array, assigning only as few bits as required/defined per object--
 * allowing for clean, small, and swift computation and storage.
 * <p>Note! "Coordinate Index" refers to the human-legible {@code [x,y,z]} coordinates within the array.</p>
 * "Contextual Index" refers to the ACTUAL index within the flat array,
 * as the bits/objects are stored within a flat 1d array as supposed to a multidimensional array.
 * <p>(Bits are stored in a long array, as solo bits are not available in Java)</p>
 * @param <T> The type of object this BitPackage stores
 */
public abstract class BitPackage3d<T> {
    /**
     * Creates a new BitPackage3d with a defined amount of bits per object and the size of the X/Y/Z axes
     * @param bitsPerObject The amount of bits reserved per-object
     * @param sizeX The size of the X axis in the flat-3d array
     * @param sizeY The size of the Y axis in the flat-3d array
     * @param sizeZ The size of the Z axis in the flat-3d array
     */
    public BitPackage3d(int bitsPerObject, int sizeX, int sizeY, int sizeZ){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.bitsPerObject = bitsPerObject;
        bits = new long[Math.floorDiv(sizeX * sizeY * sizeZ * bitsPerObject, 64) + 1]; //Floor Div plus 1 for effective ceiling
        bitMail = new long[Math.floorDiv(bitsPerObject, 64) + 2];
        yStep = sizeZ * bitsPerObject;
        xStep = sizeY * sizeZ * bitsPerObject;
    }

    /**Defines how many bits are reserved for an object within the array*/
    public final int bitsPerObject;
    /**The size of the X|Y|Z axis within the flat-3d bit array*/
    public final int sizeX, sizeY, sizeZ;
    /**The amount of bit indexes to skip when moving +1 on the X|Y axis. No step value for the Z axis as it is identical to {@link BitPackage3d#bitsPerObject}*/
    public final int xStep, yStep;
    /**The stored bits of this BitPackage.
     * <p>Do NOT edit directly unless you are CERTAIN you know what you are doing-- use any of the read/write methods to modify this array instead</p>
     * Stored as a long array as solo bits are not available in Java*/
    protected final long[] bits;
    /**An intermediate long array used and recycled for each read|write operation, as to not make a bunch of arrays that just immediately get discarded*/
    public final long[] bitMail;

    /**
     * Computes the contextual index of the first bit of a given object within the BitPackage with a coordinate index of {@code [x,y,z]}.
     * <p>WILL throw an error if the supplied coordinate index is out of the range of the BitPackage</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The index of the long containing the first bit of the object stored at the coordinate index of {@code [x,y,z]} (contextual index),
     * for use in reading or writing a given object from/to the bit array
     */
    public int computeContextualIndex(int x, int y, int z){
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) throw new IndexOutOfBoundsException(
                "INVALID Coordinates ["+x+", "+y+", "+z+"] for BitPackage3d of size ["+sizeX+", "+sizeY+", "+sizeZ+"]"
        );
        //zStep does not exist because it is the same as bitsPerObject
        return Math.floorDiv((xStep * x) + (yStep * y) + (bitsPerObject * z), 64);
    }
    /**
     * Same as {@link BitPackage3d#computeContextualIndex(int, int, int)}
     * BUT it does NOT validate the range of the coordinates and will not throw an error if the coordinates are invalid.
     * <p>Faster than its guarded variant, but may return other unrelated bits if used in cases where the safety of the index bounds are uncertain</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The index of the long containing the first bit of the object stored at the coordinate index of {@code [x,y,z]} (contextual index),
     * for use in reading or writing a given object from/to the bit array
     */
    public int computeContextualIndexUNSAFE(int x, int y, int z){
        return Math.floorDiv((xStep * x) + (yStep * y) + (bitsPerObject * z), 64);
    }

    /**
     * Computes the bit index of the first bit for the object stored at the supplied coordinate index.
     * <p>Bit indexes is the count of bits between index 0 and the wanted bit,
     * effectively {@code (contextualIndex * 64) + bitOffset},
     * where bitOffset is the amount of bits between the first bit in the "word" (long) at the contextual index</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return the bit index of the first bit of the object stored at the supplied coordinate index
     */
    public int computeBitIndex(int x, int y, int z){
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) throw new IndexOutOfBoundsException(
                "INVALID Coordinates ["+x+", "+y+", "+z+"] for BitPackage3d of size ["+sizeX+", "+sizeY+", "+sizeZ+"]"
        );
        //zStep does not exist because it is the same as bitsPerObject
        return (xStep * x) + (yStep * y) + (bitsPerObject * z);
    }
    /**
     * Same as {@link BitPackage3d#computeBitIndex(int, int, int)}
     * BUT it does NOT validate the range of the coordinates and will not throw an error if the coordinates are invalid.
     * <p>Faster than its guarded variant, but may return other unrelated bits if used in cases where the safety of the index bounds are uncertain</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return the bit index of the first bit of the object stored at the supplied contextual index
     */
    public int computeBitIndexUNSAFE(int x, int y, int z){
        //zStep does not exist because it is the same as bitsPerObject
        return (xStep * x) + (yStep * y) + (bitsPerObject * z);
    }

    /**
     * Returns the amount of objects between the object located at contextual index 0 (inclusive)
     * and the object at the supplied coordinate index (exclusive)
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The amount of objects between contextual index 0 and the object at the supplied coordinate index. Excludes self, includes index 0.
     */
    public int objectsBetween(int x, int y, int z){
        return (x * sizeY * sizeZ) + (y * sizeZ) + z;
    }

    /**
     * Reads and returns the bits of a given object at the given coordinate index via the {@link BitPackage3d#bitMail}
     * <p>Shorthand, computes the contextual index and the bit offset then invokes {@link BitPackage3d#readBits(int, int)}</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The bitMail containing all the bits pertaining to a given object at the supplied coordinate index
     */
    public long[] readBits(int x, int y, int z){
        int contextIndex = computeContextualIndex(x, y, z);
        int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64);
        return readBits(contextIndex, bitOffset);
    }
    /**
     * Reads and returns the bits of a given object at the given contextual index via the {@link BitPackage3d#bitMail}
     * @param index The contextual index of the object to read the bits from
     * @param bitOffset The bit offset of the first bit of the desired object to read the bits of relative to the first bit within the containing "word" (long)
     * @return The bitMail containing all the bits pertaining to a given object at the supplied contextual index
     */
    public long[] readBits(int index, int bitOffset){
        int lastBit = bitsPerObject + bitOffset;
        int pages = Math.floorDiv(lastBit, 64);
        if (lastBit - (pages * 64) > 0) pages++;
        System.arraycopy(bits, index, bitMail, 0, pages);
        return bitMail;
    }

    /**
     * Same as {@link BitPackage3d#readBits(int, int, int)}, but uses the faster, UNSAFE method to compute the contextual index.
     * <p>Faster, but not recommended unless you are CERTAIN that the supplied {@code [x,y,z]} coordinate index is within the bounds of the BitPackage</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The bitMail containing all the bits pertaining to a given object at the supplied coordinate index
     */
    public long[] readBitsUNSAFE(int x, int y, int z){
        int contextIndex = computeContextualIndexUNSAFE(x, y, z);
        int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64);
        return readBits(contextIndex, bitOffset);
    }

    /**
     * Modifies (or "writes") the bits of the object at the supplied coordinate index with the supplied bits (or "ink")
     * <p>Shorthand, computes the contextual index and "page count" then invokes {@link BitPackage3d#writeBits(int, long[], int)}</p>
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @param ink The long array of bits to "write" in the position of the old bits at the supplied coordinate index
     */
    public void writeBits(int x, int y, int z, long[] ink){
        int contextIndex = computeContextualIndex(x, y, z);
        int lastBit = bitsPerObject + (computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64));
        int pages = Math.floorDiv(lastBit, 64);
        if (lastBit - (pages * 64) > 0) pages++;
        writeBits(contextIndex, ink, pages);
    }
    /**
     * Modifies (or "writes") the bits of the object at the supplied coordinate index with the supplied bits (or "ink")
     * @param contextIndex The contextual index of the object to write over
     * @param ink The long array of bits to "write" in the position of the old bits at the supplied contextual index
     * @param pages The amount of "words" (longs) of the ink to write to the bit array
     */
    public void writeBits(final int contextIndex, final long[] ink, final int pages){
        System.arraycopy(ink, 0, bits, contextIndex, pages);
    }

    /**
     * An unsafe variant of {@link BitPackage3d#writeBits(int, int, int, long[])} that writes ALL the bits to the BitPackage
     * and will not truncate the copying to the expected {@link BitPackage3d#bitsPerObject}.
     * <p>Only use if you are CERTAIN you want to modify ALL words/bits after the index to how many pages the supplied ink contains,
     * and ONLY if you KNOW what you are doing.
     * May modify/corrupt other bits if used in cases where the safety of the index bounds are uncertain</p>
     * Shorthand, computes the contextual index then invokes {@link BitPackage3d#writeBitsUNSAFE(int, long[])}
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @param ink The long array of bits to "write" in the position of the old bits at the supplied coordinate index
     */
    public void writeBitsUNSAFE(int x, int y, int z, long[] ink){
        writeBitsUNSAFE(computeContextualIndex(x, y, z), ink);
    }
    /**
     * An unsafe variant of {@link BitPackage3d#writeBits(int, long[], int)} that writes ALL the bits to the BitPackage
     * and will not truncate the copying to the expected {@link BitPackage3d#bitsPerObject}.
     * <p>Only use if you are CERTAIN you want to modify ALL words/bits after the index to how many pages the supplied ink contains,
     * and ONLY if you KNOW what you are doing.
     * May modify/corrupt other bits if used in cases where the safety of the index bounds are uncertain</p>
     * @param index The contextual index of the object to write over
     * @param ink The long array of bits to "write" in the position of the old bits at the supplied contextual index
     */
    public void writeBitsUNSAFE(int index, long[] ink){
        System.arraycopy(ink, 0, bits, index, ink.length);
    }

    /**
     * Reads the bits at the supplied coordinate index and computes the object from the resulting bits
     * as defined by the {@link BitPackage3d#fromBits} method
     * @param x The X coordinate of the object
     * @param y The Y coordinate of the object
     * @param z The Z coordinate of the object
     * @return The object computed from the bits at the supplied coordinate index
     */
    public T readObject(int x, int y, int z){
        int contextIndex = computeContextualIndex(x, y, z);
        int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64);
        return fromBits(bitOffset, readBits(contextIndex, bitOffset));
    }
    /**
     * Reads the bits at the supplied contextual index and computes the object from the resulting bits
     * as defined by the {@link BitPackage3d#fromBits} method
     * @param index The contextual index of the object
     * @param bitOffset The offset of the first desired bit of the object within the "word" (long) relative to the first bit of the word
     * @return The object computed from the bits at the supplied contextual index
     */
    public T readObject(int index, int bitOffset){
        return fromBits(bitOffset, readBits(index, bitOffset));
    }

    /**
     * Writes an object into the BitPackage at the supplied coordinate index.
     * @param x The X coordinate to write the object to
     * @param y The Y coordinate to write the object to
     * @param z The Z coordinate to write the object to
     * @param obj The object to write into the BitPackage at the supplied coordinate index
     */
    public void writeObject(int x, int y, int z, T obj){
        int contextIndex = computeContextualIndex(x, y, z);
        int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64);
        int lastBit = bitsPerObject + bitOffset;
        int pages = Math.floorDiv(lastBit, 64);
        if (lastBit - (pages * 64) > 0) pages++;
        writeBits(contextIndex, toBits(obj, bitOffset, readBits(contextIndex, bitOffset)), pages);
    }
    /**
     * Writes an object into the BitPackage at the supplied contextual index.
     * @param index The contextual index to write the object to
     * @param obj The object to write into the BitPackage at the supplied contextual index
     * @param bitOffset The offset of the first desired bit of the object within the "word" (long) relative to the first bit of the word
     */
    public void writeObject(int index, T obj, int bitOffset){
        int lastBit = bitsPerObject + bitOffset;
        int pages = Math.floorDiv(lastBit, 64);
        if (lastBit - (pages * 64) > 0) pages++;
        writeBits(index, toBits(obj, bitOffset, bitMail), pages);
    }

    /**
     * An unsafe variant of {@link BitPackage3d#writeObject(int, int, int, Object)} that uses the unsafe methods for better performance.
     * @param x The X coordinate to write the object to
     * @param y The Y coordinate to write the object to
     * @param z The Z coordinate to write the object to
     * @param obj The object to write into the BitPackage at the supplied coordinate index
     */
    public void writeObjectUNSAFE(int x, int y, int z, T obj){
        int contextIndex = computeContextualIndexUNSAFE(x, y, z);
        int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64);
        int lastBit = bitsPerObject + bitOffset;
        int pages = Math.floorDiv(lastBit, 64);
        if (lastBit - (pages * 64) > 0) pages++;
        writeBits(contextIndex, toBits(obj, bitOffset, bitMail), pages);
    }


    /**
     * Packs the supplied object to bits packaged into the supplied long array.
     * @param obj The object to pack
     * @param bitOffset The amount of bits within the first "word" (long) of the supplied array that should NOT be modified.
     *                  Keep changes to the bits within and immediately after the offset.
     * @param bitMail The supplied long array to write/modify the bits to
     * @return A long array with the modified "written" bits. Should be the same array fed into the arguments
     */
    protected abstract long[] toBits(T obj, int bitOffset, long[] bitMail);
    /**
     * Unpacks the stored bit information from the supplied long array and returns the computed object.
     * <p>Only the bits included and after the bitOffset in the first "word" (long) contain the relevant data.
     * Avoid reading any bits before the bitOffset, else you might incorrectly read the bits for the object.</p>
     * @param bitOffset The amount of bits in the first "word" (long) that are not relevant to the desired object
     * @param bitMail The long array containing the relevant data bits, packaged as "words" (longs). Some bits are NOT relevant to the requested object.
     * @return The unpacked object, computed from the supplied bits
     */
    protected abstract T fromBits(int bitOffset, long[] bitMail);

    @Override
    public String toString() {
        StringBuilder base = new StringBuilder("BitPackage3d[\n");
        StringBuilder builder = new StringBuilder();
        for (long bit : bits){
            builder.setLength(0);
            StringBuilder binary = new StringBuilder(Long.toBinaryString(bit));
            while (binary.length() < 64) binary.insert(0,"0");
            builder.append(binary);
            builder.insert(7, " ");
            builder.insert(15, " ");
            builder.insert(23, " ");
            builder.insert(31, " ");
            builder.insert(39, " ");
            builder.insert(47, " ");
            builder.insert(55, " ");
            builder.insert(63, " ");
            builder.append(",\n ");
            base.append(builder);
        }
        base.setLength(base.length() - 3);
        return base.append("]").toString();
    }
}
