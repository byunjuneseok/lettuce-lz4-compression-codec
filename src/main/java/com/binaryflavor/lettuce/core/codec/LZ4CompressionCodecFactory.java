package com.binaryflavor.lettuce.core.codec;

import net.jpountz.lz4.LZ4Factory;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.internal.LettuceAssert;

public class LZ4CompressionCodecFactory {

    private LZ4CompressionCodecFactory() {
    }

    /**
     * Creates a value compressor using the fastest available LZ4 instance.
     *
     * @param delegate codec used for key-value encoding/decoding, must not be {@code null}.
     * @param <K>      Key type.
     * @param <V>      Value type.
     * @return Value-compressing codec using fastest LZ4.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K, V> RedisCodec<K, V> fastest(RedisCodec<K, V> delegate) {
        LettuceAssert.notNull(delegate, "RedisCodec must not be null");
        LZ4Factory factory = LZ4Factory.fastestInstance();
        return (RedisCodec) new LZ4CompressingCodec((RedisCodec) delegate, factory.fastCompressor(), factory.fastDecompressor());
    }

    /**
     * Creates a value compressor using the safest LZ4 instance.
     *
     * @param delegate codec used for key-value encoding/decoding, must not be {@code null}.
     * @param <K>      Key type.
     * @param <V>      Value type.
     * @return Value-compressing codec using safest LZ4.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K, V> RedisCodec<K, V> safest(RedisCodec<K, V> delegate) {
        LettuceAssert.notNull(delegate, "RedisCodec must not be null");
        LZ4Factory factory = LZ4Factory.safeInstance();
        return (RedisCodec) new LZ4CompressingCodec((RedisCodec) delegate, factory.fastCompressor(), factory.fastDecompressor());
    }

    /**
     * Creates a value compressor using the native LZ4 instance if available.
     *
     * @param delegate codec used for key-value encoding/decoding, must not be {@code null}.
     * @param <K>      Key type.
     * @param <V>      Value type.
     * @return Value-compressing codec using native LZ4.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K, V> RedisCodec<K, V> nativeInstance(RedisCodec<K, V> delegate) {
        LettuceAssert.notNull(delegate, "RedisCodec must not be null");
        LZ4Factory factory = LZ4Factory.nativeInstance();
        return (RedisCodec) new LZ4CompressingCodec((RedisCodec) delegate, factory.fastCompressor(), factory.fastDecompressor());
    }

    /**
     * Creates a value compressor using the unsafe LZ4 instance for maximum performance.
     *
     * @param delegate codec used for key-value encoding/decoding, must not be {@code null}.
     * @param <K>      Key type.
     * @param <V>      Value type.
     * @return Value-compressing codec using unsafe LZ4.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <K, V> RedisCodec<K, V> unsafeInstance(RedisCodec<K, V> delegate) {
        LettuceAssert.notNull(delegate, "RedisCodec must not be null");
        LZ4Factory factory = LZ4Factory.unsafeInstance();
        return (RedisCodec) new LZ4CompressingCodec((RedisCodec) delegate, factory.fastCompressor(), factory.fastDecompressor());
    }
}
