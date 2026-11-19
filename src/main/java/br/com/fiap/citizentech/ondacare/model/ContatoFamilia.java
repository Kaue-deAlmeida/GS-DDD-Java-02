package br.com.fiap.citizentech.ondacare.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContatoFamilia {
    private Long idContato;
    private Long idIdoso;

    @NotBlank(message = "Nome do contato e obrigatorio")
    private String nome;

    @NotBlank(message = "Telefone do contato e obrigatorio")
    private String telefone;

    private Integer ordemPrioridade;
    private Boolean contatoExtra = false;
    private String statusConvite;
}
