package it.marconi.videogiochi.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// la classe VideogiocoForm supporta il form di inserimento dati
// serve come contenitore per passare i parametri via Model
// e per introdurre controlli di validazione sulle input

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideogiocoForm {
    
    // controlli di validazione (attivati con @Valid nel controller)

    @NotEmpty
    @Size(max = 50)
    private String titolo;

    @NotEmpty
    @Size(max = 30)
    private String genere;

    @NotNull
    @Min(1950)
    private Integer anno;
}