package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.item.ModItems;
import com.deeplake.workbenchfc.util.*;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import jdk.nashorn.internal.ir.IdentNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.functions.Smelt;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_FURNACE;
import static com.deeplake.workbenchfc.util.CommonDef.N_WORKBENCH;
import static com.deeplake.workbenchfc.util.MessageDef.CHEST_NEED_HEALTH;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModEventsFurnace {

    public static final Item TORCH = Item.getItemFromBlock(Blocks.TORCH);

    @SubscribeEvent
    public static void onSmelt(PlayerEvent.ItemSmeltedEvent event)
    {
        IdlFramework.Log("Smelted event");
    }

	@SubscribeEvent
	public static void onCreatureHurt(LivingHurtEvent evt) {
		World world = evt.getEntity().getEntityWorld();
		EntityLivingBase hurtOne = evt.getEntityLiving();

		if (evt.isCanceled() || world.isRemote)
		{
			return;
		}

        DamageSource source = evt.getSource();

        Entity trueSource = evt.getSource().getTrueSource();

        int levelAtk = IDLNBTUtil.GetElemAuto(trueSource, N_WORKBENCH);
        int levelHurt = IDLNBTUtil.GetElemAuto(hurtOne, N_WORKBENCH);

        if (trueSource instanceof EntityLivingBase)
        {
            if (((EntityLivingBase) trueSource).getHeldItemOffhand().getItem() == TORCH)
            {
                if (hurtOne.getRNG().nextInt(10) < (levelAtk - levelHurt))
                {
                    source.setFireDamage();
                    hurtOne.setFire(levelAtk - levelHurt);
                }
            }
        }

        if (source.isFireDamage())
        {
            int deltaLevel = Math.max(-CommonDef.MAX_LEVEL, Math.min(CommonDef.MAX_LEVEL, (levelAtk - levelHurt)));
            float factor = 1.0f + 0.03f * ElemAttrManager.getModifier(deltaLevel);
            evt.setAmount(factor * evt.getAmount());
        }

//        if (trueSource instanceof EntityPlayer) {
//            if (IDLNBTUtil.GetStrongAuto(trueSource) == N_FURNACE)
//            {
//                source.setFireDamage();
//                hurtOne.setFire(3);
//            }
//        } else if (trueSource instanceof EntityLivingBase)
//        {
//            if (EntityUtil.GetTypeAuto((EntityLivingBase) trueSource) == N_FURNACE)
//            {
//                source.setFireDamage();
//                hurtOne.setFire(3);
//            }
//        }
//
//		if (hurtOne instanceof EntityPlayer)
//		{
//		    if (source.isFireDamage())
//            {
//                if (IDLNBTUtil.GetStrongAuto(hurtOne) == N_FURNACE) {
//                    evt.setAmount(evt.getAmount() * 0.2f);
//                } else if (IDLNBTUtil.GetWeakAuto(hurtOne) == N_FURNACE){
//                    evt.setAmount(evt.getAmount() * 2.0f);
//                }
//            }
//		}
	}

//    @SubscribeEvent
//    public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event)
//    {
//        EntityLivingBase livingBase = event.getEntityLiving();
//
//        if (livingBase instanceof EntityPlayer)
//        {
//            if (IDLNBTUtil.GetWeakAuto(livingBase) == N_FURNACE)
//            {
//                EntityUtil.TryRemoveGivenBuff(livingBase, MobEffects.FIRE_RESISTANCE);
//            }
//        }
//    }

    @SubscribeEvent
    public static void onPlayerInteractBlock(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        if (world.isRemote)
        {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof BlockFurnace)
        {
            int level = IDLNBTUtil.GetElemAuto(player, N_FURNACE);
            if (level < 0)
            {
                if (player.getRNG().nextFloat() < -0.1f * level)
                {
                    player.setFire(3);
                }
            }
            else {

            }
        }
    }
}
