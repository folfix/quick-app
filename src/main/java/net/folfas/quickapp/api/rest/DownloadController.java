package net.folfas.quickapp.api.rest;

import lombok.RequiredArgsConstructor;
import net.folfas.quickapp.domain.generator.GeneratorResult;
import net.folfas.quickapp.domain.generator.ResultsStorage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest/api/download")
@RequiredArgsConstructor
public class DownloadController {

    private final ResourceLoader resourceLoader;
    private final ResultsStorage resultsStorage;

    @GetMapping("/{resultId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("resultId") String resultId) {
        GeneratorResult result = resultsStorage.get(resultId);
        Assert.notNull(result, "Unknown generator result id");
        Resource resource = resourceLoader.getResource("file:///" + result.getZipFile().getFile().getAbsolutePath());
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"project.zip\"")
            .body(resource);
    }
}
