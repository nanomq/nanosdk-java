/**
 * Asynchronous primitives for scaling Java NNG
 *
 * <p>
 * This package provides all the bindings and logic required for utilizing {@link io.sisu.nng.Socket}s
 * in a thread-safe, concurrent fashion through three different APIs:
 *
 * <ul>
 *     <li>blocking, synchronous calls for receive, send, and sleep events on a
 *     {@link io.sisu.nng.aio.Context}</li>
 *     <li>a {@link java.util.concurrent.CompletableFuture}-based asynchronous api for the same</li>
 *     <li>a callback-oriented, event-based approach using {@link io.sisu.nng.aio.AioCallback}</li>
 * </ul>
 *
 * <p>
 * Concurrency is achieved via the underlying nng library's
 * <a href="https://nng.nanomsg.org/man/tip/nng_aio.5.html">aio framework</a> meaning you can build
 * highly concurrent applications using only a fraction of Java's concurrency capabilities (like
 * {@link java.util.concurrent.CompletableFuture}s or {@link java.util.concurrent.CountDownLatch}es).
 */
package io.sisu.nng.mqtt;