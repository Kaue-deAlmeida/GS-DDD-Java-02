package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.Idoso;
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
public class IdosoDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Idoso> rowMapper = (rs, rowNum) -> {
        Idoso i = new Idoso();
        i.setIdIdoso(rs.getLong("id_idoso"));
        i.setNome(rs.getString("nome"));
        i.setTelefone(rs.getString("telefone"));
        i.setRuaNumero(rs.getString("rua_numero"));
        i.setCidade(rs.getString("cidade"));
        i.setBairro(rs.getString("bairro"));
        i.setHorarioCheckinManha(rs.getString("horario_checkin_manha"));
        i.setHorarioCheckinTarde(rs.getString("horario_checkin_tarde"));
        i.setConsentimento(rs.getInt("consentimento") == 1);
        i.setSemFamilia(rs.getInt("sem_familia") == 1);
        i.setIdUbs(rs.getLong("id_ubs"));
        return i;
    };

    public Idoso inserir(Idoso idoso) {
        String sql = "INSERT INTO T_OC_IDOSO (nome, telefone, rua_numero, cidade, bairro, horario_checkin_manha, horario_checkin_tarde, consentimento, sem_familia, id_ubs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_idoso"});
            ps.setString(1, idoso.getNome());
            ps.setString(2, idoso.getTelefone());
            ps.setString(3, idoso.getRuaNumero());
            ps.setString(4, idoso.getCidade());
            ps.setString(5, idoso.getBairro());
            ps.setString(6, idoso.getHorarioCheckinManha());
            ps.setString(7, idoso.getHorarioCheckinTarde());
            ps.setInt(8, Boolean.TRUE.equals(idoso.getConsentimento()) ? 1 : 0);
            ps.setInt(9, Boolean.TRUE.equals(idoso.getSemFamilia()) ? 1 : 0);
            ps.setLong(10, idoso.getIdUbs());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            idoso.setIdIdoso(keyHolder.getKey().longValue());
        }
        return idoso;
    }

    public List<Idoso> listarTodos(String cidade, Long idUbs) {
        if (cidade != null && !cidade.isBlank()) {
            return jdbcTemplate.query("SELECT * FROM T_OC_IDOSO WHERE UPPER(cidade) = UPPER(?)", rowMapper, cidade);
        }
        if (idUbs != null) {
            return jdbcTemplate.query("SELECT * FROM T_OC_IDOSO WHERE id_ubs = ?", rowMapper, idUbs);
        }
        return jdbcTemplate.query("SELECT * FROM T_OC_IDOSO", rowMapper);
    }

    public Optional<Idoso> buscarPorId(Long id) {
        List<Idoso> lista = jdbcTemplate.query("SELECT * FROM T_OC_IDOSO WHERE id_idoso = ?", rowMapper, id);
        return lista.stream().findFirst();
    }

    public int atualizar(Long id, Idoso idoso) {
        String sql = "UPDATE T_OC_IDOSO SET nome = ?, telefone = ?, rua_numero = ?, cidade = ?, bairro = ?, horario_checkin_manha = ?, horario_checkin_tarde = ?, consentimento = ?, sem_familia = ?, id_ubs = ? WHERE id_idoso = ?";
        return jdbcTemplate.update(sql,
                idoso.getNome(), idoso.getTelefone(), idoso.getRuaNumero(), idoso.getCidade(), idoso.getBairro(),
                idoso.getHorarioCheckinManha(), idoso.getHorarioCheckinTarde(),
                Boolean.TRUE.equals(idoso.getConsentimento()) ? 1 : 0,
                Boolean.TRUE.equals(idoso.getSemFamilia()) ? 1 : 0,
                idoso.getIdUbs(), id);
    }

    public int deletar(Long id) {
        return jdbcTemplate.update("DELETE FROM T_OC_IDOSO WHERE id_idoso = ?", id);
    }
}
