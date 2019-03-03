package moe.plushie.armourers_workshop.client.render;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.WeakHashMap;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.ModelRendererAttachment;
import moe.plushie.armourers_workshop.client.model.skin.IEquipmentModel;
import moe.plushie.armourers_workshop.client.model.skin.ModelDummy;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinChest;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinFeet;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinHead;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinItem;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinLegs;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinOutfit;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinWings;
import moe.plushie.armourers_workshop.client.model.skin.ModelTypeHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Helps render skins on the player and other entities.
 *
 * TODO Clean up this class it's a mess >:|
 *
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public final class SkinModelRenderHelper {

    public static SkinModelRenderHelper INSTANCE;

    public static void init() {
        INSTANCE = new SkinModelRenderHelper();
    }

    private final HashMap<String, ModelTypeHelper> helperModelsMap;
    private final Set<ModelBiped> attachedBipedSet;

    public final ModelSkinHead modelHead = new ModelSkinHead();
    public final ModelSkinChest modelChest = new ModelSkinChest();
    public final ModelSkinLegs modelLegs = new ModelSkinLegs();
    public final ModelSkinFeet modelFeet = new ModelSkinFeet();
    public final ModelSkinWings modelWings = new ModelSkinWings();
    public final ModelSkinOutfit modelOutfit = new ModelSkinOutfit();

    public final ModelSkinItem modelItem = new ModelSkinItem();
    public final ModelSkinBow modelBow = new ModelSkinBow();

    public final ModelDummy modelHelperDummy = new ModelDummy();

    public EntityPlayer targetPlayer = null;

    private SkinModelRenderHelper() {
        MinecraftForge.EVENT_BUS.register(this);
        helperModelsMap = new HashMap<String, ModelTypeHelper>();
        attachedBipedSet = Collections.newSetFromMap(new WeakHashMap<ModelBiped, Boolean>());

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinHead, modelHead);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinChest, modelChest);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinLegs, modelLegs);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinFeet, modelFeet);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinWings, modelWings);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinSword, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinShield, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinBow, modelBow);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinPickaxe, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinAxe, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinShovel, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinHoe, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinItem, modelItem);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinOutfit, modelOutfit);
    }

    private boolean isPlayerWearingSkirt(EntityPlayer player) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability == null) {
            return false;
        }
        boolean limitLimbs = false;
        for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinLegs); i++) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinLegs, i);
            if (skinDescriptor != null) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                if (skin != null) {
                    if (SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties())) {
                        limitLimbs = true;
                        break;
                    }
                }
            }
        }
        if (!limitLimbs) {
            for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinOutfit); i++) {
                ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinOutfit, i);
                if (skinDescriptor != null) {
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                    if (skin != null) {
                        if (SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties())) {
                            limitLimbs = true;
                            break;
                        }
                    }
                }
            }
        }

        return limitLimbs;
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        targetPlayer = player;

        if (ClientProxy.getSkinRenderType() == SkinRenderType.MODEL_ATTACHMENT) {
            attachModelsToBiped(event.getRenderer().getMainModel(), event.getRenderer());
        }

        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (isPlayerWearingSkirt(player)) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
                player.prevLimbSwingAmount = 0.25F;
            }
        }
    }

    private void attachModelsToBiped(ModelBiped modelBiped, RenderPlayer renderPlayer) {
        if (attachedBipedSet.contains(modelBiped)) {
            return;
        }
        attachedBipedSet.add(modelBiped);
        modelBiped.bipedHead.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinHead, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:head.base")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.base")));
        modelBiped.bipedLeftArm.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.leftArm")));
        modelBiped.bipedRightArm.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinChest, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:chest.rightArm")));
        modelBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.leftLeg")));
        modelBiped.bipedRightLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.rightLeg")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinLegs, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:legs.skirt")));
        modelBiped.bipedLeftLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.leftFoot")));
        modelBiped.bipedRightLeg.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinFeet, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:feet.rightFoot")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinWings, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:wings.leftWing")));
        modelBiped.bipedBody.addChild(new ModelRendererAttachment(modelBiped, SkinTypeRegistry.skinWings, SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:wings.rightWing")));
        ModLogger.log(String.format("Added model render attachment to %s", modelBiped.toString()));
        ModLogger.log(String.format("Using player renderer %s", renderPlayer.toString()));
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        targetPlayer = null;
    }

    public ModelTypeHelper getTypeHelperForModel(ModelType modelType, ISkinType skinType) {
        ModelTypeHelper typeHelper = helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
        if (typeHelper == null) {
            return modelHelperDummy;
        }
        return typeHelper;
    }

    public void registerSkinTypeHelperForModel(ModelType modelType, ISkinType skinType, ModelTypeHelper typeHelper) {
        helperModelsMap.put(skinType.getRegistryName() + ":" + modelType.name(), typeHelper);
    }

    public ModelTypeHelper agetTypeHelperForModel(ModelType modelType, ISkinType skinType) {
        return helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
    }
    
    public boolean renderEquipmentPart(Skin skin, SkinRenderData renderData, Entity entity, ModelBiped modelBiped) {
        if (skin == null) {
            return false;
        }
        IEquipmentModel model = getTypeHelperForModel(ModelType.MODEL_BIPED, skin.getSkinType());
        GlStateManager.pushAttrib();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        model.render(entity, skin, modelBiped, renderData);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.popAttrib();
        return true;
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin skin, ISkinDye skinDye, ExtraColours extraColours, double distance, boolean doLodLoading) {
        if (skin == null) {
            return false;
        }
        IEquipmentModel model = getTypeHelperForModel(ModelType.MODEL_BIPED, skin.getSkinType());
        GlStateManager.pushAttrib();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        model.render(entity, skin, modelBiped, false, skinDye, extraColours, false, distance, doLodLoading);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.popAttrib();
        return true;
    }

    public static enum ModelType {
        MODEL_BIPED,
    }
}
