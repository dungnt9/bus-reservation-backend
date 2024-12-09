package com.example.be.repository;

import com.example.be.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    @Query("SELECT u FROM Users u WHERE u.deletedAt IS NULL")
    List<Users> findAllNotDeleted();

    @Query("SELECT u FROM Users u WHERE u.userId = :id AND u.deletedAt IS NULL")
    Users findByIdNotDeleted(Integer id);
}