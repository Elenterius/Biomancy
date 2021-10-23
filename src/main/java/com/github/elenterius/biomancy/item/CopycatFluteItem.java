package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.mixin.LivingEntityAccessor;
import com.github.elenterius.biomancy.mixin.MobEntityAccessor;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class CopycatFluteItem extends Item implements IKeyListener {

	public static final String NBT_KEY_ENTITY_NAME = "EntityName";
	public static final String NBT_KEY_ENTITY_ID = "EntityId";

	public CopycatFluteItem(Properties pProperties) {
		super(pProperties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags) {
		VoiceType voice = getVoiceType(stack).cycle();
		player.playSound(SoundEvents.GENERIC_HURT, 0.8f, 0.25f + world.random.nextFloat() * 0.25f);
		return ActionResult.success(voice.id);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		VoiceType voice = VoiceType.fromId(flags);
		byte startId = voice.id;
		while (voice.getSound(stack) == null) { //skip all sounds events that don't exist
			voice = voice.cycle();
			if (voice.id == startId) break;
		}
		setVoiceType(stack, voice);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		tooltip.add(TextUtil.getTooltipText("sound").append(": ").withStyle().withStyle(TextFormatting.GRAY).append(new TranslationTextComponent(getVoiceType(stack).getTranslationKey()).withStyle(TextFormatting.AQUA)));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTooltipText("action_cycle")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		return new StringTextComponent("").append(displayName).append(" (").append(new TranslationTextComponent(getVoiceType(stack).getTranslationKey())).append(")");
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		TranslationTextComponent name = new TranslationTextComponent(getDescriptionId(stack));
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITY_NAME)) {
			return TextUtil.getTooltipText("flute_of_the_x", name, new TranslationTextComponent(nbt.getString(NBT_KEY_ENTITY_NAME)));
		}
		return name;
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity interactionTarget, Hand usedHand) {
		if (player.level.isClientSide()) return ActionResultType.PASS;

		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putString(NBT_KEY_ENTITY_NAME, interactionTarget.getType().getDescriptionId());

		ResourceLocation registryName = interactionTarget.getType().getRegistryName();
		if (registryName != null) {
			nbt.putString(NBT_KEY_ENTITY_ID, registryName.toString());
		}

		VoiceType.saveAllSounds(interactionTarget, nbt);
		if (player.isCreative()) player.setItemInHand(usedHand, stack);

		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand usedHand) {
		ItemStack itemstack = player.getItemInHand(usedHand);
		player.startUsingItem(usedHand);
		return ActionResult.consume(itemstack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity livingEntity) {
		if (!level.isClientSide) playVoice(stack, level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
		return super.finishUsingItem(stack, level, livingEntity);
	}

	private void playVoice(ItemStack stack, World level, double x, double y, double z) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (!playVoice(stack, level, x, y, z, VoiceType.getVolume(nbt), VoiceType.getPitch(nbt))) {
			level.playSound(null, x, y, z, SoundEvents.PLAYER_BREATH, SoundCategory.RECORDS, 2f, 1f);
		}
	}

	public boolean playVoice(ItemStack stack, World level, double x, double y, double z, float volume, float pitch) {
		CompoundNBT nbt = stack.getOrCreateTag();
		VoiceType voice = VoiceType.deserialize(nbt);
		SoundEvent soundEvent = voice.getSound(nbt);
		if (soundEvent != null) {
			level.playSound(null, x, y, z, soundEvent, SoundCategory.RECORDS, volume, pitch);
			return true;
		}
		return false;
	}

	public boolean playVoice(ItemStack stack, World level, double x, double y, double z, float volume, float pitch, VoiceType voicetype) {
		CompoundNBT nbt = stack.getOrCreateTag();
		SoundEvent soundEvent = voicetype.getSound(nbt);
		if (soundEvent != null) {
			level.playSound(null, x, y, z, soundEvent, SoundCategory.RECORDS, volume, pitch);
			return true;
		}
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	public VoiceType getVoiceType(ItemStack stack) {
		return VoiceType.deserialize(stack.getOrCreateTag());
	}

	public void setVoiceType(ItemStack stack, VoiceType voice) {
		VoiceType.serialize(stack.getOrCreateTag(), voice);
	}

	public enum VoiceType {
		NONE(0, "", ""),
		AMBIENT(1, "AmbientSound", ""),
		HURT(2, "HurtSound", "minecraft:entity.generic.hurt"),
		DEATH(3, "DeathSound", "minecraft:entity.generic.death");

		public static final String NBT_KEY_VOLUME = "Volume";
		public static final String NBT_KEY_PITCH = "Pitch";
		public static final String NBT_KEY_VOICE = "VoiceType";

		public final byte id;
		private final String nbtKey;
		private final String genericSoundId;

		VoiceType(int id, String nbtKey, String genericSoundId) {
			this.nbtKey = nbtKey;
			this.id = (byte) id;
			this.genericSoundId = genericSoundId;
		}

		public static VoiceType fromId(byte id) {
			if (id < 0 || id >= values().length) return NONE;
			switch (id) {
				case 2:
					return HURT;
				case 1:
					return AMBIENT;
				case 3:
					return DEATH;
				case 0:
				default:
					return NONE;
			}
		}

		public static void serialize(CompoundNBT nbt, VoiceType soundType) {
			nbt.putByte(NBT_KEY_VOICE, soundType.id);
		}

		public static VoiceType deserialize(CompoundNBT nbt) {
			return fromId(nbt.getByte(NBT_KEY_VOICE));
		}

		private static void saveAllSounds(LivingEntity entity, CompoundNBT nbt) {
			LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor) entity;
			nbt.putFloat(NBT_KEY_VOLUME, livingEntityAccessor.biomancy_getSoundVolume());
			nbt.putFloat(NBT_KEY_PITCH, livingEntityAccessor.biomancy_getVoicePitch());
			VoiceType.DEATH.saveSound(nbt, livingEntityAccessor.biomancy_getDeathSound());
			VoiceType.HURT.saveSound(nbt, livingEntityAccessor.biomancy_getHurtSound(DamageSource.GENERIC));
			VoiceType.AMBIENT.saveSound(nbt, entity instanceof MobEntity ? ((MobEntityAccessor) entity).biomancy_getAmbientSound() : null);
		}

		private static float getPitch(CompoundNBT nbt) {
			if (nbt.contains(NBT_KEY_PITCH)) {
				return nbt.getFloat(NBT_KEY_PITCH);
			}
			return 1f;
		}

		private static float getVolume(CompoundNBT nbt) {
			if (nbt.contains(NBT_KEY_VOLUME)) {
				return nbt.getFloat(NBT_KEY_VOLUME);
			}
			return 1f;
		}

		public String getTranslationKey() {
			return "enum.biomancy.voice_type." + name().toLowerCase(Locale.ROOT);
		}

		public VoiceType cycle() {
			return fromId((byte) (id + 1));
		}

		private void saveSound(CompoundNBT nbt, @Nullable SoundEvent soundEvent) {
			if (soundEvent != null) {
				ResourceLocation registryName = soundEvent.getRegistryName();
				nbt.putString(nbtKey, registryName != null ? registryName.toString() : genericSoundId);
			}
			else nbt.remove(nbtKey);
		}

		@Nullable
		public SoundEvent getSound(ItemStack stack) {
			return getSound(stack.getOrCreateTag());
		}

		@Nullable
		public SoundEvent getSound(CompoundNBT nbt) {
			if (this == NONE) return SoundEvents.PLAYER_BREATH;

			ResourceLocation soundId = ResourceLocation.tryParse(nbt.getString(nbtKey));
			if (soundId != null) {
				return ForgeRegistries.SOUND_EVENTS.getValue(soundId);
			}
			return null;
		}

	}

}
