package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.guide.AdvancedChestGuideRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AdvancedSkinCanvasView extends UIView {

//    private final GuideRendererManager rendererManager = new GuideRendererManager();
//    private final AdvancedSkinChestModel model = new AdvancedSkinChestModel();
    private final AdvancedChestGuideRenderer renderer = new AdvancedChestGuideRenderer();

    public AdvancedSkinCanvasView(CGRect frame) {
        super(frame);
    }

//    @Override
//    public void render(PoseStack poseStack, int i, int j, float f) {
//        poseStack.pushPose();
//        poseStack.translate(width / 2.0, height / 2.0, 500.0);
////        ModDebugger.translate(poseStack);
////        ModDebugger.scale(poseStack);
////        ModDebugger.rotate(poseStack);
////        translatef(0, 0, 500);
//
//        poseStack.scale(10, 10, 10);
//        poseStack.scale(16, 16, 16);
//
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(-40));
//        poseStack.mulPose(Vector3f.YP.rotationDegrees((float) ((System.currentTimeMillis() / 10) % 360)));
//
//        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderSystem.drawPoint(poseStack, buffers);
//        RenderSystem.drawBoundingBox(poseStack, -1, -1, -1, 1, 1, 1, UIColor.RED, buffers);
//
//        RenderSystem.disableCull();
//
//        renderer.render(poseStack, 0xf000f0, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, buffers);
////        for (ISkinPartType partType : SkinTypes.ARMOR_CHEST.getParts()) {
////            IGuideRenderer renderer = rendererManager.getRenderer(partType);
////            if (renderer != null) {
////                renderer.render(poseStack, () -> false, 0xf000f0, OverlayTexture.NO_OVERLAY, buffers);
////            }
////        }
//
//        RenderSystem.enableCull();
//
//        buffers.endBatch();
//        poseStack.popPose();
//    }
//

    //        mc.renderEngine.bindTexture(DefaultPlayerSkin.getDefaultSkinLegacy());
//        GlStateManager.pushMatrix();
//
//        // GlStateManager.scale(16F / 1F, 1F, 1F);
//        ModelPlayer modelPlayer = new ModelPlayer(1, false);
//        if (skinType == SkinTypeRegistry.skinHead) {
//            modelPlayer.bipedHead.render(1F / 16F);
//        }
//        if (skinType == SkinTypeRegistry.skinChest) {
//            modelPlayer.bipedBody.render(1F / 16F);
//            GlStateManager.translate(2F * (1F / 16F), 0, 0);
//            modelPlayer.bipedLeftArm.render(1F / 16F);
//            GlStateManager.translate(-2F * (1F / 16F), 0, 0);
//
//            GlStateManager.translate(-2F * (1F / 16F), 0, 0);
//            modelPlayer.bipedRightArm.render(1F / 16F);
//            GlStateManager.translate(2F * (1F / 16F), 0, 0);
//        }
//        if (skinType == SkinTypeRegistry.skinLegs) {
//            modelPlayer.bipedLeftLeg.render(1F / 16F);
//            modelPlayer.bipedRightLeg.render(1F / 16F);
//        }
//        if (skinType == SkinTypeRegistry.skinFeet) {
//            modelPlayer.bipedLeftLeg.render(1F / 16F);
//            modelPlayer.bipedRightLeg.render(1F / 16F);
//        }
//        // ArmourerRenderHelper.renderBuildingGrid(skinType, 1F / 16F, true, new
//        // SkinProperties(), false);
//        GlStateManager.popMatrix();

}
