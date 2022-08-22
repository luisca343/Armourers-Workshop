package moe.plushie.armourers_workshop.library.blockentity;

import moe.plushie.armourers_workshop.core.blockentity.AbstractContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkinLibraryBlockEntity extends AbstractContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public SkinLibraryBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        ContainerHelper.saveAllItems(nbt, items);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

}
