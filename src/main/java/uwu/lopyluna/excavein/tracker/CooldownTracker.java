package uwu.lopyluna.excavein.tracker;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uwu.lopyluna.excavein.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uwu.lopyluna.excavein.config.ServerConfig.SELECTION_ADD_COOLDOWN;
import static uwu.lopyluna.excavein.config.ServerConfig.SELECTION_COOLDOWN;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class CooldownTracker {
    private static final Map<UUID, Integer> playerCooldowns = new HashMap<>();

    public static void resetCooldown(ServerPlayer player, int amountOfBlocks) {
        playerCooldowns.put(player.getUUID(), Mth.clamp(Utils.calculateTimeFromBlocks(SELECTION_COOLDOWN.get(), amountOfBlocks) + SELECTION_ADD_COOLDOWN.get(), 0, SELECTION_COOLDOWN.get()));
    }

    public static boolean isCooldownNotActive(ServerPlayer player) {
        return playerCooldowns.getOrDefault(player.getUUID(), 0) <= 0;
    }

    public static int getRemainingCooldown(ServerPlayer player) {
        return playerCooldowns.getOrDefault(player.getUUID(), 0);
    }

    public static void getCoolDownCheck(ServerPlayer player) {
        playerCooldowns.putIfAbsent(player.getUUID(), 0);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Map.Entry<UUID, Integer> entry : playerCooldowns.entrySet()) {
                int remainingTicks = entry.getValue();
                if (remainingTicks > 0) {
                    playerCooldowns.put(entry.getKey(), remainingTicks - 1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        playerCooldowns.putIfAbsent(player.getUUID(), 0);
    }
}
