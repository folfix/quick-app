package net.folfas.quickapp.api.intellij;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.generator.GeneratorResult;
import net.folfas.quickapp.domain.generator.ResultsStorage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/intellij")
@RequiredArgsConstructor
public class StaticIntellijController {

    private final ResourceLoader resourceLoader;
    private final ResultsStorage resultsStorage;

    @GetMapping(value = "/{resultId}", produces = {"application/vnd.initializr.v2.1+json", "application/json"})
    public String getResponse(@PathVariable String resultId) {
        return IntellijStaticResponse.getResponse();
    }

    @GetMapping("/{resultId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("resultId") String resultId) throws IOException {
        GeneratorResult result = resultsStorage.get(resultId);
        Assert.notNull(result, "Unknown generator result id");
        Resource resource = resourceLoader.getResource("file:///" + result.getZipFile().getFile().getAbsolutePath());
        return upload(resource.getFile().toPath());
    }

    private ResponseEntity<byte[]> upload(Path archive)
        throws IOException {
        byte[] bytes = Files.readAllBytes(archive);
        return createResponseEntity(bytes);
    }

    private ResponseEntity<byte[]> createResponseEntity(byte[] content) {
        String contentDispositionValue = "attachment; filename=\"" + "starter.zip" + "\"";
        return ResponseEntity.ok().header("Content-Type", "application/zip")
            .header("Content-Disposition", contentDispositionValue).body(content);
    }
}
