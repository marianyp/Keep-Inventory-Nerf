package dev.mariany.keepinventorynerf.client.gui.screen;

import dev.mariany.keepinventorynerf.KeepInventoryNerf;
import dev.mariany.keepinventorynerf.client.KeepInventoryNerfClient;
import dev.mariany.keepinventorynerf.config.KINClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
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
    private static final int TEXT_HORIZONTAL_PADDING = 20;

    private static final String LEVELS_TRANSLATION_KEY = "deathScreen.keepinventorynerf.lost_levels";
    private static final String STACKS_TRANSLATION_KEY = "deathScreen.keepinventorynerf.lost_stacks";
    private static final String LEVELS_AND_STACKS_TRANSLATION_KEY =
            "deathScreen.keepinventorynerf.lost_levels_and_stacks";

    private final MinecraftClient client;
    private final StacksContainer stacksContainer;

    private int lostLevels = 0;

    public DeathLossesRenderer() {
        this(MinecraftClient.getInstance());
    }

    public DeathLossesRenderer(MinecraftClient client) {
        this.client = client;

        this.stacksContainer = new StacksContainer(
                client,
                DeathLossesRenderer::getItemsVerticalOffset,
                SLOT_PADDING,
                SLOT_GAP,
                SLOT_SIZE
        );
    }

    private static int getItemsVerticalOffset() {
        return KeepInventoryNerfClient.getConfig().deathScreen.items.verticalOffset;
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

    public void render(DrawContext context, int mouseX, int mouseY) {
        if (!this.shouldDisplayLosses()) {
            return;
        }

        StackWithSlot stackWithSlot = this.getStackAt(mouseX, mouseY).orElse(null);

        this.drawLostLevels(context);
        this.drawDroppedStacks(context, mouseX, mouseY, stackWithSlot);
        this.updateCursor(context, stackWithSlot != null);
    }

    private void drawLostLevels(DrawContext context) {
        String translationKey;

        boolean lostLevels = this.lostLevels > 0;
        boolean lostStacks = this.stacksContainer.hasStacks();

        if (lostLevels && lostStacks) {
            translationKey = LEVELS_AND_STACKS_TRANSLATION_KEY;
        } else if (lostLevels) {
            translationKey = LEVELS_TRANSLATION_KEY;
        } else if (lostStacks) {
            translationKey = STACKS_TRANSLATION_KEY;
        } else {
            return;
        }

        Text text = Text.translatable(
                translationKey,
                Text.literal(Integer.toString(this.lostLevels)).formatted(Formatting.RED)
        );

        this.drawCenteredWrappedText(context, text);
    }

    private void drawCenteredWrappedText(DrawContext context, Text text) {
        this.getTextRenderer().ifPresent(textRenderer -> {
            int maxWidth = Math.max(1, this.getScreenWidth() - TEXT_HORIZONTAL_PADDING * 2);
            List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);
            int centerX = this.getScreenCenterX();
            int startY = this.getScreenCenterY() + getLevelsVerticalOffset();

            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                context.drawCenteredTextWithShadow(
                        textRenderer,
                        lines.get(lineIndex),
                        centerX,
                        startY + lineIndex * textRenderer.fontHeight,
                        Colors.WHITE
                );
            }
        });
    }

    private Optional<TextRenderer> getTextRenderer() {
        return this.getScreen().map(Screen::getTextRenderer);
    }

    private int getScreenWidth() {
        return this.getScreen().map(screen -> screen.width).orElse(0);
    }

    private int getScreenCenterX() {
        return this.getScreen().map(screen -> screen.width / 2).orElse(0);
    }

    private int getScreenCenterY() {
        return this.getScreen().map(screen -> screen.height / 2).orElse(0);
    }

    private Optional<Screen> getScreen() {
        return Optional.ofNullable(this.client.currentScreen);
    }

    private static int getLevelsVerticalOffset() {
        return KeepInventoryNerfClient.getConfig().deathScreen.levels.verticalOffset;
    }

    private void drawDroppedStacks(DrawContext context, int mouseX, int mouseY, @Nullable StackWithSlot stackWithSlot) {
        if (!this.stacksContainer.hasStacks()) {
            return;
        }

        this.drawDroppedStacksBackground(context);

        this.getTextRenderer()
            .ifPresent(
                    textRenderer -> this.stacksContainer.forEach(
                            (stack, index, x, y) -> {
                                Identifier texture;

                                if (stackWithSlot == null || stackWithSlot.slot() != index) {
                                    texture = SLOT_TEXTURE;
                                } else {
                                    texture = SLOT_HIGHLIGHTED_TEXTURE;
                                }

                                context.drawGuiTexture(
                                        RenderPipelines.GUI_TEXTURED,
                                        texture,
                                        x, y,
                                        SLOT_SIZE, SLOT_SIZE
                                );

                                int itemX = x + SLOT_INSET;
                                int itemY = y + SLOT_INSET;

                                context.drawItem(stack, itemX, itemY);

                                context.drawStackOverlay(textRenderer, stack, itemX, itemY);
                            })
            );

        this.drawStackTooltip(context, mouseX, mouseY, stackWithSlot);
    }

    private void drawDroppedStacksBackground(DrawContext context) {
        Box box = this.getContainerBox();

        int minX = (int) box.minX;
        int minY = (int) box.minY;

        int maxX = (int) box.maxX;
        int maxY = (int) box.maxY;

        context.fill(minX, minY, maxX, maxY, ColorHelper.withAlpha(0.5F, Colors.BLACK));
    }

    private Box getContainerBox() {
        return this.stacksContainer.getBox().expand(SLOT_GAP);
    }

    private void drawStackTooltip(DrawContext context, int x, int y, @Nullable StackWithSlot stackWithSlot) {
        if (stackWithSlot != null) {
            ItemStack stack = stackWithSlot.stack();

            getTextRenderer().ifPresent(textRenderer -> context.drawTooltip(
                    textRenderer,
                    this.getTooltipFromItem(stack),
                    stack.getTooltipData(),
                    x,
                    y,
                    stack.get(DataComponentTypes.TOOLTIP_STYLE)
            ));
        }
    }

    private Optional<StackWithSlot> getStackAt(int mouseX, int mouseY) {
        List<StackWithSlot> stacks = this.stacksContainer.map(
                (stack, index, x, y) -> {
                    boolean hit = mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
                    return hit ? new StackWithSlot(index, stack) : null;
                }
        );

        return stacks.stream().filter(Objects::nonNull).findFirst();
    }

    private List<Text> getTooltipFromItem(ItemStack stack) {
        return Screen.getTooltipFromItem(this.client, stack);
    }

    private void updateCursor(DrawContext context, boolean hovering) {
        if (!hovering) {
            return;
        }

        context.setCursor(StandardCursors.POINTING_HAND);
    }
}
