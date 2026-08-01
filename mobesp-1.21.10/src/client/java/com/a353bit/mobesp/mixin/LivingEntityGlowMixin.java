package com.a353bit.mobesp.mixin;

import com.a353bit.mobesp.config.MobEspConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces isGlowing() to return true for the chosen mob type when the ESP
 * toggle is on. Minecraft's own glow rendering (same system used by the
 * Glowing status effect and Spectator mode) already draws entity outlines
 * through blocks, so no custom rendering pipeline is needed.
 */
@Mixin(LivingEntity.class)
public class LivingEntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void mobesp$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        MobEspConfig config = MobEspConfig.get();
        if (!config.enabled) {
            return;
        }

        Identifier targetId = Identifier.tryParse(config.mobId);
        if (targetId == null) {
            return; // malformed ID typed by the user, don't crash
        }

        LivingEntity self = (LivingEntity) (Object) this;
        Identifier actualId = Registries.ENTITY_TYPE.getId(self.getType());

        if (targetId.equals(actualId)) {
            cir.setReturnValue(true);
        }
    }
}
