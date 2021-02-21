package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.util.EntityUtil;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_WORKBENCH;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModEventsWorkbench {

	@SubscribeEvent
	public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
	  	EntityPlayer player = event.player;
		final ItemStack crafting = event.crafting;
		final IInventory craftMatrix = event.craftMatrix;
		final Item resultType = crafting.getItem();

		if (IDLNBTUtil.GetStrongAuto(player) == N_WORKBENCH)
		{
			if (resultType instanceof ItemTool || resultType instanceof ItemArmor)
			{
				EnchantmentHelper.addRandomEnchantment(player.getRNG(), crafting, player.experienceLevel + 1, false);
			}

			//count plus
			int sizeBox = craftMatrix.getSizeInventory();

			for (int k = 0; k < sizeBox; ++k) {
				ItemStack stack = craftMatrix.getStackInSlot(k);
				if (stack.isEmpty())
				{
					continue;
				}

				if (stack.getItem() instanceof ItemBlock)
				{
					return;
				}
			}

			if (resultType instanceof ItemBlock)
			{
				return;
			}

			int count = crafting.getCount();
			if (count > 1)
			{
				crafting.setCount(Math.min(crafting.getMaxStackSize(), count + 1));
			}

		} else if (IDLNBTUtil.GetWeakAuto(player) == N_WORKBENCH)
		{
			if (!crafting.isEmpty())
			{
				crafting.setCount(1);
				if (crafting.isItemStackDamageable())
				{
					crafting.setItemDamage(crafting.getMaxDamage() / 2);
				}
			}
		}
	}
}
