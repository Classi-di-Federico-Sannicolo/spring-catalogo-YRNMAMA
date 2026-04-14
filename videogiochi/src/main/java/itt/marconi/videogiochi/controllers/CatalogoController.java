package itt.marconi.videogiochi.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import itt.marconi.videogiochi.domain.Videogioco;
import itt.marconi.videogiochi.domain.VideogiocoForm;
import itt.marconi.videogiochi.services.VideogiocoService;

import jakarta.validation.Valid;

@Controller
public class CatalogoController {

    @Autowired
    private VideogiocoService videogiocoService;

    @GetMapping("/")
    public ModelAndView showVideogiochiList(@RequestParam(name = "search", required = false) String search) {
        return new ModelAndView("videogioco-list")
            .addObject("videogiochi", videogiocoService.findAll(search))
            .addObject("search", search);
    }

    @GetMapping("/new")
    public ModelAndView newVideogiocoForm() {
        return new ModelAndView("videogioco-form")
            .addObject("videogiocoForm", new VideogiocoForm());
    }

    @PostMapping("/new")
    public ModelAndView handleNewVideogioco(
        @ModelAttribute @Valid VideogiocoForm videogiocoForm,
        BindingResult br,
        RedirectAttributes attr
    ) {

        if (br.hasErrors()) {
            return new ModelAndView("videogioco-form");
        }

        Videogioco v = videogiocoService.save(videogiocoForm);
        attr.addFlashAttribute("created", true);

        return new ModelAndView("redirect:/item/" + v.getId());
    }

    @GetMapping("/item/{id}")
    public ModelAndView showVideogioco(
        @PathVariable("id") UUID videogiocoId,
        @ModelAttribute("created") Boolean created
    ) {

        return videogiocoService.get(videogiocoId)
            .map(v -> new ModelAndView("videogioco-detail")
                .addObject("videogioco", v)
                .addObject("created", created))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Videogioco non trovato"));
    }

    @GetMapping("/item/delete/{id}")
    public ModelAndView deleteVideogioco(
        @PathVariable("id") UUID videogiocoId,
        RedirectAttributes attr
    ) {

        videogiocoService.deleteById(videogiocoId);
        attr.addFlashAttribute("deleted", true);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/clear")
    public ModelAndView clearCatalogo(RedirectAttributes attr) {
        videogiocoService.deleteAll();
        attr.addFlashAttribute("cleared", true);
        return new ModelAndView("redirect:/");
    }
}