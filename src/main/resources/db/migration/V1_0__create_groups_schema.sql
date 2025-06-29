-- sample.groups definition

-- Criação da sequência para groups (compatível com H2)
-- Removido CREATE OR REPLACE SEQUENCE e as opções ENGINE=InnoDB, nocache, nocycle
CREATE SEQUENCE groups_seq START WITH 1 INCREMENT BY 1;

-- Criação da tabela groups (compatível com H2)
-- Removido ENGINE=InnoDB, DEFAULT CHARSET, COLLATE
CREATE TABLE IF NOT EXISTS groups (
    id BIGINT DEFAULT NEXT VALUE FOR groups_seq PRIMARY KEY,
    name VARCHAR(255) DEFAULT NULL,
    organization_id BIGINT,
    CONSTRAINT fk_groups_organization FOREIGN KEY (organization_id) REFERENCES organizations(id)
);