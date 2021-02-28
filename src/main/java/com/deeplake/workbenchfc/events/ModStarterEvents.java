package com.deeplake.workbenchfc.events;

import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.IDLNBT;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Random;

import static com.deeplake.workbenchfc.util.IDLNBT.getPlayerIdeallandIntSafe;
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

			  Random random = player.getRNG();

			  int valStrong = 1 + random.nextInt(5);
			  int valWeak = - 1 - random.nextInt(CommonDef.MAX_LEVEL);

			  int strongType = player.getRNG().nextInt(CommonDef.N_COUNT);
			  IDLNBTUtil.SetElemAuto(player, strongType, valStrong);

			  int weakType = (strongType + 1 + player.getRNG().nextInt(CommonDef.N_COUNT - 1)) % CommonDef.N_COUNT;
			  IDLNBTUtil.SetElemAuto(player, weakType, valWeak);

			  int otherType = (CommonDef.N_CHEST + CommonDef.N_FURNACE + CommonDef.N_WORKBENCH) - strongType - weakType;
			  IDLNBTUtil.SetElemAuto(player, otherType, -(valStrong + valWeak));
		  }

			ElemAttrManager.dispExp(player, CommonDef.N_WORKBENCH);
			ElemAttrManager.dispExp(player, CommonDef.N_FURNACE);
			ElemAttrManager.dispExp(player, CommonDef.N_CHEST);
	  }
	
}
