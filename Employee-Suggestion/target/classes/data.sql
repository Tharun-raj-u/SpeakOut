INSERT IGNORE INTO employees (name, email, password, department, position, role, created_at, updated_at)
VALUES (
  'Admin',
  'admin@fleetstudio.com',
  '$2a$10$Fl0/6n/zkifJEwOg3AL9/.ZdxX2rQpSa3qdrZMl1xm5jJQu8h.C3S',
  'Management',
  'System Admin',
  'ROLE_ADMIN',
  NOW(),
  NOW()
);
