package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class RenderDebuggerQue {
    public static void renderAll(MultiBufferSource.BufferSource buffer, PoseStack poseStack, float partial, long finishNanoTime, boolean renderBlockOutline,
                                 Camera camera, GameRenderer renderer, LightTexture lightTexture, Matrix4f projection){
        if (manageQueLogic()) return;
        for (IRenderInstructions render : vQue) render.render(buffer, poseStack, partial, finishNanoTime, renderBlockOutline,
                camera, renderer, lightTexture, projection);
    }

    private static final ArrayList<IRenderInstructions> vQue = new ArrayList<>();
    private static final ArrayList<IRenderInstructions> add = new ArrayList<>(), remove = new ArrayList<>();
    private static boolean flagAdd, flagRemove;

    public static void queRender(IRenderInstructions render){
        add.add(render);
        flagAdd = true;
    }
    public static void removeRender(IRenderInstructions render){
        remove.add(render);
        flagRemove = true;
    }

    private static boolean manageQueLogic(){
        if (flagRemove){
            vQue.removeAll(remove);
            remove.clear();
            flagRemove = false;
        }
        if (flagAdd){
            vQue.addAll(add);
            add.clear();
            flagAdd = false;
            return false;
        }
        return vQue.isEmpty();
    }

    public interface IRenderInstructions{
        void render(MultiBufferSource.BufferSource buffer, PoseStack poseStack, float partial, long finishNanoTime, boolean renderBlockOutline,
                    Camera camera, GameRenderer renderer, LightTexture lightTexture, Matrix4f projection);

        default void que(){ RenderDebuggerQue.queRender(this); }
    }
}
