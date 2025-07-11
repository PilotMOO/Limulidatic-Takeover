package mod.pilot.horseshoe_crab_takeover.worlddata;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class HorseshoeWorldData extends SavedData {
    public static final String NAME = Horseshoe_Crab_Takeover.MOD_ID + "_world_data";

    public HorseshoeWorldData(){
        super();
    }
    public static void setActiveData(ServerLevel server){
        Horseshoe_Crab_Takeover.activeData = server.getDataStorage().computeIfAbsent(HorseshoeWorldData::load, HorseshoeWorldData::new, NAME);
        activeData().setDirty();
    }
    private static @NotNull HorseshoeWorldData activeData(){
        return Horseshoe_Crab_Takeover.activeData;
    }
    public static HorseshoeWorldData load(CompoundTag tag){
        HorseshoeWorldData data = new HorseshoeWorldData();
        if (tag.contains("has_started")){
            data.HasStarted = tag.getBoolean("has_started");
        }
        if (tag.contains("world_age", 99)){
            data.WorldAge = tag.getInt("world_age");
        }

        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.putBoolean("has_started", HasStarted);
        tag.putInt("world_age", WorldAge);

        return tag;
    }

    private boolean HasStarted = false;

    public static boolean hasStarted(){
        return activeData().HasStarted;
    }
    public void setHasStarted(boolean flag){
        HasStarted = flag;
    }

    private int WorldAge = 0;
    public static int getWorldAge(){
        return activeData().WorldAge;
    }
    public void setWorldAge(int age){
        WorldAge = age;
        setDirty();
    }
    public void ageWorldBy(int age){
        setWorldAge(getWorldAge() + age);
    }
    public void ageWorld(){
        ageWorldBy(1);
    }
}
