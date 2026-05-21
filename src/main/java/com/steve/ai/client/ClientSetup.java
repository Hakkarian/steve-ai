package com.steve.ai.client;

import com.steve.ai.SteveMod;
import com.steve.ai.entity.SteveEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side setup for entity renderers and other client-only initialization
 */
@Mod.EventBusSubscriber(modid = SteveMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    private static final ResourceLocation STEVE_TEXTURE = new ResourceLocation("minecraft", "textures/entity/player/wide/steve.png");

    @SubscribeEvent
    public static void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SteveMod.STEVE_ENTITY.get(), context -> 
            new HumanoidMobRenderer<SteveEntity, PlayerModel<SteveEntity>>(
                context,
                new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_MAIN),
                    context.bakeLayer(ModelLayers.PLAYER_HEAD),
                    context.bakeLayer(ModelLayers.PLAYER_HAT),
                    context.bakeLayer(ModelLayers.PLAYER_BODY),
                    context.bakeLayer(ModelLayers.PLAYER_LEFT_ARM),
                    context.bakeLayer(ModelLayers.PLAYER_RIGHT_ARM),
                    context.bakeLayer(ModelLayers.PLAYER_LEFT_LEG),
                    context.bakeLayer(ModelLayers.PLAYER_RIGHT_LEG)),
                STEVE_TEXTURE)
        );
    }
}