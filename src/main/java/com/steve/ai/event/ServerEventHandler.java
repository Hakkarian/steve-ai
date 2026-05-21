package com.steve.ai.event;

import com.steve.ai.SteveMod;
import com.steve.ai.entity.SteveManager;
import com.steve.ai.memory.StructureRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SteveMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
    private static boolean stevesSpawned = false;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = (ServerLevel) player.level();
            SteveManager manager = SteveMod.getSteveManager();
            if (!stevesSpawned) {
                manager.clearAllSteves();
                
                // Clear structure registry for fresh spatial awareness
                StructureRegistry.clear();
                
                // Then, remove ALL SteveEntity instances from the world (including ones loaded from NBT)
                int removedCount = 0;
                for (var entity : level.getAllEntities()) {
                    if (entity instanceof SteveEntity steve) {
                        steve.discard();
                        removedCount++;
                    }
                }
                
                SteveMod.LOGGER.info("Removed {} Steve entities from world on login", removedCount);
                
                // Spawn initial Steves
                int initialSteves = SteveConfig.MAX_ACTIVE_STEVES.get() / 2;
                for (int i = 0; i < initialSteves; i++) {
                    // Spawn Steves at random positions
                    Vec3 spawnPos = player.position().add(
                        (Math.random() - 0.5) * 20,
                        0,
                        (Math.random() - 0.5) * 20
                    );
                    
                    SteveEntity steve = manager.spawnSteve(level, spawnPos, "Steve-" + (i + 1));
                    if (steve != null) {
                        steve.setFlying(true);
                        steve.setInvulnerableBuilding(true);
                    }
                }
                
                stevesSpawned = true;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Clean up any Steves that might be following this player
            SteveManager manager = SteveMod.getSteveManager();
            manager.clearAllSteves();
            stevesSpawned = false;
        }
    }
}