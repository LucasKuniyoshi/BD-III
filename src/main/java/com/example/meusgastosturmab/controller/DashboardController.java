package com.example.meusgastosturmab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.meusgastosturmab.domain.dto.dashboard.DashboardResponseDTO;
import com.example.meusgastosturmab.domain.service.DashboardService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obterFluxoCaixa(
        @RequestParam(name="periodoInicial") String periodoInicial,
         @RequestParam(name="periodoFinal") String periodoFinal){
            System.out.println("controller");
            return ResponseEntity.ok(dashboardService
            .obterFluxoCaixa(periodoInicial, periodoFinal));
    }
}
