package edu.uees.reservas.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void saludRespondeApiActiva() throws Exception {
        mvc.perform(get("/api/reservas/salud"))
                .andExpect(status().isOk())
                .andExpect(content().string("API activa"));
    }

    @Test
    void puedeCancelarConDosHoras() throws Exception {
        mvc.perform(get("/api/reservas/puede-cancelar").param("horas", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void noPuedeCancelarConUnaHora() throws Exception {
        mvc.perform(get("/api/reservas/puede-cancelar").param("horas", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void horasNegativasDevuelven400() throws Exception {
        mvc.perform(get("/api/reservas/puede-cancelar").param("horas", "-3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearReservaDevuelve201() throws Exception {
        mvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"R-100\",\"tipo\":\"NORMAL\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("R-100"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crearReservaSinIdDevuelve400() throws Exception {
        mvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"\",\"tipo\":\"NORMAL\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarReservaCreada() throws Exception {
        mvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"R-200\",\"tipo\":\"VIP\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/reservas/R-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("VIP"));
    }

    @Test
    void crearReservaDuplicadaDevuelve409() throws Exception {
        String cuerpo = "{\"id\":\"R-300\",\"tipo\":\"NORMAL\"}";
        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Ya existe una reserva con id: R-300"));
    }

    @Test
    void buscarReservaInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/api/reservas/NO-EXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Reserva no encontrada: NO-EXISTE"));
    }
}
