package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

public class WindowManagerImpl {

    private boolean isCalledInit = false;

    private CGSize lastLayoutSize;
    private double lastMouseX;
    private double lastMouseY;

    protected final Queue<WindowDispatcherImpl> dispatchers = new Queue<>();

    public WindowManagerImpl() {
        this.dispatchers.add(WindowDispatcherImpl.BACKGROUND);
        this.dispatchers.add(WindowDispatcherImpl.FOREGROUND);
        this.dispatchers.add(WindowDispatcherImpl.OVERLAY);
    }

    public void init() {
        dispatchers.forEach(WindowDispatcherImpl::init);
        isCalledInit = true;
    }

    public void deinit() {
        dispatchers.forEach(WindowDispatcherImpl::deinit);
        dispatchers.removeAll();
    }

    public void addWindow(UIWindow window) {
        UIWindow.Dispatcher dispatcher = new UIWindow.Dispatcher(window);
        dispatchers.add(dispatcher);
        if (isCalledInit) {
            dispatcher.init();
        }
        if (lastLayoutSize != null) {
            dispatcher.layout(lastLayoutSize.width, lastLayoutSize.height);
        }
    }

    public void removeWindow(UIWindow window) {
        dispatchers.removeIf(dispatcher -> {
            if (dispatcher instanceof UIWindow.Dispatcher) {
                UIWindow.Dispatcher dispatcher1 = (UIWindow.Dispatcher) dispatcher;
                if (dispatcher1.window == window) {
                    dispatcher1.deinit();
                    return true;
                }
            }
            return false;
        });
        // when remove a window, first tooltip responder maybe change, so we need to recalculate.
        dispatchers.forEach(dispatcher -> dispatcher.mouseMoved(lastMouseX, lastMouseY, 0));
    }

    public void tick() {
        dispatchers.forEach(WindowDispatcherImpl::tick);
    }

    public void layout(int width, int height) {
        dispatchers.forEach(dispatcher -> dispatcher.layout(width, height));
        lastLayoutSize = new CGSize(width, height);
    }

    public void render(CGGraphicsContext context, RenderInvoker foreground, RenderInvoker background, RenderInvoker overlay) {
        PoseStack poseStack = context.poseStack;
        float partialTicks = context.partialTicks;
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;
        // we need to display a custom tooltip, so must cancel the original tooltip render,
        // we need reset mouse to impossible position to fool the original tooltip render.
        UIView tooltipResponder = firstTooltipResponder();
        if (tooltipResponder != null) {
            mouseX = -999;
            mouseY = -999;
        }
        for (WindowDispatcherImpl dispatcher : dispatchers) {
            dispatcher.render(context);
            if (dispatcher == WindowDispatcherImpl.BACKGROUND) {
                background.invoke(poseStack, mouseX, mouseY, partialTicks);
            }
            if (dispatcher == WindowDispatcherImpl.FOREGROUND) {
                foreground.invoke(poseStack, mouseX, mouseY, partialTicks);
            }
            if (dispatcher == WindowDispatcherImpl.OVERLAY) {
                overlay.invoke(poseStack, mouseX, mouseY, partialTicks);
                renderTooltip(tooltipResponder, context);
            }
        }
        lastMouseX = context.mouseX;
        lastMouseY = context.mouseY;
    }

    private void renderTooltip(UIView tooltipResponder, CGGraphicsContext context) {
        if (tooltipResponder == null) {
            return;
        }
        Object tooltip = tooltipResponder.tooltip();
        if (tooltip != null) {
            context.saveGraphicsState();
            context.translateCTM(0, 0, 400);
            context.drawTooltip(tooltip, tooltipResponder.bounds());
            context.restoreGraphicsState();
        }
    }

    public boolean keyUp(int key, int i, int j, Invoker<Integer, Integer, Integer, Boolean> invoker) {
        return dispatchers.invoke(key, i, j, invoker, WindowDispatcherImpl::keyUp);
    }

    public boolean keyDown(int key, int i, int j, Invoker<Integer, Integer, Integer, Boolean> invoker) {
        return dispatchers.invoke(key, i, j, invoker, WindowDispatcherImpl::keyDown);
    }

    public boolean charTyped(int key, int i, int j, Invoker<Integer, Integer, Integer, Boolean> invoker) {
        return dispatchers.invoke(key, i, j, invoker, WindowDispatcherImpl::charTyped);
    }

    public boolean mouseDown(double mouseX, double mouseY, int button, Invoker<Double, Double, Integer, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, button, invoker, WindowDispatcherImpl::mouseDown);
    }

    public boolean mouseUp(double mouseX, double mouseY, int button, Invoker<Double, Double, Integer, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, button, invoker, WindowDispatcherImpl::mouseUp);
    }

    public boolean mouseMoved(double mouseX, double mouseY, int button, Invoker<Double, Double, Integer, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, button, invoker, WindowDispatcherImpl::mouseMoved);
    }

    public boolean mouseWheel(double mouseX, double mouseY, double delta, Invoker<Double, Double, Double, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, delta, invoker, WindowDispatcherImpl::mouseWheel);
    }

    public boolean mouseIsInside(double mouseX, double mouseY, int button) {
        return dispatchers.test(dispatcher -> dispatcher.mouseIsInside(mouseX, mouseY, button));
    }

    public UIView firstTooltipResponder() {
        return dispatchers.flatMap(WindowDispatcherImpl::firstTooltipResponder);
    }

    public UIView firstInputResponder() {
        return dispatchers.flatMap(WindowDispatcherImpl::firstInputResponder);
    }

    public boolean isTextEditing() {
        return firstInputResponder() instanceof TextInputTraits;
    }

    @FunctionalInterface
    public interface Invoker<A, B, C, U> {
        U invoke(A a, B b, C c);
    }

    @FunctionalInterface
    public interface Invoker4<A, B, C, D, U> {
        U invoke(A a, B b, C c, D d);
    }

    @FunctionalInterface
    public interface RenderInvoker {
        void invoke(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);
    }

    public static class Queue<T extends WindowDispatcherImpl> implements Iterable<T> {

        private final LinkedList<T> values = new LinkedList<>();

        public void add(T val) {
            values.add(val);
            values.sort(Comparator.comparing(T::level));
        }

        public void remove(T val) {
            values.remove(val);
        }

        public void removeIf(Predicate<T> val) {
            values.removeIf(val);
        }

        public void removeAll() {
            values.clear();
        }

        public boolean test(Predicate<T> predicate) {
            for (T value : descendingEnum()) {
                if (predicate.test(value)) {
                    return true;
                }
            }
            return false;
        }

        public <U> U flatMap(Function<T, U> provider) {
            for (T value : descendingEnum()) {
                U ret = provider.apply(value);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        public <A, B, C> boolean invoke(A a, B b, C c, Invoker<A, B, C, Boolean> invoker, Invoker4<T, A, B, C, InvokerResult> provider) {
            for (T value : descendingEnum()) {
                InvokerResult ret = provider.invoke(value, a, b, c);
                if (ret.isDecided()) {
                    return ret.conclusion();
                }
            }
            return invoker.invoke(a, b, c);
        }

        @Override
        public Iterator<T> iterator() {
            return values.iterator();
        }

        public Iterable<T> descendingEnum() {
            return values::descendingIterator;
        }
    }
}
