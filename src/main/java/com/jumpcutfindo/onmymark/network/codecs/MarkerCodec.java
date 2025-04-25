package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.marker.Marker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class MarkerCodec implements PacketCodec<PacketByteBuf, Marker> {
    @Override
    public Marker decode(PacketByteBuf buf) {
        Type type = buf.readEnumConstant(Type.class);

        switch (type) {
            case BLOCK -> {
                return OnMyMarkCodecs.BLOCK_MARKER.decode(buf);
            }
            case ENTITY -> {
                return OnMyMarkCodecs.ENTITY_MARKER.decode(buf);
            }
        }

        return null;
    }

    @Override
    public void encode(PacketByteBuf buf, Marker marker) {
        if (marker instanceof BlockMarker blockMarker) {
            buf.writeEnumConstant(Type.BLOCK);
            OnMyMarkCodecs.BLOCK_MARKER.encode(buf, blockMarker);
        } else if (marker instanceof EntityMarker entityMarker) {
            buf.writeEnumConstant(Type.ENTITY);
            OnMyMarkCodecs.ENTITY_MARKER.encode(buf, entityMarker);
        }
    }

    private enum Type {
        BLOCK, ENTITY
    }
}
