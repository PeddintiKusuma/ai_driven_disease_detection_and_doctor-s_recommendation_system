package com.aihealthcare.service;

import com.aihealthcare.dto.response.DoctorSlotAvailabilityResponse;
import com.aihealthcare.dto.response.TimeSlotResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Appointment;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.enums.AppointmentStatus;
import com.aihealthcare.repository.AppointmentRepository;
import com.aihealthcare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentSlotService {

    public static final int MAX_DAILY_APPOINTMENTS = 30;
    public static final int SLOT_DURATION_MINUTES = 15;
    public static final int MAX_PATIENTS_PER_SLOT = 1;

    private static final Map<DayOfWeek, String> DAY_ALIASES = Map.of(
            DayOfWeek.MONDAY, "mon",
            DayOfWeek.TUESDAY, "tue",
            DayOfWeek.WEDNESDAY, "wed",
            DayOfWeek.THURSDAY, "thu",
            DayOfWeek.FRIDAY, "fri",
            DayOfWeek.SATURDAY, "sat",
            DayOfWeek.SUNDAY, "sun"
    );

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public DoctorSlotAvailabilityResponse getSlotAvailability(Long doctorId, LocalDate date) {
        return getSlotAvailability(doctorId, date, null);
    }

    public DoctorSlotAvailabilityResponse getSlotAvailability(Long doctorId, LocalDate date, Long excludeAppointmentId) {
        if (date.isBefore(LocalDate.now())) {
            throw new AppException("Cannot book appointments in the past", HttpStatus.BAD_REQUEST);
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));

        boolean scheduleConfigured = isScheduleConfigured(doctor);
        String availableDays = doctor.getAvailableDays();
        String availableTime = doctor.getAvailableTime();

        if (!scheduleConfigured) {
            return baseResponse(doctor, date, excludeAppointmentId)
                    .slots(List.of())
                    .scheduleConfigured(false)
                    .doctorAvailableOnDate(false)
                    .message("This doctor has not set their availability yet. Please try another doctor or check back later.")
                    .build();
        }

        if (!isDoctorAvailableOnDay(doctor, date)) {
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            return baseResponse(doctor, date, excludeAppointmentId)
                    .slots(List.of())
                    .scheduleConfigured(true)
                    .doctorAvailableOnDate(false)
                    .message("Dr. " + doctor.getName().replace("Dr. ", "") +
                            " is not available on " + dayName + "s. Available days: " + availableDays +
                            ". Please pick one of those days.")
                    .build();
        }

        Optional<LocalTime[]> hours = parseAvailableHours(doctor.getAvailableTime());
        if (hours.isEmpty()) {
            return baseResponse(doctor, date, excludeAppointmentId)
                    .slots(List.of())
                    .scheduleConfigured(true)
                    .doctorAvailableOnDate(true)
                    .message("Doctor's working hours are not configured correctly. Please contact the clinic.")
                    .build();
        }

        long bookedCount = countBookingsForDay(doctorId, date, excludeAppointmentId);
        if (bookedCount >= MAX_DAILY_APPOINTMENTS) {
            return baseResponse(doctor, date, excludeAppointmentId)
                    .slots(List.of())
                    .bookedCount((int) bookedCount)
                    .dailyLimitReached(true)
                    .scheduleConfigured(true)
                    .doctorAvailableOnDate(true)
                    .message("This doctor's appointments are fully booked for this day (" +
                            MAX_DAILY_APPOINTMENTS + "/" + MAX_DAILY_APPOINTMENTS +
                            "). Please choose another date from: " + availableDays + ".")
                    .build();
        }

        LocalTime start = hours.get()[0];
        LocalTime end = hours.get()[1];

        Map<LocalTime, Long> bookingsPerSlot = appointmentRepository
                .findByDoctorIdAndAppointmentDate(doctorId, date).stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> excludeAppointmentId == null || !a.getId().equals(excludeAppointmentId))
                .collect(Collectors.groupingBy(Appointment::getAppointmentTime, Collectors.counting()));

        List<TimeSlotResponse> availableSlots = new ArrayList<>();
        LocalTime current = start;
        while (current.isBefore(end)) {
            long slotBookings = bookingsPerSlot.getOrDefault(current, 0L);
            boolean slotAvailable = slotBookings < MAX_PATIENTS_PER_SLOT;
            if (date.equals(LocalDate.now()) && current.isBefore(LocalTime.now())) {
                slotAvailable = false;
            }
            if (slotAvailable) {
                availableSlots.add(TimeSlotResponse.builder()
                        .time(current)
                        .label(formatTime(current))
                        .available(true)
                        .build());
            }
            current = current.plusMinutes(SLOT_DURATION_MINUTES);
        }

        String message = null;
        if (availableSlots.isEmpty()) {
            message = "All time slots between " + availableTime +
                    " are booked for this date. Please choose another time or date (" + availableDays + ").";
        }

        return baseResponse(doctor, date, excludeAppointmentId)
                .slots(availableSlots)
                .bookedCount((int) bookedCount)
                .scheduleConfigured(true)
                .doctorAvailableOnDate(true)
                .message(message)
                .build();
    }

    public void validateBooking(Long doctorId, LocalDate date, LocalTime time, Long excludeAppointmentId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));

        if (date.isBefore(LocalDate.now())) {
            throw new AppException("Cannot book appointments in the past", HttpStatus.BAD_REQUEST);
        }

        if (!isScheduleConfigured(doctor)) {
            throw new AppException("This doctor has not set their availability schedule yet", HttpStatus.BAD_REQUEST);
        }

        if (!isDoctorAvailableOnDay(doctor, date)) {
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            throw new AppException(
                    "Doctor is not available on " + dayName + ". Available days: " + doctor.getAvailableDays(),
                    HttpStatus.BAD_REQUEST);
        }

        long bookedCount = countBookingsForDay(doctorId, date, excludeAppointmentId);
        if (bookedCount >= MAX_DAILY_APPOINTMENTS) {
            throw new AppException(
                    "This doctor is fully booked for this day. All " + MAX_DAILY_APPOINTMENTS +
                            " appointment slots are taken. Please choose another date.",
                    HttpStatus.CONFLICT);
        }

        long slotBookings = excludeAppointmentId != null
                ? appointmentRepository.countByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
                        doctorId, date, time, AppointmentStatus.CANCELLED, excludeAppointmentId)
                : appointmentRepository.countByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctorId, date, time, AppointmentStatus.CANCELLED);

        if (slotBookings >= MAX_PATIENTS_PER_SLOT) {
            throw new AppException(
                    "This time slot is already booked. Please select another available time.",
                    HttpStatus.CONFLICT);
        }

        if (date.equals(LocalDate.now()) && time.isBefore(LocalTime.now())) {
            throw new AppException("Cannot book a time slot in the past", HttpStatus.BAD_REQUEST);
        }

        boolean slotInSchedule = getSlotAvailability(doctorId, date, excludeAppointmentId)
                .getSlots().stream()
                .anyMatch(s -> s.getTime().equals(time));
        if (!slotInSchedule) {
            throw new AppException(
                    "Selected time is outside the doctor's available hours (" + doctor.getAvailableTime() + ")",
                    HttpStatus.CONFLICT);
        }
    }

    private DoctorSlotAvailabilityResponse.DoctorSlotAvailabilityResponseBuilder baseResponse(
            Doctor doctor, LocalDate date, Long excludeAppointmentId) {
        return DoctorSlotAvailabilityResponse.builder()
                .bookedCount((int) countBookingsForDay(doctor.getId(), date, excludeAppointmentId))
                .dailyLimit(MAX_DAILY_APPOINTMENTS)
                .dailyLimitReached(false)
                .availableDays(doctor.getAvailableDays())
                .availableTime(doctor.getAvailableTime());
    }

    private boolean isScheduleConfigured(Doctor doctor) {
        return doctor.getAvailableDays() != null && !doctor.getAvailableDays().isBlank()
                && doctor.getAvailableTime() != null && !doctor.getAvailableTime().isBlank()
                && doctor.getAvailableTime().contains("-");
    }

    private boolean isDoctorAvailableOnDay(Doctor doctor, LocalDate date) {
        if (doctor.getAvailableDays() == null || doctor.getAvailableDays().isBlank()) {
            return false;
        }
        String alias = DAY_ALIASES.get(date.getDayOfWeek());
        String daysLower = doctor.getAvailableDays().toLowerCase(Locale.ENGLISH);
        for (String part : daysLower.split("[,\\s]+")) {
            String token = part.trim();
            if (token.isEmpty()) continue;
            if (alias.startsWith(token) || token.startsWith(alias)) {
                return true;
            }
        }
        return false;
    }

    private Optional<LocalTime[]> parseAvailableHours(String availableTime) {
        if (availableTime == null || !availableTime.contains("-")) {
            return Optional.empty();
        }
        try {
            String[] parts = availableTime.split("-");
            LocalTime start = LocalTime.parse(parts[0].trim());
            LocalTime end = LocalTime.parse(parts[1].trim());
            if (!start.isBefore(end)) {
                return Optional.empty();
            }
            return Optional.of(new LocalTime[]{start, end});
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private long countBookingsForDay(Long doctorId, LocalDate date, Long excludeAppointmentId) {
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date).stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> excludeAppointmentId == null || !a.getId().equals(excludeAppointmentId))
                .count();
    }

    private String formatTime(LocalTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        String ampm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
        return String.format("%d:%02d %s", displayHour, minute, ampm);
    }
}
