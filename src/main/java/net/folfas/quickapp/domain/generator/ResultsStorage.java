package net.folfas.quickapp.domain.generator;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.folfas.quickapp.domain.generator.descriptor.Descriptor;
import net.lingala.zip4j.ZipFile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ResultsStorage {

    private final Map<String, GeneratorResult> generatedProjects = new ConcurrentHashMap<>();

    GeneratorResult put(File generatedDirectory, ZipFile zipFile, Descriptor descriptor) {
        String resultId = UUID.randomUUID().toString().trim().intern();
        GeneratorResult generatorResult = new GeneratorResult(resultId, generatedDirectory, zipFile, descriptor);
        generatedProjects.put(resultId, generatorResult);
        return generatorResult;
    }

    public GeneratorResult get(String id) {
        GeneratorResult generatorResult = generatedProjects.get(id.trim().intern());
        Assert.notNull(generatorResult, "Generated project not found, id: " + id);
        return generatorResult;
    }
}
