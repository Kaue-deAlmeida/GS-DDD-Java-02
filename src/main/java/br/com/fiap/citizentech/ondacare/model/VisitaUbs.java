package br.com.fiap.citizentech.ondacare.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaUbs {
    private Long idVisita;

    @NotNull(message = "ID da escalacao e obrigatorio")
    private Long idEscalacao;

    private Long idUbs;

    @NotNull(message = "ID do idoso e obrigatorio")
    private Long idIdoso;

    @NotBlank(message = "Desfecho e obrigatorio (ESTA_BEM ou PRECISOU_DE_ATENDIMENTO)")
    private String desfecho;

    private String observacao;
    private LocalDateTime registradoEm;
}
