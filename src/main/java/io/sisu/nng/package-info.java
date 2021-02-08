/**
 * Java NNG - a (somewhat) Java idiomatic way for using NNG
 *
 * <p>
 * java-nng provides high-level primitives for utilizing nng in common distributed messaging with a
 * focus on:
 * <ul>
 *     <li>Foundational brokerless messaging via core nng objects like {@link io.sisu.nng.Socket}s
 *     and {@link io.sisu.nng.aio.Context}s</li>
 *     <li>Scalable, thread-safe concurrency via {@link io.sisu.nng.aio.Aio} handles</li>
 *     <li>Durable integration, only utilizing the public (nng_*) api of nng</li>
 *     <li>Memory safety when using nng objects allocated by nng itself</li>
 * </ul>
 * <p>
 * What java-nng is not:
 * <ul>
 *     <li>a pure Java implementation of the Scalability Protocols</li>
 *     <li>a <a href="https://github.com/nativelibs4java/JNAerator">JNAerator</a> blindly generated
 *     code mess</li>
 * </ul>
 */
package io.sisu.nng;