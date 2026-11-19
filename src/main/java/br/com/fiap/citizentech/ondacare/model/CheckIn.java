package br.com.fiap.citizentech.ondacare.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {
    private Long idCheckin;

    @NotNull(message = "ID do idoso e obrigatorio")
    private Long idIdoso;

    private LocalDateTime dataHoraAgendada;
    private LocalDateTime dataHoraResposta;
    private String status;
    private Integer tentativasAlarme;
    private String localizacaoEmergencia;
}
