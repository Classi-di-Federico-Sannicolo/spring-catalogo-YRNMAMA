package itt.marconi.videogiochi.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import itt.marconi.videogiochi.api.ApiResponse;
import itt.marconi.videogiochi.domain.RawgGame;
import itt.marconi.videogiochi.domain.RawgResponse;
import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoDto;
import itt.marconi.videogiochi.domain.VideogiocoForm;
import itt.marconi.videogiochi.services.VideogiocoService;

@RestController
public class VideogiocoRestController {

    @Autowired
    private VideogiocoService service;

    @GetMapping("/api/giochi")
    public ResponseEntity<ApiResponse<RawgResponse>> searchGiochi(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page) {
        RawgResponse response = service.searchGames(search, page != null ? page : 1);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api/giochi/tutti")
    public ResponseEntity<ApiResponse<List<RawgGame>>> getTuttiGiochi(
            @RequestParam(required = false, defaultValue = "1") Integer page) {
        List<RawgGame> games = service.getAllGames(page);
        return ResponseEntity.ok(ApiResponse.success(games));
    }

    @PostMapping("/api/catalogo")
    public ResponseEntity<ApiResponse<VideogiocoDto>> createVideogioco(@Validated @RequestBody VideogiocoForm form) {
        Videogioco videogioco = service.save(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.toDto(videogioco)));
    }

    @GetMapping("/api/catalogo")
    public ResponseEntity<ApiResponse<List<VideogiocoDto>>> getCatalogo(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(service.toDtoList(service.findAll(search))));
    }

    @GetMapping("/api/catalogo/{id}")
    public ResponseEntity<ApiResponse<VideogiocoDto>> getVideogioco(@PathVariable UUID id) {
        return service.get(id)
            .map(v -> ResponseEntity.ok(ApiResponse.success(service.toDto(v))))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Videogioco non trovato"));
    }

    @PutMapping("/api/catalogo/{id}")
    public ResponseEntity<ApiResponse<VideogiocoDto>> updateVideogioco(@PathVariable UUID id, @Validated @RequestBody VideogiocoForm form) {
        return service.update(id, form)
            .map(v -> ResponseEntity.ok(ApiResponse.success(service.toDto(v))))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Videogioco non trovato"));
    }

    @DeleteMapping("/api/catalogo/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVideogioco(@PathVariable UUID id) {
        if (service.get(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Videogioco non trovato");
        }
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Videogioco eliminato"));
    }

    @DeleteMapping("/api/catalogo")
    public ResponseEntity<ApiResponse<Void>> clearCatalogo() {
        service.deleteAll();
        return ResponseEntity.ok(ApiResponse.success(null, "Catalogo svuotato"));
    }
}