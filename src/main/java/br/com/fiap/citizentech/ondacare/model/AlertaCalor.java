package br.com.fiap.citizentech.ondacare.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaCalor {
    private Long idAlerta;

    @NotBlank(message = "Cidade do alerta INMET e obrigatoria")
    private String cidade;

    @NotNull(message = "Data do alerta e obrigatoria")
    private LocalDate dataAlerta;

    private String orientacoes;
    private Boolean ativo = true;
}
