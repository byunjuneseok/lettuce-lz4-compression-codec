# Lettuce LZ4 Compression Codec

LZ4 compression codec for Lettuce Redis Client that provides transparent value compression.

[![Maven Central](https://img.shields.io/maven-central/v/com.binaryflavor/lettuce-lz4-compression-codec)](https://central.sonatype.com/artifact/com.binaryflavor/lettuce-lz4-compression-codec/overview)
[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://byunjuneseok.github.io/lettuce-lz4-compression-codec/javadoc)

## Installation

Add the dependency to your project:

### Gradle

```gradle
implementation 'com.binaryflavor:lettuce-lz4-compression-codec:1.0.0'
```

### Maven

```xml

<dependency>
    <groupId>com.binaryflavor</groupId>
    <artifactId>lettuce-lz4-compression-codec</artifactId>
    <version>1.0.0</version>
</dependency>
```

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

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
