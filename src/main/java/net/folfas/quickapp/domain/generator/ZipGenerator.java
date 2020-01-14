package net.folfas.quickapp.domain.generator;

import java.io.File;
import net.lingala.zip4j.ZipFile;

public interface ZipGenerator {

    ZipFile createZipFromDirectory(File directory);
}
