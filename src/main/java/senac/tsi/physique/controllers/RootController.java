package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "status", description = "Status básico da API")
public class RootController {

    @Operation(summary = "Status da API", description = "Endpoint público simples para confirmar que a API está online e indicar os caminhos de documentação.")
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "name", "Physique API",
                "status", "online",
                "docs", "/swagger-ui.html",
                "apiDocs", "/api-docs"
        ));
    }
}
