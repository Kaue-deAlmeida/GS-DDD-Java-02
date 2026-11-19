package br.com.fiap.citizentech.ondacare.controller;

import br.com.fiap.citizentech.ondacare.bo.CheckInEscalacaoBO;
import br.com.fiap.citizentech.ondacare.model.CheckIn;
import br.com.fiap.citizentech.ondacare.model.Escalacao;
import br.com.fiap.citizentech.ondacare.model.VisitaUbs;
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
public class CheckInEscalacaoController {

    private final CheckInEscalacaoBO serviceBO;

    @PostMapping("/checkins")
    public ResponseEntity<CheckIn> agendarCheckIn(@Valid @RequestBody CheckIn checkIn) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceBO.criarCheckIn(checkIn));
    }

    @GetMapping("/idosos/{idosoId}/checkins/hoje")
    public ResponseEntity<List<CheckIn>> listarCheckInsIdoso(@PathVariable Long idosoId) {
        return ResponseEntity.ok(serviceBO.listarCheckInsIdoso(idosoId));
    }

    @PutMapping("/checkins/{checkinId}/responder")
    public ResponseEntity<CheckIn> responderCheckIn(
            @PathVariable Long checkinId,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(serviceBO.responderCheckIn(
                checkinId,
                payload.get("resposta"),
                payload.get("localizacao")
        ));
    }

    @DeleteMapping("/checkins/{checkinId}")
    public ResponseEntity<Void> deletarCheckIn(@PathVariable Long checkinId) {
        serviceBO.removerCheckIn(checkinId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/escalacoes/{escalacaoId}")
    public ResponseEntity<Escalacao> buscarEscalacao(@PathVariable Long escalacaoId) {
        return ResponseEntity.ok(serviceBO.buscarEscalacao(escalacaoId));
    }

    @PutMapping("/escalacoes/{escalacaoId}/acao-familia")
    public ResponseEntity<Escalacao> acaoFamilia(
            @PathVariable Long escalacaoId,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(serviceBO.registrarAcaoFamilia(escalacaoId, payload.get("acao")));
    }

    @GetMapping("/ubs/{ubsId}/pendentes")
    public ResponseEntity<List<Escalacao>> listarPendentesUbs(@PathVariable Long ubsId) {
        return ResponseEntity.ok(serviceBO.listarPendentesUbs(ubsId));
    }

    @PostMapping("/ubs/{ubsId}/visitas")
    public ResponseEntity<VisitaUbs> registrarVisita(
            @PathVariable Long ubsId,
            @Valid @RequestBody VisitaUbs visita) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceBO.registrarVisitaUbs(ubsId, visita));
    }

    @GetMapping("/ubs/{ubsId}/visitas")
    public ResponseEntity<List<VisitaUbs>> listarVisitas(@PathVariable Long ubsId) {
        return ResponseEntity.ok(serviceBO.listarVisitasUbs(ubsId));
    }
}
