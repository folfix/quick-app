package net.folfas.quickapp.api.rest;

import lombok.Value;
import net.folfas.quickapp.domain.generator.GeneratorResult;

@Value
class GeneratorResultDto {

    String resultId;

    GeneratorResultDto(GeneratorResult result) {
        this.resultId = result.getResultId();
    }
}
