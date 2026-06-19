package mod.pilot.horseshoe_crab_takeover.data.syncing;

import mod.pilot.horseshoe_crab_takeover.entities.NodeVisualizerEntity;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class NodeSyncingPacket {
    public static void sync(UUID entityUUID, GreedyNode[] gnodes, ServerPlayer player) {
        sync(new ServerSyncPacket(entityUUID, gnodes), player);
    }
    public static void sync(ServerSyncPacket syncPacket, ServerPlayer player){
        PacketHub.sendToClient(syncPacket, player);
    }
    public static void syncAllClients(ServerSyncPacket syncPacket, ServerLevel server){
        for (ServerPlayer sPlayer : server.getPlayers(p -> true)){
            sync(syncPacket, sPlayer);
        }
    }

    public record ServerSyncPacket(UUID entityUUID, GreedyNode[] gnodes) {
        public static ServerSyncPacket decodeFromBuffer(FriendlyByteBuf buffer) {
            UUID entityUUID = buffer.readUUID();

            int count = buffer.readInt();
            GreedyNode[] gnodes = new GreedyNode[count];
            for(int i = 0; i < count; i++){
                byte id = buffer.readByte();
                int minorX = buffer.readInt(), minorY = buffer.readInt(), minorZ = buffer.readInt();
                int sizeX = buffer.readInt(), sizeY = buffer.readInt(), sizeZ = buffer.readInt();

                int idCount = buffer.readInt();
                byte[] bytesId = new byte[idCount];
                for (int j = 0; j < idCount; j++){
                    bytesId[j] = buffer.readByte();
                }
                GreedyNode node = new GreedyNode(id, minorX, minorY, minorZ, sizeX, sizeY, sizeZ);
                node.relativeIDs = bytesId;
                node.idRelativeSize = node.occupied = bytesId.length;
                gnodes[i] = node;
            }
            return new ServerSyncPacket(entityUUID, gnodes);
        }
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUUID(entityUUID);

            int count = gnodes.length;
            buffer.writeInt(count);
            for (int i = 0; i < count; i++){
                GreedyNode gNode = gnodes[i];
                buffer.writeByte(gNode.nodeID);
                buffer.writeInt(gNode.minorX).writeInt(gNode.minorY).writeInt(gNode.minorZ);
                buffer.writeInt(gNode.sizeX).writeInt(gNode.sizeY).writeInt(gNode.sizeZ);
                int idCount = gNode.relativeIDs.length;
                buffer.writeInt(idCount);
                for (int j = 0; j < idCount; j++){
                    buffer.writeByte(gNode.relativeIDs[j]);
                }
            }
        }

        public static void sync(ServerSyncPacket syncPacket, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                LogicalSide side = context.get().getDirection().getReceptionSide();
                if (side.isClient()){
                    NodeVisualizerEntity.NodeSyncingBay.update(syncPacket);
                }
                else postError("[SYNC] Sync packet was invoked on the logical server! Very cringe.");
            });
            context.get().setPacketHandled(true);
        }
    }


    private static void postError(String reason) {
        System.err.println("[NODE CLIENT SYNCER] ERROR! Invalid packet detected! Bailing...");
        System.err.println("[NODE CLIENT SYNCER] Info -- Cause of invalidity: " + reason);
    }
}
