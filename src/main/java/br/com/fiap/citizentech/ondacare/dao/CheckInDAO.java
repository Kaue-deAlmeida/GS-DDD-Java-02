package br.com.fiap.citizentech.ondacare.dao;

import br.com.fiap.citizentech.ondacare.model.CheckIn;
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
public class CheckInDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CheckIn> rowMapper = (rs, rowNum) -> {
        CheckIn c = new CheckIn();
        c.setIdCheckin(rs.getLong("id_checkin"));
        c.setIdIdoso(rs.getLong("id_idoso"));
        Timestamp agendada = rs.getTimestamp("data_hora_agendada");
        if (agendada != null) c.setDataHoraAgendada(agendada.toLocalDateTime());
        Timestamp resp = rs.getTimestamp("data_hora_resposta");
        if (resp != null) c.setDataHoraResposta(resp.toLocalDateTime());
        c.setStatus(rs.getString("status"));
        c.setTentativasAlarme(rs.getInt("tentativas_alarme"));
        c.setLocalizacaoEmergencia(rs.getString("localizacao_emergencia"));
        return c;
    };

    public CheckIn inserir(CheckIn checkIn) {
        String sql = "INSERT INTO T_OC_CHECKIN (id_idoso, data_hora_agendada, status, tentativas_alarme) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_checkin"});
            ps.setLong(1, checkIn.getIdIdoso());
            ps.setTimestamp(2, Timestamp.valueOf(checkIn.getDataHoraAgendada()));
            ps.setString(3, checkIn.getStatus());
            ps.setInt(4, checkIn.getTentativasAlarme());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            checkIn.setIdCheckin(keyHolder.getKey().longValue());
        }
        return checkIn;
    }

    public List<CheckIn> listarPorIdoso(Long idIdoso) {
        return jdbcTemplate.query("SELECT * FROM T_OC_CHECKIN WHERE id_idoso = ? ORDER BY data_hora_agendada DESC", rowMapper, idIdoso);
    }

    public Optional<CheckIn> buscarPorId(Long id) {
        List<CheckIn> lista = jdbcTemplate.query("SELECT * FROM T_OC_CHECKIN WHERE id_checkin = ?", rowMapper, id);
        return lista.stream().findFirst();
    }

    public int atualizarResposta(CheckIn checkIn) {
        String sql = "UPDATE T_OC_CHECKIN SET status = ?, data_hora_resposta = ?, localizacao_emergencia = ? WHERE id_checkin = ?";
        return jdbcTemplate.update(sql,
                checkIn.getStatus(),
                checkIn.getDataHoraResposta() != null ? Timestamp.valueOf(checkIn.getDataHoraResposta()) : null,
                checkIn.getLocalizacaoEmergencia(),
                checkIn.getIdCheckin());
    }

    public int deletar(Long id) {
        return jdbcTemplate.update("DELETE FROM T_OC_CHECKIN WHERE id_checkin = ?", id);
    }
}
