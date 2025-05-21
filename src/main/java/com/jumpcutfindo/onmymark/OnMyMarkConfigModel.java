package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.client.graphics.markers.*;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = OnMyMarkMod.MOD_ID)
@Config(name = OnMyMarkMod.MOD_ID, wrapperName = "OnMyMarkConfig")
public class OnMyMarkConfigModel {

    @SectionHeader("markerDrawingSection")
    public ClampType markerClampType = ClampType.DEFAULT_CLAMP_TYPE;

    @RangeConstraint(min = 0, max = 100)
    public int ovalClampPadding = ClampType.DEFAULT_OVAL_CLAMP_PADDING;

    @RangeConstraint(min = 256, max = 768)
    public int circleClampDiameter = ClampType.DEFAULT_OVAL_CLAMP_PADDING;

    @SectionHeader("markerPointerSection")

    @RangeConstraint(min = 0.60F, max = 1.00F)
    public float distanceLabelScale = MarkerRenderer.DEFAULT_LABEL_SCALE;

    @RangeConstraint(min = 4, max = 16)
    public int markerPointerWidth = MarkerRenderer.DEFAULT_POINTER_WIDTH;

    @RangeConstraint(min = 4, max = 16)
    public int markerPointerHeight = MarkerRenderer.DEFAULT_POINTER_HEIGHT;

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
