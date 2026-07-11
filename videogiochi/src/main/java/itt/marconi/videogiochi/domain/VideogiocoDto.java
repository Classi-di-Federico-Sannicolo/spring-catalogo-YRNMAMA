package itt.marconi.videogiochi.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideogiocoDto {

    private UUID id;
    private String titolo;
    private String produttore;
    private String genere;
    private Integer anno;
}
