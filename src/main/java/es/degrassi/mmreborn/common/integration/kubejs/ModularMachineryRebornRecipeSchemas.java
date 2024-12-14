package es.degrassi.mmreborn.common.integration.kubejs;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.type.TypeInfo;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.crafting.helper.ComponentRequirement;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public interface ModularMachineryRebornRecipeSchemas {
  RecipeComponent<ResourceLocation> RESOURCE_LOCATION = new RecipeComponent<>() {
    @Override
    public Codec<ResourceLocation> codec() {
      return ResourceLocation.CODEC;
    }

    @Override
    public TypeInfo typeInfo() {
      return TypeInfo.of(ResourceLocation.class);
    }
  };

  RecipeComponent<ComponentRequirement<?, ?>> REQUIREMENT_COMPONENT = new RecipeComponent<>() {
    @Override
    public Codec<ComponentRequirement<?, ?>> codec() {
      return ComponentRequirement.CODEC.codec();
    }

    @Override
    public TypeInfo typeInfo() {
      return TypeInfo.of(ComponentRequirement.class);
    }
  };

  RecipeComponent<List<ComponentRequirement<?, ?>>> REQUIREMENT_LIST = REQUIREMENT_COMPONENT.asList();

  RecipeKey<ResourceLocation> MACHINE_ID = RESOURCE_LOCATION.key("machine", ComponentRole.OTHER);
  RecipeKey<TickDuration> TIME = TimeComponent.TICKS.key("time", ComponentRole.OTHER);
  RecipeKey<Boolean> VOID = BooleanComponent.BOOLEAN.key("voidOnFailure", ComponentRole.OTHER).optional(true).alwaysWrite().exclude();
  RecipeKey<Boolean> SHOULD_RENDER_PROGRESS = BooleanComponent.BOOLEAN.key("renderProgress", ComponentRole.OTHER).optional(true).alwaysWrite().exclude();
  RecipeKey<Integer> PROGRESS_X = NumberComponent.INT.key("progress_x", ComponentRole.OTHER).optional(74).alwaysWrite().exclude();
  RecipeKey<Integer> PROGRESS_Y = NumberComponent.INT.key("progress_y", ComponentRole.OTHER).optional(8).alwaysWrite().exclude();
  RecipeKey<Integer> WIDTH = NumberComponent.INT.key("width", ComponentRole.OTHER).optional(256).alwaysWrite().exclude();
  RecipeKey<Integer> HEIGHT = NumberComponent.INT.key("height", ComponentRole.OTHER).optional(256).alwaysWrite().exclude();
  RecipeKey<List<ComponentRequirement<?, ?>>> REQUIREMENTS = REQUIREMENT_LIST.key("requirements", ComponentRole.OTHER).optional(List.of()).allowEmpty().alwaysWrite().exclude();
  RecipeKey<Integer> PRIORITY = NumberComponent.INT.key("priority", ComponentRole.OTHER).optional(0).alwaysWrite().exclude();
  RecipeSchema CUSTOM_MACHINE = new RecipeSchema(MACHINE_ID, TIME, PROGRESS_X, PROGRESS_Y, WIDTH, HEIGHT,
      REQUIREMENTS, PRIORITY, VOID, SHOULD_RENDER_PROGRESS).factory(new KubeRecipeFactory(ModularMachineryReborn.rl("recipe"),
      TypeInfo.of(MachineRecipeBuilderJS.class), MachineRecipeBuilderJS::new));
}
