package com.aihealthcare.repository;

import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    long countByRole(Role role);

    @Query(value = "SELECT MONTH(created_at) as month, COUNT(*) as count FROM users GROUP BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> countByMonth();
}
