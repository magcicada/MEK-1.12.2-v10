package mekanism.common.inventory.container;

import javax.annotation.Nonnull;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.common.base.IFactory.MachineFuelType;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.item.ItemBlockMachine;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.ChargeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class ContainerFactory extends ContainerMekanism<TileEntityFactory> {

    public ContainerFactory(InventoryPlayer inventory, TileEntityFactory tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotDischarge(tileEntity, 1, 7, 13));
        if (tileEntity.tier == FactoryTier.BASIC || tileEntity.tier == FactoryTier.ADVANCED || tileEntity.tier == FactoryTier.ELITE) {
            addSlotToContainer(new Slot(tileEntity, 2, 180, 75) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    MachineType swapType = MachineType.get(stack);
                    return swapType != null && !swapType.isFactory();
                }
            });
            addSlotToContainer(new SlotOutput(tileEntity, 3, 180, 112));
        }else if(tileEntity.tier == FactoryTier.ULTIMATE ) {
            addSlotToContainer(new Slot(tileEntity, 2, 180 + 34, 75) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    MachineType swapType = MachineType.get(stack);
                    return swapType != null && !swapType.isFactory();
                }
            });
            addSlotToContainer(new SlotOutput(tileEntity, 3, 180 + 34, 112 ));
        }else if (tileEntity.tier == FactoryTier.CREATIVE){
            addSlotToContainer(new Slot(tileEntity, 2, 180 + 72, 75) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    MachineType swapType = MachineType.get(stack);
                    return swapType != null && !swapType.isFactory();
                }
            });
            addSlotToContainer(new SlotOutput(tileEntity, 3, 180 + 72, 112 ));
        }
            addSlotToContainer(new Slot(tileEntity, 4, 7, 57));
        if (tileEntity.tier == FactoryTier.BASIC) {
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new FactoryInputSlot(tileEntity, getInputSlotIndex(i), 55 + (i * 38), 13, i));
            }
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new SlotOutput(tileEntity, getOutputSlotIndex(i), 55 + (i * 38), 57));
            }

        } else if (tileEntity.tier == FactoryTier.ADVANCED) {
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new FactoryInputSlot(tileEntity, getInputSlotIndex(i), 35 + (i * 26), 13, i));
            }
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new SlotOutput(tileEntity, getOutputSlotIndex(i), 35 + (i * 26), 57));
            }
        } else if (tileEntity.tier == FactoryTier.ELITE) {
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new FactoryInputSlot(tileEntity, getInputSlotIndex(i), 29 + (i * 19), 13, i));

            }
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new SlotOutput(tileEntity, getOutputSlotIndex(i), 29 + (i * 19), 57));
            }
        }else if (tileEntity.tier == FactoryTier.ULTIMATE) {
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new FactoryInputSlot(tileEntity, getInputSlotIndex(i), 27 + (i * 19), 13, i));
            }
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new SlotOutput(tileEntity, getOutputSlotIndex(i), 27 + (i * 19), 57));
            }
        }else if (tileEntity.tier == FactoryTier.CREATIVE) {
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new FactoryInputSlot(tileEntity, getInputSlotIndex(i), 27 + (i * 19), 13, i));
            }
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addSlotToContainer(new SlotOutput(tileEntity, getOutputSlotIndex(i), 27 + (i * 19), 57));
            }
        }


    }

    @Override
    protected int getInventoryOffset() {
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED ||tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM ||tileEntity.getRecipeType() == RecipeType.INFUSING){
            return 95;
        }else if (tileEntity.tier == FactoryTier.ULTIMATE|| tileEntity.tier == FactoryTier.CREATIVE){
            return 87;
        }else {
            return 88;
        }
    }

    @Override
    protected void addInventorySlots(InventoryPlayer inventory) {
        int offset = getInventoryOffset();
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                if (tileEntity.tier == FactoryTier.CREATIVE){
                    addSlotToContainer(new Slot(inventory, slotX + slotY * 9 + 9, 44 + slotX * 18, offset + slotY * 18));
                }else if (tileEntity.tier == FactoryTier.ULTIMATE){
                    addSlotToContainer(new Slot(inventory, slotX + slotY * 9 + 9, 27 + slotX * 18, offset + slotY * 18));
                }else {
                    addSlotToContainer(new Slot(inventory, slotX + slotY * 9 + 9, 8 + slotX * 18, offset + slotY * 18));
                }
            }
        }
        offset += 58;
        for (int slotY = 0; slotY < 9; slotY++) {
            if (tileEntity.tier == FactoryTier.CREATIVE){
                addSlotToContainer(new Slot(inventory, slotY, 44 + slotY * 18, offset));
            }else if (tileEntity.tier == FactoryTier.ULTIMATE){
                addSlotToContainer(new Slot(inventory, slotY, 27 + slotY * 18, offset));
            }else {
                addSlotToContainer(new Slot(inventory, slotY, 8 + slotY * 18, offset));
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = ItemStack.EMPTY;
        Slot currentSlot = inventorySlots.get(slotID);
        if (currentSlot != null && currentSlot.getHasStack()) {
            ItemStack slotStack = currentSlot.getStack();
            stack = slotStack.copy();
            if (isOutputSlot(slotID)) {
                if (!mergeItemStack(slotStack, tileEntity.inventory.size() - 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID != 1 && slotID != 2 && isProperMachine(slotStack) && !ItemHandlerHelper.canItemStacksStack(slotStack, tileEntity.getMachineStack())) {
                if (!mergeItemStack(slotStack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID == 2) {
                if (!mergeItemStack(slotStack, tileEntity.inventory.size() - 1, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (tileEntity.getRecipeType().getAnyRecipe(slotStack, inventorySlots.get(4).getStack(), tileEntity.gasTank.getGasType(), tileEntity.infuseStored) != null) {
                if (isInputSlot(slotID)) {
                    if (!mergeItemStack(slotStack, tileEntity.inventory.size() - 1, inventorySlots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!mergeItemStack(slotStack, 4, 4 + tileEntity.tier.processes, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (ChargeUtils.canBeDischarged(slotStack)) {
                if (slotID == 0) {
                    if (!mergeItemStack(slotStack, tileEntity.inventory.size() - 1, inventorySlots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!mergeItemStack(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (tileEntity.getItemGas(slotStack) != null) {
                if (transferExtraSlot(slotID, slotStack)) {
                    return ItemStack.EMPTY;
                }
            } else if (tileEntity.getRecipeType() == RecipeType.INFUSING && InfuseRegistry.getObject(slotStack) != null
                       && (tileEntity.infuseStored.getType() == null || tileEntity.infuseStored.getType() == InfuseRegistry.getObject(slotStack).type)) {
                if (transferExtraSlot(slotID, slotStack)) {
                    return ItemStack.EMPTY;
                }
            } else {
                int slotEnd = tileEntity.inventory.size() - 1;
                if (slotID >= slotEnd && slotID <= (slotEnd + 26)) {
                    if (!mergeItemStack(slotStack, slotEnd + 27, inventorySlots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotID > (slotEnd + 26)) {
                    if (!mergeItemStack(slotStack, slotEnd, slotEnd + 26, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!mergeItemStack(slotStack, slotEnd, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.getCount() == 0) {
                currentSlot.putStack(ItemStack.EMPTY);
            } else {
                currentSlot.onSlotChanged();
            }
            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            currentSlot.onTake(player, slotStack);
        }
        return stack;
    }

    private boolean transferExtraSlot(int slotID, ItemStack slotStack) {
        if (slotID >= tileEntity.inventory.size() - 1) {
            return !mergeItemStack(slotStack, 3, 4, false);
        }
        return !mergeItemStack(slotStack, tileEntity.inventory.size() - 1, inventorySlots.size(), true);
    }

    public boolean isProperMachine(ItemStack itemStack) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemBlockMachine) {
            for (RecipeType type : RecipeType.values()) {
                if (ItemHandlerHelper.canItemStacksStack(itemStack, type.getStack())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInputSlot(int slot) {
        return slot >= 4 && slot < 4 + tileEntity.tier.processes;
    }

    public boolean isOutputSlot(int slot) {
        return slot >= 4 + tileEntity.tier.processes && slot < 4 + tileEntity.tier.processes * 2;
    }

    private int getOutputSlotIndex(int processNumber) {
        return tileEntity.tier.processes + getInputSlotIndex(processNumber);
    }


    private int getInputSlotIndex(int processNumber) {
        return 5 + processNumber;
    }

    private class FactoryInputSlot extends Slot {

        /**
         * The index of the processes slot. 0 <= processNumber < tileEntity.tier.processes For matching the input to output slot
         */
        private final int processNumber;

        private FactoryInputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, int processNumber) {
            super(inventoryIn, index, xPosition, yPosition);
            this.processNumber = processNumber;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            ItemStack outputSlotStack = tileEntity.inventory.get(getOutputSlotIndex(this.processNumber));
            return tileEntity.inputProducesOutput(getInputSlotIndex(this.processNumber), stack, outputSlotStack, false) && super.isItemValid(stack);
        }
    }
}