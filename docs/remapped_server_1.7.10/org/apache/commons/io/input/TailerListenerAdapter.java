package org.apache.commons.io.input;

public class TailerListenerAdapter implements TailerListener {

    public TailerListenerAdapter() {}

    @Override
    public void init(Tailer tailer) {}

    @Override
    public void fileNotFound() {}

    @Override
    public void fileRotated() {}

    @Override
    public void handle(String s) {}

    @Override
    public void handle(Exception exception) {}
}
