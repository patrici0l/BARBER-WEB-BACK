package com.barberia.barberia_backend.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberServiceRepository extends JpaRepository<BarberService, Long> {

    List<BarberService> findByActiveTrue();

    List<BarberService> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}