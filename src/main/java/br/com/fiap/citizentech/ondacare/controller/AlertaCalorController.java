package br.com.fiap.citizentech.ondacare.controller;

import br.com.fiap.citizentech.ondacare.bo.AlertaCalorBO;
import br.com.fiap.citizentech.ondacare.model.AlertaCalor;
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
public class AlertaCalorController {

    private final AlertaCalorBO alertaBO;

    @PostMapping("/alertas-calor")
    public ResponseEntity<AlertaCalor> criarAlerta(@Valid @RequestBody AlertaCalor alerta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaBO.registrarAlerta(alerta));
    }

    @GetMapping("/alertas-calor")
    public ResponseEntity<List<AlertaCalor>> listarAlertas(@RequestParam(required = false) String cidade) {
        return ResponseEntity.ok(alertaBO.listarPorCidade(cidade));
    }

    @GetMapping("/idosos/{idosoId}/ponto-fresco")
    public ResponseEntity<Map<String, Object>> consultarPontoFresco(@PathVariable Long idosoId) {
        return ResponseEntity.ok(alertaBO.consultarPontoFresco(idosoId));
    }

    @PutMapping("/alertas-calor/{alertaId}")
    public ResponseEntity<AlertaCalor> atualizar(@PathVariable Long alertaId, @RequestBody AlertaCalor alerta) {
        return ResponseEntity.ok(alertaBO.atualizar(alertaId, alerta));
    }

    @DeleteMapping("/alertas-calor/{alertaId}")
    public ResponseEntity<Void> deletar(@PathVariable Long alertaId) {
        alertaBO.remover(alertaId);
        return ResponseEntity.noContent().build();
    }
}
