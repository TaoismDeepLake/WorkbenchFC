package com.deeplake.workbenchfc.events;

import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.IDLNBT;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_CHEST;
import static com.deeplake.workbenchfc.util.CommonDef.N_WORKBENCH;
import static com.deeplake.workbenchfc.util.IDLNBT.*;
import static com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTDef.*;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModStarterEvents {

	@SubscribeEvent
	  public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		  EntityPlayer player = event.player;
		  //IdlFramework.Log(getPlyrIdlTagSafe(player).toString());
		  int lastStarterVer = getPlayerIdeallandIntSafe(player, STARTER_KIT_VERSION_TAG);
		  if(lastStarterVer < CUR_STARTER_KIT_VERSION) {
			  IDLNBT.setPlayerIdeallandTagSafe(player, STARTER_KIT_VERSION_TAG, CUR_STARTER_KIT_VERSION);

//			  int strong = player.getRNG().nextInt(CommonDef.N_COUNT);
//			  IDLNBTUtil.SetIntAuto(player, STRONG_TAG, strong);
//			  int weak = (strong + 1 + player.getRNG().nextInt(CommonDef.N_COUNT - 1)) % CommonDef.N_COUNT;
//			  IDLNBTUtil.SetIntAuto(player, WEAK_TAG, weak);//a different race

//			  if (strong == N_CHEST)
//			  {
//				  IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
//				  player.addItemStackToInventory(new ItemStack(Blocks.CHEST, 4));
//				  iattributeinstance.applyModifier(ElemAttrManager.CHEST_BUFF);
//			  }
//
//			  if (strong == N_WORKBENCH)
//			  {
//				  IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
//				  player.addItemStackToInventory(new ItemStack(Blocks.CHEST, 4));
//				  iattributeinstance.applyModifier(ElemAttrManager.WB_BUFF);
//			  }
		  }
	  }
	
}
