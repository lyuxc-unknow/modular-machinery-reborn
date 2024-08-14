package es.degrassi.mmreborn.client.util;

public class DebugOverlayHelper {

//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public void onTextOverlay(Text.RenderGameOverlayEvent.Text event) {
//        Minecraft mc = Minecraft.getMinecraft();
//        if(mc.gameSettings.showDebugInfo && !event.getRight().isEmpty()) {
//            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
//                BlockPos pos = mc.objectMouseOver.getBlockPos();
//                IBlockState state = mc.world.getBlockState(pos);
//                try {
//                    int meta = state.getBlock().getMetaFromState(state);
//                    event.getRight().add("");
//                    event.getRight().add("serialized as metadata: " + meta);
//                } catch (Exception ignored) {}
//            }
//        }
//    }

}
