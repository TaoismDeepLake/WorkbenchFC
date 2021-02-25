package com.deeplake.workbenchfc.item.food;

import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFoodCoal extends ItemFoodBase {
    public ItemFoodCoal(String name, int amount, float saturation, boolean isWolfFood) {
        super(name, amount, saturation, isWolfFood);
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 1600;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote)
        {
            int furLevel = IDLNBTUtil.GetElemAuto(player, CommonDef.N_FURNACE);
            if (furLevel < 0)
            {
                player.setFire(-furLevel);
            }
        }
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (super.onItemRightClick(worldIn, playerIn, handIn).getType() == EnumActionResult.SUCCESS
        && IDLNBTUtil.GetElemAuto(playerIn, CommonDef.N_FURNACE) > 0)
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
        else
        {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }
}
