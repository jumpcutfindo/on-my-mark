package com.jumpcutfindo.onmymark.client.graphics.screen.toast;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
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

import java.util.List;

/**
 * Toast for party invitations.
 * Derived heavily from {@link net.minecraft.client.toast.SystemToast}.
 */
public class PartyInvitationToast implements Toast {
    private static final Identifier TOAST_ID = Identifier.of(OnMyMarkMod.MOD_ID, "party_invitation_toast");
    private static final Identifier BASE_TEXTURE = Identifier.ofVanilla("toast/tutorial");
    private static final Identifier TOAST_TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/party_invitation_toast.png");

    private static final int TEXTURE_WIDTH = 256, TEXTURE_HEIGHT = 256;

    private static final long DISPLAY_DURATION_MS = 5000;

    private Text title, description;
    private List<OrderedText> lines;

    private int width;

    private long startTime;
    private boolean justUpdated;
    private boolean hidden;
    private Visibility visibility = Visibility.HIDE;

    private PartyInvitationToast(PartyInvite<ClientPartyMember> partyInvite) {
        this.title = Text.translatable("onmymark.toast.partyInvite.title", partyInvite.from().displayName());
        this.description = Text.translatable("onmymark.toast.partyInvite.description");

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.lines = textRenderer.wrapLines(description, 160);

        int descriptionLength = this.lines.stream().mapToInt(textRenderer::getWidth).max().getAsInt();

        this.width = Math.max(
                134,
                30 + Math.max(textRenderer.getWidth(title), description == null ? 0 : descriptionLength)
        );
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

        double d = DISPLAY_DURATION_MS * manager.getNotificationDisplayTimeMultiplier();
        long l = time - this.startTime;
        this.visibility = !this.hidden && l < d ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.drawGuiTexture(RenderLayer::getGuiTextured, BASE_TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, this.title, 26, 7, Colors.BLUE, false);
        context.drawTexture(RenderLayer::getGuiTextured, TOAST_TEXTURE, 4, (this.getHeight() / 2) - 10, 0, 0, 20, 20, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        for (int i = 0; i < this.lines.size(); i++) {
            context.drawText(textRenderer, this.lines.get(i), 26, 18 + i * 12, Colors.BLACK, false);
        }
    }

    public static void add(ToastManager manager, PartyInvite<ClientPartyMember> partyInvite) {
        manager.add(new PartyInvitationToast(partyInvite));
    }

    public static void show(ToastManager manager, PartyInvite<ClientPartyMember> partyInvite) {
        PartyInvitationToast PartyInvitationToast = manager.getToast(PartyInvitationToast.class, TOAST_ID);
        if (PartyInvitationToast == null) {
            add(manager, partyInvite);
        }
    }

    public static void hide(ToastManager manager) {
        PartyInvitationToast PartyInvitationToast = manager.getToast(PartyInvitationToast.class, TOAST_ID);
        if (PartyInvitationToast != null) {
            PartyInvitationToast.hide();
        }
    }
}
