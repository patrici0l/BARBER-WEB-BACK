package com.barberia.barberia_backend.businesshour;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessHourService {

    private final BusinessHourRepository businessHourRepository;

    public List<BusinessHourResponse> findAll() {
        return businessHourRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(BusinessHour::getDayOfWeek))
                .map(BusinessHourResponse::fromEntity)
                .toList();
    }

    public BusinessHourResponse findByDay(DayOfWeek dayOfWeek) {
        BusinessHour businessHour = businessHourRepository.findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado para el día: " + dayOfWeek));

        return BusinessHourResponse.fromEntity(businessHour);
    }

    public BusinessHourResponse create(BusinessHourRequest request) {
        if (businessHourRepository.existsByDayOfWeek(request.dayOfWeek())) {
            throw new IllegalArgumentException("Ya existe un horario configurado para el día: " + request.dayOfWeek());
        }

        validateBusinessHour(request);

        BusinessHour businessHour = BusinessHour.builder()
                .dayOfWeek(request.dayOfWeek())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .active(request.active())
                .build();

        BusinessHour saved = businessHourRepository.save(businessHour);

        return BusinessHourResponse.fromEntity(saved);
    }

    public BusinessHourResponse update(Long id, BusinessHourRequest request) {
        BusinessHour businessHour = businessHourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado con id: " + id));

        validateBusinessHour(request);

        businessHour.setDayOfWeek(request.dayOfWeek());
        businessHour.setOpenTime(request.openTime());
        businessHour.setCloseTime(request.closeTime());
        businessHour.setActive(request.active());

        BusinessHour updated = businessHourRepository.save(businessHour);

        return BusinessHourResponse.fromEntity(updated);
    }

    public BusinessHourResponse updateByDay(DayOfWeek dayOfWeek, BusinessHourRequest request) {
        BusinessHour businessHour = businessHourRepository.findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado para el día: " + dayOfWeek));

        validateBusinessHour(request);

        businessHour.setOpenTime(request.openTime());
        businessHour.setCloseTime(request.closeTime());
        businessHour.setActive(request.active());

        BusinessHour updated = businessHourRepository.save(businessHour);

        return BusinessHourResponse.fromEntity(updated);
    }

    private void validateBusinessHour(BusinessHourRequest request) {
        if (Boolean.TRUE.equals(request.active())) {
            if (request.openTime() == null || request.closeTime() == null) {
                throw new IllegalArgumentException("Si el día está activo, debe tener hora de apertura y cierre");
            }

            if (!request.openTime().isBefore(request.closeTime())) {
                throw new IllegalArgumentException("La hora de apertura debe ser menor que la hora de cierre");
            }
        }

        if (Boolean.FALSE.equals(request.active())) {
            if (request.openTime() != null || request.closeTime() != null) {
                throw new IllegalArgumentException("Si el día está cerrado, openTime y closeTime deben ser null");
            }
        }
    }
}