package com.deeplake.workbenchfc.item.food;

import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTDef.XP_TAG;

public class ItemFoodExp extends ItemFoodBase{
    int type;
    int xp;
    public ItemFoodExp(String name, int type, int xp) {
        super(name, 0, 0, false);
        this.type = type;
        this.xp = xp;
        setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote)
        {
            IDLNBTUtil.AddXPAuto(player, type, xp);
        }
    }

}
