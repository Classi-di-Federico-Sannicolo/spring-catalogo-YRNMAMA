package itt.marconi.videogiochi.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoForm;
import itt.marconi.videogiochi.repositories.VideogiocoRepository;

@Service
public class VideogiocoService {
    
    // dependency injection
    @Autowired
    private VideogiocoRepository videogiocoRepo;

    public Videogioco save(VideogiocoForm videogiocoForm) {
        Videogioco v = mapVideogioco(videogiocoForm);
        return videogiocoRepo.save(v);
    }

    private Videogioco mapVideogioco(VideogiocoForm form) {
        Videogioco v = new Videogioco();
        v.setTitolo(form.getTitolo());
        v.setProduttore(form.getProduttore());
        v.setGenere(form.getGenere());
        v.setAnno(form.getAnno());
        return v;
    }

    public List<Videogioco> findAll(String search) {
        if (search == null || search.isBlank()) {
            return videogiocoRepo.findAll(Sort.by("titolo"));
        }
        return videogiocoRepo.findByTitoloContainingIgnoreCase(search);
    }

    public Optional<Videogioco> get(UUID id) {
        return videogiocoRepo.findById(id);
    }

    public void deleteById(UUID id) {
        videogiocoRepo.deleteById(id);
    }

    public void deleteAll() {
        videogiocoRepo.deleteAll();
    }
} 