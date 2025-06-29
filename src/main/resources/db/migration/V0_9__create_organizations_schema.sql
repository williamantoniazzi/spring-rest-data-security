-- sample.organizations definition

-- Criação da sequência para organizations (compatível com H2)
CREATE SEQUENCE organizations_seq START WITH 1 INCREMENT BY 1;

-- Criação da tabela organizations (compatível com H2)
CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT DEFAULT NEXT VALUE FOR organizations_seq PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cnpj VARCHAR(255),
    street VARCHAR(255),
    number VARCHAR(255),
    neighborhood VARCHAR(255),
    zip_code VARCHAR(255), -- snake_case para compatibilidade com JPA default
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    institution_name VARCHAR(255),
    headquarters_country VARCHAR(255)
);

-- Note: O H2 não usa ENGINE=InnoDB, CHARSET ou COLLATE.
-- Para o H2, as colunas @Embedded (como Address) são criadas diretamente na tabela principal.