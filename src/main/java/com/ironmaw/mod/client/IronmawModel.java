package com.ironmaw.mod.client;

import com.ironmaw.mod.IronmawMod;
import com.ironmaw.mod.entity.IronmawEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

/**
 * Loads:
 *   assets/ironmaw/geo/entity/ironmaw.geo.json
 *   assets/ironmaw/animations/entity/ironmaw.animation.json
 *   assets/ironmaw/textures/entity/ironmaw.png
 * The second argument makes the "head" bone follow where the Ironmaw is looking.
 */
public class IronmawModel extends DefaultedEntityGeoModel<IronmawEntity> {
    public IronmawModel() {
        super(new ResourceLocation(IronmawMod.MODID, "ironmaw"), true);
    }
}
