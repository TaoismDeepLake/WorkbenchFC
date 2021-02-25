package com.deeplake.workbenchfc.command;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.CommonFunctions;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;


public class CommandDisp extends CommandBase {

    private final List<String> aliases = Lists.newArrayList(IdlFramework.MODID, "wfcl", "wfclist");

    @SubscribeEvent
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getName() {
        return "wfclist";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "wfclist";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) sender;

            ElemAttrManager.dispExp(player, CommonDef.N_WORKBENCH);
            ElemAttrManager.dispExp(player, CommonDef.N_FURNACE);
            ElemAttrManager.dispExp(player, CommonDef.N_CHEST);
        }
    }
}
