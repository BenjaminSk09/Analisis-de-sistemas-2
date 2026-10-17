package com.condominio.sistema.controller;

import com.condominio.sistema.model.Persona;
import com.condominio.sistema.model.Vivienda;
import com.condominio.sistema.model.ViviendaPersona;
import com.condominio.sistema.util.Paginacion;
import com.condominio.sistema.service.PersonaService;
import com.condominio.sistema.service.ViviendaPersonaService;
import com.condominio.sistema.service.ViviendaService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/vivienda-persona")
public class ViviendaPersonaController {

    private final ViviendaPersonaService viviendaPersonaService;
    private final ViviendaService viviendaService;
    private final PersonaService personaService;

    public ViviendaPersonaController(
            ViviendaPersonaService viviendaPersonaService,
            ViviendaService viviendaService,
            PersonaService personaService) {
        this.viviendaPersonaService = viviendaPersonaService;
        this.viviendaService = viviendaService;
        this.personaService = personaService;
    }

    @GetMapping
    public String listar(
            Model model,
            @RequestParam(defaultValue = "1") int pagina) {

        Paginacion.aplicar(model, "relaciones", viviendaPersonaService.listarTodas(), pagina);

        return "vivienda-persona/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        ViviendaPersona relacion = new ViviendaPersona();
        relacion.setEstado(true);
        relacion.setFechaInicio(LocalDate.now());

        model.addAttribute("relacion", relacion);
        model.addAttribute("esNuevo", true);
        cargarListas(model);

        return "vivienda-persona/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Integer idViviendaPersona,
            @RequestParam Integer viviendaId,
            @RequestParam Integer personaId,
            @RequestParam String tipoRelacion,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin,
            @RequestParam Boolean estado,
            Model model) {

        ViviendaPersona relacion = new ViviendaPersona();

        relacion.setIdViviendaPersona(idViviendaPersona);
        relacion.setTipoRelacion(tipoRelacion);
        relacion.setFechaInicio(fechaInicio);
        relacion.setFechaFin(fechaFin);
        relacion.setEstado(estado);

        try {

            Vivienda vivienda = viviendaService.buscarPorId(viviendaId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Vivienda no encontrada: " + viviendaId
                            )
                    );

            Persona persona = personaService.buscarPorId(personaId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Persona no encontrada: " + personaId
                            )
                    );

            relacion.setVivienda(vivienda);
            relacion.setPersona(persona);

            viviendaPersonaService.guardar(relacion);

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("relacion", relacion);
            model.addAttribute("esNuevo", idViviendaPersona == null);
            model.addAttribute("viviendas", viviendaService.listarTodas());
            model.addAttribute("personas", personaService.listarTodas());

            return "vivienda-persona/formulario";
        }

        return "redirect:/vivienda-persona";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Integer id,
            Model model) {

        ViviendaPersona relacion =
                viviendaPersonaService.buscarPorId(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Relación no encontrada: " + id
                                )
                        );

        model.addAttribute("relacion", relacion);
        model.addAttribute("esNuevo", false);
        cargarListas(model);

        return "vivienda-persona/formulario";
    }

    private void cargarListas(Model model) {
        model.addAttribute("viviendas", viviendaService.listarTodas());
        model.addAttribute("personas", personaService.listarTodas());
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {

        viviendaPersonaService.eliminar(id);

        return "redirect:/vivienda-persona";
    }
}
