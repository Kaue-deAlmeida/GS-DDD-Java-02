package br.com.fiap.citizentech.ondacare.controller;

import br.com.fiap.citizentech.ondacare.bo.IdosoBO;
import br.com.fiap.citizentech.ondacare.model.Idoso;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/idosos")
@RequiredArgsConstructor
public class IdosoController {

    private final IdosoBO idosoBO;

    @PostMapping
    public ResponseEntity<Idoso> cadastrar(@Valid @RequestBody Idoso idoso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(idosoBO.cadastrar(idoso));
    }

    @GetMapping
    public ResponseEntity<List<Idoso>> listar(
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) Long ubsId) {
        return ResponseEntity.ok(idosoBO.listar(cidade, ubsId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idoso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(idosoBO.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idoso> atualizar(@PathVariable Long id, @Valid @RequestBody Idoso idoso) {
        return ResponseEntity.ok(idosoBO.atualizar(id, idoso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        idosoBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}
