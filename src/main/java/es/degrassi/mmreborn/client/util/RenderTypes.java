package es.degrassi.mmreborn.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderType;

public class RenderTypes extends RenderType {

    public static final RenderType PHANTOM = create("phantom", DefaultVertexFormat.BLOCK, Mode.QUADS, 2097152, true, false, CompositeState.builder().setShaderState(RENDERTYPE_TRANSLUCENT_SHADER).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));
    public static final RenderType NOPE = create("nope", DefaultVertexFormat.BLOCK, Mode.QUADS, 2097152, true, false, CompositeState.builder().setShaderState(RENDERTYPE_TRANSLUCENT_SHADER).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(true));
    public static final RenderType THICK_LINES = create("thick_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, Mode.LINES, 256, false, false, CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new LineStateShard(OptionalDouble.of(10.0D))).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));

    //Dummy constructor, don't call.
    public RenderTypes(String nameIn, VertexFormat formatIn, Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }
}
