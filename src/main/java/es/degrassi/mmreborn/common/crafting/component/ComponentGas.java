package es.degrassi.mmreborn.common.crafting.component;

import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.util.Mods;
import javax.annotation.Nullable;

public class ComponentGas extends ComponentType {

    @Nullable
    @Override
    public String requiresModid() {
        return Mods.MEKANISM.modid;
    }

}
