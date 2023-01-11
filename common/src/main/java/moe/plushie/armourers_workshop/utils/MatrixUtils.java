package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.compatibility.AbstractMatrixUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.FloatBuffer;

@Environment(value = EnvType.CLIENT)
public class MatrixUtils extends AbstractMatrixUtils {

    public static IMatrix4f mat4(FloatBuffer buffer) {
        OpenMatrix4f mat = new OpenMatrix4f();
        mat.load(buffer);
        return mat;
    }

    public static IMatrix3f mat3(FloatBuffer buffer) {
        OpenMatrix3f mat = new OpenMatrix3f();
        if (buffer.remaining() == 9) {
            mat.load(buffer);
        } else {
            mat.import44(buffer);
        }
        return mat;
    }

}
