package mekanism.client.jei.machine.other;

import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiPowerBar.IPowerInfoHandler;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.SeparatorRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;

public class ElectrolyticSeparatorRecipeCategory<WRAPPER extends ElectrolyticSeparatorRecipeWrapper<SeparatorRecipe>> extends BaseRecipeCategory<WRAPPER> {

    public ElectrolyticSeparatorRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/GuiElectrolyticSeparator.png", Recipe.ELECTROLYTIC_SEPARATOR.getJEICategory(),
              "tile.MachineBlock2.ElectrolyticSeparator.name", ProgressBar.BI, 4, 9, 167, 62);
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(GuiFluidGauge.getDummy(GuiGauge.Type.STANDARD_RED, this, guiLocation, 5, 10));
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.SMALL_BLUE, this, guiLocation, 58, 18));
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.SMALL_ORANGE, this, guiLocation, 100, 18));
        guiElements.add(new GuiPowerBar(this, new IPowerInfoHandler() {
            @Override
            public double getLevel() {
                return 1F;
            }
        }, guiLocation, 164, 15));
        guiElements.add(new GuiSlot(SlotType.INPUT, this, guiLocation, 25, 34));
        guiElements.add(new GuiSlot(SlotType.OUTPUT, this, guiLocation, 58, 51));
        guiElements.add(new GuiSlot(SlotType.EXTRA, this, guiLocation, 100, 51));
        guiElements.add(new GuiSlot(SlotType.POWER, this, guiLocation, 142, 34).with(SlotOverlay.POWER));
        guiElements.add(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return 1;
            }
        }, progressBar, this, guiLocation, 78, 29));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WRAPPER recipeWrapper, IIngredients ingredients) {
        SeparatorRecipe tempRecipe = recipeWrapper.getRecipe();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
        fluidStacks.init(0, true, 2, 2, 16, 58, tempRecipe.getInput().ingredient.amount, false, fluidOverlayLarge);
        fluidStacks.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        IGuiIngredientGroup<GasStack> gasStacks = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gasStacks, 0, false, 59 - xOffset, 19 - yOffset, 16, 28, tempRecipe.recipeOutput.leftGas, true);
        initGas(gasStacks, 1, false, 101 - xOffset, 19 - yOffset, 16, 28, tempRecipe.recipeOutput.rightGas, true);
    }
}