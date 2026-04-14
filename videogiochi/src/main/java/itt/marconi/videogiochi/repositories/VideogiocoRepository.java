package itt.marconi.videogiochi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import itt.marconi.videogiochi.domain.Videogioco;

// tutta la gestione del repository viene lasciata a Spring, abbiamo scelto JPA apposta
public interface VideogiocoRepository extends JpaRepository<Videogioco, UUID> {
    List<Videogioco> findByTitoloContainingIgnoreCase(String titolo);
}