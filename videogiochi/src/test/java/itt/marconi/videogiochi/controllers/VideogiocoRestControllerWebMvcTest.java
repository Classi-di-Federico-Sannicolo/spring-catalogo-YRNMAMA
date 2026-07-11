package itt.marconi.videogiochi.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.services.VideogiocoService;

@WebMvcTest(VideogiocoRestController.class)
@Import(itt.marconi.videogiochi.api.GlobalExceptionHandler.class)
class VideogiocoRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideogiocoService service;

    @Test
    void getCatalogoReturnsStandardizedSuccessPayload() throws Exception {
        Videogioco videogioco = new Videogioco(
            UUID.randomUUID(),
            "Super Mario",
            "Nintendo",
            "Platform",
            1985
        );

        when(service.findAll(any())).thenReturn(List.of(videogioco));

        mockMvc.perform(get("/api/catalogo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data[0].titolo").value("Super Mario"));
    }

    @Test
    void getVideogiocoWhenMissingReturnsStandardizedFailPayload() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.get(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/catalogo/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value("fail"))
            .andExpect(jsonPath("$.message").value("Videogioco non trovato"));
    }
}
