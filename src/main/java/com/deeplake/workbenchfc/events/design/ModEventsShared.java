package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_NONE;
import static com.deeplake.workbenchfc.util.CommonDef.N_WORKBENCH;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModEventsShared {
    @SubscribeEvent
    public static void onSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getWorld().isRemote)
        {
            return;
        }

        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase instanceof EntityZombie || livingBase instanceof EntitySkeleton)
        {
            int rand = livingBase.getRNG().nextInt(CommonDef.N_COUNT);
            switch (rand)
            {
                case CommonDef.N_WORKBENCH:
                    livingBase.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.CRAFTING_TABLE));
                    break;
                case CommonDef.N_FURNACE:
                    livingBase.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.FURNACE));
                    break;
                case CommonDef.N_CHEST:
                    livingBase.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Blocks.CHEST));
                    break;
                default:
                    break;
            }
        }
    }

    @SubscribeEvent
    public static void onDrop(LivingDropsEvent evt)
    {
        World world = evt.getEntity().getEntityWorld();
        EntityLivingBase hurtOne = evt.getEntityLiving();

        if (evt.isCanceled() || world.isRemote) {
            return;
        }

        int typeHurt = ElemAttrManager.getElemTypeFromStack(hurtOne.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        if (hurtOne.getRNG().nextFloat() < 0.5f)
        {
            hurtOne.entityDropItem(ElemAttrManager.getSmallXPStackFromType(typeHurt), 1f);
        }
    }


    @SubscribeEvent
    public static void onCreatureHurt(LivingHurtEvent evt) {
        World world = evt.getEntity().getEntityWorld();
        EntityLivingBase hurtOne = evt.getEntityLiving();

        if (evt.isCanceled() || world.isRemote) {
            return;
        }

        Entity trueSource = evt.getSource().getTrueSource();

        int typeHurt = ElemAttrManager.getElemTypeFromStack(hurtOne.getItemStackFromSlot(EntityEquipmentSlot.HEAD));

        if (trueSource instanceof EntityPlayer && typeHurt != N_NONE) {
            int levelAtk = IDLNBTUtil.GetElemAuto(trueSource, typeHurt);
            float factor = 1 + 0.075f * levelAtk;
            evt.setAmount(evt.getAmount() * factor);
        }
    }
}
