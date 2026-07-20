package dev.mariany.keepinventorynerf.client.gui.screen;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import dev.mariany.keepinventorynerf.client.KeepInventoryNerfClient;
import dev.mariany.keepinventorynerf.config.KINClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class DeathLossesRenderer {
    private static final Identifier SLOT_TEXTURE = KeepInventoryNerf.id("death/slot");
    private static final Identifier SLOT_HIGHLIGHTED_TEXTURE = KeepInventoryNerf.id("death/slot_highlighted");

    private static final int SLOT_GAP = 4;
    private static final int SLOT_PADDING = 20;
    private static final int SLOT_SIZE = 26;
    private static final int SLOT_INSET = 5;

    private static final String LEVELS_TRANSLATION_KEY = "deathScreen.keepinventorynerf.lost_levels";
    private static final String STACKS_TRANSLATION_KEY = "deathScreen.keepinventorynerf.lost_stacks";
    private static final String LEVELS_AND_STACKS_TRANSLATION_KEY =
            "deathScreen.keepinventorynerf.lost_levels_and_stacks";

    private final Minecraft client;
    private final StacksContainer stacksContainer;

    private int lostLevels = 0;

    public DeathLossesRenderer() {
        this(Minecraft.getInstance());
    }

    public DeathLossesRenderer(Minecraft client) {
        this.client = client;

        this.stacksContainer = new StacksContainer(
                client,
                DeathLossesRenderer::getHorizontalOffset,
                SLOT_PADDING,
                SLOT_GAP,
                SLOT_SIZE
        );
    }

    private static int getHorizontalOffset() {
        return KeepInventoryNerfClient.getConfig().deathScreen.items.horizontalOffset;
    }

    public void updateLostLevels(int lostLevels) {
        this.lostLevels = lostLevels;
    }

    public void updateDroppedStacks(Collection<ItemStack> stacks) {
        this.stacksContainer.updateStacks(stacks);
    }

    public boolean disableScoreRender(int y) {
        return this.shouldDisplayLosses() && y == 100;
    }

    public boolean shouldDisplayLosses() {
        KINClientConfig config = KeepInventoryNerfClient.getConfig();

        if (!config.deathScreen.enabled) {
            return false;
        }

        return this.lostLevels > 0 || this.stacksContainer.hasStacks();
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (!this.shouldDisplayLosses()) {
            return;
        }

        ItemStackWithSlot stackWithSlot = this.getStackAt(mouseX, mouseY).orElse(null);

        this.drawLostLevels(graphics);
        this.drawDroppedStacks(graphics, mouseX, mouseY, stackWithSlot);
        this.updateCursor(graphics, stackWithSlot != null);
    }

    private void drawLostLevels(GuiGraphicsExtractor graphics) {
        boolean lostLevels = this.lostLevels > 0;
        boolean lostStacks = this.stacksContainer.hasStacks();

        String translationKey;

        if (lostLevels && lostStacks) {
            translationKey = LEVELS_AND_STACKS_TRANSLATION_KEY;
        } else if (lostLevels) {
            translationKey = LEVELS_TRANSLATION_KEY;
        } else if (lostStacks) {
            translationKey = STACKS_TRANSLATION_KEY;
        } else {
            return;
        }

        Component text = Component.translatable(
                translationKey,
                Component.literal(Integer.toString(this.lostLevels)).withStyle(ChatFormatting.RED)
        );

        graphics.textRenderer().accept(TextAlignment.CENTER, this.getScreenWidth() / 2, getLevelsTextY(), text);
    }

    private int getScreenWidth() {
        return this.getScreen().map(screen -> screen.width).orElse(0);
    }

    private Optional<Screen> getScreen() {
        return Optional.ofNullable(this.client.gui.screen());
    }

    private static int getLevelsTextY() {
        return KeepInventoryNerfClient.getConfig().deathScreen.levels.textY;
    }

    private void drawDroppedStacks(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            @Nullable ItemStackWithSlot highlightedStackWithSlot
    ) {
        if (!this.stacksContainer.hasStacks()) {
            return;
        }

        this.drawDroppedStacksBackground(graphics);

        this.stacksContainer.forEach((stack, index, x, y) -> this.drawDroppedStack(
                graphics,
                highlightedStackWithSlot,
                stack,
                index,
                x,
                y
        ));

        this.drawStackTooltip(graphics, mouseX, mouseY, highlightedStackWithSlot);
    }

    private void drawDroppedStack(
            GuiGraphicsExtractor graphics,
            @Nullable ItemStackWithSlot highlightedStackWithSlot,
            ItemStack stack,
            int index,
            int x,
            int y
    ) {
        Identifier texture;

        if (highlightedStackWithSlot == null || highlightedStackWithSlot.slot() != index) {
            texture = SLOT_TEXTURE;
        } else {
            texture = SLOT_HIGHLIGHTED_TEXTURE;
        }

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x, y,
                SLOT_SIZE, SLOT_SIZE
        );

        int itemX = x + SLOT_INSET;
        int itemY = y + SLOT_INSET;

        graphics.item(stack, itemX, itemY);
        graphics.itemDecorations(this.client.font, stack, itemX, itemY);
    }

    private void drawDroppedStacksBackground(GuiGraphicsExtractor graphics) {
        AABB box = this.getContainerBox();

        int minX = (int) box.minX;
        int minY = (int) box.minY;

        int maxX = (int) box.maxX;
        int maxY = (int) box.maxY;

        graphics.fill(minX, minY, maxX, maxY, ARGB.colorFromFloat(0.5F, 0F, 0F, 0F));
    }

    private AABB getContainerBox() {
        return this.stacksContainer.getBox().inflate(SLOT_GAP);
    }

    private void drawStackTooltip(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            @Nullable ItemStackWithSlot stackWithSlot
    ) {
        if (stackWithSlot == null) {
            return;
        }

        ItemStack stack = stackWithSlot.stack();

        graphics.setTooltipForNextFrame(this.client.font, stack, x, y);
    }

    private Optional<ItemStackWithSlot> getStackAt(int mouseX, int mouseY) {
        List<ItemStackWithSlot> stacks = this.stacksContainer.map(
                (stack, index, x, y) -> {
                    boolean hit = mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
                    return hit ? new ItemStackWithSlot(index, stack) : null;
                }
        );

        return stacks.stream().filter(Objects::nonNull).findFirst();
    }

    private void updateCursor(GuiGraphicsExtractor graphics, boolean hovering) {
        if (!hovering) {
            return;
        }

        graphics.requestCursor(CursorTypes.POINTING_HAND);
    }
}
