package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.Ubs;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UbsDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Ubs> rowMapper = (rs, rowNum) -> {
        Ubs u = new Ubs();
        u.setIdUbs(rs.getLong("id_ubs"));
        u.setCodigoCnes(rs.getString("codigo_cnes"));
        u.setNome(rs.getString("nome"));
        u.setEndereco(rs.getString("endereco"));
        u.setCidade(rs.getString("cidade"));
        u.setBairro(rs.getString("bairro"));
        u.setHorarioAbertura(rs.getString("horario_abertura"));
        u.setHorarioFechamento(rs.getString("horario_fechamento"));
        return u;
    };

    public Optional<Ubs> buscarPorId(Long id) {
        List<Ubs> lista = jdbcTemplate.query("SELECT * FROM T_OC_UBS WHERE id_ubs = ?", rowMapper, id);
        return lista.stream().findFirst();
    }

    public List<Ubs> listarPorCidade(String cidade) {
        return jdbcTemplate.query("SELECT * FROM T_OC_UBS WHERE UPPER(cidade) = UPPER(?)", rowMapper, cidade);
    }
}
