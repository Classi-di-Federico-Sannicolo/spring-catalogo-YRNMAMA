package itt.marconi.videogiochi.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import itt.marconi.videogiochi.api.GlobalExceptionHandler;
import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoDto;
import itt.marconi.videogiochi.services.VideogiocoService;

@WebMvcTest(VideogiocoRestController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
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

        when(service.findAll(isNull())).thenReturn(List.of(videogioco));
        when(service.toDtoList(List.of(videogioco))).thenReturn(List.of(
            new VideogiocoDto(videogioco.getId(), videogioco.getTitolo(), videogioco.getProduttore(), videogioco.getGenere(), videogioco.getAnno())
        ));

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

    @Test
    void createVideogiocoWhenBusinessValidationFailsReturnsStandardizedBadRequestPayload() throws Exception {
        when(service.save(any())).thenThrow(new IllegalArgumentException("VideogiocoForm non può essere nullo"));

        mockMvc.perform(post("/api/catalogo")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"titolo\":\"Super Mario\",\"produttore\":\"Nintendo\",\"genere\":\"Platform\",\"anno\":1985}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("fail"))
            .andExpect(jsonPath("$.message").value("VideogiocoForm non può essere nullo"));
    }
}
