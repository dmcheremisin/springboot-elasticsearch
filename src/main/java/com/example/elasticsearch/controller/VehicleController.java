package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.Vehicle;
import com.example.elasticsearch.search.SearchRequestDto;
import com.example.elasticsearch.service.VehicleDummyDataService;
import com.example.elasticsearch.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleDummyDataService vehicleDummyDataService;

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

    @GetMapping("/created-since/{date}")
    public List<Vehicle> index(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return vehicleService.getAllVehiclesCreatedSince(date);
    }

    @PostMapping("/search-created-since-with-request/{date}")
    public List<Vehicle> searchCreatedSince(
            @RequestBody final SearchRequestDto searchRequestDto,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") final Date date) {
        return vehicleService.searchCreatedSinceWithRequest(searchRequestDto, date);
    }

    @PostMapping("/insert-dummy-data")
    public void insertDummyData() {
        vehicleDummyDataService.insertDummyData();
    }

}
