package mod.pilot.horseshoe_crab_takeover.data.syncing;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;

public class PacketHub {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "main"), () -> VERSION,
            VERSION::equals, VERSION::equals);

    private static final AtomicInteger packetId = new AtomicInteger(0);
    public static void registerPackets(){
        CHANNEL.messageBuilder(NodeSyncingPacket.ServerSyncPacket.class, packetId.getAndIncrement())
                .encoder(NodeSyncingPacket.ServerSyncPacket::writeToBuffer)
                .decoder(NodeSyncingPacket.ServerSyncPacket::decodeFromBuffer)
                .consumerMainThread(NodeSyncingPacket.ServerSyncPacket::sync)
                .add();
    }

    public static <T> void sendToServer(T packet) {
        CHANNEL.sendToServer(packet);
    }
    public static <T> void sendToClient(T packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
