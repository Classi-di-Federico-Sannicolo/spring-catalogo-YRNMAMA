package itt.marconi.videogiochi.domain;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// la classe Videogioco serve a specificare la struttura dell'entità da salvare nel database

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "videogiochi")
public class Videogioco {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "titolo", nullable = false)
    private String titolo;

    @Column(name = "produttore", nullable = false)
    private String produttore;

    @Column(name = "genere", nullable = false)
    private String genere;

    @Column(name = "anno", nullable = false)
    private Integer anno;
} 