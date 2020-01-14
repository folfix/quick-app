package net.folfas.quickapp.domain.generator;

import java.io.File;
import lombok.Value;
import net.folfas.quickapp.domain.generator.descriptor.Descriptor;
import net.lingala.zip4j.ZipFile;

@Value
public class GeneratorResult {

    String resultId;
    File generatedDirectory;
    ZipFile zipFile;
    Descriptor descriptor;

    GeneratorResult(String resultId, File generatedDirectory, ZipFile zipFile, Descriptor descriptor) {
        this.resultId = resultId;
        this.generatedDirectory = generatedDirectory;
        this.zipFile = zipFile;
        this.descriptor = descriptor;
    }
}
