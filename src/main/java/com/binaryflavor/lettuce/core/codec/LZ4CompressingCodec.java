package com.binaryflavor.lettuce.core.codec;

import java.nio.ByteBuffer;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4FastDecompressor;

import io.lettuce.core.codec.RedisCodec;

public class LZ4CompressingCodec implements RedisCodec<Object, Object> {
    private static final int ORIGINAL_LENGTH_HEADER_SIZE = Integer.SIZE / 8;

    private final RedisCodec<Object, Object> delegate;
    private final LZ4Compressor compressor;
    private final LZ4FastDecompressor decompressor;

    public LZ4CompressingCodec(RedisCodec<Object, Object> delegate, LZ4Compressor compressor, LZ4FastDecompressor decompressor) {
        if (delegate == null) {
            throw new NullPointerException("Delegate codec must not be null");
        }
        if (compressor == null) {
            throw new NullPointerException("LZ4Compressor must not be null");
        }
        if (decompressor == null) {
            throw new NullPointerException("LZ4FastDecompressor must not be null");
        }
        this.delegate = delegate;
        this.compressor = compressor;
        this.decompressor = decompressor;
    }

    @Override
    public Object decodeKey(ByteBuffer bytes) {
        return delegate.decodeKey(bytes);
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
        if(!bytes.hasRemaining()) {
            return delegate.decodeValue(bytes);
        }

        try {
            byte[] data = new byte[bytes.remaining()];
            bytes.get(data);

            ByteBuffer buffer = ByteBuffer.wrap(data);
            int originalLength = buffer.getInt();

            byte[] compressed = new byte[buffer.remaining()];
            buffer.get(compressed);

            byte[] decompressed = decompressor.decompress(compressed, originalLength);
            return delegate.decodeValue(ByteBuffer.wrap(decompressed));
        } catch(Exception e) {
            throw new RuntimeException("Failed to decompress value", e);
        }
    }

    @Override
    public ByteBuffer encodeKey(Object key) {
        return delegate.encodeKey(key);
    }

    @Override
    public ByteBuffer encodeValue(Object value) {
        ByteBuffer encoded = delegate.encodeValue(value);
        if(!encoded.hasRemaining()) {
            return encoded;
        }

        try {
            byte[] originalData = new byte[encoded.remaining()];
            encoded.get(originalData);

            byte[] compressed = compressor.compress(originalData);

            ByteBuffer buffer = ByteBuffer.allocate(ORIGINAL_LENGTH_HEADER_SIZE + compressed.length);
            buffer.putInt(originalData.length);
            buffer.put(compressed);
            buffer.flip();

            return buffer;
        } catch(Exception e) {
            throw new RuntimeException("Failed to compress value", e);
        }
    }
}
