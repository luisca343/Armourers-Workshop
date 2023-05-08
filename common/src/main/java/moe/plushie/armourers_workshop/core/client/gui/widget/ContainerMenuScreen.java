package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIWindow;
import com.apple.library.uikit.UIWindowManager;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.ISize2i;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractMenuScreen;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Size2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

@Environment(value = EnvType.CLIENT)
public class ContainerMenuScreen<M extends AbstractContainerMenu, W extends UIWindow> extends AbstractMenuScreen<M> {

    private UIFont font;

    private final W window;
    private final MenuWindow<?> menuWindow;
    private final UIWindowManager manager;

    public ContainerMenuScreen(W window, M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.window = window;
        this.menuWindow = ObjectUtils.safeCast(window, MenuWindow.class);

        this.manager = new UIWindowManager();
        this.manager.addWindow(window);
        this.manager.init();
    }

    @Override
    public void init() {
        ISize2i screenSize = getScreenSize();
        font = new UIFont(getFont());
        manager.layout(screenSize.getWidth(), screenSize.getHeight());
        CGRect rect = window.bounds();
        setContentSize(new Size2i(rect.width, rect.height));
        super.init();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        menuWindow.screenWillTick();
    }

    @Override
    public void removed() {
        super.removed();
        manager.deinit();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        CGGraphicsContext context = new CGGraphicsContext(poseStack, mouseX, mouseY, partialTicks, font, this);
        manager.tick();
        manager.render(context, this::_render, this::_renderBackground, this::_renderTooltip);
    }

    @Override
    public void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        // ignored
    }

    @Override
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return manager.mouseDown(mouseX, mouseY, button, this::_mouseClicked);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return manager.mouseUp(mouseX, mouseY, button, this::_mouseReleased);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return manager.mouseWheel(mouseX, mouseY, delta, this::_mouseScrolled);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        manager.mouseMoved(mouseX, mouseY, 0, this::_mouseMoved);
    }

    @Override
    public boolean keyPressed(int key, int i, int j) {
        return manager.keyDown(key, i, j, this::_keyPressed);
    }

    @Override
    public boolean keyReleased(int key, int i, int j) {
        return manager.keyUp(key, i, j, this::_keyReleased);
    }

    @Override
    public boolean charTyped(char ch, int i) {
        return manager.charTyped(ch, i, 0, this::_charTyped);
    }

    @Override
    public boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return !manager.mouseIsInside(mouseX, mouseY, button);
    }

    @Override
    public void slotClicked(Slot slot, int slotIndex, int j, ClickType clickType) {
        super.slotClicked(slot, slotIndex, j, clickType);
        // in normal case the clicking will call the `slotClicked`,
        // we need to know that this is a slot click.
        if (slotIndex >= 0) {
            menuWindow.menuDidChange();
        }
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return manager.changeKeyView(bl);
    }

    public boolean shouldDrawPluginScreen() {
        if (menuWindow != null) {
            return menuWindow.shouldRenderExtendScreen();
        }
        return false;
    }

    protected void _render(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        super.render(context.poseStack, mouseX, mouseY, partialTicks);
    }

    protected void _renderTooltip(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        PoseStack poseStack = context.poseStack;
        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        renderTooltip(context.poseStack, mouseX, mouseY);
        poseStack.popPose();
    }

    protected void _renderBackground(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        // draw bg
        if (menuWindow != null && menuWindow.shouldRenderBackground()) {
            renderBackground(context.poseStack);
        }
    }

    protected boolean _charTyped(int key, int i, int j) {
        super.charTyped((char) key, i);
        return true;
    }

    protected boolean _keyPressed(int key, int i, int j) {
        // when input first responder is actived, the shortcut key events not allowed.
        if (manager.isTextEditing() && !_editingPassKey((char) key)) {
            return false;
        }
        return super.keyPressed(key, i, j);
    }

    protected boolean _keyReleased(int key, int i, int j) {
        return super.keyReleased(key, i, j);
    }

    protected boolean _mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean _mouseMoved(double mouseX, double mouseY, int button) {
        super.mouseMoved(mouseX, mouseY);
        return true;
    }

    protected boolean _mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    protected boolean _mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected boolean _editingPassKey(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE:
            case GLFW.GLFW_KEY_TAB:
                return true;

            default:
                return false;
        }
    }
}
