package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.client.graphics.markers.*;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = OnMyMarkMod.MOD_ID)
@Config(name = OnMyMarkMod.MOD_ID, wrapperName = "OnMyMarkConfig")
public class OnMyMarkConfigModel {

    @SectionHeader("markerPositionSection")
    @Sync(Option.SyncMode.NONE)
    public ClampType markerClampType = ClampType.DEFAULT_CLAMP_TYPE;

    @RangeConstraint(min = 0, max = 100)
    @Sync(Option.SyncMode.NONE)
    public int ovalClampPadding = ClampType.DEFAULT_OVAL_CLAMP_PADDING;

    @RangeConstraint(min = 256, max = 768)
    @Sync(Option.SyncMode.NONE)
    public int circleClampDiameter = ClampType.DEFAULT_OVAL_CLAMP_PADDING;

    @SectionHeader("markerStyleSection")

    @RangeConstraint(min = 0.60F, max = 1.00F)
    @Sync(Option.SyncMode.NONE)
    public float distanceLabelScale = MarkerRenderer.DEFAULT_LABEL_SCALE;

    @RangeConstraint(min = 4, max = 16)
    @Sync(Option.SyncMode.NONE)
    public int markerPointerWidth = MarkerRenderer.DEFAULT_POINTER_WIDTH;

    @RangeConstraint(min = 4, max = 16)
    @Sync(Option.SyncMode.NONE)
    public int markerPointerHeight = MarkerRenderer.DEFAULT_POINTER_HEIGHT;

    @Sync(Option.SyncMode.NONE)
    public boolean isPlayerHeadEnabled = true;

    @RangeConstraint(min = 0.60F, max = 1.50F)
    @Sync(Option.SyncMode.NONE)
    public float playerHeadScale = 1.0F;

    @SectionHeader("markerLifetimeSection")

    @RangeConstraint(min = 1, max = 86400)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public long blockMarkerLifetimeSecs = BlockMarkerRenderer.DEFAULT_LIFETIME_SECS;

    @RangeConstraint(min = 1, max = 86400)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public long entityMarkerLifetimeSecs = EntityMarkerRenderer.DEFAULT_LIFETIME_SECS;

    @RangeConstraint(min = 1, max = 86400)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public long playerMarkerLifetimeSecs = PlayerMarkerRenderer.DEFAULT_LIFETIME_SECS;
}
