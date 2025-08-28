package com.fleetstudio.Employee.Suggestion.controller;


import com.fleetstudio.Employee.Suggestion.model.Employee;
import com.fleetstudio.Employee.Suggestion.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Get all employees (for employee directory)
     */
    @GetMapping
   @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employee by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get employee by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
        Optional<Employee> employee = employeeService.getEmployeeByEmail(email);
        
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new employee
     */
    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            Employee createdEmployee = employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to create employee"));
        }
    }

    /**
     * Update existing employee
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to update employee"));
        }
    }

    /**
     * Delete employee
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok(new SuccessResponse("Employee deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to delete employee"));
        }
    }

    /**
     * Search employees by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(@RequestParam String name) {
        List<Employee> employees = employeeService.searchEmployeesByName(name);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees by department
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees by position
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Employee>> getEmployeesByPosition(@PathVariable String position) {
        List<Employee> employees = employeeService.getEmployeesByPosition(position);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employees by department and position
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Employee>> getEmployeesByFilter(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position) {
        List<Employee> employees = employeeService.getEmployeesByDepartmentAndPosition(department, position);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get all departments
     */
    @GetMapping("/departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        List<String> departments = employeeService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Get all positions
     */
    @GetMapping("/positions")
    public ResponseEntity<List<String>> getAllPositions() {
        List<String> positions = employeeService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    /**
     * Check if employee exists by email
     */
    @GetMapping("/exists/{email}")
    public ResponseEntity<ExistenceResponse> checkEmployeeExists(@PathVariable String email) {
        boolean exists = employeeService.employeeExistsByEmail(email);
        return ResponseEntity.ok(new ExistenceResponse(exists));
    }

    /**
     * Get employee statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<EmployeeStatistics> getEmployeeStatistics() {
        EmployeeStatistics stats = new EmployeeStatistics();
        stats.totalEmployees = employeeService.getTotalEmployeeCount();
        stats.departments = employeeService.getAllDepartments();
        stats.positions = employeeService.getAllPositions();
        return ResponseEntity.ok(stats);
    }

    /**
     * Initialize sample employees (for demo purposes)
     */
    @PostMapping("/sample-data")
    public ResponseEntity<SuccessResponse> createSampleEmployees() {
        try {
            employeeService.createSampleEmployees();
            return ResponseEntity.ok(new SuccessResponse("Sample employees created successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SuccessResponse("Failed to create sample employees"));
        }
    }

    // Response classes
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class ExistenceResponse {
        private boolean exists;

        public ExistenceResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() { return exists; }
    }

    public static class EmployeeStatistics {
        public long totalEmployees;
        public List<String> departments;
        public List<String> positions;
    }
}