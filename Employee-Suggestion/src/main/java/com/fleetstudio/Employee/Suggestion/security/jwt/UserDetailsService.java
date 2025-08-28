package com.fleetstudio.Employee.Suggestion.security.jwt;

import com.fleetstudio.Employee.Suggestion.model.Employee;
import com.fleetstudio.Employee.Suggestion.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

     @Autowired
     EmployeeRepository userRepo;

    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee user=  userRepo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Email Not exist"));

        return UserDetailsImpl.build(user);
    }
}
