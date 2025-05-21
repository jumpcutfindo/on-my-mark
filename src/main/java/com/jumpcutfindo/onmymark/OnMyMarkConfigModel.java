package com.jumpcutfindo.onmymark;

import com.jumpcutfindo.onmymark.client.graphics.markers.BlockMarkerRenderer;
import com.jumpcutfindo.onmymark.client.graphics.markers.EntityMarkerRenderer;
import com.jumpcutfindo.onmymark.client.graphics.markers.PlayerMarkerRenderer;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = OnMyMarkMod.MOD_ID)
@Config(name = OnMyMarkMod.MOD_ID, wrapperName = "OnMyMarkConfig")
public class OnMyMarkConfigModel {
    @SectionHeader("markerSection")

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
