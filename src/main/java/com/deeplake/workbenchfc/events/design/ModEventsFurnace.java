package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.util.EntityUtil;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
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
        if (trueSource instanceof EntityPlayer) {
            if (IDLNBTUtil.GetStrongAuto(trueSource) == N_FURNACE)
            {
                source.setFireDamage();
                hurtOne.setFire(3);
            }
        } else if (trueSource instanceof EntityLivingBase)
        {
            if (EntityUtil.GetTypeAuto((EntityLivingBase) trueSource) == N_FURNACE)
            {
                source.setFireDamage();
                hurtOne.setFire(3);
            }
        }

		if (hurtOne instanceof EntityPlayer)
		{
		    if (source.isFireDamage())
            {
                if (IDLNBTUtil.GetStrongAuto(hurtOne) == N_FURNACE) {
                    evt.setAmount(evt.getAmount() * 0.2f);
                } else if (IDLNBTUtil.GetWeakAuto(hurtOne) == N_FURNACE){
                    evt.setAmount(evt.getAmount() * 2.0f);
                }
            }
		}
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
