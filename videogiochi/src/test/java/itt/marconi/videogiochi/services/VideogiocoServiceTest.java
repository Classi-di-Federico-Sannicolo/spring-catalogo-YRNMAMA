package itt.marconi.videogiochi.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoForm;
import itt.marconi.videogiochi.repositories.VideogiocoRepository;

@ExtendWith(MockitoExtension.class)
class VideogiocoServiceTest {

    @Mock
    private VideogiocoRepository videogiocoRepo;

    @InjectMocks
    private VideogiocoService service;

    @Test
    void saveShouldClearIdBeforePersisting() {
        VideogiocoForm form = new VideogiocoForm("Super Mario", "Nintendo", "Platform", 1985);

        when(videogiocoRepo.save(any(Videogioco.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.save(form);

        verify(videogiocoRepo).save(any(Videogioco.class));
        verify(videogiocoRepo).save(org.mockito.ArgumentMatchers.argThat(v ->
            v.getId() == null &&
            "Super Mario".equals(v.getTitolo()) &&
            "Nintendo".equals(v.getProduttore()) &&
            "Platform".equals(v.getGenere()) &&
            Integer.valueOf(1985).equals(v.getAnno())
        ));
    }
}
