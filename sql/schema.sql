CREATE DATABASE IF NOT EXISTS food_donation_db;
USE food_donation_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS donations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    quantity INT NOT NULL,
    unit VARCHAR(50) NOT NULL,
    expiry_date DATETIME,
    pickup_address TEXT,
    source_type VARCHAR(50),
    source_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    donation_id INT NOT NULL,
    recipient_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),
    read_status BOOLEAN DEFAULT FALSE,
    related_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS emergency_aids (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    emergency_type VARCHAR(100),
    urgency VARCHAR(50),
    people_affected INT,
    location TEXT,
    required_items TEXT,
    required_quantity INT,
    contact_phone VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS distribution_places (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    address TEXT,
    contact_person VARCHAR(255),
    contact_phone VARCHAR(50),
    operating_hours VARCHAR(255),
    capacity INT,
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS distribution_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    place_id INT NOT NULL,
    day_of_week VARCHAR(20),
    time_slot VARCHAR(50),
    food_type VARCHAR(100),
    quantity_target INT,
    unit VARCHAR(50),
    assigned_volunteer VARCHAR(255),
    volunteer_phone VARCHAR(50),
    donation_id INT NULL,
    status VARCHAR(50),
    notes TEXT,
    recurring BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (place_id) REFERENCES distribution_places(id) ON DELETE CASCADE,
    FOREIGN KEY (donation_id) REFERENCES donations(id) ON DELETE SET NULL
);

-- Seed data for users
INSERT IGNORE INTO users (id, name, email, password_hash, role, phone, address, is_active) VALUES
(1, 'Admin', 'admin@foodshare.com', '$2a$10$q8jD3G00ZWvAd30pSsuu2O6G/ALZPDAWkzu3Q9EgW6dd68UfkZ4RW', 'admin', '1234567890', 'Admin Office', 1),
(2, 'Ravi Sharma (City Bakery)', 'donor@foodshare.com', '$2a$10$83YgHwL4B.w6H3R5iW8JteD0bN3r9a2w3L5aB1p8o8Y5h0r7.e6wK', 'donor', '9876543210', '12 MG Road, Downtown', 1),
(3, 'Hope Shelter & Care NGO', 'recipient@foodshare.com', '$2a$10$Q7eY9r1t3u5i7o9p1a3s5d7f9g1h3j5k7l9z1x3c5v7b9n1m3q5w', 'recipient', '9123456780', '45 Peace Lane, North District', 1);

-- Seed data for distribution places
INSERT IGNORE INTO distribution_places (id, name, category, address, contact_person, contact_phone, operating_hours, capacity, active) VALUES
(1, 'Happy Paws Rescue', 'dog_food_service', '123 Bark Ave', 'John Doe', '555-0101', '9 AM - 5 PM', 50, 1),
(2, 'Sunset Retirement Home', 'old_age_home', '456 Golden Years Way', 'Jane Smith', '555-0102', '24/7', 100, 1),
(3, 'Sri Ram Mandir', 'temple', '789 Faith Blvd', 'Pandit Ji', '555-0103', '6 AM - 8 PM', 500, 1),
(4, 'Hope Orphanage', 'orphanage', '101 Children St', 'Sister Mary', '555-0104', '24/7', 75, 1),
(5, 'City Community Kitchen', 'community_kitchen', '202 Center Sq', 'Chef Mike', '555-0105', '11 AM - 2 PM', 200, 1),
(6, 'General Hospital', 'hospital', '303 Health Rd', 'Dr. Adams', '555-0106', '24/7', 300, 1),
(7, 'Springfield Elementary', 'school', '404 Education Ln', 'Principal Skinner', '555-0107', '8 AM - 3 PM', 400, 1),
(8, 'Downtown Shelter', 'other', '505 Main St', 'Bob Builder', '555-0108', '6 PM - 8 AM', 150, 1);

-- Seed data for distribution schedules
INSERT IGNORE INTO distribution_schedule (id, place_id, day_of_week, time_slot, food_type, quantity_target, unit, assigned_volunteer, volunteer_phone, status, recurring) VALUES
(1, 1, 'monday', 'morning', 'Dog Food', 20, 'kg', 'Alice', '555-1111', 'scheduled', 1),
(2, 5, 'wednesday', 'afternoon', 'Hot Meals', 100, 'plates', 'Bob', '555-2222', 'assigned', 1);
