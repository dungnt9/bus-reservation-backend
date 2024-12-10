package com.example.be.repository;

import com.example.be.model.Drivers;
//interface cung cấp các phương thức thao tác với CSDL (như save(), findById(), delete()
import org.springframework.data.jpa.repository.JpaRepository;
//annotation @Query, viết các câu truy vấn tùy chỉnh.
import org.springframework.data.jpa.repository.Query;
//Annotation @Repository đánh dấu lớp là 1 repository, giúp Spring nhận diện và quản lý các bean truy cập CSDL.
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriversRepository extends JpaRepository<Drivers, Integer> {
    // Tìm tất cả tài xế chưa bị xóa mềm
    @Query("SELECT d FROM Drivers d WHERE d.deletedAt IS NULL")
    List<Drivers> findAllNotDeleted();

    // Tìm tài xế chưa bị xóa mềm theo ID
    @Query("SELECT d FROM Drivers d WHERE d.driverId = :id AND d.deletedAt IS NULL")
    Drivers findByIdNotDeleted(Integer id);
}