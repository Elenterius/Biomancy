package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.renderer.item.ArmCannonRenderer;
import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.world.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.world.entity.projectile.CorrosiveAcidProjectile;
import com.github.elenterius.biomancy.world.item.IArmPoseProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ArmCannonItem extends Item implements IAnimatable, IArmPoseProvider {

    public static final Set<Enchantment> VALID_ENCHANTMENTS = Set.of(Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS);
    private final AnimationFactory animationFactory = new AnimationFactory(this);

    public ArmCannonItem(Properties properties) {
        super(properties);
    }

    private static float convertToInaccuracy(float accuracy) {
        return -IGun.MAX_INACCURACY * accuracy + IGun.MAX_INACCURACY;
    }

    private static float getBonusDamage(ItemStack stack) {
        return 0.6f * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
    }

    private static int getBonusKnockBack(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
    }

    public static boolean fireProjectile(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float inaccuracy, BiFunction<Level, LivingEntity, BaseProjectile> factory) {
        BaseProjectile projectile = factory.apply(level, shooter);
        projectile.setDamage(damage);
        if (knockback > 0) {
            projectile.setKnockback((byte) knockback);
        }

        Vec3 direction = shooter.getLookAngle();
        projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

        if (level.addFreshEntity(projectile)) {
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.8f, 0.4f);
            return true;
        }

        return false;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            private final ArmCannonRenderer renderer = new ArmCannonRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            //            ItemStack stack = player.getItemInHand(usedHand);
            //            float bonusDamage = getBonusDamage(stack);
            //            int bonusKnockBack = getBonusKnockBack(stack);
            //            fireProjectile(level, player, 1.75f, 5f + bonusDamage, bonusKnockBack, convertToInaccuracy(0.92f), ToothProjectile::new);
            //            fireProjectile(level, player, 0.8f, 8f + bonusDamage, bonusKnockBack, convertToInaccuracy(0.9f), WitherProjectile::new);
            //            fireProjectile(level, player, 1.75f, 0, 0, convertToInaccuracy(0.92f), AntiGravityProjectile::new);
            fireProjectile(level, player, 1.5f, 4, 0, convertToInaccuracy(0.9f), CorrosiveAcidProjectile::new);
        }
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return VALID_ENCHANTMENTS.contains(enchantment) || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(Player player, InteractionHand usedHand, ItemStack stack) {
        return !player.swinging ? HumanoidModel.ArmPose.CROSSBOW_HOLD : HumanoidModel.ArmPose.ITEM;
    }

    @Override
    public void registerControllers(AnimationData data) {
        //do nothing
    }

    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        ClientTextUtil.appendItemInfoTooltip(stack.getItem(), tooltip);
        tooltip.add(new TextComponent("The quick brown fox jumps over the lazy dog. 1234567890!?").withStyle(TextStyles.MAYKR_RUNES_GRAY));
        // /tellraw @a {"text":"The quick brown fox jumps over the lazy dog. 1234567890!?","color":"#9e1316","font":"biomancy:maykr_runes"}
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new HrTooltipComponent());
    }
}
