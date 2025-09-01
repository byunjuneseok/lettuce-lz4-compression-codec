package com.binaryflavor.lettuce.core.codec;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

@DisplayName("LZ4CompressingCodec")
class LZ4CompressingCodecTest {

    private RedisCodec<String, String> stringCodec;
    private RedisCodec<String, byte[]> byteArrayCodec;
    private LZ4Factory lz4Factory;
    private LZ4Compressor compressor;
    private LZ4FastDecompressor decompressor;

    @BeforeEach
    void setUp() {
        stringCodec = RedisCodec.of(StringCodec.UTF8, StringCodec.UTF8);
        byteArrayCodec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
        lz4Factory = LZ4Factory.fastestInstance();
        compressor = lz4Factory.fastCompressor();
        decompressor = lz4Factory.fastDecompressor();
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Test
        @DisplayName("should create codec with valid parameters")
        void shouldCreateCodecWithValidParameters() {
            assertDoesNotThrow(() ->
                new LZ4CompressingCodec((RedisCodec) stringCodec, compressor, decompressor));
        }

        @Test
        @DisplayName("should throw when delegate is null")
        void shouldThrowWhenDelegateIsNull() {
            assertThrows(NullPointerException.class, () ->
                new LZ4CompressingCodec(null, compressor, decompressor));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Test
        @DisplayName("should throw when compressor is null")
        void shouldThrowWhenCompressorIsNull() {
            assertThrows(NullPointerException.class, () ->
                new LZ4CompressingCodec((RedisCodec) stringCodec, null, decompressor));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Test
        @DisplayName("should throw when decompressor is null")
        void shouldThrowWhenDecompressorIsNull() {
            assertThrows(NullPointerException.class, () ->
                new LZ4CompressingCodec((RedisCodec) stringCodec, compressor, null));
        }
    }

    @Nested
    @DisplayName("Key Encoding/Decoding")
    class KeyTests {

        private RedisCodec<String, String> codec;

        @BeforeEach
        void setUp() {
            codec = LZ4CompressionCodecFactory.fastest(stringCodec);
        }

        @Test
        @DisplayName("should pass through key encoding unchanged")
        void shouldPassThroughKeyEncodingUnchanged() {
            String key = "test-key";
            ByteBuffer encoded = codec.encodeKey(key);
            ByteBuffer expected = stringCodec.encodeKey(key);

            assertEquals(expected, encoded);
        }

        @Test
        @DisplayName("should pass through key decoding unchanged")
        void shouldPassThroughKeyDecodingUnchanged() {
            String originalKey = "test-key";
            ByteBuffer encodedKey = stringCodec.encodeKey(originalKey);

            Object decodedKey = codec.decodeKey(encodedKey);
            Object expectedKey = stringCodec.decodeKey(encodedKey.duplicate());

            assertEquals(expectedKey, decodedKey);
        }

        @Test
        @DisplayName("should handle null key")
        void shouldHandleNullKey() {
            assertDoesNotThrow(() -> {
                ByteBuffer encoded = codec.encodeKey(null);
                codec.decodeKey(encoded);
            });
        }
    }

    @Nested
    @DisplayName("Value Compression/Decompression")
    class ValueCompressionTests {

        private RedisCodec<String, String> codec;

        @BeforeEach
        void setUp() {
            codec = LZ4CompressionCodecFactory.fastest(stringCodec);
        }

        @Test
        @DisplayName("should compress and decompress string values correctly")
        void shouldCompressAndDecompressStringValuesCorrectly() {
            String originalValue = "This is a test string that should be compressed by LZ4";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should compress and decompress large strings")
        void shouldCompressAndDecompressLargeStrings() {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 1000; i++) {
                sb.append("This is line ").append(i).append(" of a very long string. ");
            }
            String originalValue = sb.toString();

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);

            // Verify compression actually happened
            ByteBuffer uncompressed = stringCodec.encodeValue(originalValue);
            assertTrue(compressed.remaining() < uncompressed.remaining(),
                "Compressed data should be smaller than original");
        }

        @Test
        @DisplayName("should handle empty string values")
        void shouldHandleEmptyStringValues() {
            String originalValue = "";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should handle null values")
        void shouldHandleNullValues() {
            assertDoesNotThrow(() -> {
                ByteBuffer compressed = codec.encodeValue(null);
                codec.decodeValue(compressed);
            });
        }

        @Test
        @DisplayName("should handle empty ByteBuffer")
        void shouldHandleEmptyByteBuffer() {
            ByteBuffer emptyBuffer = ByteBuffer.allocate(0);

            Object result = codec.decodeValue(emptyBuffer);
            Object expected = stringCodec.decodeValue(ByteBuffer.allocate(0));

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("should preserve data integrity with different character sets")
        void shouldPreserveDataIntegrityWithDifferentCharacterSets() {
            String originalValue = "Hello 世界! 🌍 Café naïve résumé";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }
    }

    @Nested
    @DisplayName("Byte Array Values")
    class ByteArrayValueTests {

        private RedisCodec<String, byte[]> codec;

        @BeforeEach
        void setUp() {
            codec = LZ4CompressionCodecFactory.fastest(byteArrayCodec);
        }

        @Test
        @DisplayName("should compress and decompress byte array values")
        void shouldCompressAndDecompressByteArrayValues() {
            byte[] originalValue = "This is a test byte array".getBytes(StandardCharsets.UTF_8);

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertArrayEquals(originalValue, (byte[]) decompressed);
        }

        @Test
        @DisplayName("should handle binary data")
        void shouldHandleBinaryData() {
            byte[] originalValue = new byte[256];
            for(int i = 0; i < 256; i++) {
                originalValue[i] = (byte) i;
            }

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertArrayEquals(originalValue, (byte[]) decompressed);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        private RedisCodec<String, String> codec;

        @BeforeEach
        void setUp() {
            codec = LZ4CompressionCodecFactory.fastest(stringCodec);
        }

        @Test
        @DisplayName("should throw RuntimeException on decompression failure")
        void shouldThrowRuntimeExceptionOnDecompressionFailure() {
            // Create invalid compressed data
            ByteBuffer invalidData = ByteBuffer.allocate(8);
            invalidData.putInt(100); // Original length
            invalidData.put(new byte[] {1, 2, 3, 4}); // Invalid compressed data
            invalidData.flip();

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                codec.decodeValue(invalidData));

            assertEquals("Failed to decompress value", exception.getMessage());
        }

        @Test
        @DisplayName("should throw RuntimeException on compression failure")
        void shouldThrowRuntimeExceptionOnCompressionFailure() {
            // This test uses a mock scenario where compression might fail
            // In practice, LZ4 compression rarely fails with valid input
            RedisCodec<String, String> throwingCodec = new RedisCodec<>() {
                @Override
                public String decodeKey(ByteBuffer bytes) {
                    return null;
                }

                @Override
                public String decodeValue(ByteBuffer bytes) {
                    return null;
                }

                @Override
                public ByteBuffer encodeKey(String key) {
                    return ByteBuffer.allocate(0);
                }

                @Override
                public ByteBuffer encodeValue(String value) {
                    throw new RuntimeException("Delegate encoding failed");
                }
            };

            @SuppressWarnings({"rawtypes", "unchecked"})
            RedisCodec<String, String> faultyCodec = (RedisCodec) new LZ4CompressingCodec((RedisCodec) throwingCodec, compressor, decompressor);

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                faultyCodec.encodeValue("test"));

            assertEquals("Delegate encoding failed", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Different LZ4 Factory Types")
    class FactoryTypeTests {

        @Test
        @DisplayName("should work with safe instance")
        void shouldWorkWithSafeInstance() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.safest(stringCodec);

            String originalValue = "Test with safe factory";
            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should work with unsafe instance")
        void shouldWorkWithUnsafeInstance() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.unsafeInstance(stringCodec);

            String originalValue = "Test with unsafe factory";
            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should work with high compressor")
        void shouldWorkWithHighCompressor() {
            LZ4Factory factory = LZ4Factory.fastestInstance();
            @SuppressWarnings({"rawtypes", "unchecked"})
            RedisCodec<String, String> codec = (RedisCodec) new LZ4CompressingCodec(
                (RedisCodec) stringCodec,
                factory.highCompressor(),
                factory.fastDecompressor()
            );

            String originalValue = "Test with high compression ratio compressor";
            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }
    }

    @Nested
    @DisplayName("Compression Efficiency")
    class CompressionEfficiencyTests {

        @Test
        @DisplayName("should achieve compression on repetitive data")
        void shouldAchieveCompressionOnRepetitiveData() {
            String pattern = "This is a repetitive pattern. ";
            String originalValue = pattern.repeat(100);

            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);

            ByteBuffer compressed = codec.encodeValue(originalValue);
            ByteBuffer uncompressed = stringCodec.encodeValue(originalValue);

            // Account for the 4-byte length header
            int compressionOverhead = 4;
            assertTrue(compressed.remaining() < uncompressed.remaining() - compressionOverhead,
                "Compressed data should be smaller than original for repetitive content");
        }

        @Test
        @DisplayName("should handle non-compressible data gracefully")
        void shouldHandleNonCompressibleDataGracefully() {
            // Generate deterministic pseudo-random data using printable ASCII characters
            StringBuilder sb = new StringBuilder();
            java.util.Random rand = new java.util.Random(12345); // Fixed seed for deterministic test
            for(int i = 0; i < 1000; i++) {
                // Use printable ASCII characters (32-126) to avoid encoding issues
                sb.append((char) (32 + rand.nextInt(95)));
            }
            String originalValue = sb.toString();

            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
            // Don't assert compression ratio for random data as it may not compress
        }
    }
}
