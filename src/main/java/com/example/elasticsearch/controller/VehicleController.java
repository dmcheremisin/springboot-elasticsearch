package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.Vehicle;
import com.example.elasticsearch.search.SearchRequestDto;
import com.example.elasticsearch.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public void index(@RequestBody Vehicle vehicle) {
        vehicleService.index(vehicle);
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable String id) {
        return vehicleService.getById(id);
    }

    @PostMapping("/search")
    public List<Vehicle> index(@RequestBody SearchRequestDto searchRequestDto) {
        return vehicleService.search(searchRequestDto);
    }

}
