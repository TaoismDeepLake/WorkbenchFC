package com.deeplake.workbenchfc.entity;

import com.deeplake.workbenchfc.IdlFramework;
import com.deeplake.workbenchfc.entity.creatures.moroon.EntityMoroonUnitBase;
import com.deeplake.workbenchfc.entity.creatures.render.*;
import com.deeplake.workbenchfc.entity.projectiles.EntityIdlProjectile;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler {

    public static void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityMoroonUnitBase.class, RenderMoroonHumanoid::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityIdlProjectile.class, renderManager -> new RenderBullet<>(renderManager, new ResourceLocation(IdlFramework.MODID,
                "textures/entity/projectiles/bullet_norm.png")));
    }
}
