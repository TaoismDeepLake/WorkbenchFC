package com.deeplake.workbenchfc.util;

import net.minecraft.item.ItemStack;

public class MessageDef {
    //GENERAL:
    public static final String OUT_OF_RANGE = "workbenchfc.msg.out_of_range";
    public static final String IN_COOLDOWN = "workbenchfc.skill.msg.cool_down";
    public static final String NOT_CASTABLE_MAINHAND = "workbenchfc.skill.msg.not_castable_mainhand";
    public static final String NOT_CASTABLE_OFFHAND = "workbenchfc.skill.msg.not_castable_offhand";

    public static final String OPENED_A_CHEST = "workbenchfc.msg.opened_chest";

    public static final String CHEST_NEED_HEALTH = "workbenchfc.msg.chest_need_more_health";

    public static String getSkillCastKey(ItemStack stack, int index)
    {
        //remove"item."
        return String.format("msg.%s.cast.%d", stack.getUnlocalizedName().substring(5), index);
    }
}
