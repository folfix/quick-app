package net.folfas.quickapp.domain;

import java.io.File;
import java.util.List;

public interface Filesystem {

    File createTemplateDirectory();

    File getTemplateDirectory();

    List<File> getTemplateDirectoryDirectories();

    File createTempDirectory();
}
