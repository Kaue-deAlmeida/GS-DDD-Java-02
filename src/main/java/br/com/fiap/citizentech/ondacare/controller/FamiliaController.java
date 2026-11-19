package br.com.fiap.citizentech.ondacare.controller;

import br.com.fiap.citizentech.ondacare.bo.FamiliaBO;
import br.com.fiap.citizentech.ondacare.model.ContatoFamilia;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FamiliaController {

    private final FamiliaBO familiaBO;

    @PostMapping("/idosos/{idosoId}/familia/convites")
    public ResponseEntity<ContatoFamilia> convidar(
            @PathVariable Long idosoId,
            @Valid @RequestBody ContatoFamilia contato) {
        return ResponseEntity.status(HttpStatus.CREATED).body(familiaBO.convidarContato(idosoId, contato));
    }

    @GetMapping("/idosos/{idosoId}/familia")
    public ResponseEntity<List<ContatoFamilia>> listarFamilia(@PathVariable Long idosoId) {
        return ResponseEntity.ok(familiaBO.listarFamilia(idosoId));
    }

    @PutMapping("/familia/convites/{conviteId}/responder")
    public ResponseEntity<ContatoFamilia> responderConvite(
            @PathVariable Long conviteId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(familiaBO.responderConvite(conviteId, body.get("status")));
    }

    @DeleteMapping("/idosos/{idosoId}/familia/membros/{membroId}")
    public ResponseEntity<Void> removerMembro(@PathVariable Long idosoId, @PathVariable Long membroId) {
        familiaBO.removerMembro(idosoId, membroId);
        return ResponseEntity.noContent().build();
    }
}
