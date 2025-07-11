package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IMulticlassMemory {
    int getMemoryCapacity();

    void rememberNode(IMemoryNode<?> target);

    Iterable<IMemoryNode<?>> accessMemory();
    IMemoryNode<?> getNextNode();

    default void readMemoryNode(IMemoryNode<?> target){
        target.readSafe(this);
    }

    interface IMemoryNode<user> {
        Class<user> getNodeClass();

        void readNode(user _user);
        default void readSafe(Object _user){
            if (getNodeClass().isInstance(_user)) readNode(getNodeClass().cast(_user));
        }
    }

    record SplitTarget(LivingEntity target, int lastX, int lastY, int lastZ, int age, int discoveredBy)
            implements IMemoryNode<WorldEntity> {
        public SplitTarget(LivingEntity target, int age, LivingEntity owner){
            this(target, (int)target.position().x, (int)target.position().y, (int)target.position().z, age, owner.tickCount);
        }
        public SplitTarget(LivingEntity target, int age, int discoveredBy){
            this(target, (int)target.position().x, (int)target.position().y, (int)target.position().z, age, discoveredBy);
        }
        public SplitTarget(LivingEntity target, int lastX, int lastY, int lastZ, int age, int discoveredBy){
            this.target = target;
            this.lastX = lastX; this.lastY = lastY; this.lastZ = lastZ;
            this.age = age;
            this.discoveredBy = discoveredBy;
        }

        public boolean expired(int counter){
            return (counter - discoveredBy) >= age;
        }

        @Override
        public Class<WorldEntity> getNodeClass() {
            return WorldEntity.class;
        }

        @Override
        public void readNode(WorldEntity _user){
            _user.setTarget(target);
            if (_user instanceof IPathfindAccess<?> pA) pA.pathfindTo(lastX, lastY, lastZ, 1);
        }

        public Vec3 getLastPosAsVec3(){
            return new Vec3(lastX, lastY, lastZ);
        }
        public BlockPos getLastPosAsBlockPos(){
            return new BlockPos(lastX, lastY, lastZ);
        }
    }
}
