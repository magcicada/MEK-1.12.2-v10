package mekanism.common.integration.crafttweaker.handlers;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import mekanism.common.Mekanism;
import mekanism.common.integration.crafttweaker.CrafttweakerIntegration;
import mekanism.common.integration.crafttweaker.helpers.IngredientHelper;
import mekanism.common.integration.crafttweaker.util.AddMekanismRecipe;
import mekanism.common.integration.crafttweaker.util.IngredientWrapper;
import mekanism.common.integration.crafttweaker.util.RemoveAllMekanismRecipe;
import mekanism.common.integration.crafttweaker.util.RemoveMekanismRecipe;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.FusionCoolingRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.mekanism.fusioncooling")
@ZenRegister
public class FusionCooling {

    public static final String NAME = Mekanism.MOD_NAME + " Fusion Cooling";

    @ZenMethod
    public static void addRecipe(ILiquidStack liquidInput, ILiquidStack liquidOutput) {
        if (IngredientHelper.checkNotNull(NAME, liquidInput, liquidOutput)) {
            CrafttweakerIntegration.LATE_ADDITIONS.add(new AddMekanismRecipe<>(NAME, Recipe.FUSION_COOLING,
                  new FusionCoolingRecipe(IngredientHelper.toFluid(liquidInput), IngredientHelper.toFluid(liquidOutput))));
        }
    }

    @ZenMethod
    public static void removeRecipe(IIngredient liquidInput, @Optional IIngredient liquidOutput) {
        if (IngredientHelper.checkNotNull(NAME, liquidInput)) {
            CrafttweakerIntegration.LATE_REMOVALS.add(new RemoveMekanismRecipe<>(NAME, Recipe.FUSION_COOLING, new IngredientWrapper(liquidOutput),
                  new IngredientWrapper(liquidInput)));
        }
    }

    @ZenMethod
    public static void removeAllRecipes() {
        CrafttweakerIntegration.LATE_REMOVALS.add(new RemoveAllMekanismRecipe<>(NAME, Recipe.FUSION_COOLING));
    }
}