package com.condominio.sistema.controller;

import com.condominio.sistema.model.Vivienda;
import com.condominio.sistema.util.Paginacion;
import com.condominio.sistema.service.ViviendaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/viviendas")
public class ViviendaController {

    private final ViviendaService viviendaService;

    public ViviendaController(ViviendaService viviendaService) {
        this.viviendaService = viviendaService;
    }

    @GetMapping
    public String listar(
            Model model,
            @RequestParam(defaultValue = "1") int pagina) {
        Paginacion.aplicar(model, "viviendas", viviendaService.listarTodas(), pagina);
        return "viviendas/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("vivienda", new Vivienda());
        model.addAttribute("esNuevo", true);
        return "viviendas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Vivienda vivienda, Model model) {

        try {
            viviendaService.guardar(vivienda);
        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("vivienda", vivienda);
            model.addAttribute("esNuevo", vivienda.getIdVivienda() == null);

            return "viviendas/formulario";
        }

        return "redirect:/viviendas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Integer id,
            Model model) {

        Vivienda vivienda = viviendaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vivienda no encontrada: " + id
                ));

        model.addAttribute("vivienda", vivienda);
        model.addAttribute("esNuevo", false);

        return "viviendas/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        viviendaService.eliminar(id);
        return "redirect:/viviendas";
    }
}
