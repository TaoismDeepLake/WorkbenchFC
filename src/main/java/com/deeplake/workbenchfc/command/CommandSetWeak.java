package com.deeplake.workbenchfc.command;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.CommonFunctions;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.google.common.collect.Lists;
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


public class CommandSetWeak extends CommandBase {

    private final List<String> aliases = Lists.newArrayList(IdlFramework.MODID, "wfcw", "wfcweak");

    @SubscribeEvent
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getName() {
        return "wfcweak";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "wfcweak <id 0 ~2>";
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            return;
        }

        String s = args[0];
        int typeID;

        try{
            typeID = Integer.parseInt(s);
            if (typeID < 0 || typeID >= CommonDef.N_COUNT)
            {
                throw  new NumberFormatException("type must be 0~2");
            }
        }catch (NumberFormatException e)
        {
            if (sender instanceof EntityPlayerMP)
            {
                CommonFunctions.SendMsgToPlayerStyled((EntityPlayerMP) sender, "workbenchfc.msg.type_id_invalid", TextFormatting.RED, s);
            }
            return;
        }

        if (sender instanceof EntityPlayer)
        {
            IDLNBTUtil.SetIntAuto((Entity) sender, IDLNBTDef.WEAK_TAG, typeID);
        }
    }
}
