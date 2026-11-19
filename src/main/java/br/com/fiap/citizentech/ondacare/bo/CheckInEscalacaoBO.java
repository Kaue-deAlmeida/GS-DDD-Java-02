package br.com.fiap.citizentech.ondacare.bo;

import br.com.fiap.citizentech.ondacare.dao.CheckInDAO;
import br.com.fiap.citizentech.ondacare.dao.ContatoFamiliaDAO;
import br.com.fiap.citizentech.ondacare.dao.EscalacaoDAO;
import br.com.fiap.citizentech.ondacare.dao.VisitaUbsDAO;
import br.com.fiap.citizentech.ondacare.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.ondacare.exception.RegraNegocioException;
import br.com.fiap.citizentech.ondacare.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInEscalacaoBO {

    private final CheckInDAO checkInDAO;
    private final EscalacaoDAO escalacaoDAO;
    private final VisitaUbsDAO visitaUbsDAO;
    private final ContatoFamiliaDAO contatoDAO;
    private final IdosoBO idosoBO;

    public CheckIn criarCheckIn(CheckIn checkIn) {
        idosoBO.buscarPorId(checkIn.getIdIdoso());
        if (checkIn.getDataHoraAgendada() == null) {
            checkIn.setDataHoraAgendada(LocalDateTime.now());
        }
        checkIn.setStatus("AGUARDANDO");
        checkIn.setTentativasAlarme(1);
        return checkInDAO.inserir(checkIn);
    }

    public List<CheckIn> listarCheckInsIdoso(Long idIdoso) {
        idosoBO.buscarPorId(idIdoso);
        return checkInDAO.listarPorIdoso(idIdoso);
    }

    public CheckIn responderCheckIn(Long idCheckin, String resposta, String localizacaoPontual) {
        CheckIn checkIn = checkInDAO.buscarPorId(idCheckin)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Check-in nao encontrado: " + idCheckin));

        if (resposta == null) {
            throw new RegraNegocioException("Informe TO_BEM, PRECISO_DE_AJUDA ou SEM_RESPOSTA.");
        }

        String r = resposta.toUpperCase();
        checkIn.setDataHoraResposta(LocalDateTime.now());

        switch (r) {
            case "TO_BEM" -> {
                checkIn.setStatus("TO_BEM");
                checkIn.setLocalizacaoEmergencia(null);
            }
            case "PRECISO_DE_AJUDA" -> {
                checkIn.setStatus("PRECISO_DE_AJUDA");
                checkIn.setLocalizacaoEmergencia(localizacaoPontual != null ? localizacaoPontual : "Endereco cadastrado acionado");
            }
            case "SEM_RESPOSTA" -> {
                checkIn.setStatus("SEM_RESPOSTA");
                iniciarEscadaConfirmacao(checkIn);
            }
            default -> throw new RegraNegocioException("Resposta invalida. Use TO_BEM, PRECISO_DE_AJUDA ou SEM_RESPOSTA.");
        }

        checkInDAO.atualizarResposta(checkIn);
        return checkIn;
    }

    private void iniciarEscadaConfirmacao(CheckIn checkIn) {
        Idoso idoso = idosoBO.buscarPorId(checkIn.getIdIdoso());
        List<ContatoFamilia> familiaAtiva = contatoDAO.listarPorIdoso(idoso.getIdIdoso()).stream()
                .filter(c -> "ACEITO".equals(c.getStatusConvite()))
                .toList();

        Escalacao esc = new Escalacao();
        esc.setIdCheckin(checkIn.getIdCheckin());
        esc.setIdIdoso(idoso.getIdIdoso());

        if (Boolean.TRUE.equals(idoso.getSemFamilia()) || familiaAtiva.isEmpty()) {
            esc.setEtapaAtual("UBS");
            esc.setPrioridadeUbs("ALTA");
            esc.setMotivoUbs("Sem familia cadastrada: a UBS e o contato");
        } else {
            esc.setEtapaAtual("FAMILIA");
            esc.setStatusFamilia("AGUARDANDO");
        }
        escalacaoDAO.inserir(esc);
    }

    public Escalacao registrarAcaoFamilia(Long idEscalacao, String acao) {
        Escalacao esc = escalacaoDAO.buscarPorId(idEscalacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Escalacao nao encontrada: " + idEscalacao));

        if (acao == null) {
            throw new RegraNegocioException("Informe a acao da familia.");
        }

        switch (acao.toUpperCase()) {
            case "FALEI_TA_BEM" -> {
                esc.setStatusFamilia("FALEI_TA_BEM");
                esc.setEtapaAtual("ENCERRADA");
            }
            case "NAO_CONSEGUI_FALAR" -> {
                esc.setStatusFamilia("NAO_CONSEGUI_FALAR");
                esc.setEtapaAtual("UBS");
                esc.setPrioridadeUbs("ALTA");
                esc.setMotivoUbs("A Familia nao conseguiu falar");
            }
            case "NAO_POSSO_AGORA" -> {
                esc.setStatusFamilia("NAO_POSSO_AGORA");
            }
            case "SAIU_CONFIRO_DEPOIS" -> {
                esc.setStatusFamilia("SAIU_CONFIRO_DEPOIS");
                esc.setReagendadoPara(LocalDateTime.now().plusHours(2));
            }
            default -> throw new RegraNegocioException(
                    "Acao invalida. Use FALEI_TA_BEM, NAO_CONSEGUI_FALAR, NAO_POSSO_AGORA ou SAIU_CONFIRO_DEPOIS.");
        }

        escalacaoDAO.atualizar(esc);
        return esc;
    }

    public Escalacao buscarEscalacao(Long idEscalacao) {
        return escalacaoDAO.buscarPorId(idEscalacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Escalacao nao encontrada: " + idEscalacao));
    }

    public List<Escalacao> listarPendentesUbs(Long idUbs) {
        return escalacaoDAO.listarPendentesUbsOrdenados(idUbs);
    }

    public VisitaUbs registrarVisitaUbs(Long idUbs, VisitaUbs visita) {
        Escalacao esc = buscarEscalacao(visita.getIdEscalacao());
        if (!"ESTA_BEM".equalsIgnoreCase(visita.getDesfecho())
                && !"PRECISOU_DE_ATENDIMENTO".equalsIgnoreCase(visita.getDesfecho())) {
            throw new RegraNegocioException("Desfecho invalido. Use ESTA_BEM ou PRECISOU_DE_ATENDIMENTO (HU17).");
        }
        visita.setIdUbs(idUbs);
        visita.setDesfecho(visita.getDesfecho().toUpperCase());
        VisitaUbs salva = visitaUbsDAO.inserir(visita);

        esc.setEtapaAtual("ENCERRADA");
        escalacaoDAO.atualizar(esc);
        return salva;
    }

    public List<VisitaUbs> listarVisitasUbs(Long idUbs) {
        return visitaUbsDAO.listarPorUbs(idUbs);
    }

    public void removerCheckIn(Long id) {
        checkInDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Check-in nao encontrado: " + id));
        checkInDAO.deletar(id);
    }
}
