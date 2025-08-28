package com.fleetstudio.Employee.Suggestion.service;

import com.fleetstudio.Employee.Suggestion.model.Employee;
import com.fleetstudio.Employee.Suggestion.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Get all employees ordered by name
     */
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get employee by ID
     */
    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    /**
     * Get employee by email
     */
    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    /**
     * Create new employee
     */
    public Employee createEmployee(Employee employee) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + employee.getEmail() + " already exists");
        }
        return employeeRepository.save(employee);
    }

    /**
     * Update existing employee
     */
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        
        if (existingEmployee.isEmpty()) {
            throw new IllegalArgumentException("Employee with ID " + id + " not found");
        }

        Employee employee = existingEmployee.get();
        
        // Check if email is being changed and if new email already exists
        if (!employee.getEmail().equals(updatedEmployee.getEmail()) && 
            employeeRepository.existsByEmail(updatedEmployee.getEmail())) {
            throw new IllegalArgumentException("Email " + updatedEmployee.getEmail() + " is already in use");
        }

        // Update fields
        employee.setName(updatedEmployee.getName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setDepartment(updatedEmployee.getDepartment());
        employee.setPosition(updatedEmployee.getPosition());

        return employeeRepository.save(employee);
    }

    /**
     * Delete employee (should be used carefully as it affects suggestions)
     */
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee with ID " + id + " not found");
        }
        employeeRepository.deleteById(id);
    }

    /**
     * Search employees by name
     */
    @Transactional(readOnly = true)
    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get employees by department
     */
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartmentIgnoreCase(department);
    }

    /**
     * Get employees by position
     */
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByPosition(String position) {
        return employeeRepository.findByPositionIgnoreCase(position);
    }

    /**
     * Get employees by department and position
     */
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartmentAndPosition(String department, String position) {
        return employeeRepository.findByDepartmentAndPosition(department, position);
    }

    /**
     * Get all distinct departments
     */
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return employeeRepository.findAllDistinctDepartments();
    }

    /**
     * Get all distinct positions
     */
    @Transactional(readOnly = true)
    public List<String> getAllPositions() {
        return employeeRepository.findAllDistinctPositions();
    }

    /**
     * Check if employee exists by email
     */
    @Transactional(readOnly = true)
    public boolean employeeExistsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    /**
     * Get total employee count
     */
    @Transactional(readOnly = true)
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    /**
     * Get employee count by department
     */
    @Transactional(readOnly = true)
    public long getEmployeeCountByDepartment(String department) {
        return employeeRepository.countByDepartment(department);
    }

    /**
     * Create sample employees for demo purposes
     */
    public void createSampleEmployees() {
        if (employeeRepository.count() == 0) {
            List<Employee> sampleEmployees = List.of(
                new Employee("John Smith", "john.smith@company.com", "Engineering", "Software Engineer"),
                new Employee("Sarah Johnson", "sarah.johnson@company.com", "Engineering", "Senior Developer"),
                new Employee("Mike Chen", "mike.chen@company.com", "Engineering", "Tech Lead"),
                new Employee("Emily Davis", "emily.davis@company.com", "Product", "Product Manager"),
                new Employee("Robert Wilson", "robert.wilson@company.com", "Product", "UX Designer"),
                new Employee("Lisa Anderson", "lisa.anderson@company.com", "Marketing", "Marketing Manager"),
                new Employee("David Brown", "david.brown@company.com", "Marketing", "Content Specialist"),
                new Employee("Jennifer Taylor", "jennifer.taylor@company.com", "Sales", "Sales Manager"),
                new Employee("Kevin Martinez", "kevin.martinez@company.com", "Sales", "Account Executive"),
                new Employee("Amanda White", "amanda.white@company.com", "HR", "HR Manager"),
                new Employee("James Garcia", "james.garcia@company.com", "Finance", "Financial Analyst"),
                new Employee("Michelle Lee", "michelle.lee@company.com", "Operations", "Operations Manager"),
                new Employee("Christopher Moore", "chris.moore@company.com", "Engineering", "DevOps Engineer"),
                new Employee("Rachel Thompson", "rachel.thompson@company.com", "Product", "Product Designer"),
                new Employee("Daniel Jackson", "daniel.jackson@company.com", "Customer Success", "Customer Success Manager")
            );

            employeeRepository.saveAll(sampleEmployees);
        }
    }

    /**
     * Validate employee data
     */
    private void validateEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee email is required");
        }
        if (!employee.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}