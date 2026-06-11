package com.barberia.barberia_backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberServiceService {

    private final BarberServiceRepository barberServiceRepository;

    public List<BarberServiceResponse> findAllActive() {
        return barberServiceRepository.findByActiveTrue()
                .stream()
                .map(BarberServiceResponse::fromEntity)
                .toList();
    }

    public List<BarberServiceResponse> findAll() {
        return barberServiceRepository.findAll()
                .stream()
                .map(BarberServiceResponse::fromEntity)
                .toList();
    }

    public BarberServiceResponse findById(Long id) {
        BarberService service = getServiceById(id);
        return BarberServiceResponse.fromEntity(service);
    }

    public BarberServiceResponse create(BarberServiceRequest request) {
        BarberService service = BarberService.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .durationMinutes(request.durationMinutes())
                .imageUrl(cleanImageUrl(request.imageUrl()))
                .active(true)
                .build();

        BarberService savedService = barberServiceRepository.save(service);

        return BarberServiceResponse.fromEntity(savedService);
    }

    public BarberServiceResponse update(Long id, BarberServiceRequest request) {
        BarberService service = getServiceById(id);

        service.setName(request.name());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setDurationMinutes(request.durationMinutes());
        service.setImageUrl(cleanImageUrl(request.imageUrl()));

        BarberService updatedService = barberServiceRepository.save(service);

        return BarberServiceResponse.fromEntity(updatedService);
    }

    public BarberServiceResponse deactivate(Long id) {
        BarberService service = getServiceById(id);
        service.setActive(false);

        BarberService updatedService = barberServiceRepository.save(service);

        return BarberServiceResponse.fromEntity(updatedService);
    }

    public BarberServiceResponse activate(Long id) {
        BarberService service = getServiceById(id);
        service.setActive(true);

        BarberService updatedService = barberServiceRepository.save(service);

        return BarberServiceResponse.fromEntity(updatedService);
    }

    private BarberService getServiceById(Long id) {
        return barberServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con id: " + id));
    }

    private String cleanImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        return imageUrl.trim();
    }
}
