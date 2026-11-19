package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.ContatoFamilia;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContatoFamiliaDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ContatoFamilia> rowMapper = (rs, rowNum) -> {
        ContatoFamilia c = new ContatoFamilia();
        c.setIdContato(rs.getLong("id_contato"));
        c.setIdIdoso(rs.getLong("id_idoso"));
        c.setNome(rs.getString("nome"));
        c.setTelefone(rs.getString("telefone"));
        c.setOrdemPrioridade(rs.getInt("ordem_prioridade"));
        c.setContatoExtra(rs.getInt("contato_extra") == 1);
        c.setStatusConvite(rs.getString("status_convite"));
        return c;
    };

    public ContatoFamilia inserir(ContatoFamilia contato) {
        String sql = "INSERT INTO T_OC_CONTATO_FAMILIA (id_idoso, nome, telefone, ordem_prioridade, contato_extra, status_convite) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_contato"});
            ps.setLong(1, contato.getIdIdoso());
            ps.setString(2, contato.getNome());
            ps.setString(3, contato.getTelefone());
            ps.setInt(4, contato.getOrdemPrioridade());
            ps.setInt(5, Boolean.TRUE.equals(contato.getContatoExtra()) ? 1 : 0);
            ps.setString(6, contato.getStatusConvite());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            contato.setIdContato(keyHolder.getKey().longValue());
        }
        return contato;
    }

    public List<ContatoFamilia> listarPorIdoso(Long idIdoso) {
        return jdbcTemplate.query("SELECT * FROM T_OC_CONTATO_FAMILIA WHERE id_idoso = ? ORDER BY ordem_prioridade", rowMapper, idIdoso);
    }

    public Optional<ContatoFamilia> buscarPorId(Long idContato) {
        List<ContatoFamilia> lista = jdbcTemplate.query("SELECT * FROM T_OC_CONTATO_FAMILIA WHERE id_contato = ?", rowMapper, idContato);
        return lista.stream().findFirst();
    }

    public int atualizarStatus(Long idContato, String status) {
        return jdbcTemplate.update("UPDATE T_OC_CONTATO_FAMILIA SET status_convite = ? WHERE id_contato = ?", status, idContato);
    }

    public int deletar(Long idContato) {
        return jdbcTemplate.update("DELETE FROM T_OC_CONTATO_FAMILIA WHERE id_contato = ?", idContato);
    }
}
