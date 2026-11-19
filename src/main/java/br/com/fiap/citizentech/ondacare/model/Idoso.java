package br.com.fiap.citizentech.ondacare.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Idoso {
    private Long idIdoso;

    @NotBlank(message = "Nome do idoso e obrigatorio")
    private String nome;

    @NotBlank(message = "Telefone e obrigatorio")
    private String telefone;

    @NotBlank(message = "Rua e numero sao obrigatorios")
    private String ruaNumero;

    @NotBlank(message = "Cidade e obrigatoria para consulta de alerta INMET")
    private String cidade;

    @NotBlank(message = "Bairro e obrigatorio")
    private String bairro;

    @NotBlank(message = "Horario do check-in da manha e obrigatorio")
    private String horarioCheckinManha;

    @NotBlank(message = "Horario do check-in da tarde e obrigatorio")
    private String horarioCheckinTarde;

    @NotNull(message = "Consentimento do idoso deve ser informado")
    private Boolean consentimento;

    private Boolean semFamilia = false;

    @NotNull(message = "UBS de referencia e obrigatoria")
    private Long idUbs;
}
