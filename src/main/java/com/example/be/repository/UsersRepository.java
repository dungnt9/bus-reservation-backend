package com.example.be.repository;

import com.example.be.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    @Query("SELECT u FROM Users u WHERE u.deletedAt IS NULL")
    List<Users> findAllNotDeleted();

    @Query("SELECT u FROM Users u WHERE u.userId = :id AND u.deletedAt IS NULL")
    Users findByIdNotDeleted(Integer id);

    @Query("SELECT u FROM Users u WHERE u.phoneNumber = :phoneNumber AND u.deletedAt IS NULL")
    Optional<Users> findByPhoneNumber(String phoneNumber);
}