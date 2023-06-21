package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

@Available("[1.18, )")
public abstract class AbstractContainerMenuImpl extends AbstractContainerMenu {

    public AbstractContainerMenuImpl(@Nullable MenuType<?> containerType, int containerId) {
        super(containerType, containerId);
    }

    @Override
    protected void clearContainer(Player player, Container container) {
        super.clearContainer(player, container);
    }
}
