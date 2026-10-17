package com.condominio.sistema.controller;

import com.condominio.sistema.model.Persona;
import com.condominio.sistema.util.Paginacion;
import com.condominio.sistema.service.PersonaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public String listar(
            Model model,
            @RequestParam(defaultValue = "1") int pagina) {
        Paginacion.aplicar(model, "personas", personaService.listarTodas(), pagina);
        return "personas/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        Persona persona = new Persona();
        persona.setEstado(true);

        model.addAttribute("persona", persona);
        model.addAttribute("esNuevo", true);

        return "personas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Persona persona, Model model) {

        if (personaService.existeOtroConDpi(
                persona.getDpi(), persona.getIdPersona())) {

            model.addAttribute(
                    "error",
                    "Ese DPI ya está registrado en otra persona."
            );
            model.addAttribute("persona", persona);
            model.addAttribute("esNuevo", persona.getIdPersona() == null);

            return "personas/formulario";
        }

        personaService.guardar(persona);

        return "redirect:/personas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Integer id,
            Model model) {

        Persona persona = personaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Persona no encontrada: " + id
                ));

        model.addAttribute("persona", persona);
        model.addAttribute("esNuevo", false);

        return "personas/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        personaService.eliminar(id);
        return "redirect:/personas";
    }
}
