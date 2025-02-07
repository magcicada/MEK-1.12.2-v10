package mekanism.common.recipe.machines;

import mekanism.api.gas.GasStack;
import mekanism.common.recipe.inputs.IntegerInput;
import mekanism.common.recipe.outputs.GasOutput;

public class AmbientGasRecipe extends MachineRecipe<IntegerInput, GasOutput, AmbientGasRecipe> {

    public AmbientGasRecipe(IntegerInput input, GasOutput output) {
        super(input, output);
    }

    public AmbientGasRecipe(int input, GasStack output) {
        this(new IntegerInput(input), new GasOutput(output));
    }

    @Override
    public AmbientGasRecipe copy() {
        return new AmbientGasRecipe(getInput().copy(), getOutput().copy());
    }
}