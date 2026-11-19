package br.com.fiap.citizentech.ondacare.bo;

import br.com.fiap.citizentech.ondacare.dao.AlertaCalorDAO;
import br.com.fiap.citizentech.ondacare.dao.UbsDAO;
import br.com.fiap.citizentech.ondacare.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.ondacare.model.AlertaCalor;
import br.com.fiap.citizentech.ondacare.model.Idoso;
import br.com.fiap.citizentech.ondacare.model.Ubs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlertaCalorBO {

    private static final String ORIENTACOES_PADRAO =
            "Beba agua; Deixe a casa ventilada; Evite sair nas horas mais quentes; A noite, deixe agua ao lado da cama.";

    private final AlertaCalorDAO alertaDAO;
    private final IdosoBO idosoBO;
    private final UbsDAO ubsDAO;

    public AlertaCalor registrarAlerta(AlertaCalor alerta) {
        if (alerta.getOrientacoes() == null || alerta.getOrientacoes().isBlank()) {
            alerta.setOrientacoes(ORIENTACOES_PADRAO);
        }
        alerta.setAtivo(true);
        return alertaDAO.inserir(alerta);
    }

    public List<AlertaCalor> listarPorCidade(String cidade) {
        return alertaDAO.listarPorCidade(cidade);
    }

    public Map<String, Object> consultarPontoFresco(Long idIdoso) {
        Idoso idoso = idosoBO.buscarPorId(idIdoso);
        Ubs ubs = ubsDAO.buscarPorId(idoso.getIdUbs())
                .orElseThrow(() -> new RecursoNaoEncontradoException("UBS vinculada nao encontrada."));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("idoso", idoso.getNome());
        resp.put("pontoFresco", ubs.getNome());
        resp.put("enderecoDestino", ubs.getEndereco() + " - " + ubs.getBairro() + ", " + ubs.getCidade());
        resp.put("distanciaEstimada", "650 m");
        resp.put("tempoCaminhada", "10 minutos a pe");
        resp.put("horarioFuncionamento", ubs.getHorarioAbertura() + " as " + ubs.getHorarioFechamento());
        resp.put("origemCadastro", idoso.getRuaNumero());
        return resp;
    }

    public AlertaCalor atualizar(Long id, AlertaCalor alerta) {
        AlertaCalor existente = alertaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta de calor nao encontrado: " + id));
        if (alerta.getOrientacoes() != null) existente.setOrientacoes(alerta.getOrientacoes());
        if (alerta.getAtivo() != null) existente.setAtivo(alerta.getAtivo());
        alertaDAO.atualizar(id, existente);
        return existente;
    }

    public void remover(Long id) {
        alertaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta de calor nao encontrado: " + id));
        alertaDAO.deletar(id);
    }
}
