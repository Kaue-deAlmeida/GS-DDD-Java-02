package br.com.fiap.citizentech.ondacare.bo;

import br.com.fiap.citizentech.ondacare.dao.IdosoDAO;
import br.com.fiap.citizentech.ondacare.dao.UbsDAO;
import br.com.fiap.citizentech.ondacare.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.ondacare.exception.RegraNegocioException;
import br.com.fiap.citizentech.ondacare.model.Idoso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdosoBO {

    private final IdosoDAO idosoDAO;
    private final UbsDAO ubsDAO;

    public Idoso cadastrar(Idoso idoso) {
        if (!Boolean.TRUE.equals(idoso.getConsentimento())) {
            throw new RegraNegocioException("O cadastro exige o consentimento explicito do idoso (HU01).");
        }
        ubsDAO.buscarPorId(idoso.getIdUbs())
                .orElseThrow(() -> new RecursoNaoEncontradoException("UBS nao encontrada com ID: " + idoso.getIdUbs()));

        if (idoso.getSemFamilia() == null) {
            idoso.setSemFamilia(false);
        }
        return idosoDAO.inserir(idoso);
    }

    public List<Idoso> listar(String cidade, Long idUbs) {
        return idosoDAO.listarTodos(cidade, idUbs);
    }

    public Idoso buscarPorId(Long id) {
        return idosoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso nao encontrado com ID: " + id));
    }

    public Idoso atualizar(Long id, Idoso idoso) {
        buscarPorId(id);
        if (!Boolean.TRUE.equals(idoso.getConsentimento())) {
            throw new RegraNegocioException("O consentimento do idoso deve permanecer ativo.");
        }
        ubsDAO.buscarPorId(idoso.getIdUbs())
                .orElseThrow(() -> new RecursoNaoEncontradoException("UBS nao encontrada com ID: " + idoso.getIdUbs()));
        idosoDAO.atualizar(id, idoso);
        idoso.setIdIdoso(id);
        return idoso;
    }

    public void remover(Long id) {
        buscarPorId(id);
        idosoDAO.deletar(id);
    }
}
