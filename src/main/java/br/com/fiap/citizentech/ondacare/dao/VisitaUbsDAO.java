package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.VisitaUbs;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VisitaUbsDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<VisitaUbs> rowMapper = (rs, rowNum) -> {
        VisitaUbs v = new VisitaUbs();
        v.setIdVisita(rs.getLong("id_visita"));
        v.setIdEscalacao(rs.getLong("id_escalacao"));
        v.setIdUbs(rs.getLong("id_ubs"));
        v.setIdIdoso(rs.getLong("id_idoso"));
        v.setDesfecho(rs.getString("desfecho"));
        v.setObservacao(rs.getString("observacao"));
        Timestamp reg = rs.getTimestamp("registrado_em");
        if (reg != null) v.setRegistradoEm(reg.toLocalDateTime());
        return v;
    };

    public VisitaUbs inserir(VisitaUbs visita) {
        String sql = "INSERT INTO T_OC_VISITA_UBS (id_escalacao, id_ubs, id_idoso, desfecho, observacao) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_visita"});
            ps.setLong(1, visita.getIdEscalacao());
            ps.setLong(2, visita.getIdUbs());
            ps.setLong(3, visita.getIdIdoso());
            ps.setString(4, visita.getDesfecho());
            ps.setString(5, visita.getObservacao());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            visita.setIdVisita(keyHolder.getKey().longValue());
        }
        return visita;
    }

    public List<VisitaUbs> listarPorUbs(Long idUbs) {
        return jdbcTemplate.query("SELECT * FROM T_OC_VISITA_UBS WHERE id_ubs = ? ORDER BY registrado_em DESC", rowMapper, idUbs);
    }

    public int deletar(Long id) {
        return jdbcTemplate.update("DELETE FROM T_OC_VISITA_UBS WHERE id_visita = ?", id);
    }
}
