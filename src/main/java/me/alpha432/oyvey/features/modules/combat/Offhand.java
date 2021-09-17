// 
// Decompiled by Procyon v0.5.36
// 

package me.alpha432.oyvey.features.modules.combat;

import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.Entity;
import me.alpha432.oyvey.util.EntityUtil;
import net.minecraft.item.ItemSword;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.event.events.PacketEvent;
import org.lwjgl.input.Mouse;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import me.alpha432.oyvey.event.events.ProcessRightClickBlockEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.alpha432.oyvey.util.Timer;
import me.alpha432.oyvey.util.InventoryUtil;
import java.util.Queue;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;

public class Offhand extends Module
{
    public Setting<Boolean> crystal;
    public Setting<Float> crystalHealth;
    public Setting<Float> crystalHoleHealth;
    public Setting<Boolean> gapple;
    public Setting<Boolean> armorCheck;
    public Setting<Integer> actions;
    public Mode2 currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private final Queue<InventoryUtil.Task> taskList;
    private static Offhand instance;
    private Timer timer;
    private Timer secondTimer;
    private boolean second;
    private boolean switchedForHealthReason;
    
    public Offhand() {
        super("Offhand", "Allows you to switch up your Offhand.", Category.COMBAT, true, false, false);
        this.crystal = (Setting<Boolean>)this.register(new Setting("Crystal", (T)true));
        this.crystalHealth = (Setting<Float>)this.register(new Setting("CrystalHP", (T)13.0f, (T)0.1f, (T)36.0f));
        this.crystalHoleHealth = (Setting<Float>)this.register(new Setting("CrystalHoleHP", (T)3.5f, (T)0.1f, (T)36.0f));
        this.gapple = (Setting<Boolean>)this.register(new Setting("Gapple", (T)true));
        this.armorCheck = (Setting<Boolean>)this.register(new Setting("ArmorCheck", (T)true));
        this.actions = (Setting<Integer>)this.register(new Setting("Packets", (T)4, (T)1, (T)4));
        this.currentMode = Mode2.TOTEMS;
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.second = false;
        this.switchedForHealthReason = false;
        Offhand.instance = this;
    }
    
    public static Offhand getInstance() {
        if (Offhand.instance == null) {
            Offhand.instance = new Offhand();
        }
        return Offhand.instance;
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.func_77973_b() == Items.field_185158_cP && Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && Offhand.mc.field_71476_x != null && event.pos == Offhand.mc.field_71476_x.func_178782_a()) {
            event.setCanceled(true);
            Offhand.mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
            Offhand.mc.field_71442_b.func_187101_a((EntityPlayer)Offhand.mc.field_71439_g, (World)Offhand.mc.field_71441_e, EnumHand.OFF_HAND);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (Offhand.mc.field_71439_g != null && Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && Offhand.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && Mouse.isButtonDown(1)) {
                Offhand.mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
                Offhand.mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
            }
        }
        else if (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && Offhand.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
            Offhand.mc.field_71474_y.field_74313_G.field_74513_e = false;
        }
        if (nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!Feature.fullNullCheck() && Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && Offhand.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && Offhand.mc.field_71474_y.field_74313_G.func_151470_d()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
                if (packet.func_187022_c() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        Offhand.mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
                        Offhand.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            }
            else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                final CPacketPlayerTryUseItem packet2 = event.getPacket();
                if (packet2.func_187028_a() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
            return "Crystals";
        }
        if (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
            return "Totems";
        }
        if (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao) {
            return "Gapples";
        }
        return null;
    }
    
    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP);
        this.holdingTotem = (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY);
        this.holdingGapple = (Offhand.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao);
        this.totems = Offhand.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        if (this.holdingTotem) {
            this.totems += Offhand.mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        }
        this.crystals = Offhand.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_185158_cP).mapToInt(ItemStack::func_190916_E).sum();
        if (this.holdingCrystal) {
            this.crystals += Offhand.mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_185158_cP).mapToInt(ItemStack::func_190916_E).sum();
        }
        this.gapples = Offhand.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_151153_ao).mapToInt(ItemStack::func_190916_E).sum();
        if (this.holdingGapple) {
            this.gapples += Offhand.mc.field_71439_g.field_71071_by.field_184439_c.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_151153_ao).mapToInt(ItemStack::func_190916_E).sum();
        }
        this.doSwitch();
    }
    
    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.gapple.getValue() && Offhand.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && Offhand.mc.field_71474_y.field_74313_G.func_151470_d()) {
            this.currentMode = Mode2.GAPPLES;
        }
        else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue() && ((EntityUtil.isSafe((Entity)Offhand.mc.field_71439_g) && EntityUtil.getHealth((Entity)Offhand.mc.field_71439_g, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.field_71439_g, true) > this.crystalHealth.getValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity)Offhand.mc.field_71439_g) && EntityUtil.getHealth((Entity)Offhand.mc.field_71439_g, true) <= this.crystalHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.field_71439_g, true) <= this.crystalHoleHealth.getValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)Offhand.mc.field_71439_g) && EntityUtil.getHealth((Entity)Offhand.mc.field_71439_g, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity)Offhand.mc.field_71439_g, true) > this.crystalHealth.getValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue() && (Offhand.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_190931_a || Offhand.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.HEAD).func_77973_b() == Items.field_190931_a || Offhand.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.LEGS).func_77973_b() == Items.field_190931_a || Offhand.mc.field_71439_g.func_184582_a(EntityEquipmentSlot.FEET).func_77973_b() == Items.field_190931_a)) {
            this.setMode(Mode2.TOTEMS);
        }
        if (Offhand.mc.field_71462_r instanceof GuiContainer && !(Offhand.mc.field_71462_r instanceof GuiInventory)) {
            return;
        }
        final Item currentOffhandItem = Offhand.mc.field_71439_g.func_184592_cb().func_77973_b();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems > 0 && !this.holdingTotem) {
                    this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.field_190929_cY, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                    this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                    break;
                }
                break;
            }
            case GAPPLES: {
                if (this.gapples > 0 && !this.holdingGapple) {
                    this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.field_151153_ao, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                    this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                    break;
                }
                break;
            }
            default: {
                if (this.crystals > 0 && !this.holdingCrystal) {
                    this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.field_185158_cP, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                    this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                    break;
                }
                break;
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            final InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
                task.run();
                if (task.isSwitching()) {
                    this.didSwitchThisTick = true;
                }
            }
        }
    }
    
    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.field_185158_cP) {
            return this.lastCrystalSlot;
        }
        if (item == Items.field_151153_ao) {
            return this.lastGappleSlot;
        }
        if (item == Items.field_190929_cY) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.field_190931_a) {
            return -1;
        }
        return slotIn;
    }
    
    private void putItemInOffhand(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }
    
    public void setMode(final Mode2 mode) {
        if (this.currentMode == mode) {
            this.currentMode = Mode2.TOTEMS;
        }
        else {
            this.currentMode = mode;
        }
    }
    
    public enum Mode2
    {
        TOTEMS, 
        GAPPLES, 
        CRYSTALS;
    }
}
