# Lettuce LZ4 Compression Codec

A high-performance LZ4 compression codec for Lettuce Redis Client that provides transparent value compression.

## Usage

```java
// Create LZ4 compressed codec
RedisCodec<String, String> baseCodec = RedisCodec.of(StringCodec.UTF8, StringCodec.UTF8);
RedisCodec<String, String> lz4Codec = LZ4CompressionCodecFactory.fastest(baseCodec);

// Use with Redis connection
RedisClient client = RedisClient.create("redis://localhost:6379");
StatefulRedisConnection<String, String> connection = client.connect(lz4Codec);
```

## Factory Methods

- `LZ4CompressionCodecFactory.fastest(codec)` - Recommended for best performance
- `LZ4CompressionCodecFactory.safest(codec)` - Pure Java implementation
- `LZ4CompressionCodecFactory.nativeInstance(codec)` - JNI-based implementation
- `LZ4CompressionCodecFactory.unsafeInstance(codec)` - Maximum performance
