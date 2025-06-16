CREATE TABLE IF NOT EXISTS billing_accounts (
    id UUID PRIMARY KEY,
    patient_id VARCHAR(255) UNIQUE NOT NULL,
    patient_name VARCHAR(255) NOT NULL,
    patient_email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(50) NOT NULL
);