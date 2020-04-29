package net.folfas.quickapp.domain.templates;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.domain.templates.manifest.Manifest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateFactory {

    private final ObjectMapper objectMapper;

    public TemplateFactory() {
        this.objectMapper = getRestrictedObjectMapper();
    }

    boolean isValidTemplate(File templateDirectory) {
        File[] filesInTemplate = templateDirectory.listFiles();
        Assert.notNull(filesInTemplate, "There are no files in template");
        boolean isValidTemplate = Arrays.stream(filesInTemplate)
                .anyMatch(file -> Manifest.MANIFEST_FILENAME.equals(file.getName()));
        if (!isValidTemplate) {
            log.warn("Found not valid template directory: {}", templateDirectory.getName());
        }
        return isValidTemplate;
    }

    Template getTemplateFromTemplateDirectory(File templateDirectory) {
        File[] filesInTemplate = templateDirectory.listFiles();
        Assert.notNull(filesInTemplate, "There are no files in template");
        File manifestFile = Arrays.stream(filesInTemplate)
            .filter(file -> Manifest.MANIFEST_FILENAME.equals(file.getName()))
            .findAny().orElseThrow(() -> new RuntimeException("Failed to find manifest file in template: " + templateDirectory.getPath()));

        Manifest manifest = parseManifest(manifestFile);
        manifest.validate();
        return new Template(
            templateDirectory.getName(),
            templateDirectory,
            manifest
        );
    }

    private Manifest parseManifest(File manifestFile) {
        try {
            return objectMapper.readValue(new FileInputStream(manifestFile), Manifest.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse manifest file: " + manifestFile.getAbsolutePath(), e);
        }
    }

    private ObjectMapper getRestrictedObjectMapper() {
        return new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
