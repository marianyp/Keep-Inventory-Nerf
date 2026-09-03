package dev.mariany.keepinventorynerf.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class StacksContainer {
    private final MinecraftClient client;

    private final Supplier<Integer> verticalOffsetSupplier;
    private final int horizontalPadding;
    private final int gap;
    private final int slotSize;

    private final List<ItemStack> stacks = new ArrayList<>();

    public StacksContainer(
            MinecraftClient client,
            Supplier<Integer> verticalOffsetSupplier,
            int horizontalPadding,
            int gap,
            int slotSize
    ) {
        this.client = client;
        this.verticalOffsetSupplier = verticalOffsetSupplier;
        this.horizontalPadding = horizontalPadding;
        this.gap = gap;
        this.slotSize = slotSize;
    }

    public boolean hasStacks() {
        return !this.stacks.isEmpty();
    }

    public Box getBox() {
        if (this.stacks.isEmpty()) {
            return new Box(0, 0, 0, 0, 0, 0);
        }

        final int screenWidth = this.getScreenWidth();

        if (screenWidth <= 0) {
            return new Box(0, 0, 0, 0, 0, 0);
        }

        final int startY = this.getStartY();
        final int stacksWidth = this.getWidth();

        final int step = this.slotSize + this.gap;
        final int maxPerRow = Math.max(1, (stacksWidth + this.gap) / step);

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        int index = 0;
        int row = 0;

        while (index < this.stacks.size()) {
            int remaining = this.stacks.size() - index;
            int countThisRow = Math.min(maxPerRow, remaining);

            int rowWidth = countThisRow * this.slotSize + (countThisRow - 1) * this.gap;
            int startX = screenWidth / 2 - rowWidth / 2;

            int y = startY + row * step;

            int rowMaxX = startX + rowWidth;
            int rowMaxY = y + this.slotSize;

            minX = Math.min(minX, startX);
            maxX = Math.max(maxX, rowMaxX);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, rowMaxY);

            index += countThisRow;
            row++;
        }

        return new Box(minX, minY, 0, maxX, maxY, 0);
    }


    public void updateStacks(Collection<ItemStack> stacks) {
        this.stacks.clear();
        this.stacks.addAll(stacks);
    }

    public void forEach(SlotConsumer consumer) {
        iterate(consumer);
    }

    public <R> List<R> map(SlotMapper<R> mapper) {
        ArrayList<R> out = new ArrayList<>(this.stacks.size());
        iterate((stack, index, x, y) -> out.add(mapper.map(stack, index, x, y)));
        return out;
    }

    private void iterate(SlotConsumer consumer) {
        if (this.stacks.isEmpty()) {
            return;
        }

        final int screenWidth = this.getScreenWidth();

        if (screenWidth <= 0) {
            return;
        }

        final int startY = this.getStartY();
        final int stacksWidth = this.getWidth();

        final int step = this.slotSize + this.gap;

        final int maxPerRow = Math.max(1, (stacksWidth + this.gap) / step);

        int index = 0;
        int row = 0;

        while (index < this.stacks.size()) {
            int remaining = this.stacks.size() - index;
            int countThisRow = Math.min(maxPerRow, remaining);

            int rowWidth = countThisRow * this.slotSize + (countThisRow - 1) * this.gap;
            int startX = screenWidth / 2 - rowWidth / 2;

            int y = startY + row * step;

            for (int column = 0; column < countThisRow; column++) {
                int x = startX + column * step;

                ItemStack stack = this.stacks.get(index);
                consumer.accept(stack, index, x, y);

                index++;
            }

            row++;
        }
    }

    private int getWidth() {
        return Math.max(1, this.getScreenWidth() - this.horizontalPadding * 2);
    }

    private int getStartY() {
        return this.getScreenHeight() / 2 + this.getVerticalOffset();
    }

    private int getScreenWidth() {
        return this.getScreen().map(screen -> screen.width).orElse(0);
    }

    private int getScreenHeight() {
        return this.getScreen().map(screen -> screen.height).orElse(0);
    }

    private int getVerticalOffset() {
        return this.verticalOffsetSupplier.get();
    }

    private Optional<Screen> getScreen() {
        return Optional.ofNullable(this.client.currentScreen);
    }

    @FunctionalInterface
    public interface SlotConsumer {
        void accept(ItemStack stack, int index, int x, int y);
    }

    @FunctionalInterface
    public interface SlotMapper<R> {
        R map(ItemStack stack, int index, int x, int y);
    }
}
