package com.condominio.sistema.controller;

import com.condominio.sistema.model.Vehiculo;
import com.condominio.sistema.model.Vivienda;
import com.condominio.sistema.util.Paginacion;
import com.condominio.sistema.service.VehiculoService;
import com.condominio.sistema.service.ViviendaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final ViviendaService viviendaService;

    public VehiculoController(
            VehiculoService vehiculoService,
            ViviendaService viviendaService) {

        this.vehiculoService = vehiculoService;
        this.viviendaService = viviendaService;
    }

    @GetMapping
    public String listar(
            Model model,
            @RequestParam(defaultValue = "1") int pagina) {
        Paginacion.aplicar(model, "vehiculos", vehiculoService.listarTodos(), pagina);
        return "vehiculos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setEstado(true);

        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("esNuevo", true);
        model.addAttribute("viviendas", viviendaService.listarTodas());

        return "vehiculos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Integer idVehiculo,
            @RequestParam Integer viviendaId,
            @RequestParam String placa,
            @RequestParam String marca,
            @RequestParam String modelo,
            @RequestParam String color,
            @RequestParam String tipo,
            @RequestParam Boolean estado,
            Model model) {

        Vivienda vivienda = viviendaService.buscarPorId(viviendaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vivienda no encontrada: " + viviendaId
                        )
                );

        if (vehiculoService.existeOtraConPlaca(placa, idVehiculo)) {

            model.addAttribute(
                    "error",
                    "Esa placa ya está registrada en otro vehículo."
            );
            model.addAttribute("vehiculo", vehiculoCon(
                    idVehiculo, vivienda, placa, marca,
                    modelo, color, tipo, estado));
            model.addAttribute("esNuevo", idVehiculo == null);
            model.addAttribute("viviendas", viviendaService.listarTodas());

            return "vehiculos/formulario";
        }

        Vehiculo vehiculo = vehiculoCon(
                idVehiculo, vivienda, placa, marca, modelo, color, tipo, estado);

        vehiculoService.guardar(vehiculo);

        return "redirect:/vehiculos";
    }

    private Vehiculo vehiculoCon(
            Integer idVehiculo,
            Vivienda vivienda,
            String placa,
            String marca,
            String modelo,
            String color,
            String tipo,
            Boolean estado) {

        Vehiculo vehiculo = new Vehiculo();

        vehiculo.setIdVehiculo(idVehiculo);
        vehiculo.setVivienda(vivienda);
        vehiculo.setPlaca(placa);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setColor(color);
        vehiculo.setTipo(tipo);
        vehiculo.setEstado(estado);

        return vehiculo;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Integer id,
            Model model) {

        Vehiculo vehiculo = vehiculoService.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vehículo no encontrado: " + id
                        )
                );

        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("esNuevo", false);
        model.addAttribute("viviendas", viviendaService.listarTodas());

        return "vehiculos/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {

        vehiculoService.eliminar(id);

        return "redirect:/vehiculos";
    }
}
