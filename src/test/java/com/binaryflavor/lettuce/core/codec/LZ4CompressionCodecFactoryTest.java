package com.binaryflavor.lettuce.core.codec;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.jpountz.lz4.LZ4Factory;

import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

@DisplayName("LZ4CompressionCodecFactory")
class LZ4CompressionCodecFactoryTest {

    private RedisCodec<String, String> stringCodec;
    private RedisCodec<String, byte[]> byteArrayCodec;

    @BeforeEach
    void setUp() {
        stringCodec = RedisCodec.of(StringCodec.UTF8, StringCodec.UTF8);
        byteArrayCodec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
    }

    @Nested
    @DisplayName("Factory Method: fastest")
    class FastestFactoryTests {

        @Test
        @DisplayName("should create codec using fastest LZ4 instance")
        void shouldCreateCodecUsingFastestLZ4Instance() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);

            assertNotNull(codec);
            assertInstanceOf(LZ4CompressingCodec.class, codec);
        }

        @Test
        @DisplayName("should compress and decompress values correctly")
        void shouldCompressAndDecompressValuesCorrectly() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);
            String originalValue = "Test value for fastest factory";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when delegate is null")
        void shouldThrowIllegalArgumentExceptionWhenDelegateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                LZ4CompressionCodecFactory.fastest(null));
        }

        @Test
        @DisplayName("should preserve key operations from delegate")
        void shouldPreserveKeyOperationsFromDelegate() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);
            String key = "test-key";

            ByteBuffer encodedKey = codec.encodeKey(key);
            ByteBuffer expectedKey = stringCodec.encodeKey(key);

            assertEquals(expectedKey, encodedKey);
        }
    }

    @Nested
    @DisplayName("Factory Method: safest")
    class SafestFactoryTests {

        @Test
        @DisplayName("should create codec using safest LZ4 instance")
        void shouldCreateCodecUsingSafestLZ4Instance() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.safest(stringCodec);

            assertNotNull(codec);
            assertInstanceOf(LZ4CompressingCodec.class, codec);
        }

        @Test
        @DisplayName("should compress and decompress values correctly")
        void shouldCompressAndDecompressValuesCorrectly() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.safest(stringCodec);
            String originalValue = "Test value for safest factory";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when delegate is null")
        void shouldThrowIllegalArgumentExceptionWhenDelegateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                LZ4CompressionCodecFactory.safest(null));
        }

        @Test
        @DisplayName("should work with byte array values")
        void shouldWorkWithByteArrayValues() {
            RedisCodec<String, byte[]> codec = LZ4CompressionCodecFactory.safest(byteArrayCodec);
            byte[] originalValue = "Test byte array for safest factory".getBytes();

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertArrayEquals(originalValue, (byte[]) decompressed);
        }
    }

    @Nested
    @DisplayName("Factory Method: nativeInstan ce")
    class NativeInstanceFactoryTests {

        @Test
        @DisplayName("should create codec using native LZ4 instance")
        void shouldCreateCodecUsingNativeLZ4Instance() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.nativeInstance(stringCodec);

            assertNotNull(codec);
            assertInstanceOf(LZ4CompressingCodec.class, codec);
        }

        @Test
        @DisplayName("should compress and decompress values correctly")
        void shouldCompressAndDecompressValuesCorrectly() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.nativeInstance(stringCodec);
            String originalValue = "Test value for native instance factory";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when delegate is null")
        void shouldThrowIllegalArgumentExceptionWhenDelegateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                LZ4CompressionCodecFactory.nativeInstance(null));
        }

        @Test
        @DisplayName("should handle large data sets")
        void shouldHandleLargeDataSets() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.nativeInstance(stringCodec);

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 10000; i++) {
                sb.append("Line ").append(i).append(" of test data. ");
            }
            String originalValue = sb.toString();

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }
    }

    @Nested
    @DisplayName("Factory Method: unsafeInstance")
    class UnsafeInstanceFactoryTests {

        @Test
        @DisplayName("should create codec using unsafe LZ4 instance")
        void shouldCreateCodecUsingUnsafeLZ4Instance() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.unsafeInstance(stringCodec);

            assertNotNull(codec);
            assertInstanceOf(LZ4CompressingCodec.class, codec);
        }

        @Test
        @DisplayName("should compress and decompress values correctly")
        void shouldCompressAndDecompressValuesCorrectly() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.unsafeInstance(stringCodec);
            String originalValue = "Test value for unsafe instance factory";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when delegate is null")
        void shouldThrowIllegalArgumentExceptionWhenDelegateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                LZ4CompressionCodecFactory.unsafeInstance(null));
        }

        @Test
        @DisplayName("should maintain performance with repetitive operations")
        void shouldMaintainPerformanceWithRepetitiveOperations() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.unsafeInstance(stringCodec);
            String originalValue = "Repetitive test value";

            // Perform multiple encode/decode cycles
            for(int i = 0; i < 1000; i++) {
                ByteBuffer compressed = codec.encodeValue(originalValue + i);
                Object decompressed = codec.decodeValue(compressed);
                assertEquals(originalValue + i, decompressed);
            }
        }
    }

    @Nested
    @DisplayName("Factory Method: custom")
    class CustomFactoryTests {

        @Test
        @DisplayName("should work with fastest instance factory")
        void shouldWorkWithFastestInstanceFactory() {
            LZ4Factory customFactory = LZ4Factory.fastestInstance();
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);

            assertNotNull(codec);
            String originalValue = "Test value for fastest factory";

            ByteBuffer compressed = codec.encodeValue(originalValue);
            Object decompressed = codec.decodeValue(compressed);

            assertEquals(originalValue, decompressed);
        }

        @Test
        @DisplayName("should work with all available factory types")
        void shouldWorkWithAllAvailableFactoryTypes() {
            String originalValue = "Test value for multiple factory types";

            // Test all available factory methods
            RedisCodec<String, String>[] codecs = new RedisCodec[] {
                LZ4CompressionCodecFactory.fastest(stringCodec),
                LZ4CompressionCodecFactory.safest(stringCodec),
                LZ4CompressionCodecFactory.nativeInstance(stringCodec),
                LZ4CompressionCodecFactory.unsafeInstance(stringCodec)
            };

            for(int i = 0; i < codecs.length; i++) {
                RedisCodec<String, String> codec = codecs[i];

                ByteBuffer compressed = codec.encodeValue(originalValue);
                Object decompressed = codec.decodeValue(compressed);

                assertEquals(originalValue, decompressed,
                    "Failed with codec index: " + i);
            }
        }
    }

    @Nested
    @DisplayName("Factory Method Comparison")
    class FactoryComparisonTests {

        @Test
        @DisplayName("should produce compatible codecs across factory methods")
        void shouldProduceCompatibleCodecsAcrossFactoryMethods() {
            String originalValue = "Cross-factory compatibility test value";

            // Create codecs using different factory methods
            RedisCodec<String, String> fastestCodec = LZ4CompressionCodecFactory.fastest(stringCodec);
            RedisCodec<String, String> safestCodec = LZ4CompressionCodecFactory.safest(stringCodec);
            RedisCodec<String, String> nativeCodec = LZ4CompressionCodecFactory.nativeInstance(stringCodec);
            RedisCodec<String, String> unsafeCodec = LZ4CompressionCodecFactory.unsafeInstance(stringCodec);

            // Test that all can compress the same value
            ByteBuffer compressedByFastest = fastestCodec.encodeValue(originalValue);
            ByteBuffer compressedBySafest = safestCodec.encodeValue(originalValue);
            ByteBuffer compressedByNative = nativeCodec.encodeValue(originalValue);
            ByteBuffer compressedByUnsafe = unsafeCodec.encodeValue(originalValue);

            // Test that all can decompress their own compressed values
            assertEquals(originalValue, fastestCodec.decodeValue(compressedByFastest));
            assertEquals(originalValue, safestCodec.decodeValue(compressedBySafest));
            assertEquals(originalValue, nativeCodec.decodeValue(compressedByNative));
            assertEquals(originalValue, unsafeCodec.decodeValue(compressedByUnsafe));
        }

        @Test
        @DisplayName("should handle empty values consistently across factory methods")
        void shouldHandleEmptyValuesConsistentlyAcrossFactoryMethods() {
            String emptyValue = "";

            RedisCodec<String, String> fastestCodec = LZ4CompressionCodecFactory.fastest(stringCodec);
            RedisCodec<String, String> safestCodec = LZ4CompressionCodecFactory.safest(stringCodec);

            ByteBuffer compressedByFastest = fastestCodec.encodeValue(emptyValue);
            ByteBuffer compressedBySafest = safestCodec.encodeValue(emptyValue);

            assertEquals(emptyValue, fastestCodec.decodeValue(compressedByFastest));
            assertEquals(emptyValue, safestCodec.decodeValue(compressedBySafest));
        }

        @Test
        @DisplayName("should preserve type safety across all factory methods")
        void shouldPreserveTypeSafetyAcrossAllFactoryMethods() {
            // Test with different generic types
            RedisCodec<String, String> stringStringCodec = LZ4CompressionCodecFactory.fastest(stringCodec);
            RedisCodec<String, byte[]> stringByteArrayCodec = LZ4CompressionCodecFactory.safest(byteArrayCodec);

            assertNotNull(stringStringCodec);
            assertNotNull(stringByteArrayCodec);

            // Verify they work with their respective types
            String stringValue = "String test";
            byte[] byteArrayValue = "Byte array test".getBytes();

            ByteBuffer compressedString = stringStringCodec.encodeValue(stringValue);
            ByteBuffer compressedByteArray = stringByteArrayCodec.encodeValue(byteArrayValue);

            assertEquals(stringValue, stringStringCodec.decodeValue(compressedString));
            assertArrayEquals(byteArrayValue, (byte[]) stringByteArrayCodec.decodeValue(compressedByteArray));
        }
    }

    @Nested
    @DisplayName("Performance and Integration Tests")
    class PerformanceAndIntegrationTests {

        @Test
        @DisplayName("should handle concurrent access safely")
        void shouldHandleConcurrentAccessSafely() throws InterruptedException {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);
            String baseValue = "Concurrent test value";
            int threadCount = 10;
            int operationsPerThread = 100;

            Thread[] threads = new Thread[threadCount];
            for(int t = 0; t < threadCount; t++) {
                final int threadId = t;
                threads[t] = new Thread(() -> {
                    for(int i = 0; i < operationsPerThread; i++) {
                        String value = baseValue + "_thread" + threadId + "_op" + i;
                        ByteBuffer compressed = codec.encodeValue(value);
                        Object decompressed = codec.decodeValue(compressed);
                        assertEquals(value, decompressed);
                    }
                });
            }

            // Start all threads
            for(Thread thread : threads) {
                thread.start();
            }

            // Wait for all threads to complete
            for(Thread thread : threads) {
                thread.join(5000); // 5 second timeout
            }

            // Verify all threads completed successfully
            for(Thread thread : threads) {
                assertFalse(thread.isAlive(), "Thread should have completed");
            }
        }

        @Test
        @DisplayName("should maintain compression ratio benefits")
        void shouldMaintainCompressionRatioBenefits() {
            RedisCodec<String, String> codec = LZ4CompressionCodecFactory.fastest(stringCodec);

            // Create highly repetitive content that should compress well
            StringBuilder sb = new StringBuilder();
            String pattern = "This is a highly repetitive pattern that should compress very well. ";
            for(int i = 0; i < 200; i++) {
                sb.append(pattern);
            }
            String originalValue = sb.toString();

            ByteBuffer compressed = codec.encodeValue(originalValue);
            ByteBuffer uncompressed = stringCodec.encodeValue(originalValue);

            // Should achieve significant compression
            double compressionRatio = (double) compressed.remaining() / uncompressed.remaining();
            assertTrue(compressionRatio < 0.5,
                "Should achieve at least 50% compression on repetitive data, got: " +
                    (compressionRatio * 100) + "%");

            // Verify data integrity
            assertEquals(originalValue, codec.decodeValue(compressed));
        }
    }
}
