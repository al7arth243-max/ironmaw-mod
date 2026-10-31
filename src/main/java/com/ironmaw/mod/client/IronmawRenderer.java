package com.ironmaw.mod.client;

import com.ironmaw.mod.entity.IronmawEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IronmawRenderer extends GeoEntityRenderer<IronmawEntity> {

    /**
     * The source model is about 3 blocks tall and 6 blocks long, so it is shrunk to 40%.
     * In game that is roughly 1 block at the shoulders (1.3 to the spike tips) and about 2 blocks long:
     * clearly bigger than a wolf (0.85 tall) and smaller than a polar bear (1.4 tall).
     * Change this one number to resize the Ironmaw (remember to match the hitbox in ModEntities).
     */
    public static final float MODEL_SCALE = 0.4F;

    public IronmawRenderer(EntityRendererProvider.Context context) {
        super(context, new IronmawModel());
        this.withScale(MODEL_SCALE);
        this.shadowRadius = 0.75F;
    }
}
