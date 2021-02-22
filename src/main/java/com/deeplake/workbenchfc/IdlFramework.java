package com.deeplake.workbenchfc;

import com.deeplake.workbenchfc.design.ElemAttrManager;
import com.deeplake.workbenchfc.gui.ModGuiElementLoader;
import com.deeplake.workbenchfc.init.ModConfig;
import com.deeplake.workbenchfc.init.ModRecipes;
import com.deeplake.workbenchfc.init.ModSpawn;
import com.deeplake.workbenchfc.init.RegistryHandler;
import com.deeplake.workbenchfc.keys.KeyboardManager;
import com.deeplake.workbenchfc.meta.MetaUtil;
import com.deeplake.workbenchfc.network.NetworkHandler;
import com.deeplake.workbenchfc.proxy.ProxyBase;
import com.deeplake.workbenchfc.util.CommonDef;
import com.deeplake.workbenchfc.util.Reference;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import static com.deeplake.workbenchfc.init.RegistryHandler.initRegistries;

//To let the player be a traveling god who plays yin-yang magic.

@Mod(modid = IdlFramework.MODID, name = IdlFramework.NAME, version = IdlFramework.VERSION)//dependencies = "required-after:Forge@[14.23.5.2705,)"
public class IdlFramework {
    public static final String MODID = "workbenchfc";
    public static final String NAME = "Workbench, Furnace & Chests";
    public static final String VERSION = "0.1.001";

    public static Logger logger;

    public static final boolean SHOW_WARN = true;

    @Mod.Instance
    public static IdlFramework instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static ProxyBase proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        RegistryHandler.preInitRegistries(event);

    }

    @EventHandler
    public static void Init(FMLInitializationEvent event) {
        ModRecipes.Init();
        RegisterTileEntity();
        initRegistries(event);
        new ModGuiElementLoader();
        if (!proxy.isServer())
        {
            KeyboardManager.init();
        }
        NetworkHandler.init();

		LogWarning("%s has finished its initializations", MODID);
        ElemAttrManager.initFactor();
	}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Moved Spawning registry to last since forge doesn't auto-generate sub
        // "M' biomes until late
        if (ModConfig.SPAWN_CONF.SPAWN) {
            ModSpawn.registerSpawnList();
        }

        MetaUtil.isIdeallandLoaded = Loader.isModLoaded("idealland");


        TrashTalking();

        RegistryHandler.postInitReg();
    }

    @EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        RegistryHandler.serverRegistries(event);
    }


    private void TrashTalking() {
        if (MetaUtil.isIdeallandLoaded) {
            IdlFramework.Log("Bow to idealland.");
        } else {
            //IdlFramework.Log("No Item Renderer found.");
        }
    }

    private static void RegisterTileEntity() {
//        GameRegistry.registerTileEntity(TileEntityDeBoomOrb.class, new ResourceLocation(MODID, "deboom_orb_basic"));

        //GameRegistry.registerTileEntity(TileEntityBuilderFarm.class, new ResourceLocation(MODID, "builder_farm_basic"));
        //GameRegistry.registerTileEntity(TileEntityBuilderOne.class, new ResourceLocation(MODID, "builder.builder_one"));
    }

    public static void LogWarning(String str, Object... args) {
        if (SHOW_WARN) {
            logger.warn(String.format(str, args));
        }
    }

    public static void LogWarning(String str) {
        if (SHOW_WARN) {
            logger.warn(str);
        }
    }

    public static void Log(String str) {
//        if (ModConfig.GeneralConf.LOG_ON)
//        {
        logger.info(str);
//        }
    }

    public static void Log(String str, Object... args) {
//        if (ModConfig.GeneralConf.LOG_ON)
//        {
        logger.info(String.format(str, args));
//        }
    }
}