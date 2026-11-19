package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.Escalacao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EscalacaoDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Escalacao> rowMapper = (rs, rowNum) -> {
        Escalacao e = new Escalacao();
        e.setIdEscalacao(rs.getLong("id_escalacao"));
        e.setIdCheckin(rs.getLong("id_checkin"));
        e.setIdIdoso(rs.getLong("id_idoso"));
        e.setEtapaAtual(rs.getString("etapa_atual"));
        e.setStatusFamilia(rs.getString("status_familia"));
        e.setPrioridadeUbs(rs.getString("prioridade_ubs"));
        e.setMotivoUbs(rs.getString("motivo_ubs"));
        Timestamp reag = rs.getTimestamp("reagendado_para");
        if (reag != null) e.setReagendadoPara(reag.toLocalDateTime());
        Timestamp criado = rs.getTimestamp("criado_em");
        if (criado != null) e.setCriadoEm(criado.toLocalDateTime());
        return e;
    };

    public Escalacao inserir(Escalacao esc) {
        String sql = "INSERT INTO T_OC_ESCALACAO (id_checkin, id_idoso, etapa_atual, status_familia, prioridade_ubs, motivo_ubs) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_escalacao"});
            ps.setLong(1, esc.getIdCheckin());
            ps.setLong(2, esc.getIdIdoso());
            ps.setString(3, esc.getEtapaAtual());
            ps.setString(4, esc.getStatusFamilia());
            ps.setString(5, esc.getPrioridadeUbs());
            ps.setString(6, esc.getMotivoUbs());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            esc.setIdEscalacao(keyHolder.getKey().longValue());
        }
        return esc;
    }

    public Optional<Escalacao> buscarPorId(Long id) {
        List<Escalacao> lista = jdbcTemplate.query("SELECT * FROM T_OC_ESCALACAO WHERE id_escalacao = ?", rowMapper, id);
        return lista.stream().findFirst();
    }

    public List<Escalacao> listarPendentesUbsOrdenados(Long idUbs) {
        String sql = "SELECT e.* FROM T_OC_ESCALACAO e " +
                     "JOIN T_OC_IDOSO i ON e.id_idoso = i.id_idoso " +
                     "WHERE i.id_ubs = ? AND e.etapa_atual = 'UBS' " +
                     "ORDER BY CASE e.prioridade_ubs WHEN 'ALTA' THEN 1 ELSE 2 END, e.criado_em ASC";
        return jdbcTemplate.query(sql, rowMapper, idUbs);
    }

    public int atualizar(Escalacao esc) {
        String sql = "UPDATE T_OC_ESCALACAO SET etapa_atual = ?, status_familia = ?, prioridade_ubs = ?, motivo_ubs = ?, reagendado_para = ? WHERE id_escalacao = ?";
        return jdbcTemplate.update(sql,
                esc.getEtapaAtual(), esc.getStatusFamilia(), esc.getPrioridadeUbs(), esc.getMotivoUbs(),
                esc.getReagendadoPara() != null ? Timestamp.valueOf(esc.getReagendadoPara()) : null,
                esc.getIdEscalacao());
    }

    public int deletar(Long id) {
        return jdbcTemplate.update("DELETE FROM T_OC_ESCALACAO WHERE id_escalacao = ?", id);
    }
}
