package org.apache.commons.io.monitor;

import java.io.File;

public class FileAlterationListenerAdaptor implements FileAlterationListener {

    public FileAlterationListenerAdaptor() {}

    @Override
    public void onStart(FileAlterationObserver filealterationobserver) {}

    @Override
    public void onDirectoryCreate(File file) {}

    @Override
    public void onDirectoryChange(File file) {}

    @Override
    public void onDirectoryDelete(File file) {}

    @Override
    public void onFileCreate(File file) {}

    @Override
    public void onFileChange(File file) {}

    @Override
    public void onFileDelete(File file) {}

    @Override
    public void onStop(FileAlterationObserver filealterationobserver) {}
}
