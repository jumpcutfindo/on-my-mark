package com.jumpcutfindo.onmymark.client.graphics.screen.components;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ScreenUtils;
import com.jumpcutfindo.onmymark.client.sounds.SoundPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ListView<T extends ListItem<?>> implements Interactable {
    protected final OnMyMarkScreen screen;

    // Texture position
    public int x, y;

    // Texture information
    protected Identifier texture;
    protected int textureWidth, textureHeight;
    protected int textureU, textureV, width, height;
    protected int listX, listY;
    protected int scrollbarWidth, scrollbarHeight, scrollbarU, scrollbarV, scrollbarX, scrollbarY;
    protected int maxItems;

    private SelectType selectType;

    private int step;
    private int maxSteps;
    private float stepAmount;

    private float scrollPosition; // Range from 0.0 to 1.0
    private boolean scrolling;

    protected List<T> listItems;
    protected List<T> visibleItems;

    private Set<T> selectedItems;
    private Set<Integer> selectedIndices;

    private int lastToggledIndex = 0;

    public ListView(OnMyMarkScreen screen) {
        this.screen = screen;

        this.selectedItems = new HashSet<>();
        this.selectedIndices = new HashSet<>();
    }

    public ListView<T> setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ListView<T> setTexture(Identifier texture, int textureWidth, int textureHeight, int u, int v, int width, int height) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textureU = u;
        this.textureV = v;
        this.width = width;
        this.height = height;
        return this;
    }

    public ListView<T> setListPosition(int x, int y) {
        this.listX = x;
        this.listY = y;
        return this;
    }

    public ListView<T> setScrollbar(int x, int y, int u, int v, int width, int height) {
        this.scrollbarX = x;
        this.scrollbarY = y;
        this.scrollbarU = u;
        this.scrollbarV = v;
        this.scrollbarWidth = width;
        this.scrollbarHeight = height;
        return this;
    }

    public ListView<T> setList(List<T> listItems, int maxItems) {
        this.listItems = listItems;
        this.maxItems = maxItems;
        this.visibleItems = new ArrayList<>();
        this.maxSteps = Math.max(0, listItems.size() - maxItems);
        this.stepAmount = (float) (this.scrollbarHeight - 15) / (float) (this.maxSteps);
        return this;
    }

    public ListView<T> setSelectType(SelectType selectType) {
        this.selectType = selectType;
        return this;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, this.texture, x, y, this.textureU, this.textureV, this.width, this.height, this.textureWidth, this.textureHeight);
        this.renderScrollbar(context, mouseX, mouseY);
    }

    public void renderItems(DrawContext context, int mouseX, int mouseY) {
        if (this.listItems.isEmpty()) return;

        this.step = (int) (this.scrollPosition * this.maxSteps);

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            T item = this.listItems.get(i);
            item.render(context, x + listX, y + listY + offsetY, mouseX, mouseY);
            offsetY += item.getHeight();
        }
    }

    private void renderScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (!this.hasScrollbar()) return;

        context.drawTexture(RenderLayer::getGuiTextured, this.texture, x + scrollbarX, y + scrollbarY + (int) (this.scrollPosition * (scrollbarHeight - 15)), scrollbarU, scrollbarV, 13, 15, this.textureWidth, this.textureHeight);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isClickInScrollbar(mouseX, mouseY)) {
            this.scrolling = this.hasScrollbar();
            return true;
        }

        // Block action if player cannot select due to some specified condition
        // If nothing can be selected, don't even check any items
        if (!this.canSelect() || this.selectType == SelectType.NONE) {
            return false;
        }

        int offsetY = 0;
        for (int i = step; i < step + maxItems; i++) {
            if (i >= this.listItems.size()) break;

            T item = this.listItems.get(i);

            if (item.mouseClicked(x + listX, y + listY + offsetY, mouseX, mouseY)) {
                SoundPlayer.playClickSound(MinecraftClient.getInstance().getSoundManager());
                this.toggleSelected(i);
                return true;
            }

            offsetY += item.getHeight();
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            this.scrollPosition += deltaY / (this.scrollbarHeight - 15);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.hasScrollbar()) {
            return false;
        } else {
            this.scrollPosition += (1.0f / (float) this.maxSteps) * -verticalAmount;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.step += (int) -verticalAmount;
            this.step = MathHelper.clamp(this.step, 0, this.maxSteps);
            return true;
        }
    }

    public boolean isClickInScrollbar(int mouseX, int mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, x + scrollbarX, y + scrollbarY, scrollbarWidth, scrollbarHeight);
    }

    public boolean hasScrollbar() {
        return listItems.size() > maxItems;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean toggleSelected(int index) {
        if (index >= this.listItems.size()) {
            return false;
        }

        T item = this.listItems.get(index);

        if (selectType == SelectType.SINGLE) {
            if (!this.selectedItems.isEmpty()) {
                this.resetSelection();
                item.setSelected(false);
            } else {
                this.selectedItems.add(item);
                this.selectedIndices.add(index);
                item.setSelected(true);
            }
        } else {
            if (item.isSelected()) {
                this.selectedItems.remove(item);
                this.selectedIndices.remove(index);
                item.setSelected(false);
            } else {
                this.selectedItems.add(item);
                this.selectedIndices.add(index);
                item.setSelected(true);
            }
        }

        this.lastToggledIndex = index;

        return true;
    }

    public boolean isAnySelected() {
       return selectedItems.size() > 0;
    }

    public boolean isAllSelected() {
        return selectedItems.size() == listItems.size();
    }

    public void setLastToggledIndex(int lastToggledIndex) {
        this.lastToggledIndex = lastToggledIndex;
    }

    public int getLastToggledIndex() {
        return lastToggledIndex;
    }

    public List<T> getSelectedItems() {
        return selectedItems.stream().toList();
    }

    public List<Integer> getSelectedIndices() {
        return selectedIndices.stream().toList();
    }

    public void resetSelection() {
        for (T item : listItems) item.setSelected(false);
        this.selectedItems = new HashSet<>();
        this.selectedIndices = new HashSet<>();
    }

    public float getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(float scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public boolean canSelect() {
        return true;
    }

    /**
     * Retrieves an NbtCompound that represents the various settings (e.g. state) of the ListView.
     */
    public abstract NbtCompound getSettings();

    /**
     * Applies a given set of settings to the ListView.
     */
    public abstract void applySettings(NbtCompound settings);

    public enum SelectType {
        NONE, SINGLE, MULTIPLE
    }
}
