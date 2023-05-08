package extensions.net.minecraft.client.gui.Font;

import com.mojang.blaze3d.vertex.PoseStack;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;

@Extension
@Available("[1.16, 1.19.4)")
public class MatrixSupport {

    public static int drawInBatch(@This Font font, FormattedCharSequence sequence, float f, float g, int i, boolean bl, PoseStack.Pose pose, MultiBufferSource buffers, boolean bl2, int j, int k) {
        return font.drawInBatch(sequence, f, g, i, bl, pose.pose(), buffers, bl2, j, k);
    }
}
