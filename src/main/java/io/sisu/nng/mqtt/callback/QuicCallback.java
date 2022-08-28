package io.sisu.nng.mqtt.callback;

import com.sun.jna.Pointer;
import io.sisu.nng.Message;
import io.sisu.nng.internal.NngMsgCallback;

import java.util.function.BiFunction;

public class QuicCallback<T, U, R> implements NngMsgCallback {

    private T arg = null;

    private Message msg;
    private BiFunction<Message, T, Integer> function;

    public QuicCallback() {

    }

    public QuicCallback(BiFunction<Message, T, Integer> function) {
        this.function = function;
    }

    public QuicCallback(BiFunction<Message, T, Integer> function, T arg) {
        this.arg = arg;
        this.function = function;
    }

    public T getArg() {
        return arg;
    }

    public void setArg(T arg) {
        this.arg = arg;
    }

    @Override
    public int callback(Pointer p1, Pointer p2) {
        try {
            return this.function.apply(p1 == Pointer.NULL ? null : new Message(p1), this.arg);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
        return 0;
    }
}