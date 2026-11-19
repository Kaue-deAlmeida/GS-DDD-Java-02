package br.com.fiap.citizentech.ondacare.bo;

import br.com.fiap.citizentech.ondacare.dao.ContatoFamiliaDAO;
import br.com.fiap.citizentech.ondacare.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.ondacare.exception.RegraNegocioException;
import br.com.fiap.citizentech.ondacare.model.ContatoFamilia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FamiliaBO {

    private final ContatoFamiliaDAO contatoDAO;
    private final IdosoBO idosoBO;

    public ContatoFamilia convidarContato(Long idIdoso, ContatoFamilia contato) {
        idosoBO.buscarPorId(idIdoso);
        List<ContatoFamilia> atuais = contatoDAO.listarPorIdoso(idIdoso);

        long familiaresPrincipais = atuais.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getContatoExtra()) && !"RECUSADO".equals(c.getStatusConvite()) && !"SAIU".equals(c.getStatusConvite()))
                .count();

        if (!Boolean.TRUE.equals(contato.getContatoExtra()) && familiaresPrincipais >= 2) {
            throw new RegraNegocioException("O idoso pode ter no maximo 2 contatos principais na Familia (HU01).");
        }

        contato.setIdIdoso(idIdoso);
        contato.setOrdemPrioridade(atuais.size() + 1);
        contato.setStatusConvite("PENDENTE");
        return contatoDAO.inserir(contato);
    }

    public List<ContatoFamilia> listarFamilia(Long idIdoso) {
        idosoBO.buscarPorId(idIdoso);
        return contatoDAO.listarPorIdoso(idIdoso);
    }

    public ContatoFamilia responderConvite(Long idConvite, String resposta) {
        ContatoFamilia contato = contatoDAO.buscarPorId(idConvite)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Convite nao encontrado com ID: " + idConvite));

        Set<String> validos = Set.of("ACEITO", "RECUSADO", "SAIU");
        if (resposta == null || !validos.contains(resposta.toUpperCase())) {
            throw new RegraNegocioException("Status invalido. Use: ACEITO, RECUSADO ou SAIU (HU02/HU20).");
        }
        String novoStatus = resposta.toUpperCase();
        contatoDAO.atualizarStatus(idConvite, novoStatus);
        contato.setStatusConvite(novoStatus);
        return contato;
    }

    public void removerMembro(Long idIdoso, Long idMembro) {
        idosoBO.buscarPorId(idIdoso);
        contatoDAO.buscarPorId(idMembro)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Membro da familia nao encontrado: " + idMembro));
        contatoDAO.deletar(idMembro);
    }
}
