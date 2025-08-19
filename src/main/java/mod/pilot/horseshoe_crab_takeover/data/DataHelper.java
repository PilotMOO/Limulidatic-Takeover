package mod.pilot.horseshoe_crab_takeover.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class DataHelper {
    public static class ForVec3 {
        public static Vec3 getDirectionFromAToB(Entity from, Entity to) {
            return directionVec3(from.position(), to.position());
        }

        public static Vec3 getDirectionFromAToB(Vec3 from, Entity to) {
            return directionVec3(from, to.position());
        }

        public static Vec3 getDirectionFromAToB(Entity from, Vec3 to) {
            return directionVec3(from.position(), to);
        }

        public static Vec3 getDirectionFromAToB(Vec3 from, Vec3 to) {
            return directionVec3(from, to);
        }

        public static Vec3 getDirectionToAFromB(Entity to, Entity from) {
            return directionVec3(from.position(), to.position());
        }

        public static Vec3 getDirectionToAFromB(Vec3 to, Entity from) {
            return directionVec3(from.position(), to);
        }

        public static Vec3 getDirectionToAFromB(Entity to, Vec3 from) {
            return directionVec3(from, to.position());
        }

        //Helpers
        public static Vec3 directionVec3(Vec3 from, Vec3 to){
            return directionVec3(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vec3 directionVec3(Vector3d from, Vector3d to){
            return directionVec3(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vec3 directionVec3(double xF, double yF, double zF, double xT, double yT, double zT){
            double x = xT - xF, y = yT - yF, z = zT - zF;
            double d0 = Math.sqrt((x * x) + (y * y) + (z * z));
            return new Vec3(x / d0, y / d0, z / d0);
        }

        public static Vec3 from(Vector3d vector){ return new Vec3(vector.x, vector.y, vector.z); }
        public static Vec3 from(Vector3f vector){ return new Vec3(vector); }
    }
    public static class ForVector3d {
        //From
        public static Vector3d getDirectionFromAToB(Entity from, Entity to) {
            return directionVector3d(from.position(), to.position());
        }
        public static Vector3d getDirectionFromAToB(Vector3d from, Entity to) {
            return directionVector3d(from, to.position());
        }
        public static Vector3d getDirectionFromAToB(Entity from, Vector3d to) {
            return directionVector3d(from.position(), to);
        }
        public static Vector3d getDirectionFromAToB(Vector3d from, Vector3d to) {
            return directionVector3d(from, to);
        }
        //To
        public static Vector3d getDirectionToAFromB(Entity to, Entity from) {
            return directionVector3d(from.position(), to.position());
        }
        public static Vector3d getDirectionToAFromB(Vector3d to, Entity from) {
            return directionVector3d(from.position(), to);
        }
        public static Vector3d getDirectionToAFromB(Entity to, Vector3d from) {
            return directionVector3d(from, to.position());
        }
        public static Vector3d getDirectionToAFromB(Vector3d to, Vector3d from) {
            return directionVector3d(from, to);
        }

        //Helpers
        public static Vector3d directionVector3d(Vec3 from, Vec3 to) {
            return directionVector3d(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vector3d directionVector3d(Vec3 from, Vector3d to) {
            return directionVector3d(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vector3d directionVector3d(Vector3d from, Vec3 to) {
            return directionVector3d(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vector3d directionVector3d(Vector3d from, Vector3d to) {
            return directionVector3d(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vector3d directionVector3d(double xF, double yF, double zF, double xT, double yT, double zT) {
            double x = xT - xF, y = yT - yF, z = zT - zF;
            double d0 = Math.sqrt((x * x) + (y * y) + (z * z));
            return new Vector3d(x / d0, y / d0, z / d0);
        }

        public static Vector3d from(Vec3 vector){ return new Vector3d(vector.x, vector.y, vector.z); }
        public static Vector3d from(Vector3f vector){ return new Vector3d(vector); }
        public static Vector3d from(Vector3i vector){ return new Vector3d(vector); }

        public static void copy(Vector3d paper, Vec3 ink){
            paper.x = ink.x; paper.y = ink.y; paper.z = ink.z;
        }
        public static void copy(Vector3d paper, Vector3f ink){
            paper.x = ink.x; paper.y = ink.y; paper.z = ink.z;
        }
        public static void copy(Vector3d paper, double x, double y, double z){
            paper.x = x; paper.y = y; paper.z = z;
        }
    }
    public static class ForVector3f {
        //From
        public static Vector3f getDirectionFromAToB(Entity from, Entity to) {
            return directionVector3f(from.position(), to.position());
        }
        public static Vector3f getDirectionFromAToB(Vector3f from, Entity to) {
            return directionVector3f(from, to.position());
        }
        public static Vector3f getDirectionFromAToB(Entity from, Vector3f to) {
            return directionVector3f(from.position(), to);
        }
        public static Vector3f getDirectionFromAToB(Vector3f from, Vector3f to) {
            return directionVector3f(from, to);
        }
        //To
        public static Vector3f getDirectionToAFromB(Entity to, Entity from) {
            return directionVector3f(from.position(), to.position());
        }
        public static Vector3f getDirectionToAFromB(Vector3f to, Entity from) {
            return directionVector3f(from.position(), to);
        }
        public static Vector3f getDirectionToAFromB(Entity to, Vector3f from) {
            return directionVector3f(from, to.position());
        }
        public static Vector3f getDirectionToAFromB(Vector3f to, Vector3f from) {
            return directionVector3f(from, to);
        }

        //Helpers
        public static Vector3f directionVector3f(Vec3 from, Vec3 to) {
            return directionVector3f((float)from.x, (float)from.y, (float)from.z, (float)to.x, (float)to.y, (float)to.z);
        }
        public static Vector3f directionVector3f(Vec3 from, Vector3f to) {
            return directionVector3f((float)from.x, (float)from.y, (float)from.z, to.x, to.y, to.z);
        }
        public static Vector3f directionVector3f(Vector3f from, Vec3 to) {
            return directionVector3f(from.x, from.y, from.z, (float)to.x, (float)to.y, (float)to.z);
        }
        public static Vector3f directionVector3f(Vector3f from, Vector3f to) {
            return directionVector3f(from.x, from.y, from.z, to.x, to.y, to.z);
        }
        public static Vector3f directionVector3f(float xF, float yF, float zF, float xT, float yT, float zT) {
            float x = xT - xF, y = yT - yF, z = zT - zF;
            double d0 = Math.sqrt((x * x) + (y * y) + (z * z));
            return new Vector3f((float)(x / d0), (float)(y / d0), (float)(z / d0));
        }

        public static Vector3f from(Vec3 vector){ return new Vector3f((float)vector.x, (float)vector.y, (float)vector.z); }
        public static Vector3f from(Vector3d vector){ return new Vector3f((float)vector.x, (float)vector.y, (float)vector.z); }

        public static void copy(Vector3f paper, Vec3 ink){
            paper.x = (float) ink.x; paper.y = (float) ink.y; paper.z = (float) ink.z;
        }
        public static void copy(Vector3f paper, Vector3d ink){
            paper.x = (float) ink.x; paper.y = (float) ink.y; paper.z = (float) ink.z;
        }
        public static void copy(Vector3f paper, float x, float y, float z){
            paper.x = x; paper.y = y; paper.z = z;
        }
    }

    /**
     * NOTE! All values returned by these methods that involve higher-precision initial values (floats, double, variables that contain said values)
     * WILL have their values FLOORED before being applied (unless stated otherwise)
     * <p>Effectively, converting a {@code Vector3d[6.7, 8.9, -4.1]} to {@code Vector3i} via {@link DataHelper.ForVector3i#from(Vector3d)} will return a {@code Vector3i[6, 8, -4]}</p>
     * If Ceiling is preferred, utilize the ceiling-explicit methods
     */
    public static class ForVector3i {
        private static int floor(double a) { return (int)Math.floor(a); }
        private static int ceil(double a) { return (int)Math.ceil(a); }
        public static Vector3i from(Vec3 vector){ return new Vector3i(floor(vector.x), floor(vector.y), floor(vector.z)); }
        public static Vector3i from(Vector3d vector){ return new Vector3i(floor(vector.x), floor(vector.y), floor(vector.z)); }
        public static Vector3i from(Vector3f vector){ return new Vector3i(floor(vector.x), floor(vector.y), floor(vector.z)); }

        public static void copy(Vector3i paper, Vec3 ink){
            paper.x = floor(ink.x); paper.y = floor(ink.y); paper.z = floor(ink.z);
        }
        public static void copy(Vector3i paper, Vector3d ink){
            paper.x = floor(ink.x); paper.y = floor(ink.y); paper.z = floor(ink.z);
        }
        public static void copy(Vector3i paper, Vector3f ink){
            paper.x = floor(ink.x); paper.y = floor(ink.y); paper.z = floor(ink.z);
        }
        public static void copy(Vector3i paper, int x, int y, int z){
            paper.x = x; paper.y = y; paper.z = z;
        }

        public static Vector3i ceilFrom(Vec3 vector){ return new Vector3i(ceil(vector.x), ceil(vector.y), ceil(vector.z)); }
        public static Vector3i ceilFrom(Vector3d vector){ return new Vector3i(ceil(vector.x), ceil(vector.y), ceil(vector.z)); }
        public static Vector3i ceilFrom(Vector3f vector){ return new Vector3i(ceil(vector.x), ceil(vector.y), ceil(vector.z)); }

        public static void ceilCopy(Vector3i paper, Vec3 ink){
            paper.x = ceil(ink.x); paper.y = ceil(ink.y); paper.z = ceil(ink.z);
        }
        public static void ceilCopy(Vector3i paper, Vector3d ink){
            paper.x = ceil(ink.x); paper.y = ceil(ink.y); paper.z = ceil(ink.z);
        }
        public static void ceilCopy(Vector3i paper, Vector3f ink){
            paper.x = ceil(ink.x); paper.y = ceil(ink.y); paper.z = ceil(ink.z);
        }

        public static double flatDistance(Vector3i a, Vector3i b){
            double x = a.x - b.x, z = a.z - b.z;
            return Math.sqrt(x * x + z * z);
        }
    }

    public static double lerp(double a, double b, double partial){
        return a + (b - a) * partial;
    }
    public static float lerp(float a, float b, float partial){
        return a + (b - a) * partial;
    }

    /**
     * Sets the bit of a "word" (long) at the given offset to whatever the bit argument is
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @param bit the bit (in the form of a boolean) to write. 0 or 1
     * @return A new "word" that is identical to the "word" argument except for the bit at the supplied index being changed to the supplied bit argument.
     * May be fully identical if the original bit was the same as the argument
     */
    public static long writeBit(final long word, final int bitOffset, boolean bit){
        return bit ? bit1AtPosition(word, bitOffset) : bit0AtPosition(word, bitOffset);
    }
    /**
     * Sets the bit at the supplied index of the "word" (long) to 1, regardless of the original value
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @return A new "word" that is identical to the "word" argument except for the bit at the supplied index being changed to 1.
     * May be fully identical if the original bit was the same as the argument
     */
    public static long bit1AtPosition(final long word, final int bitOffset){
        //Create a mask by creating a long[00...01], shift by the offset, then "Or" it against the word. return
        return word | (1L << bitOffset);
    }
    /**
     * Sets the bit at the supplied index of the "word" (long) to 0, regardless of the original value
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @return A new "word" that is identical to the "word" argument except for the bit at the supplied index being changed to 0.
     * May be fully identical if the original bit was the same as the argument
     */
    public static long bit0AtPosition(final long word, final int bitOffset){
        //"Not" the word (I.E. invert all bits, so 1 becomes 0 and vise versa)
        //"Or" it by the mask-- a bit of 1 offset to the position, so [00...1...00] where 1 is the wanted bit position setting it to 1
        //Then "Not" it again, so it goes back to the original BUT the part masked (which set the "NOT" to 1) is now 0
        return ~(~word | (1L << bitOffset));
    }
    /**
     * Writes a list of bits to the supplied word then returns the result. Does NOT modify any of the variables, just returns a new "word" (long)
     * <p>The "ink" must have all of its relevant bits secluded to the first bits of the word within the inkRange as defined by the last argument.
     * The rest of the unrelated bits in the word need to be 0</p>
     * @param word the "word" (long) to write to (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to modify. Index 0 is the first bit farthest to the right.
     * @param ink the "word" containing all the bits to write to the original word. All relevant bits must start
     *           from index 0 out to the supplied ink range-- all other bits MUST BE 0, otherwise corruption may occur.
     * @param inkRange how many bits to expect to write from the ink. Do NOT add more bits to the ink than allocated by the inkRange.
     *                It can corrupt bits outside the range otherwise.
     * @return A new "word" that is identical to the "word" argument except for the bits at the supplied index being changed
     * to the bits supplied in the ink. May be fully identical if the original bits were the same as the argument
     */
    public static long writeRange(final long word, final int bitOffset, final long ink, final int inkRange){
        //Define a mask of all 1's, bitshift to the right by the inkRange, then NOT.
        //Then shift the mask over by the bitOffset then NOT again so all the bits will be 1's excluding the 0's covering the
        //area where the wanted bits are
        long mask = ~(~(-1L << inkRange) << bitOffset);
        //Mask all the wanted bits in the word to 0 by "Not"ing the word, "Or"ing the result with the mask
        //(setting all values within the range of the mask in the negative word to 1)
        //then "Not"ing the word again to return to the original value, but with the wanted bits wiped and set to 0.
        //Example: starting with bits [11111111], and we want to mask 4 bits that are offset by 1. Result would be [11100001]
        long wordMasked = ~(~word | mask);
        return wordMasked | (ink << bitOffset); //Finally, "Or" in the ink (bits to write)
    }

    /**
     * Locates a range of bits within the "word" then isolates it, returning a word with all the wanted bits
     * pushed to the first indexes with everything else defaulted to 0
     * @param word the "word" (long) to read from (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bits to read. Index 0 is the first bit farthest to the right.
     * @param range how many bits to read, starting from the index outwards. Can NOT be greater than or equal to 64 (the amount of bits within a long)
     * @return a new "word" with all the wanted bits (defined by the bitOffset and range) isolated to the starting index outwards.
     * All other bits default to zero
     */
    public static long isolateRange(final long word, final int bitOffset, final int range){
        //Shift the word over by the offset to shave off all irrelevant bits in front of the wanted ones (seen in [word >>> bitOffset])
        //Create a mask of full 1's then bitshift in as many zeros as there are bits to save (seen in [-1L << range])
        //Then use NOT-wise bitmasking to preserve the portion we want while defaulting everything else to 0 (seen in the nested ~(~ )
        //NOT-wise bitmasking works by creating a mask that covers everything wanted with 0's and everything else with 1's
        //Bits: [10110100], we want the first 4, so we make a bitmask of [11110000]. NOT the bits to [01001011] then OR the mask to get [11111011]
        //Finally, NOT again to get [00001011]-- isolating and preserving the first 4 bits while dumping the rest
        //[Scaled down model, longs are comprised of 64 bits, not 8]
        return ~(~(word >>> bitOffset) | -1L << range);
    }
    //ToDo: TEST "writeRange(args...)" and "isolateRange(args...)" using a new complex BitPackage wand that uses more than 1 bit per obj
    /**
     * Returns a boolean identical to the bit at the given index of the word.
     * @param word the "word" (long) to read from (is NOT modified in the process, returns a new variable)
     * @param bitOffset the "offset" (index) of the bit to read. Index 0 is the first bit farthest to the right.
     * @return {@code true} if the bit at the index was 1, {@code false} if the bit was 0
     */
    public static boolean readBitAt(final long word, final int bitOffset){
        //Shift ALL bits over to the right by the offset (including the sign bit, hence ">>>" and not ">>") so the wanted bit is the first bit
        //Then, mask out all other bits by setting them to zero. If the first bit is one [00...01 = 1] return true
        //Else it is [00...00 = 0] which is zero, meaning the bit to test was 0.
        return ((word >>> bitOffset) & 1L) == 1;
    }
}
