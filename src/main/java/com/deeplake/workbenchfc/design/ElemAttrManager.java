package com.deeplake.workbenchfc.design;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.item.ModItems;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.CommonFunctions;
import com.deeplake.workbenchfc.util.MessageDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

import static com.deeplake.workbenchfc.util.CommonDef.*;

public class ElemAttrManager {
    static UUID CHEST_BUFF_UUID = UUID.fromString("4c85682d-3e57-49cf-9f8e-a5965b55ff64");
    public static final AttributeModifier CHEST_BUFF = (new AttributeModifier(CHEST_BUFF_UUID, "Chest Health boost", 1.0, 1));
    static UUID WB_BUFF_UUID = UUID.fromString("aad17c23-58dd-489e-b45d-619bd8f48e22");
    public static final AttributeModifier WB_BUFF = (new AttributeModifier(WB_BUFF_UUID, "CraftingTable boost", 0.3, 1));

    static float[] attrFactor;
    static float[] attrFactorDelta;

    public static final Item I_CHEST = Item.getItemFromBlock(Blocks.CHEST);
    public static final Item I_WORKBENCH = Item.getItemFromBlock(Blocks.CRAFTING_TABLE);
    public static final Item I_FURNACE = Item.getItemFromBlock(Blocks.FURNACE);

    public static int[] XP_TO_NEXT;

    public static int getArrayIndex(int level)
    {
        if (level < -CommonDef.MAX_LEVEL || level > CommonDef.MAX_LEVEL)
        {
            IdlFramework.LogWarning("invalid level %d", level);
            return 0;
        }

        return level + CommonDef.MAX_LEVEL;
    }

    public static float getModifier(int level)
    {
        return attrFactor[getArrayIndex(level)];
    }

    public static float getModifierDelta(int level)
    {
        return attrFactorDelta[getArrayIndex(level)];
    }

    public static void initFactor() {
        attrFactor = new float[CommonDef.MAX_LEVEL * 2 + 1];
        attrFactorDelta = new float[CommonDef.MAX_LEVEL * 2 + 1];
        for (int i = -CommonDef.MAX_LEVEL; i <= CommonDef.MAX_LEVEL; i++) {
            attrFactorDelta[getArrayIndex(i)] = i > 0 ? 1f : -1f;//standard
        }

        int[] doubleIndex = {3, 5, 8};
        for (int i = 0; i < doubleIndex.length; i++) {
            //some level increase attr more
            attrFactorDelta[getArrayIndex(doubleIndex[i])] = 2f;
            attrFactorDelta[getArrayIndex(-doubleIndex[i])] = -2f;
        }

        //base
        attrFactorDelta[0] = 0;

        //level 10 increase attr even more
        attrFactorDelta[getArrayIndex(CommonDef.MAX_LEVEL)] = 3f;
        attrFactorDelta[getArrayIndex(-CommonDef.MAX_LEVEL)] = -3f;

        attrFactor[0] = 0f;

        for (int i = 1; i <= CommonDef.MAX_LEVEL; i++) {
            int index = getArrayIndex(i);
            //positive
            attrFactor[index] = attrFactor[index - 1] + attrFactorDelta[index];
            index = getArrayIndex(-i);
            //negative
            attrFactor[index] = attrFactor[index + 1] + attrFactorDelta[index];
        }

        String s = "";
        for (int i = -CommonDef.MAX_LEVEL; i <= CommonDef.MAX_LEVEL; i++) {
            s = s + String.format("Lv%d:%f\n", i, attrFactor[getArrayIndex(i)]);
        }
        IdlFramework.Log(s);

        XP_TO_NEXT = new int[attrFactorDelta.length];
        for (int i = -CommonDef.MAX_LEVEL; i <= 0; i++) {
            XP_TO_NEXT[getArrayIndex(i)] = 300;
        }
        for (int i = 1; i <= CommonDef.MAX_LEVEL; i++) {
            XP_TO_NEXT[getArrayIndex(i)] = 300 + i * 100 + i * i * 10;
        }
    }

    public static boolean isValidLevel(int level)
    {
        return !(level < -MAX_LEVEL || level >= MAX_LEVEL);
    }

    public static int getNeedXP(int level)
    {
        if (!isValidLevel(level))
        {
            return 999999;
        }
        return XP_TO_NEXT[getArrayIndex(level)];
    }

    public static void examineXP(EntityPlayer player, int type)
    {
        int curLv = IDLNBTUtil.GetElemAuto(player, type);

        if (curLv < -MAX_LEVEL || curLv >= MAX_LEVEL)
        {
            IdlFramework.LogWarning("Examing xp for %s while at level %d", player.getDisplayNameString(), curLv);
            return;
        }

        int curXP = IDLNBTUtil.GetXPAuto(player, type);
        while (curLv < MAX_LEVEL && curXP >= XP_TO_NEXT[getArrayIndex(curLv)])
        {
            curXP-= XP_TO_NEXT[getArrayIndex(curLv)];
            curLv++;
            IDLNBTUtil.SetElemAuto(player, type, curLv);
            IDLNBTUtil.SetXPAuto(player, type, curXP);
            onLevelUp(player, type, curLv);
        }
    }

    public static void onLevelUp(EntityPlayer player, int type, int newLv)
    {
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
        CommonFunctions.SafeSendMsgToPlayer(TextFormatting.BOLD, player, MessageDef.getLevelupKey(type), newLv);
        updatePlayerGivenTypeAttr(player, type);
    }

    public static void dispExp(EntityPlayer player, int type)
    {
        int level = IDLNBTUtil.GetElemAuto(player, type);
        if (isValidLevel(level))
        {
            CommonFunctions.SafeSendMsgToPlayer(TextFormatting.ITALIC, player, MessageDef.getExpKey(type), level, IDLNBTUtil.GetXPAuto(player, type), getNeedXP(level));
        }
        else {
            IdlFramework.Log("Ignored Exp disp: %s is Lv %d", player.getName(), level);
        }
    }

    public static void setAttrModifier(IAttributeInstance iattributeinstance, AttributeModifier modifier)
    {
        if (iattributeinstance.hasModifier(modifier)) {
            //prevent crash
            iattributeinstance.removeModifier(WB_BUFF_UUID);
        }
        iattributeinstance.applyModifier(modifier);
    }

    public static void updatePlayerGivenTypeAttr(Entity player, int type)
    {
        if (player instanceof EntityPlayer)
        {
            switch (type)
            {
                case CommonDef.N_WORKBENCH:
                    updatePlayerWorkbenchAttr((EntityPlayer) player, IDLNBTUtil.GetElemAuto(player, type));
                    break;
                case CommonDef.N_FURNACE:
                    updatePlayerFurnaceAttr((EntityPlayer) player, IDLNBTUtil.GetElemAuto(player, type));
                    break;
                case CommonDef.N_CHEST:
                    updatePlayerChestAttr((EntityPlayer) player, IDLNBTUtil.GetElemAuto(player, type));
                    break;
                default:
                    break;
            }
        }
    }

    public static int getElemTypeFromStack(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return N_NONE;
        }

        Item item = stack.getItem();
        if (item == I_CHEST)
        {
            return CommonDef.N_CHEST;
        } else if (item == I_FURNACE)
        {
            return CommonDef.N_FURNACE;
        } else if (item == I_WORKBENCH)
        {
            return CommonDef.N_WORKBENCH;
        } else
        {
            return N_NONE;
        }
    }

    public static ItemStack getStackFromType(int type)
    {
        switch (type)
        {
            case N_WORKBENCH:
                return new ItemStack(Blocks.CRAFTING_TABLE);
            case N_FURNACE:
                return new ItemStack(Blocks.FURNACE);
            case N_CHEST:
                return new ItemStack(Blocks.CHEST);
            default:
                return ItemStack.EMPTY;
        }
    }

    public static ItemStack getSmallXPStackFromType(int type)
    {
        switch (type)
        {
            case N_WORKBENCH:
                return new ItemStack(ModItems.XP_CARD[3]);
            case N_FURNACE:
                return new ItemStack(ModItems.XP_CARD[4]);
            case N_CHEST:
                return new ItemStack(ModItems.XP_CARD[5]);
            default:
                return ItemStack.EMPTY;
        }
    }

    public static final float BASE_WB_ATK_SPD = 0.05f;//5% atk speed
    public static final float BASE_C_SPD = 0.05f;//5% speed
    public static final float BASE_C_SPD_NEGA = 0.02f;//2% speed
    public static final float BASE_C_HP = 0.05f;//5% HPMax
    public static final float BASE_C_LUCK = 0.2f;//full = +4 luck

    public static void updatePlayerWorkbenchAttr(EntityPlayer player, int level)
    {
        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        float value = level > 0 ?
                getModifier(level) * BASE_C_HP:
                (level * BASE_WB_ATK_SPD);
        AttributeModifier modifier = (new AttributeModifier(WB_BUFF_UUID, "workbench element bonus", value, 1));
        setAttrModifier(iattributeinstance, modifier);
        IdlFramework.Log("attr:%s",iattributeinstance);
    }

    public static void updatePlayerChestAttr(EntityPlayer player, int level)
    {
        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        float value = level > 0 ?
                getModifier(level) * BASE_C_HP:
               0;
        AttributeModifier modifier = (new AttributeModifier(WB_BUFF_UUID, "chest element bonus", value, 1));
        setAttrModifier(iattributeinstance, modifier);
        IdlFramework.Log("attr:%s",iattributeinstance);

        iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        value = level > 0 ?
                getModifier(level) * BASE_C_SPD:
                getModifier(level) * BASE_C_SPD_NEGA;
        modifier = (new AttributeModifier(WB_BUFF_UUID, "chest element bonus", value, 1));
        setAttrModifier(iattributeinstance, modifier);
        IdlFramework.Log("attr:%s",iattributeinstance);

        iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.LUCK);
        value = level > 0 ?
                getModifier(level) * BASE_C_LUCK:
                0;
        modifier = (new AttributeModifier(WB_BUFF_UUID, "chest element bonus", value, 1));
        setAttrModifier(iattributeinstance, modifier);
        IdlFramework.Log("attr:%s",iattributeinstance);
    }

    public static void updatePlayerFurnaceAttr(EntityPlayer player, int level)
    {
//        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
//        AttributeModifier modifier = (new AttributeModifier(WB_BUFF_UUID, "furnace element bonus", getModifier(level) * BASE_WB_ATK_SPD, 0));
//        setAttrModifier(iattributeinstance, modifier);
    }

    public static void logValue(EntityPlayer player)
    {
        CommonFunctions.SafeSendMsgToPlayer(player, "%s,%s,%s", IDLNBTUtil.GetElemAuto(player, 0), IDLNBTUtil.GetElemAuto(player, 1), IDLNBTUtil.GetElemAuto(player, 2));
    }

}
