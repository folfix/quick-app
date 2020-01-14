package net.folfas.quickapp.integration.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.domain.Filesystem;
import net.folfas.quickapp.domain.generator.ZipGenerator;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
class FilesystemManager implements Filesystem, ZipGenerator {

    private File templateDirectory;

    @Override
    public File createTemplateDirectory() {
        String templateDirectoryName = "quick-app-templates-" + UUID.randomUUID().toString();
        templateDirectory = createTempDir(templateDirectoryName);
        return templateDirectory;
    }

    @Override
    public File getTemplateDirectory() {
        return templateDirectory;
    }

    @Override
    public List<File> getTemplateDirectoryDirectories() {
        File[] allFiles = this.templateDirectory.listFiles();
        return Arrays.stream(Objects.requireNonNull(allFiles))
            .filter(File::exists)
            .filter(File::isDirectory)
            .collect(Collectors.toList());
    }

    @Override
    public File createTempDirectory() {
        String tempDirectoryName = "quick-app-generator-" + UUID.randomUUID().toString();
        return createTempDir(tempDirectoryName);
    }

    @Override
    public ZipFile createZipFromDirectory(File directory) {
        Assert.isTrue(directory.isDirectory(), "Given file is not directory");
        File tempDirectory = createTempDirectory();
        File destinationFile = new File(tempDirectory, "project.zip");
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setIncludeRootFolder(false);
            ZipFile zipFile = new ZipFile(destinationFile);
            zipFile.addFolder(directory, zipParameters);
            log.info("Created ZIP file: file:///{}", zipFile.getFile().getAbsolutePath());
            return zipFile;
        } catch (ZipException e) {
            throw new RuntimeException("Failed to create ZIP", e);
        }
    }

    private File createTempDir(String tempDirPath) {
        try {
            return Files.createTempDirectory(tempDirPath).toFile();
        } catch (IOException e) {
            log.error("Failed to create temp directory", e);
            String alternativeTempDir = "/tmp/" + tempDirPath;
            boolean created = new File(alternativeTempDir).mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create alternative temp dir!");
            }
            return Paths.get(alternativeTempDir).toFile();
        }
    }

    public void deleteDirectory(File directory) {
        try {
            FileUtils.forceDelete(directory);
            log.info("Deleted directory: {}", directory.getPath());
        } catch (IOException e) {
            log.warn("Failed to remove directory: {}", directory.getPath());
        }
    }
}
