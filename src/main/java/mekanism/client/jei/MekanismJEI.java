package mekanism.client.jei;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.jei.gas.GasStackRenderer;
import mekanism.client.jei.machine.*;
import mekanism.client.jei.machine.chemical.ChemicalCrystallizerRecipeCategory;
import mekanism.client.jei.machine.chemical.ChemicalDissolutionChamberRecipeCategory;
import mekanism.client.jei.machine.chemical.ChemicalInfuserRecipeCategory;
import mekanism.client.jei.machine.chemical.ChemicalOxidizerRecipeCategory;
import mekanism.client.jei.machine.chemical.ChemicalWasherRecipeCategory;
import mekanism.client.jei.machine.chemical.NutritionalLiquifierRecipeCategory;
import mekanism.client.jei.machine.other.*;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.base.IFactory;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.ITierItem;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.inventory.container.robit.ContainerRobitInventory;
import mekanism.common.item.ItemBlockEnergyCube;
import mekanism.common.item.ItemBlockGasTank;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.generators.common.MekanismGenerators;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

@JEIPlugin
public class MekanismJEI implements IModPlugin {

    public static final IIngredientType<GasStack> TYPE_GAS = () -> GasStack.class;

    public static final ISubtypeInterpreter NBT_INTERPRETER = itemStack -> {
        String ret = Integer.toString(itemStack.getMetadata());

        if (itemStack.getItem() instanceof ITierItem) {
            ret += ":" + ((ITierItem) itemStack.getItem()).getBaseTier(itemStack).getSimpleName();
        }

        if (itemStack.getItem() instanceof IFactory) {
            RecipeType recipeType = ((IFactory) itemStack.getItem()).getRecipeTypeOrNull(itemStack);
            if (recipeType != null) {
                ret += ":" + recipeType.getName();
            }
        }

        if (itemStack.getItem() instanceof ItemBlockGasTank) {
            GasStack gasStack = ((ItemBlockGasTank) itemStack.getItem()).getGas(itemStack);
            if (gasStack != null) {
                ret += ":" + gasStack.getGas().getName();
            }
        }

        if (itemStack.getItem() instanceof ItemBlockEnergyCube) {
            ret += ":" + (((ItemBlockEnergyCube) itemStack.getItem()).getEnergy(itemStack) > 0 ? "filled" : "empty");
        }

        return ret.toLowerCase(Locale.ROOT);
    };

    @Override
    public void registerItemSubtypes(ISubtypeRegistry registry) {
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.EnergyCube), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.MachineBlock), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.MachineBlock2), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.MachineBlock3), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.MachineBlock4), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.BasicBlock), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.BasicBlock2), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.BasicBlock3), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.GasTank), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.CardboardBox), NBT_INTERPRETER);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(MekanismBlocks.Transmitter), NBT_INTERPRETER);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        List<GasStack> list = GasRegistry.getRegisteredGasses().stream().filter(Gas::isVisible).map(g -> new GasStack(g, Fluid.BUCKET_VOLUME)).collect(Collectors.toList());
        registry.register(MekanismJEI.TYPE_GAS, list, new GasStackHelper(), new GasStackRenderer());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        addRecipeCategory(registry, MachineType.CHEMICAL_CRYSTALLIZER, new ChemicalCrystallizerRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.CHEMICAL_DISSOLUTION_CHAMBER, new ChemicalDissolutionChamberRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.CHEMICAL_INFUSER, new ChemicalInfuserRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.CHEMICAL_OXIDIZER, new ChemicalOxidizerRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.CHEMICAL_WASHER, new ChemicalWasherRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.ELECTROLYTIC_SEPARATOR, new ElectrolyticSeparatorRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.METALLURGIC_INFUSER, new MetallurgicInfuserRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.PRESSURIZED_REACTION_CHAMBER, new PRCRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.ANTIPROTONIC_NUCLEOSYNTHESIZER, new AntiprotonicNucleosynthesizerRecipeCategory(guiHelper));
        addRecipeCategory(registry, MachineType.Nutritional_Liquifier, new NutritionalLiquifierRecipeCategory(guiHelper));

        addRecipeCategory(registry, MachineType.ROTARY_CONDENSENTRATOR, new RotaryCondensentratorRecipeCategory(guiHelper, true));
        addRecipeCategory(registry, MachineType.ROTARY_CONDENSENTRATOR, new RotaryCondensentratorRecipeCategory(guiHelper, false));

        addRecipeCategory(registry, MachineType.SOLAR_NEUTRON_ACTIVATOR, new SolarNeutronRecipeCategory(guiHelper));

        addRecipeCategory(registry, MachineType.ISOTOPIC_CENTRIFUGE, new IsotopicRecipeCategory(guiHelper));

        addRecipeCategory(registry, MachineType.COMBINER, new DoubleMachineRecipeCategory(guiHelper, Recipe.COMBINER.getJEICategory(),
              "tile.MachineBlock.Combiner.name", ProgressBar.STONE));

        addRecipeCategory(registry, MachineType.ALLOY, new DoubleMachineRecipeCategory(guiHelper, Recipe.ALLOY.getJEICategory(),
                "tile.MachineBlock4.Alloy.name", ProgressBar.STONE));

        addRecipeCategory(registry, MachineType.CELL_CULTIVATE, new CultivateMachineRecipeCategory(guiHelper, Recipe.CELL_CULTIVATE.getJEICategory(),
                "tile.MachineBlock4.CellCultivate.name", ProgressBar.YELLOW));

        addRecipeCategory(registry, MachineType.PURIFICATION_CHAMBER, new AdvancedMachineRecipeCategory(guiHelper, Recipe.PURIFICATION_CHAMBER.getJEICategory(),
              "tile.MachineBlock.PurificationChamber.name", ProgressBar.RED));
        addRecipeCategory(registry, MachineType.OSMIUM_COMPRESSOR, new AdvancedMachineRecipeCategory(guiHelper, Recipe.OSMIUM_COMPRESSOR.getJEICategory(),
              "tile.MachineBlock.OsmiumCompressor.name", ProgressBar.RED));
        addRecipeCategory(registry, MachineType.CHEMICAL_INJECTION_CHAMBER, new AdvancedMachineRecipeCategory(guiHelper, Recipe.CHEMICAL_INJECTION_CHAMBER.getJEICategory(),
              "tile.MachineBlock2.ChemicalInjectionChamber.name", ProgressBar.YELLOW));

        addRecipeCategory(registry, MachineType.PRECISION_SAWMILL, new ChanceMachineRecipeCategory(guiHelper, Recipe.PRECISION_SAWMILL.getJEICategory(),
              "tile.MachineBlock2.PrecisionSawmill.name", ProgressBar.PURPLE));

        addRecipeCategory(registry, MachineType.ORGANIC_FARM, new FarmMachineRecipeCategory(guiHelper, Recipe.ORGANIC_FARM.getJEICategory(),
                "tile.MachineBlock3.OrganicFarm.name", ProgressBar.PURPLE));

        addRecipeCategory(registry, MachineType.ENRICHMENT_CHAMBER, new MachineRecipeCategory(guiHelper, Recipe.ENRICHMENT_CHAMBER.getJEICategory(),
              "tile.MachineBlock.EnrichmentChamber.name", ProgressBar.BLUE));
        addRecipeCategory(registry, MachineType.CRUSHER, new MachineRecipeCategory(guiHelper, Recipe.CRUSHER.getJEICategory(), "tile.MachineBlock.Crusher.name",
              ProgressBar.CRUSH));
        addRecipeCategory(registry, MachineType.ENERGIZED_SMELTER, new MachineRecipeCategory(guiHelper, Recipe.ENERGIZED_SMELTER.getJEICategory(),
              "tile.MachineBlock.EnergizedSmelter.name", ProgressBar.BLUE));

        addRecipeCategory(registry, MachineType.STAMPING, new MachineRecipeCategory(guiHelper, Recipe.STAMPING.getJEICategory(), "tile.MachineBlock4.Stamping.name",
                ProgressBar.CRUSH));
        addRecipeCategory(registry, MachineType.ROLLING, new MachineRecipeCategory(guiHelper, Recipe.ROLLING.getJEICategory(), "tile.MachineBlock4.Rolling.name",
                ProgressBar.CRUSH));
        addRecipeCategory(registry, MachineType.BRUSHED, new MachineRecipeCategory(guiHelper, Recipe.BRUSHED.getJEICategory(), "tile.MachineBlock4.Brushed.name",
                ProgressBar.CRUSH));
        addRecipeCategory(registry, MachineType.TURNING, new MachineRecipeCategory(guiHelper, Recipe.TURNING.getJEICategory(), "tile.MachineBlock4.Turning.name",
                ProgressBar.CRUSH));
        addRecipeCategory(registry, MachineType.CELL_EXTRACTOR, new ChanceMachineRecipeCategory(guiHelper, Recipe.CELL_EXTRACTOR.getJEICategory(),
                "tile.MachineBlock4.CellExtractor.name", ProgressBar.PURPLE));
        addRecipeCategory(registry, MachineType.CELL_SEPARATOR, new ChanceMachineRecipeCategory(guiHelper, Recipe.CELL_SEPARATOR.getJEICategory(),
                "tile.MachineBlock4.CellSeparator.name", ProgressBar.PURPLE));




        //There is no config option to disable the thermal evaporation plant
        registry.addRecipeCategories(new ThermalEvaporationRecipeCategory(guiHelper));

        //Check if Mekanism Generators is installed
        registry.addRecipeCategories(new FusionCoolingRecipeCategory(guiHelper));

    }

    private void addRecipeCategory(IRecipeCategoryRegistration registry, MachineType type, BaseRecipeCategory category) {
        if (type.isEnabled()) {
            registry.addRecipeCategories(category);
        }
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new GuiElementHandler());

        //Blacklist
        IIngredientBlacklist ingredientBlacklist = registry.getJeiHelpers().getIngredientBlacklist();
        ingredientBlacklist.addIngredientToBlacklist(new ItemStack(MekanismItems.ItemProxy));
        ingredientBlacklist.addIngredientToBlacklist(new ItemStack(MekanismBlocks.BoundingBlock));

        //Register the recipes and their catalysts if enabled
        RecipeRegistryHelper.registerEnrichmentChamber(registry);
        RecipeRegistryHelper.registerCrusher(registry);
        RecipeRegistryHelper.registerCombiner(registry);
        RecipeRegistryHelper.registerPurification(registry);
        RecipeRegistryHelper.registerCompressor(registry);
        RecipeRegistryHelper.registerInjection(registry);
        RecipeRegistryHelper.registerSawmill(registry);
        RecipeRegistryHelper.registerMetallurgicInfuser(registry);
        RecipeRegistryHelper.registerCrystallizer(registry);
        RecipeRegistryHelper.registerDissolution(registry);
        RecipeRegistryHelper.registerChemicalInfuser(registry);
        RecipeRegistryHelper.registerOxidizer(registry);
        RecipeRegistryHelper.registerNutritional(registry);
        RecipeRegistryHelper.registerWasher(registry);
        RecipeRegistryHelper.registerNeutronActivator(registry);
        RecipeRegistryHelper.registerAntiprotonicNucleosynthesizer(registry);
        RecipeRegistryHelper.registerIsotopicCentrifuge(registry);
        RecipeRegistryHelper.registerSeparator(registry);
        RecipeRegistryHelper.registerEvaporationPlant(registry);
        RecipeRegistryHelper.registerReactionChamber(registry);
        RecipeRegistryHelper.registerCondensentrator(registry);
        RecipeRegistryHelper.registerSmelter(registry);
        RecipeRegistryHelper.registerFormulaicAssemblicator(registry);
        RecipeRegistryHelper.registerFarm(registry);

        RecipeRegistryHelper.registerStamping(registry);
        RecipeRegistryHelper.registerRolling(registry);
        RecipeRegistryHelper.registerBrushed(registry);
        RecipeRegistryHelper.registerTurning(registry);
        RecipeRegistryHelper.registerAlloy(registry);
        RecipeRegistryHelper.registerCellCultivate(registry);

        RecipeRegistryHelper.registerCellExtractor(registry);
        RecipeRegistryHelper.registerCellSeparator(registry);

        if (Loader.isModLoaded(MekanismGenerators.MODID)){
            RecipeRegistryHelper.registerFusionCooling(registry);
        }

        registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerRobitInventory.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
    }
}