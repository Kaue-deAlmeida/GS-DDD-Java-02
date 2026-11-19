package br.com.fiap.citizentech.ondacare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubs {
    private Long idUbs;
    private String codigoCnes;
    private String nome;
    private String endereco;
    private String cidade;
    private String bairro;
    private String horarioAbertura;
    private String horarioFechamento;
}
