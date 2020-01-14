package net.folfas.quickapp.domain.generator.descriptor;

import java.io.File;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.folfas.quickapp.domain.templates.Template;

@Value
public class DescriptorDestinationFile {

    private File sourceFile;
    private String destinationPath;
    private boolean isToCopy;

    DescriptorDestinationFile(Path sourceFilePath, Descriptor descriptor, Template template) {
        this.sourceFile = sourceFilePath.toFile();
        String directorySeparator = "/";

        int slashLength = 1;
        int basePathLength = template.getTemplateDirectory().getAbsolutePath().length() + slashLength;
        String relativeDestinationFilename = sourceFilePath.toString().substring(basePathLength);
        this.isToCopy = template.getManifest().getJustCopy().stream().anyMatch(justToCopy -> {
            if (justToCopy.endsWith(directorySeparator) && relativeDestinationFilename.startsWith(justToCopy)) {
                return true;
            } else {
                return justToCopy.equals(relativeDestinationFilename);
            }
        });

        FilenameWrapper wrapper = new FilenameWrapper(relativeDestinationFilename);
        descriptor.getVariables().forEach(variable -> {
            String inputVariableKey = variable.getId();
            String inputVariableValue = variable.getValue();

            template.getManifest().getPathsOverrides().forEach(manifestDirectoryPath -> {
                String manifestVariableKey = manifestDirectoryPath.getFromVariable();
                if (inputVariableKey.equals(manifestVariableKey)) {
                    String result = inputVariableValue;
                    if (manifestDirectoryPath.hasSeparator()) {
                        String manifestVariableValueReplaceSeparator = manifestDirectoryPath.getDirectoriesSeparator();
                        result = inputVariableValue.replace(manifestVariableValueReplaceSeparator, directorySeparator);
                    }
                    wrapper.filename = wrapper.filename.replace(inputVariableKey, result);
                }
            });
        });
        this.destinationPath = wrapper.filename;
    }

    @AllArgsConstructor
    private class FilenameWrapper {
        String filename;
    }
}