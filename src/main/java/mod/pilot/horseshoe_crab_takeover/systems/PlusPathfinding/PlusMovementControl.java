package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class PlusMovementControl{
    public PlusMovementControl(WorldEntity user){
        this.user = user;
    }
    public final WorldEntity user;

    public double x, y, z;
    public double speed;
    public double error;
    protected boolean moving;
    public boolean isMoving(){ return moving; }

    protected boolean forceActive = false;
    public final boolean isActive(){
        return forceActive || activeAltCheck();
    }
    protected boolean activeAltCheck(){
        return moving;
    }

    public boolean canMove(){
        return user.isAlive() && user.onGround();
    }

    public void moveTo(Vec3 pos, double error){
        moveTo(pos.x, pos.y, pos.z, user.getSpeed(), error);
    }
    public void moveTo(Vec3 pos, double speed, double error){
        moveTo(pos.x, pos.y, pos.z, speed, error);
    }
    public void moveTo(Vector3d pos, double error){
        moveTo(pos.x, pos.y, pos.z, user.getSpeed(), error);
    }
    public void moveTo(Vector3d pos, double speed, double error){
        moveTo(pos.x, pos.y, pos.z, speed, error);
    }
    public void moveTo(double x, double y, double z, double error){
        moveTo(x, y, z, user.getSpeed(), error);
    }
    public void moveTo(double x, double y, double z, double speed, double error) {
        this.x = x; this.y = y; this.z = z;
        this.speed = speed;
        this.error = error;
        moving = true;
    }

    public void tick(){
        if (isActive()){
            if (canMove()){
                if (Math.sqrt(user.position().distanceToSqr(x, y, z)) > error) {
                    double relX = x - user.getX(), relY = y - user.getY(), relZ = z - user.getZ();
                    double sqrt = Math.sqrt(relX * relX + relY * relY + relZ * relZ);
                    relX /= sqrt; relZ /= sqrt;
                    relX *= speed; relZ *= speed;
                    Vector3d delta = new Vector3d(relX, 0, relZ);
                    user.setDeltaMovement(delta);
                } else{
                    moving = false;
                }
            }
        }
    }

    public void rotateTowards(Vec3 to) {
        Vec3 from = user.position();
        rotateToRelative(from.x - to.x, from.z - to.z);
    }
    public void rotateToRelative(double relativeX, double relativeZ){
        float wantedRotX = 0f;
        float wantedRotY = (-((float)Mth.atan2(relativeX, relativeZ)) * 57.295776f) + 180f;
        user.setXRot(wantedRotX);
        user.setYRot(wantedRotY);
    }
}
