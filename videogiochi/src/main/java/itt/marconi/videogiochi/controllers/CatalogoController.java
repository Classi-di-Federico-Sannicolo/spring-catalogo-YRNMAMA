package itt.marconi.videogiochi.controllers;

import java.util.Optional;
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

    // 📋 Lista videogiochi
    @GetMapping
    public ModelAndView showVideogiochiList() {
        return new ModelAndView("videogioco-list")
            .addObject("videogiochi", videogiocoService.findAll());
    }

    // 🆕 Form nuovo videogioco
    @GetMapping("/new")
    public ModelAndView newVideogiocoForm() {
        return new ModelAndView("videogioco-form")
            .addObject(new VideogiocoForm());
    }

    // 💾 Salvataggio videogioco
    @PostMapping("/new")
    public ModelAndView handleNewVideogioco(
        @ModelAttribute @Valid VideogiocoForm videogiocoForm,
        BindingResult br,
        RedirectAttributes attr
    ) {

        if (br.hasErrors())
            return new ModelAndView("videogioco-form");

        Videogioco v = videogiocoService.save(videogiocoForm);

        attr.addFlashAttribute("newVideogioco", true);

        return new ModelAndView("redirect:/videogioco?id=" + v.getId());
    }

    // 🔍 Dettaglio videogioco (PRG pattern)
    @GetMapping(path = "videogioco", params = "id")
    public ModelAndView showVideogioco(@RequestParam("id") UUID videogiocoId) {

        Optional<Videogioco> opVideogioco = videogiocoService.get(videogiocoId);

        if (opVideogioco.isPresent()) {
            return new ModelAndView("videogioco-detail")
                .addObject("videogioco", opVideogioco.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Videogioco non trovato");
        }
    }

    // 🗑️ Eliminazione
    @GetMapping("videogioco/delete/{id}")
    public ModelAndView deleteVideogioco(
        @PathVariable("id") UUID videogiocoId,
        RedirectAttributes attr
    ) {

        videogiocoService.deleteById(videogiocoId);

        attr.addFlashAttribute("deleted", true);
        return new ModelAndView("redirect:/");
    }
}