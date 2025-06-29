-- sample.users definition
-- Criação da sequência para users (compatível com H2)
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

-- Criação da tabela users (compatível com H2)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT DEFAULT NEXT VALUE FOR users_seq PRIMARY KEY,
    email VARCHAR(255) DEFAULT NULL,
    first_name VARCHAR(255) DEFAULT NULL,
    last_name VARCHAR(255) DEFAULT NULL,
    password VARCHAR(255) DEFAULT NULL,
    role VARCHAR(255) DEFAULT NULL -- H2 simula ENUMs como VARCHAR
);

-- sample.tokens definition
-- Criação da sequência para tokens (compatível com H2)
CREATE SEQUENCE tokens_seq START WITH 1 INCREMENT BY 1;

-- Criação da tabela tokens (compatível com H2)
CREATE TABLE tokens (
    expired BOOLEAN NOT NULL, -- BIT(1) no MySQL é BOOLEAN no H2
    revoked BOOLEAN NOT NULL,
    id BIGINT DEFAULT NEXT VALUE FOR tokens_seq PRIMARY KEY,
    user_id BIGINT DEFAULT NULL,
    token VARCHAR(255) DEFAULT NULL,
    token_type VARCHAR(255) DEFAULT NULL, -- H2 simula ENUMs como VARCHAR
    UNIQUE (token), -- Removido 'uk_token_token' para compatibilidade mais ampla
    FOREIGN KEY (user_id) REFERENCES users (id) -- Removido 'fk_token_user_id' e KEY
);