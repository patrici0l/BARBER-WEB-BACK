package com.barberia.barberia_backend.appointment;

import com.barberia.barberia_backend.common.enums.AppointmentStatus;
import com.barberia.barberia_backend.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);

        List<Appointment> findByUserOrderByAppointmentDateDescStartTimeDesc(User user);

        List<Appointment> findAllByOrderByAppointmentDateDescStartTimeDesc();

        boolean existsByAppointmentDateAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        LocalDate appointmentDate,
                        AppointmentStatus status,
                        LocalTime endTime,
                        LocalTime startTime);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("""
                        select a
                        from Appointment a
                        where a.appointmentDate = :appointmentDate
                          and a.status = :status
                          and a.startTime < :endTime
                          and a.endTime > :startTime
                        """)
        List<Appointment> findOverlappingAppointmentsForUpdate(
                        @Param("appointmentDate") LocalDate appointmentDate,
                        @Param("status") AppointmentStatus status,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        @Query("""
                        select a
                        from Appointment a
                        join fetch a.user
                        join fetch a.service
                        where a.status = :status
                          and (
                                a.appointmentDate > :fromDate
                                or (a.appointmentDate = :fromDate and a.startTime >= :fromTime)
                              )
                          and (
                                a.appointmentDate < :toDate
                                or (a.appointmentDate = :toDate and a.startTime <= :toTime)
                              )
                        order by a.appointmentDate asc, a.startTime asc
                        """)
        List<Appointment> findAppointmentsStartingBetween(
                        @Param("status") AppointmentStatus status,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("fromTime") LocalTime fromTime,
                        @Param("toDate") LocalDate toDate,
                        @Param("toTime") LocalTime toTime);
}
