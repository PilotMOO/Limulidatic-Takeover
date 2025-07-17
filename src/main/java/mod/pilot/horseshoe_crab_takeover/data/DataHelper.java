package mod.pilot.horseshoe_crab_takeover.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

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
    }

    public static double lerp(double a, double b, double partial){
        return a + (b - a) * partial;
    }
    public static float lerp(float a, float b, float partial){
        return a + (b - a) * partial;
    }
}
