-- Create users table for testing
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    age INT,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test user Muhammad Ovi
INSERT INTO users (firstname, lastname, age, email) 
VALUES ('Muhammad', 'Ovi', 25, 'muhammad.ovi@example.com');

-- Insert additional test data for more comprehensive testing
INSERT INTO users (firstname, lastname, age, email) 
VALUES 
    ('John', 'Doe', 30, 'john.doe@example.com'),
    ('Jane', 'Smith', 28, 'jane.smith@example.com'),
    ('Alice', 'Johnson', 35, 'alice.johnson@example.com');