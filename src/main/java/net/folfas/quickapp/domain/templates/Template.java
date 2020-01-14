package net.folfas.quickapp.domain.templates;

import java.io.File;
import lombok.Value;
import net.folfas.quickapp.domain.templates.manifest.Manifest;

@Value
public class Template {

    String key;
    File templateDirectory;
    Manifest manifest;
}
