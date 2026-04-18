package itt.marconi.videogiochi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawgGame {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("released")
    private String released;

    @JsonProperty("background_image")
    private String backgroundImage;

    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("genres")
    private Object[] genres;

    @JsonProperty("platforms")
    private Object[] platforms;

    @JsonProperty("description")
    private String description;
}
