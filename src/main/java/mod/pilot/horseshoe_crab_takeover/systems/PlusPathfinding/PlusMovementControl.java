package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class PlusMovementControl{
    public PlusMovementControl(WorldEntity user){
        this.user = user;
        this.cRot = new Vector3f(user.getXRot(), user.getYRot(), 0);
        this.oRot = new Vector3f(user.xRotO, user.yRotO, 0);
    }
    public final WorldEntity user;
    public float wantedSpeed;
    public Vector3d wantedPosition;

    public float wantedRotX, wantedRotY;
    protected Vector3f cRot, oRot;
    public void setUserRotation(float x, float y){
        user.setXRot(x);
        user.setYRot(y);
    }
    public float rotateDuration;
    protected float lerpAge;

    protected boolean rotating;

    protected boolean active = true; //set to TRUE by default for testing
    public final boolean isActive(){
        return active && activeAltCheck();
    }
    protected boolean activeAltCheck(){
        return true;
    }

    public void rotateTowards(Vec3 to, float lerpSpeed){
        Vec3 from = user.position();
        Vector3d vF = DataHelper.ForVector3d.directionVector3d(from.x, from.y, from.z, to.x, to.y, to.z);
        rotateToDirection(from.x - to.x, from.z - to.z, lerpSpeed);
    }
    public void rotateTowards(Vector3f position, float lerpSpeed){
        Vector3f vF = DataHelper.ForVector3f.getDirectionFromAToB(position, user);
        rotateToDirection(vF.x, vF.z, lerpSpeed);
    }
    public void rotateToDirection(double x, double z, float lerpSpeed){
        wantedRotX = 0f;
        wantedRotY = Mth.wrapDegrees((float) (Math.atan2(z, x) * (double) (180F / (float) Math.PI)) - 90.0F);
        System.out.println("X, Z: [" + x + ", " + z + "], rotY: [" + wantedRotY + "]");
        resetRotationLerp();
        this.rotateDuration = lerpSpeed;
    }

    public void tick(){
        if (isActive()){
            lerpAge += 1 / rotateDuration;
            lerpAge = Math.min(lerpAge, 1);
            setUserRotation(DataHelper.lerp(user.getXRot(), wantedRotX, lerpAge),
                    DataHelper.lerp(user.getYRot(), wantedRotY, lerpAge));

            if (lerpAge >= 1) resetRotationLerp();
        }
    }

    protected void resetRotationLerp(){
        lerpAge = 0;
        wantedRotX = wantedRotY = 0;
        rotateDuration = -1;
        rotating = false;
    }
}
