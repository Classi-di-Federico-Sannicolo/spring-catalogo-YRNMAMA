package itt.marconi.videogiochi.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoDto;
import itt.marconi.videogiochi.domain.VideogiocoForm;
import itt.marconi.videogiochi.domain.RawgGame;
import itt.marconi.videogiochi.domain.RawgResponse;
import itt.marconi.videogiochi.repositories.VideogiocoRepository;

@Service
public class VideogiocoService {
    
    // dependency injection
    @Autowired
    private VideogiocoRepository videogiocoRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${rawg.api.key}")
    private String apiKey;

    @Value("${rawg.api.base-url}")
    private String baseUrl;

    public Videogioco save(VideogiocoForm videogiocoForm) {
        if (videogiocoForm == null) {
            throw new IllegalArgumentException("VideogiocoForm non può essere nullo");
        }

        Videogioco v = mapVideogioco(videogiocoForm);
        v.setId(null);
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

    public VideogiocoDto toDto(Videogioco v) {
        return new VideogiocoDto(v.getId(), v.getTitolo(), v.getProduttore(), v.getGenere(), v.getAnno());
    }

    public List<VideogiocoDto> toDtoList(List<Videogioco> videogiochi) {
        return videogiochi.stream().map(this::toDto).toList();
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

    public RawgResponse searchGames(String search, Integer page) {
        try {
            StringBuilder url = new StringBuilder(baseUrl + "/games?key=" + apiKey);
            
            if (page != null && page > 0) {
                url.append("&page=").append(page);
            }
            
            if (search != null && !search.isBlank()) {
                url.append("&search=").append(search);
                url.append("&search_exact=true");
            }
            
            url.append("&page_size=20");
            url.append("&ordering=-rating");
            
            RawgResponse response = restTemplate.getForObject(url.toString(), RawgResponse.class);
            return response != null ? response : new RawgResponse();
        } catch (RestClientException e) {
            System.err.println("Errore nella chiamata a RAWG API: " + e.getMessage());
            return new RawgResponse();
        }
    }

    public List<RawgGame> getAllGames(Integer page) {
        try {
            String url = baseUrl + "/games?key=" + apiKey + "&page=" + (page != null ? page : 1) + "&page_size=20&ordering=-rating";
            RawgResponse response = restTemplate.getForObject(url, RawgResponse.class);
            return response != null && response.getResults() != null ? response.getResults() : List.of();
        } catch (RestClientException e) {
            System.err.println("Errore nella chiamata a RAWG API: " + e.getMessage());
            return List.of();
        }
    }

    public Optional<Videogioco> update(UUID id, VideogiocoForm form) {
        if (form == null) {
            throw new IllegalArgumentException("VideogiocoForm non può essere nullo");
        }

        Optional<Videogioco> existing = videogiocoRepo.findById(id);
        if (existing.isPresent()) {
            Videogioco v = existing.get();
            v.setTitolo(form.getTitolo());
            v.setProduttore(form.getProduttore());
            v.setGenere(form.getGenere());
            v.setAnno(form.getAnno());
            return Optional.of(videogiocoRepo.save(v));
        }
        return Optional.empty();
    }
} 