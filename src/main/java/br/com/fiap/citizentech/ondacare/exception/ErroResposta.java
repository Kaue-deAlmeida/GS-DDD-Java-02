package br.com.fiap.citizentech.ondacare.exception;

import java.time.LocalDateTime;

public record ErroResposta(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho
) {}
