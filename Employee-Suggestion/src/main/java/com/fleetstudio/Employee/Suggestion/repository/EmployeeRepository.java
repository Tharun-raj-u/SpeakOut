package com.fleetstudio.Employee.Suggestion.repository;


import com.fleetstudio.Employee.Suggestion.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Find employee by email address
     */
    Optional<Employee> findByEmail(String email);
    
    /**
     * Find employees by department
     */
    List<Employee> findByDepartmentIgnoreCase(String department);
    
    /**
     * Find employees by position
     */
    List<Employee> findByPositionIgnoreCase(String position);
    
    /**
     * Check if employee exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find employees by name containing search term (case insensitive)
     */
    List<Employee> findByNameContainingIgnoreCase(String nameSearch);
    
    /**
     * Find all employees ordered by name
     */
    List<Employee> findAllByOrderByNameAsc();
    
    /**
     * Get all distinct departments
     */
    @Query("SELECT DISTINCT e.department FROM Employee e WHERE e.department IS NOT NULL ORDER BY e.department")
    List<String> findAllDistinctDepartments();
    
    /**
     * Get all distinct positions
     */
    @Query("SELECT DISTINCT e.position FROM Employee e WHERE e.position IS NOT NULL ORDER BY e.position")
    List<String> findAllDistinctPositions();
    
    /**
     * Find employees by department and position
     */
    @Query("SELECT e FROM Employee e WHERE " +
           "(:department IS NULL OR LOWER(e.department) = LOWER(:department)) AND " +
           "(:position IS NULL OR LOWER(e.position) = LOWER(:position)) " +
           "ORDER BY e.name ASC")
    List<Employee> findByDepartmentAndPosition(@Param("department") String department, 
                                             @Param("position") String position);
    
    /**
     * Count employees by department
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE LOWER(e.department) = LOWER(:department)")
    long countByDepartment(@Param("department") String department);
}