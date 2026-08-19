package com.aihealthcare.config;

import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.Specialization;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.model.enums.Gender;
import com.aihealthcare.model.enums.Role;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.SpecializationRepository;
import com.aihealthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DoctorDataSeeder {

    private static final String DOCTOR_PASSWORD = "Doctor@123";

    private final PasswordEncoder passwordEncoder;

    /**
     * name, email, phone, specialization, qualification, experience, rating, fee,
     * hospital, city, availableDays, availableTime, bio, licenseNumber
     */
    private static final Object[][] DOCTORS = {
        {"Dr. Priya Sharma", "priya.sharma@healix.in", "+91-9848012345", "General Physician",
            "MBBS, MD (General Medicine)", 15, 4.9, 500, "Apollo Hospital", "Hyderabad",
            "Mon,Tue,Wed,Thu,Fri", "09:00-17:00",
            "Senior general physician with 15 years of experience in primary care, fever management, and preventive health checkups.",
            "MCI-AP-2010-4521"},
        {"Dr. Arun Kumar", "arun.kumar@healix.in", "+91-9848023456", "Cardiologist",
            "MBBS, MD (Cardiology), DM", 18, 4.8, 800, "Heart Care Institute", "Hyderabad",
            "Mon,Wed,Fri", "10:00-18:00",
            "Interventional cardiologist specializing in hypertension, chest pain evaluation, and cardiac risk assessment.",
            "MCI-TG-2008-3312"},
        {"Dr. Sneha Reddy", "sneha.reddy@healix.in", "+91-9848034567", "Pulmonologist",
            "MBBS, MD (Pulmonology)", 12, 4.8, 750, "Lung Health Center", "Hyderabad",
            "Mon,Tue,Thu,Fri", "08:00-15:00",
            "Expert in respiratory conditions including asthma, bronchitis, cough, and influenza-related complications.",
            "MCI-AP-2012-7789"},
        {"Dr. Amit Patel", "amit.patel@healix.in", "+91-9848045678", "Dermatologist",
            "MBBS, MD (Dermatology)", 10, 4.7, 600, "Skin Wellness Clinic", "Bangalore",
            "Tue,Thu,Sat", "09:00-16:00",
            "Dermatologist treating eczema, allergic skin reactions, rashes, and general skin health concerns.",
            "MCI-KA-2014-5567"},
        {"Dr. Ananya Gupta", "ananya.gupta@healix.in", "+91-9848056789", "Neurologist",
            "MBBS, MD (Neurology)", 13, 4.9, 900, "Neuro Care Institute", "Mumbai",
            "Tue,Wed,Fri,Sat", "09:00-16:00",
            "Neurologist focused on migraines, headaches, dizziness, and nerve-related symptom evaluation.",
            "MCI-MH-2011-8890"},
        {"Dr. Vikram Singh", "vikram.singh@healix.in", "+91-9848067890", "Gastroenterologist",
            "MBBS, MD (Gastroenterology)", 11, 4.6, 700, "Digestive Care Hospital", "Delhi",
            "Mon,Wed,Thu,Sat", "10:00-17:00",
            "Specialist in gastritis, food poisoning, abdominal pain, and digestive system disorders.",
            "MCI-DL-2013-2234"},
        {"Dr. Lakshmi Iyer", "lakshmi.iyer@healix.in", "+91-9848078901", "Psychiatrist",
            "MBBS, MD (Psychiatry)", 16, 4.8, 800, "Mind Wellness Center", "Bangalore",
            "Mon,Tue,Wed,Thu,Fri", "09:00-18:00",
            "Psychiatrist providing care for anxiety, depression, stress-related conditions, and mental wellness.",
            "MCI-KA-2009-6678"},
        {"Dr. Suresh Nair", "suresh.nair@healix.in", "+91-9848089012", "Endocrinologist",
            "MBBS, MD (Endocrinology)", 12, 4.7, 750, "Diabetes Care Institute", "Kochi",
            "Mon,Wed,Fri,Sat", "08:00-16:00",
            "Endocrinologist specializing in diabetes management, thyroid disorders, and metabolic health.",
            "MCI-KL-2012-4456"},
    };

    @Bean
    @Profile("!test")
    CommandLineRunner seedDoctorsAndUsers(
            DoctorRepository doctorRepo,
            SpecializationRepository specRepo,
            UserRepository userRepo) {
        return args -> {
            seedAdmin(userRepo);
            deactivateLegacyDoctors(doctorRepo);
            seedDoctorProfiles(doctorRepo, specRepo);
            linkDoctorLoginAccounts(doctorRepo, userRepo);
        };
    }

    private void seedAdmin(UserRepository userRepo) {
        if (!userRepo.existsByEmail("admin@healix.in")) {
            if (userRepo.existsByEmail("admin@aihealthcare.com")) {
                userRepo.findByEmail("admin@aihealthcare.com").ifPresent(u -> {
                    u.setEmail("admin@healix.in");
                    userRepo.save(u);
                });
            } else {
                userRepo.save(User.builder()
                        .email("admin@healix.in")
                        .password(passwordEncoder.encode("Admin@123"))
                        .firstName("System").lastName("Admin")
                        .phone("+91-9000000000").age(35).gender(Gender.OTHER)
                        .role(Role.ADMIN).enabled(true).build());
            }
        }
    }

    private void deactivateLegacyDoctors(DoctorRepository doctorRepo) {
        doctorRepo.findAll().stream()
                .filter(d -> d.getEmail() != null && d.getEmail().contains("demo-hospital"))
                .forEach(d -> {
                    d.setApprovalStatus(DoctorApprovalStatus.REJECTED);
                    doctorRepo.save(d);
                });
    }

    private void seedDoctorProfiles(DoctorRepository doctorRepo, SpecializationRepository specRepo) {
        for (Object[] d : DOCTORS) {
            String email = (String) d[1];
            if (doctorRepo.findByEmail(email).isPresent()) continue;

            Specialization spec = specRepo.findByName((String) d[3]).orElse(null);
            if (spec == null) continue;

            doctorRepo.save(Doctor.builder()
                    .name((String) d[0])
                    .email(email)
                    .phone((String) d[2])
                    .specialization(spec)
                    .qualification((String) d[4])
                    .experience((Integer) d[5])
                    .rating(BigDecimal.valueOf((Double) d[6]))
                    .consultationFee(BigDecimal.valueOf((Integer) d[7]))
                    .hospital((String) d[8])
                    .city((String) d[9])
                    .availableDays((String) d[10])
                    .availableTime((String) d[11])
                    .bio((String) d[12])
                    .licenseNumber((String) d[13])
                    .approvalStatus(DoctorApprovalStatus.APPROVED)
                    .build());
        }
    }

    private void linkDoctorLoginAccounts(DoctorRepository doctorRepo, UserRepository userRepo) {
        for (Doctor doctor : doctorRepo.findAll()) {
            if (doctor.getUserId() != null) continue;

            User doctorUser = userRepo.findByEmail(doctor.getEmail()).orElse(null);

            if (doctorUser == null) {
                String[] nameParts = doctor.getName().replace("Dr. ", "").split(" ", 2);
                doctorUser = userRepo.save(User.builder()
                        .email(doctor.getEmail())
                        .password(passwordEncoder.encode(DOCTOR_PASSWORD))
                        .firstName(nameParts[0])
                        .lastName(nameParts.length > 1 ? nameParts[1] : "")
                        .phone(doctor.getPhone())
                        .role(Role.DOCTOR)
                        .enabled(true)
                        .build());
            } else if (doctorUser.getRole() != Role.DOCTOR) {
                doctorUser.setRole(Role.DOCTOR);
                userRepo.save(doctorUser);
            }

            doctor.setUserId(doctorUser.getId());
            if (doctor.getApprovalStatus() == null) {
                doctor.setApprovalStatus(DoctorApprovalStatus.APPROVED);
            }
            doctorRepo.save(doctor);
        }
    }
}
