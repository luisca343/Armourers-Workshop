package extensions.com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

@Available("[1.16, 1.19.3)")
@Extension
public class ABI {

    private static final Matrix3f CONVERTER_MAT3 = new Matrix3f();
    private static final Matrix4f CONVERTER_MAT4 = new Matrix4f();

    public static Matrix3f convertMatrix(@ThisClass Class<?> clazz, IMatrix3f mat) {
        Matrix3f newValue = ObjectUtils.safeCast(mat, Matrix3f.class);
        if (newValue != null) {
            return newValue;
        }
        ObjectUtils.set(mat, ObjectUtils.unsafeCast(CONVERTER_MAT3));
        return CONVERTER_MAT3;
    }

    public static Matrix4f convertMatrix(@ThisClass Class<?> clazz, IMatrix4f mat) {
        Matrix4f newValue = ObjectUtils.safeCast(mat, Matrix4f.class);
        if (newValue != null) {
            return newValue;
        }
        ObjectUtils.set(mat, ObjectUtils.unsafeCast(CONVERTER_MAT4));
        return CONVERTER_MAT4;
    }

    public static PoseStack copy(@This PoseStack poseStack) {
        PoseStack poseStack1 = new PoseStack();
        poseStack1.lastPose().multiply(poseStack.lastPose());
        poseStack1.lastNormal().multiply(poseStack.lastNormal());
        return poseStack1;
    }

    public static void mulPose(@This PoseStack poseStack, IQuaternionf q) {
        poseStack.mulPose(new Quaternion(q.x(), q.y(), q.z(), q.w()));
    }

    public static void mulPoseMatrix(@This PoseStack poseStack, IMatrix4f matrix) {
        poseStack.lastPose().multiply(matrix);
    }

    public static IMatrix4f lastPose(@This PoseStack poseStack) {
        return ObjectUtils.unsafeCast(poseStack.last().pose());
    }

    public static IMatrix3f lastNormal(@This PoseStack poseStack) {
        return ObjectUtils.unsafeCast(poseStack.last().normal());
    }
}
