package com.steve.ai.entity;

import com.steve.ai.SteveMod;
import com.steve.ai.config.SteveConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class SteveEntity extends PathfinderMob {
    private static final EntityDataAccessor<String> STEVE_NAME = 
        SynchedEntityData.defineId(SteveEntity.class, EntityDataSerializers.STRING);

    private String steveName;
    private String aiProvider;
    private String openAIKey;
    private String openAIModel;
    private int maxTokens;
    private double temperature;
    private boolean enableChatResponses;
    private int actionTickDelay;

    public SteveEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.steveName = "Steve";
        this.aiProvider = "groq";
        this.openAIKey = "";
        this.openAIModel = "gpt-4-turbo-preview";
        this.maxTokens = 8000;
        this.temperature = 0.7;
        this.enableChatResponses = true;
        this.actionTickDelay = 20;
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STEVE_NAME, "Steve");
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // AI processing logic would go here
        }
    }

    public void setSteveName(String name) {
        this.steveName = name;
        this.entityData.set(STEVE_NAME, name);
        this.setCustomName(Component.literal(name));
    }

    public String getSteveName() {
        return this.steveName;
    }

    public void setAIProvider(String provider) {
        this.aiProvider = provider;
    }

    public String getAIProvider() {
        return this.aiProvider;
    }

    public void setOpenAIKey(String key) {
        this.openAIKey = key;
    }

    public String getOpenAIKey() {
        return this.openAIKey;
    }

    public void setOpenAIModel(String model) {
        this.openAIModel = model;
    }

    public String getOpenAIModel() {
        return this.openAIModel;
    }

    public void setMaxTokens(int tokens) {
        this.maxTokens = tokens;
    }

    public int getMaxTokens() {
        return this.maxTokens;
    }

    public void setTemperature(double temp) {
        this.temperature = temp;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void setEnableChatResponses(boolean enable) {
        this.enableChatResponses = enable;
    }

    public boolean getEnableChatResponses() {
        return this.enableChatResponses;
    }

    public void setActionTickDelay(int delay) {
        this.actionTickDelay = delay;
    }

    public int getActionTickDelay() {
        return this.actionTickDelay;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("SteveName", this.steveName);
        tag.putString("AIProvider", this.aiProvider);
        tag.putString("OpenAIKey", this.openAIKey);
        tag.putString("OpenAIModel", this.openAIModel);
        tag.putInt("MaxTokens", this.maxTokens);
        tag.putDouble("Temperature", this.temperature);
        tag.putBoolean("EnableChatResponses", this.enableChatResponses);
        tag.putInt("ActionTickDelay", this.actionTickDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SteveName")) {
            this.setSteveName(tag.getString("SteveName"));
        }
        if (tag.contains("AIProvider")) {
            this.setAIProvider(tag.getString("AIProvider"));
        }
        if (tag.contains("OpenAIKey")) {
            this.setOpenAIKey(tag.getString("OpenAIKey"));
        }
        if (tag.contains("OpenAIModel")) {
            this.setOpenAIModel(tag.getString("OpenAIModel"));
        }
        if (tag.contains("MaxTokens")) {
            this.setMaxTokens(tag.getInt("MaxTokens"));
        }
        if (tag.contains("Temperature")) {
            this.setTemperature(tag.getDouble("Temperature"));
        }
        if (tag.contains("EnableChatResponses")) {
            this.setEnableChatResponses(tag.getBoolean("EnableChatResponses"));
        }
        if (tag.contains("ActionTickDelay")) {
            this.setActionTickDelay(tag.getInt("ActionTickDelay"));
        }
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                       MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                       @Nullable CompoundTag tag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);
        return spawnData;
    }

    public void sendChatMessage(String message) {
        if (this.level().isClientSide) return;
        
        Component chatComponent = Component.literal("<" + this.steveName + "> " + message);
        this.level().players().forEach(player -> player.sendSystemMessage(chatComponent));
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }

    public void setFlying(boolean flying) {
        this.setNoGravity(flying);
        this.setInvulnerableBuilding(flying);
    }

    public boolean isFlying() {
        return false; // Placeholder
    }

    /**
     * Set invulnerability for building (immune to ALL damage: fire, lava, suffocation, fall, etc.)
     */
    public void setInvulnerableBuilding(boolean invulnerable) {
        this.setInvulnerable(invulnerable);
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.world.damagesource.DamageSource source) {
        return true;
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        super.travel(travelVector);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }
}