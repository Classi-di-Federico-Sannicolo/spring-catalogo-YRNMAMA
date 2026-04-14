package itt.marconi.videogiochi.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoForm;
import itt.marconi.videogiochi.services.VideogiocoService;

@RestController
public class VideogiocoRestController {

    @Autowired
    private VideogiocoService service;

    // API RAWG
    @GetMapping("/api/giochi")
    public String getGiochi() {
        return service.getGiochi();
    }

    // CRUD CATALOGO LOCALE

    // CREATE - Crea nuovo videogioco
    @PostMapping("/api/catalogo")
    public ResponseEntity<Videogioco> createVideogioco(@RequestBody VideogiocoForm form) {
        try {
            Videogioco videogioco = service.save(form);
            return ResponseEntity.status(HttpStatus.CREATED).body(videogioco);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Lista tutti i videogiochi con ricerca opzionale
    @GetMapping("/api/catalogo")
    public List<Videogioco> getCatalogo(@RequestParam(required = false) String search) {
        return service.findAll(search);
    }

    // READ - Ottieni videogioco per ID
    @GetMapping("/api/catalogo/{id}")
    public ResponseEntity<Videogioco> getVideogioco(@PathVariable UUID id) {
        Optional<Videogioco> videogioco = service.get(id);
        if (videogioco.isPresent()) {
            return ResponseEntity.ok(videogioco.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE - Aggiorna videogioco esistente
    @PutMapping("/api/catalogo/{id}")
    public ResponseEntity<Videogioco> updateVideogioco(@PathVariable UUID id, @RequestBody VideogiocoForm form) {
        Optional<Videogioco> updated = service.update(id, form);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Elimina videogioco per ID
    @DeleteMapping("/api/catalogo/{id}")
    public ResponseEntity<Void> deleteVideogioco(@PathVariable UUID id) {
        Optional<Videogioco> videogioco = service.get(id);
        if (videogioco.isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Svuota tutto il catalogo
    @DeleteMapping("/api/catalogo")
    public ResponseEntity<Void> clearCatalogo() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}