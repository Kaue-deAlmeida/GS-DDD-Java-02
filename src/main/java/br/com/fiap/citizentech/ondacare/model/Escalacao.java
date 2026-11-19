package br.com.fiap.citizentech.ondacare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Escalacao {
    private Long idEscalacao;
    private Long idCheckin;
    private Long idIdoso;
    private String etapaAtual;
    private String statusFamilia;
    private String prioridadeUbs;
    private String motivoUbs;
    private LocalDateTime reagendadoPara;
    private LocalDateTime criadoEm;
}
