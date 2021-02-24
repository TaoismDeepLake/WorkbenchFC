package com.deeplake.workbenchfc.design;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.CommonFunctions;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class ElemAttrManager {
    static UUID CHEST_BUFF_UUID = UUID.fromString("4c85682d-3e57-49cf-9f8e-a5965b55ff64");
    public static final AttributeModifier CHEST_BUFF = (new AttributeModifier(CHEST_BUFF_UUID, "Chest Health boost", 1.0, 1));
    static UUID WB_BUFF_UUID = UUID.fromString("aad17c23-58dd-489e-b45d-619bd8f48e22");
    public static final AttributeModifier WB_BUFF = (new AttributeModifier(WB_BUFF_UUID, "CraftingTable boost", 0.3, 1));

    static float[] attrFactor;
    static float[] attrFactorDelta;

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
