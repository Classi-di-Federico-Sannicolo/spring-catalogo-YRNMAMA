package itt.marconi.videogiochi.repositories ; 
import  java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import itt.marconi.videogiochi.domain.Videogioco;

// tutta la gestione del repository viene lasciata a Spring, abbiamo scelto JPA apposta
public interface VideogiocoRepository extends JpaRepository<Videogioco, UUID> {
    
}