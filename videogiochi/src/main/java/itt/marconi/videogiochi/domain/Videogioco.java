package itt.marconi.videogiochi.domain;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// la classe Videogioco serve a specificare la struttura dell'entità da salvare nel database

@Data                   // getter e setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "videogiochi") 
public class Videogioco {
    
    @Id                 // primary key
    @GeneratedValue     // generazione automatica
    @Column(name = "id")
    private UUID id;

    @Column(name = "titolo")
    private String titolo;

    @Column(name = "genere")
    private String genere;

    @Column(name = "anno")
    private Integer anno;
}