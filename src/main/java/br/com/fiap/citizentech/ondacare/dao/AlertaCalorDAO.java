package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.AlertaCalor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AlertaCalorDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AlertaCalor> rowMapper = (rs, rowNum) -> {
        AlertaCalor a = new AlertaCalor();
        a.setIdAlerta(rs.getLong("id_alerta"));
        a.setCidade(rs.getString("cidade"));
        a.setDataAlerta(rs.getDate("data_alerta").toLocalDate());
        a.setOrientacoes(rs.getString("orientacoes"));
        a.setAtivo(rs.getInt("ativo") == 1);
        return a;
    };

    public AlertaCalor inserir(AlertaCalor alerta) {
        String sql = "INSERT INTO T_OC_ALERTA_CALOR (cidade, data_alerta, orientacoes, ativo) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_alerta"});
            ps.setString(1, alerta.getCidade());
            ps.setDate(2, Date.valueOf(alerta.getDataAlerta()));
            ps.setString(3, alerta.getOrientacoes());
            ps.setInt(4, Boolean.TRUE.equals(alerta.getAtivo()) ? 1 : 0);
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            alerta.setIdAlerta(keyHolder.getKey().longValue());
        }
        return alerta;
    }

    public List<AlertaCalor> listarPorCidade(String cidade) {
        if (cidade != null && !cidade.isBlank()) {
            return jdbcTemplate.query("SELECT * FROM T_OC_ALERTA_CALOR WHERE UPPER(cidade) = UPPER(?) AND ativo = 1", rowMapper, cidade);
        }
        return jdbcTemplate.query("SELECT * FROM T_OC_ALERTA_CALOR WHERE ativo = 1", rowMapper);
    }

    public Optional<AlertaCalor> buscarPorId(Long id) {
        List<AlertaCalor> lista = jdbcTemplate.query("SELECT * FROM T_OC_ALERTA_CALOR WHERE id_alerta = ?", rowMapper, id);
        return lista.stream().findFirst();
    }

    public int atualizar(Long id, AlertaCalor alerta) {
        return jdbcTemplate.update("UPDATE T_OC_ALERTA_CALOR SET orientacoes = ?, ativo = ? WHERE id_alerta = ?",
                alerta.getOrientacoes(), Boolean.TRUE.equals(alerta.getAtivo()) ? 1 : 0, id);
    }

    public int deletar(Long id) {
        return jdbcTemplate.update("DELETE FROM T_OC_ALERTA_CALOR WHERE id_alerta = ?", id);
    }
}
