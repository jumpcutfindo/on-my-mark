package com.jumpcutfindo.onmymark.graphics.screen.toast;

import com.google.common.collect.ImmutableList;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Toast for OnMyMark.
 * Derived heavily from {@link OnMyMarkToast}.
 */
public class OnMyMarkToast implements Toast {
    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/system");
    private static final int MIN_WIDTH = 200;
    private static final int LINE_HEIGHT = 12;
    private static final int PADDING_Y = 10;

    private final Type type;
    private Text title;
    private List<OrderedText> lines;
    private long startTime;
    private boolean justUpdated;
    private final int width;
    private boolean hidden;
    private Visibility visibility = Visibility.HIDE;

    public static OnMyMarkToast create(MinecraftClient client, OnMyMarkToast.Type type, Text title, Text description) {
        TextRenderer textRenderer = client.textRenderer;
        List<OrderedText> list = textRenderer.wrapLines(description, 200);
        int i = Math.max(200, list.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
        return new OnMyMarkToast(type, title, list, i + 30);
    }

    public OnMyMarkToast(OnMyMarkToast.Type type, Text title, @Nullable Text description) {
        this(
                type,
                title,
                getTextAsList(description),
                Math.max(
                        160,
                        30
                                + Math.max(
                                MinecraftClient.getInstance().textRenderer.getWidth(title), description == null ? 0 : MinecraftClient.getInstance().textRenderer.getWidth(description)
                        )
                )
        );
    }

    private OnMyMarkToast(OnMyMarkToast.Type type, Text title, List<OrderedText> lines, int width) {
        this.type = type;
        this.title = title;
        this.lines = lines;
        this.width = width;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return 20 + Math.max(this.lines.size(), 1) * 12;
    }

    public void hide() {
        this.hidden = true;
    }

    private static ImmutableList<OrderedText> getTextAsList(@Nullable Text text) {
        return text == null ? ImmutableList.of() : ImmutableList.of(text.asOrderedText());
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.justUpdated) {
            this.startTime = time;
            this.justUpdated = false;
        }

        double d = this.type.displayDuration * manager.getNotificationDisplayTimeMultiplier();
        long l = time - this.startTime;
        this.visibility = !this.hidden && l < d ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        // TODO: Customise toast icon and styling
        context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        if (this.lines.isEmpty()) {
            context.drawText(textRenderer, this.title, 18, 12, Colors.YELLOW, false);
        } else {
            context.drawText(textRenderer, this.title, 18, 7, Colors.YELLOW, false);

            for (int i = 0; i < this.lines.size(); i++) {
                context.drawText(textRenderer, (OrderedText)this.lines.get(i), 18, 18 + i * 12, -1, false);
            }
        }
    }

    public OnMyMarkToast.Type getType() {
        return this.type;
    }

    public void setContent(Text title, @Nullable Text description) {
        this.title = title;
        this.lines = getTextAsList(description);
        this.justUpdated = true;
    }

    public static void add(ToastManager manager, OnMyMarkToast.Type type, Text title, @Nullable Text description) {
        manager.add(new OnMyMarkToast(type, title, description));
    }

    public static void show(ToastManager manager, OnMyMarkToast.Type type, Text title, @Nullable Text description) {
        OnMyMarkToast OnMyMarkToast = manager.getToast(OnMyMarkToast.class, type);
        if (OnMyMarkToast == null) {
            add(manager, type, title, description);
        } else {
            OnMyMarkToast.setContent(title, description);
        }
    }

    public static void hide(ToastManager manager, OnMyMarkToast.Type type) {
        OnMyMarkToast OnMyMarkToast = manager.getToast(OnMyMarkToast.class, type);
        if (OnMyMarkToast != null) {
            OnMyMarkToast.hide();
        }
    }

    public static void addPartyInvitationToast(MinecraftClient client, PartyInvite partyInvite) {
        add(
                client.getToastManager(),
                Type.PARTY_INVITATION,
                Text.translatable("onmymark.toast.partyInvite.title"),
                Text.translatable("onmymark.toast.partyInvite.description", partyInvite.from().displayName())
        );
    }

    public static class Type {
        public static final OnMyMarkToast.Type PARTY_INVITATION = new OnMyMarkToast.Type();

        final long displayDuration;

        public Type(long displayDuration) {
            this.displayDuration = displayDuration;
        }

        public Type() {
            this(5000L);
        }
    }
}
