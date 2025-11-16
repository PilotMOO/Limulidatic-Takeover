package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import org.joml.Vector3i;

public class GreedyNode {
    public GreedyNode(Vector3i cornerMinor){
        this.cornerMinor = cornerMinor;
    }
    public final Vector3i cornerMinor;
    public byte x, y, z;
    public int compressSize(){
        int cX = z;
        cX = (cX << 8) | y;
        cX = (cX << 8) | x;
        return cX;
    }

    public static byte decompressX(int compressed){
        return (byte)(compressed >>> 16);
    }
    public static byte decompressY(int compressed){
        return (byte)(compressed << 16 >>> 8);
    }
    public static byte decompressZ(int compressed){
        return (byte)(compressed << 24 >>> 24);
    }

    public void unpackSize(int compressed){
        x = decompressX(compressed); y = decompressY(compressed); z = decompressZ(compressed);
    }

    public void constructFromContext(/*WIP*/){

    }
}

