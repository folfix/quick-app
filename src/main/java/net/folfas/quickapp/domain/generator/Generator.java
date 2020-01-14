package net.folfas.quickapp.domain.generator;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.folfas.quickapp.domain.Filesystem;
import net.folfas.quickapp.domain.generator.descriptor.Descriptor;
import net.folfas.quickapp.domain.generator.descriptor.DescriptorDestinationFile;
import net.folfas.quickapp.domain.publish.PublishGitVersionControl;
import net.lingala.zip4j.ZipFile;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Generator {

    private final MustacheConfig mustacheConfig;
    private final Filesystem filesystem;
    private final ZipGenerator zipGenerator;
    private final ResultsStorage resultsStorage;
    private final PublishGitVersionControl versionControl;

    public GeneratorResult generate(Descriptor descriptor) throws IOException {
        File generatedProjectDestination = filesystem.createTempDirectory();

        log.info("Generating project...");
        log.info("Temp directory for generating project is: file:///{}", generatedProjectDestination);

        for (DescriptorDestinationFile destinationFile : descriptor.getDescriptorDestinationFiles()) {
            File fileToSave = new File(generatedProjectDestination, destinationFile.getDestinationPath());
            FileUtils.mkdirs(fileToSave.getParentFile(), true);

            if (destinationFile.isToCopy()) {
                Files.copy(destinationFile.getSourceFile().toPath(), fileToSave.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } else {
                FileUtils.createNewFile(fileToSave);
                Writer destinationWriter = new FileWriter(fileToSave);
                MustacheFactory mf = mustacheConfig.mustacheFactory();
                Reader sourceReader = new FileReader(destinationFile.getSourceFile());
                Mustache mustache = mf.compile(sourceReader, "generator");
                mustache.execute(destinationWriter, descriptor.getMustacheModel());
                destinationWriter.flush();
            }
        }
        ZipFile zipped = zipGenerator.createZipFromDirectory(generatedProjectDestination);
        Optional.ofNullable(descriptor.getGitPushRepository()).ifPresent(gitPushRepository -> {
            log.info("Pushing repository to {}", gitPushRepository.getUrl());
            versionControl.pushRepository(gitPushRepository, generatedProjectDestination);
        });

        return resultsStorage.put(generatedProjectDestination, zipped, descriptor);
    }
}
