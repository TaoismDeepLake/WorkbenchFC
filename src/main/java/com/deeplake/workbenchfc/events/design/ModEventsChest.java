package com.deeplake.workbenchfc.events.design;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.util.CommonFunctions;
import com.deeplake.workbenchfc.util.MessageDef;
import com.deeplake.workbenchfc.util.NBTStrDef.IDLNBTUtil;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.deeplake.workbenchfc.util.CommonDef.N_CHEST;
import static com.deeplake.workbenchfc.util.MessageDef.CHEST_NEED_HEALTH;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModEventsChest {

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
        if (block instanceof BlockChest)
        {
            int level = IDLNBTUtil.GetElemAuto(player, N_CHEST);

            BlockChest blockChest = (BlockChest) block;
            ILockableContainer container = blockChest.getContainer(world, pos, false);
            if (container != null)//successfully opened
            {
                //first time to open
                if (container instanceof ILootContainer && ((ILootContainer)container).getLootTable() != null)
                {
                    CommonFunctions.SafeSendMsgToPlayer(TextFormatting.GREEN, player, MessageDef.OPENED_A_CHEST);
                    if (level > 0)
                    {
                        player.heal(level);
                        player.addExperience(level);
                    }
                    IDLNBTUtil.AddXPAuto(player, N_CHEST, 20);
                }
            }


            if (level < 0)
            {
                if (player.getMaxHealth() == 0)
                {
                    //prevent div 0 crash
                    IdlFramework.LogWarning("Player Max HP = 0");
                    return;
                }

                if (player.getHealth() / player.getMaxHealth() < -0.1f * level)
                {
                    //need more health
                    CommonFunctions.SafeSendMsgToPlayer(TextFormatting.RED, player, CHEST_NEED_HEALTH, level * 10);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
