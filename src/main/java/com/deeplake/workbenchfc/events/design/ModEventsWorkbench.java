package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.EntityUtil;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_FURNACE;
import static com.deeplake.workbenchfc.util.CommonDef.N_WORKBENCH;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModEventsWorkbench {

	static final Item AIR = Item.getItemFromBlock(Blocks.AIR);

	public static boolean checkCanHaveMoreResult(PlayerEvent.ItemCraftedEvent event)
	{
		final ItemStack crafting = event.crafting;
		final IInventory craftMatrix = event.craftMatrix;

		if (crafting.isEmpty())
		{
			return false;
		}

		if (crafting.getCount() == 1)
		{
			return false;
		}

		int ingreCount = 0;
		int sizeBox = craftMatrix.getSizeInventory();
		Item lastType = AIR;
		boolean singularIngredient = true;
		for (int k = 0; k < sizeBox; ++k) {
			ItemStack stack = craftMatrix.getStackInSlot(k);
			if (stack.isEmpty())
			{
				continue;
			}
			else {
				if (singularIngredient)
				{
					Item type = stack.getItem();
					if (type != lastType)
					{
						if (lastType != AIR)
						{
							singularIngredient = false;
						}
						lastType = type;
					}
				}

				ingreCount++;
			}
		}

		//if there is only one type of ingredient, forbid it from makeing extra items (ingot-nugget) etc
		if (ingreCount == 1 || singularIngredient)
		{
			return false;
		}

		return true;
	}

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
		if (block instanceof BlockWorkbench)
		{
			int level = IDLNBTUtil.GetElemAuto(player, N_WORKBENCH);
			if (level < 0)
			{
				if (player.getRNG().nextFloat() < -0.1f * level)
				{
					player.attackEntityFrom(DamageSource.GENERIC, 2f);
				}
			}
			else {

			}
		}
	}

	@SubscribeEvent
	public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
	  	EntityPlayer player = event.player;
		final ItemStack crafting = event.crafting;
		final IInventory craftMatrix = event.craftMatrix;
		final Item resultType = crafting.getItem();

		if (player.world.isRemote)
		{
			return;
		}

		if (crafting.isEmpty())
		{
			return;
		}

		int levelWB = IDLNBTUtil.GetElemAuto(player, N_WORKBENCH);

		if (levelWB > 0)
		{
			if (crafting.isItemEnchantable())
			{
				EnchantmentHelper.addRandomEnchantment(player.getRNG(), crafting,  player.experienceLevel + levelWB + levelWB, false);

			}else if (checkCanHaveMoreResult(event))
			{//check ingredient
				if (player.getRNG().nextFloat() < 0.1f * levelWB) {
					crafting.setCount(Math.min(crafting.getMaxStackSize(), crafting.getCount() + 1));
				}
			}
		}
		else if (levelWB < 0)
		{
			if (!crafting.isEmpty())
			{
				//crafting.setCount(1);
				if (crafting.isItemStackDamageable())
				{
					crafting.setItemDamage((int) Math.min(crafting.getMaxDamage() - 1, -levelWB * crafting.getMaxDamage() * 0.09f));
				}
			}
		}
	}
}
