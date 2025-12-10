package mod.pilot.horseshoe_crab_takeover.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataHelper {
    private static final Logger log = LoggerFactory.getLogger(DataHelper.class);

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
        public static Vector3i from(BlockPos bPos){ return new Vector3i(bPos.getX(), bPos.getY(), bPos.getZ()); }

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

    @SuppressWarnings("unchecked")
    public static class Arrays{
        public static <I> I[] insertElement(I[] array, I element, int index){
            int size = array.length;
            if (index >= size){
                array = growArray(array,size - (index + 1));
            }
            else {
                array = growArray(array, 1);
                System.arraycopy(array, index, array, index + 1, size - index);
            }
            array[index] = element;
            return array;
        }
        public static <I> I[] growArray(I[] array, int count){
            int newSize = array.length + count;
            I[] newArray = (I[]) new Object[newSize];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        public static <I> I[] expandAndAdd(I[] array, I element){
            int newSize = array.length + 1;
            I[] newArray = (I[]) new Object[newSize];
            if (newSize > 1) System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[newSize - 1] = element;
            return newArray;
        }
        public static <I> I[] cap(I[] array){
            int cIndex = array.length - 1;
            while (--cIndex > -1 && array[cIndex] != null);
            I[] newArray = (I[])new Object[cIndex + 1];
            System.arraycopy(array, 0, newArray, 0, cIndex + 1);
            return newArray;
        }
        public static <I> I[] removeAndDecrement(I[] array, int index){
            if (index < array.length){
                int newSize = array.length - 1;
                I[] newArray = (I[]) new Object[newSize];
                System.arraycopy(array, 0, newArray, 0, index - 1);
                System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
                return newArray;
            } else return array;
        }
        public static <I> I[] putInNextValidSlot(I[] array, I element){
            boolean _null = false;
            int index = 0;
            for (; index < array.length; index++) {
                I i = array[index];
                if (i == null) {
                    _null = true;
                    break;
                }
            }
            if (!_null) return expandAndAdd(array, element);
            else{
                array[index] = element;
                return array;
            }
        }
    }

    public static double lerp(double a, double b, double partial){
        return a + (b - a) * partial;
    }
    public static float lerp(float a, float b, float partial){
        return a + (b - a) * partial;
    }
}
