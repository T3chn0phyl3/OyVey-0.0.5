// 
// Decompiled by Procyon v0.5.36
// 

package me.alpha432.oyvey.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import net.minecraft.util.MovementInput;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import java.util.Objects;
import net.minecraft.potion.Potion;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import me.alpha432.oyvey.OyVey;
import net.minecraft.entity.EntityLivingBase;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import java.util.Collection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import java.util.ArrayList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityUtil implements Util
{
    public static final Vec3d[] antiDropOffsetList;
    public static final Vec3d[] platformOffsetList;
    public static final Vec3d[] legOffsetList;
    public static final Vec3d[] OffsetList;
    public static final Vec3d[] antiStepOffsetList;
    public static final Vec3d[] antiScaffoldOffsetList;
    
    public static void attackEntity(final Entity entity, final boolean packet, final boolean swingArm) {
        if (packet) {
            EntityUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(entity));
        }
        else {
            EntityUtil.mc.field_71442_b.func_78764_a((EntityPlayer)EntityUtil.mc.field_71439_g, entity);
        }
        if (swingArm) {
            EntityUtil.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        }
    }
    
    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * time);
    }
    
    public static Vec3d getInterpolatedPos(final Entity entity, final float partialTicks) {
        return new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U).func_178787_e(getInterpolatedAmount(entity, partialTicks));
    }
    
    public static Vec3d getInterpolatedRenderPos(final Entity entity, final float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).func_178786_a(EntityUtil.mc.func_175598_ae().field_78725_b, EntityUtil.mc.func_175598_ae().field_78726_c, EntityUtil.mc.func_175598_ae().field_78723_d);
    }
    
    public static Vec3d getInterpolatedRenderPos(final Vec3d vec) {
        return new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c).func_178786_a(EntityUtil.mc.func_175598_ae().field_78725_b, EntityUtil.mc.func_175598_ae().field_78726_c, EntityUtil.mc.func_175598_ae().field_78723_d);
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final double x, final double y, final double z) {
        return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final Vec3d vec) {
        return getInterpolatedAmount(entity, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final float partialTicks) {
        return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }
    
    public static boolean isPassive(final Entity entity) {
        return (!(entity instanceof EntityWolf) || !((EntityWolf)entity).func_70919_bu()) && (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid || (entity instanceof EntityIronGolem && ((EntityIronGolem)entity).func_70643_av() == null));
    }
    
    public static boolean isSafe(final Entity entity, final int height, final boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }
    
    public static boolean stopSneaking(final boolean isSneaking) {
        if (isSneaking && EntityUtil.mc.field_71439_g != null) {
            EntityUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)EntityUtil.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }
    
    public static boolean isSafe(final Entity entity) {
        return isSafe(entity, 0, false);
    }
    
    public static BlockPos getPlayerPos(final EntityPlayer player) {
        return new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u), Math.floor(player.field_70161_v));
    }
    
    public static List<Vec3d> getUnsafeBlocks(final Entity entity, final int height, final boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
    }
    
    public static boolean isMobAggressive(final Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie)entity).func_184734_db() || ((EntityPigZombie)entity).func_175457_ck()) {
                return true;
            }
        }
        else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf)entity).func_70919_bu() && !EntityUtil.mc.field_71439_g.equals((Object)((EntityWolf)entity).func_70902_q());
            }
            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman)entity).func_70823_r();
            }
        }
        return isHostileMob(entity);
    }
    
    public static boolean isNeutralMob(final Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }
    
    public static boolean isProjectile(final Entity entity) {
        return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
    }
    
    public static boolean isVehicle(final Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }
    
    public static boolean isFriendlyMob(final Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !isMobAggressive(entity));
    }
    
    public static boolean isHostileMob(final Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }
    
    public static List<Vec3d> getUnsafeBlocksFromVec3d(final Vec3d pos, final int height, final boolean floor) {
        final List<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (final Vec3d vector : getOffsets(height, floor)) {
            final BlockPos targetPos = new BlockPos(pos).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
            final Block block = EntityUtil.mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }
    
    public static boolean isInHole(final Entity entity) {
        return isBlockValid(new BlockPos(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
    }
    
    public static boolean isBlockValid(final BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }
    
    public static boolean isObbyHole(final BlockPos blockPos) {
        final BlockPos[] array;
        final BlockPos[] touchingBlocks = array = new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
        for (final BlockPos pos : array) {
            final IBlockState touchingState = EntityUtil.mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150343_Z) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBedrockHole(final BlockPos blockPos) {
        final BlockPos[] array;
        final BlockPos[] touchingBlocks = array = new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
        for (final BlockPos pos : array) {
            final IBlockState touchingState = EntityUtil.mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBothHole(final BlockPos blockPos) {
        final BlockPos[] array;
        final BlockPos[] touchingBlocks = array = new BlockPos[] { blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b() };
        for (final BlockPos pos : array) {
            final IBlockState touchingState = EntityUtil.mc.field_71441_e.func_180495_p(pos);
            if (touchingState.func_177230_c() == Blocks.field_150350_a || (touchingState.func_177230_c() != Blocks.field_150357_h && touchingState.func_177230_c() != Blocks.field_150343_Z)) {
                return false;
            }
        }
        return true;
    }
    
    public static Vec3d[] getUnsafeBlockArray(final Entity entity, final int height, final boolean floor) {
        final List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }
    
    public static Vec3d[] getUnsafeBlockArrayFromVec3d(final Vec3d pos, final int height, final boolean floor) {
        final List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }
    
    public static double getDst(final Vec3d vec) {
        return EntityUtil.mc.field_71439_g.func_174791_d().func_72438_d(vec);
    }
    
    public static boolean isTrapped(final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }
    
    public static boolean isTrappedExtended(final int extension, final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop, final boolean raytrace) {
        return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
    }
    
    public static List<Vec3d> getUntrappedBlocks(final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        final List<Vec3d> vec3ds = new ArrayList<Vec3d>();
        if (!antiStep && getUnsafeBlocks((Entity)player, 2, false).size() == 4) {
            vec3ds.addAll(getUnsafeBlocks((Entity)player, 2, false));
        }
        for (int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
            final Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
            final BlockPos targetPos = new BlockPos(player.func_174791_d()).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
            final Block block = EntityUtil.mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }
    
    public static boolean isInWater(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double y = entity.field_70163_u + 0.01;
        for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ++z) {
                final BlockPos pos = new BlockPos(x, (int)y, z);
                if (EntityUtil.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isDrivenByPlayer(final Entity entityIn) {
        return EntityUtil.mc.field_71439_g != null && entityIn != null && entityIn.equals((Object)EntityUtil.mc.field_71439_g.func_184187_bx());
    }
    
    public static boolean isPlayer(final Entity entity) {
        return entity instanceof EntityPlayer;
    }
    
    public static boolean isAboveWater(final Entity entity) {
        return isAboveWater(entity, false);
    }
    
    public static boolean isAboveWater(final Entity entity, final boolean packet) {
        if (entity == null) {
            return false;
        }
        final double y = entity.field_70163_u - (packet ? 0.03 : (isPlayer(entity) ? 0.2 : 0.5));
        for (int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
                if (EntityUtil.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static List<Vec3d> getUntrappedBlocksExtended(final int extension, final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop, final boolean raytrace) {
        final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
        if (extension == 1) {
            placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
        }
        else {
            int extend = 1;
            for (final Vec3d vec3d : MathUtil.getBlockBlocks((Entity)player)) {
                if (extend > extension) {
                    break;
                }
                placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
                ++extend;
            }
        }
        final List<Vec3d> removeList = new ArrayList<Vec3d>();
        for (final Vec3d vec3d : placeTargets) {
            final BlockPos pos = new BlockPos(vec3d);
            if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
                removeList.add(vec3d);
            }
        }
        for (final Vec3d vec3d : removeList) {
            placeTargets.remove(vec3d);
        }
        return placeTargets;
    }
    
    public static List<Vec3d> targets(final Vec3d vec3d, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop, final boolean raytrace) {
        final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
        if (antiDrop) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.antiDropOffsetList));
        }
        if (platform) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.platformOffsetList));
        }
        if (legs) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.legOffsetList));
        }
        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.OffsetList));
        if (antiStep) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.antiStepOffsetList));
        }
        else {
            final List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if (vec3ds.size() == 4) {
                for (final Vec3d vector : vec3ds) {
                    final BlockPos position = new BlockPos(vec3d).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
                    switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
                        case -1:
                        case 1:
                        case 2: {
                            continue;
                        }
                        case 3: {
                            placeTargets.add(vec3d.func_178787_e(vector));
                            break;
                        }
                    }
                    if (antiScaffold) {
                        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.antiScaffoldOffsetList));
                    }
                    return placeTargets;
                }
            }
        }
        if (antiScaffold) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, EntityUtil.antiScaffoldOffsetList));
        }
        return placeTargets;
    }
    
    public static List<Vec3d> getOffsetList(final int y, final boolean floor) {
        final List<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, (double)(y - 1), 0.0));
        }
        return offsets;
    }
    
    public static Vec3d[] getOffsets(final int y, final boolean floor) {
        final List<Vec3d> offsets = getOffsetList(y, floor);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    
    public static Vec3d[] getTrapOffsets(final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        final List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    
    public static List<Vec3d> getTrapOffsetsList(final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        final List<Vec3d> offsets = new ArrayList<Vec3d>(getOffsetList(1, false));
        offsets.add(new Vec3d(0.0, 2.0, 0.0));
        if (antiScaffold) {
            offsets.add(new Vec3d(0.0, 3.0, 0.0));
        }
        if (antiStep) {
            offsets.addAll(getOffsetList(2, false));
        }
        if (legs) {
            offsets.addAll(getOffsetList(0, false));
        }
        if (platform) {
            offsets.addAll(getOffsetList(-1, false));
            offsets.add(new Vec3d(0.0, -1.0, 0.0));
        }
        if (antiDrop) {
            offsets.add(new Vec3d(0.0, -2.0, 0.0));
        }
        return offsets;
    }
    
    public static Vec3d[] getHeightOffsets(final int min, final int max) {
        final List<Vec3d> offsets = new ArrayList<Vec3d>();
        for (int i = min; i <= max; ++i) {
            offsets.add(new Vec3d(0.0, (double)i, 0.0));
        }
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    
    public static BlockPos getRoundedBlockPos(final Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.func_174791_d(), 0));
    }
    
    public static boolean isLiving(final Entity entity) {
        return entity instanceof EntityLivingBase;
    }
    
    public static boolean isAlive(final Entity entity) {
        return isLiving(entity) && !entity.field_70128_L && ((EntityLivingBase)entity).func_110143_aJ() > 0.0f;
    }
    
    public static boolean isDead(final Entity entity) {
        return !isAlive(entity);
    }
    
    public static float getHealth(final Entity entity) {
        if (isLiving(entity)) {
            final EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.func_110143_aJ() + livingBase.func_110139_bj();
        }
        return 0.0f;
    }
    
    public static float getHealth(final Entity entity, final boolean absorption) {
        if (isLiving(entity)) {
            final EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.func_110143_aJ() + (absorption ? livingBase.func_110139_bj() : 0.0f);
        }
        return 0.0f;
    }
    
    public static boolean canEntityFeetBeSeen(final Entity entityIn) {
        return EntityUtil.mc.field_71441_e.func_147447_a(new Vec3d(EntityUtil.mc.field_71439_g.field_70165_t, EntityUtil.mc.field_71439_g.field_70165_t + EntityUtil.mc.field_71439_g.func_70047_e(), EntityUtil.mc.field_71439_g.field_70161_v), new Vec3d(entityIn.field_70165_t, entityIn.field_70163_u, entityIn.field_70161_v), false, true, false) == null;
    }
    
    public static boolean isntValid(final Entity entity, final double range) {
        return entity == null || isDead(entity) || entity.equals((Object)EntityUtil.mc.field_71439_g) || (entity instanceof EntityPlayer && OyVey.friendManager.isFriend(entity.func_70005_c_())) || EntityUtil.mc.field_71439_g.func_70068_e(entity) > MathUtil.square(range);
    }
    
    public static boolean isValid(final Entity entity, final double range) {
        return !isntValid(entity, range);
    }
    
    public static boolean holdingWeapon(final EntityPlayer player) {
        return player.func_184614_ca().func_77973_b() instanceof ItemSword || player.func_184614_ca().func_77973_b() instanceof ItemAxe;
    }
    
    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (EntityUtil.mc.field_71439_g.func_70644_a((Potion)Objects.requireNonNull(Potion.func_188412_a(1)))) {
            maxModifier *= 1.0 + 0.2 * (Objects.requireNonNull(EntityUtil.mc.field_71439_g.func_70660_b((Potion)Objects.requireNonNull(Potion.func_188412_a(1)))).func_76458_c() + 1);
        }
        return maxModifier;
    }
    
    public static void mutliplyEntitySpeed(final Entity entity, final double multiplier) {
        if (entity != null) {
            entity.field_70159_w *= multiplier;
            entity.field_70179_y *= multiplier;
        }
    }
    
    public static boolean isEntityMoving(final Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return EntityUtil.mc.field_71474_y.field_74351_w.func_151470_d() || EntityUtil.mc.field_71474_y.field_74368_y.func_151470_d() || EntityUtil.mc.field_71474_y.field_74370_x.func_151470_d() || EntityUtil.mc.field_71474_y.field_74366_z.func_151470_d();
        }
        return entity.field_70159_w != 0.0 || entity.field_70181_x != 0.0 || entity.field_70179_y != 0.0;
    }
    
    public static double getEntitySpeed(final Entity entity) {
        if (entity != null) {
            final double distTraveledLastTickX = entity.field_70165_t - entity.field_70169_q;
            final double distTraveledLastTickZ = entity.field_70161_v - entity.field_70166_s;
            final double speed = MathHelper.func_76133_a(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
            return speed * 20.0;
        }
        return 0.0;
    }
    
    public static boolean is32k(final ItemStack stack) {
        return EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, stack) >= 1000;
    }
    
    public static void moveEntityStrafe(final double speed, final Entity entity) {
        if (entity != null) {
            final MovementInput movementInput = EntityUtil.mc.field_71439_g.field_71158_b;
            double forward = movementInput.field_192832_b;
            double strafe = movementInput.field_78902_a;
            float yaw = EntityUtil.mc.field_71439_g.field_70177_z;
            if (forward == 0.0 && strafe == 0.0) {
                entity.field_70159_w = 0.0;
                entity.field_70179_y = 0.0;
            }
            else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    }
                    else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    }
                    else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                entity.field_70159_w = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
                entity.field_70179_y = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
            }
        }
    }
    
    public static boolean rayTraceHitCheck(final Entity entity, final boolean shouldCheck) {
        return !shouldCheck || EntityUtil.mc.field_71439_g.func_70685_l(entity);
    }
    
    public static Color getColor(final Entity entity, final int red, final int green, final int blue, final int alpha, final boolean colorFriends) {
        Color color = new Color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
        if (entity instanceof EntityPlayer && colorFriends && OyVey.friendManager.isFriend((EntityPlayer)entity)) {
            color = new Color(0.33333334f, 1.0f, 1.0f, alpha / 255.0f);
        }
        return color;
    }
    
    public static boolean isMoving() {
        return EntityUtil.mc.field_71439_g.field_191988_bg != 0.0 || EntityUtil.mc.field_71439_g.field_70702_br != 0.0;
    }
    
    public static EntityPlayer getClosestEnemy(final double distance) {
        EntityPlayer closest = null;
        for (final EntityPlayer player : EntityUtil.mc.field_71441_e.field_73010_i) {
            if (isntValid((Entity)player, distance)) {
                continue;
            }
            if (closest == null) {
                closest = player;
            }
            else {
                if (EntityUtil.mc.field_71439_g.func_70068_e((Entity)player) >= EntityUtil.mc.field_71439_g.func_70068_e((Entity)closest)) {
                    continue;
                }
                closest = player;
            }
        }
        return closest;
    }
    
    public static boolean checkCollide() {
        return !EntityUtil.mc.field_71439_g.func_70093_af() && (EntityUtil.mc.field_71439_g.func_184187_bx() == null || EntityUtil.mc.field_71439_g.func_184187_bx().field_70143_R < 3.0f) && EntityUtil.mc.field_71439_g.field_70143_R < 3.0f;
    }
    
    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos((EntityUtil.mc.field_71439_g.func_184187_bx() != null) ? EntityUtil.mc.field_71439_g.func_184187_bx().field_70165_t : EntityUtil.mc.field_71439_g.field_70165_t, (EntityUtil.mc.field_71439_g.func_184187_bx() != null) ? EntityUtil.mc.field_71439_g.func_184187_bx().field_70163_u : EntityUtil.mc.field_71439_g.field_70163_u, (EntityUtil.mc.field_71439_g.func_184187_bx() != null) ? EntityUtil.mc.field_71439_g.func_184187_bx().field_70161_v : EntityUtil.mc.field_71439_g.field_70161_v);
    }
    
    public static double[] forward(final double speed) {
        float forward = EntityUtil.mc.field_71439_g.field_71158_b.field_192832_b;
        float side = EntityUtil.mc.field_71439_g.field_71158_b.field_78902_a;
        float yaw = EntityUtil.mc.field_71439_g.field_70126_B + (EntityUtil.mc.field_71439_g.field_70177_z - EntityUtil.mc.field_71439_g.field_70126_B) * EntityUtil.mc.func_184121_ak();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
    
    public static Map<String, Integer> getTextRadarPlayers() {
        Map<String, Integer> output = new HashMap<String, Integer>();
        final DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.CEILING);
        final DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        final StringBuilder healthSB = new StringBuilder();
        final StringBuilder distanceSB = new StringBuilder();
        for (final EntityPlayer player : EntityUtil.mc.field_71441_e.field_73010_i) {
            if (player.func_82150_aj()) {
                continue;
            }
            if (player.func_70005_c_().equals(EntityUtil.mc.field_71439_g.func_70005_c_())) {
                continue;
            }
            final int hpRaw = (int)getHealth((Entity)player);
            final String hp = dfHealth.format(hpRaw);
            healthSB.append("\u00c2§");
            if (hpRaw >= 20) {
                healthSB.append("a");
            }
            else if (hpRaw >= 10) {
                healthSB.append("e");
            }
            else if (hpRaw >= 5) {
                healthSB.append("6");
            }
            else {
                healthSB.append("c");
            }
            healthSB.append(hp);
            final int distanceInt = (int)EntityUtil.mc.field_71439_g.func_70032_d((Entity)player);
            final String distance = dfDistance.format(distanceInt);
            distanceSB.append("\u00c2§");
            if (distanceInt >= 25) {
                distanceSB.append("a");
            }
            else if (distanceInt > 10) {
                distanceSB.append("6");
            }
            else {
                distanceSB.append("c");
            }
            distanceSB.append(distance);
            output.put(healthSB.toString() + " " + (OyVey.friendManager.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RED) + player.func_70005_c_() + " " + distanceSB.toString() + " \u00c2§f0", (int)EntityUtil.mc.field_71439_g.func_70032_d((Entity)player));
            healthSB.setLength(0);
            distanceSB.setLength(0);
        }
        if (!output.isEmpty()) {
            output = MathUtil.sortByValue(output, false);
        }
        return output;
    }
    
    public static boolean isAboveBlock(final Entity entity, final BlockPos blockPos) {
        return entity.field_70163_u >= blockPos.func_177956_o();
    }
    
    static {
        antiDropOffsetList = new Vec3d[] { new Vec3d(0.0, -2.0, 0.0) };
        platformOffsetList = new Vec3d[] { new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0) };
        legOffsetList = new Vec3d[] { new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0) };
        OffsetList = new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0) };
        antiStepOffsetList = new Vec3d[] { new Vec3d(-1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0) };
        antiScaffoldOffsetList = new Vec3d[] { new Vec3d(0.0, 3.0, 0.0) };
    }
}
