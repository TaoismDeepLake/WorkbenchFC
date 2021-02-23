package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.item.ModItems;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.EntityUtil;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import jdk.nashorn.internal.ir.Block;
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
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_FURNACE;
import static com.deeplake.workbenchfc.util.CommonDef.N_WORKBENCH;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModEventsFurnace {

    public static final Item TORCH = Item.getItemFromBlock(Blocks.TORCH);

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

    @SubscribeEvent
    public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();

        if (livingBase instanceof EntityPlayer)
        {
            if (IDLNBTUtil.GetWeakAuto(livingBase) == N_FURNACE)
            {
                EntityUtil.TryRemoveGivenBuff(livingBase, MobEffects.FIRE_RESISTANCE);
            }
        }
    }
}
