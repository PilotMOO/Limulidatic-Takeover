package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;

/**
 * A dynamic, system-ready extension of {@link BitPackage3d} that allows Functions to be defined on-init for the to/from methods
 * @param <T> The type of object this BitPackage stores
 */
public class DynamicBitPackage3d<T> extends BitPackage3d<T>{
    /**
     * Creates a new DynamicBitPackage3d with a defined amount of bits per object and the size of the X/Y/Z axes
     *<p>Also allows you to define the to/from bit methods via functions supplied in the constructor arguments</p>
     * @param bitsPerObject The amount of bits reserved per-object
     * @param sizeX         The size of the X axis in the flat-3d array
     * @param sizeY         The size of the Y axis in the flat-3d array
     * @param sizeZ         The size of the Z axis in the flat-3d array
     */
    public DynamicBitPackage3d(int bitsPerObject, int sizeX, int sizeY, int sizeZ,
                               TriFunction<T, Integer, long[], long[]> toBits,
                               BiFunction<Integer, long[], T> fromBits) {
        super(bitsPerObject, sizeX, sizeY, sizeZ);
        this.toBits = toBits; this.fromBits = fromBits;
    }

    public TriFunction<T, Integer, long[], long[]> toBits;
    @Override protected long[] toBits(T obj, int bitOffset, long[] bitMail) {return toBits.apply(obj, bitOffset, bitMail);}
    public BiFunction<Integer, long[], T> fromBits;
    @Override protected T fromBits(int bitOffset, long[] bitMail) {return fromBits.apply(bitOffset, bitMail);}
}
