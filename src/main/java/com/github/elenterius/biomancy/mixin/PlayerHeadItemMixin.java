package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.BiomancyMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHeadItem.class)
public abstract class PlayerHeadItemMixin {

	@Inject(method = "verifyTagAfterLoad", at = @At(value = "TAIL"))
	private void onVerifyTagAfterLoad(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(BiomancyMod.MOD_ID) && tag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_COMPOUND)) {
			GameProfile gameProfile = NbtUtils.readGameProfile(tag.getCompound(PlayerHeadItem.TAG_SKULL_OWNER));

			SkullBlockEntity.updateGameprofile(gameProfile, profile -> {
				tag.put(PlayerHeadItem.TAG_SKULL_OWNER, NbtUtils.writeGameProfile(new CompoundTag(), profile));
				tag.remove(BiomancyMod.MOD_ID);
			});
		}
	}

}
