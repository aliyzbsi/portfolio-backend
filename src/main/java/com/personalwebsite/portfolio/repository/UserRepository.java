package com.personalwebsite.portfolio.repository;

import com.personalwebsite.portfolio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Find a user by email - Email'e göre bir kullanıcı bul
     * @param email The email to search for - aranacak email adresi
     * @return Optional containing the user if found - Eğer kullanıcı bulunursa opsiyonel dönüş
     */
    @Query("SELECT u FROM User u WHERE u.email=:email")
    Optional<User> findByEmail(String email);
}
