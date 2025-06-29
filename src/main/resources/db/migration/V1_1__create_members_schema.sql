-- sample.members definition

-- Criação da sequência para members (compatível com H2)
-- Removido CREATE OR REPLACE SEQUENCE e as opções ENGINE=InnoDB, nocache, nocycle
CREATE SEQUENCE members_seq START WITH 1 INCREMENT BY 1;

-- Criação da tabela members (compatível com H2)
-- Removido ENGINE=InnoDB, DEFAULT CHARSET, COLLATE
CREATE TABLE IF NOT EXISTS members (
    age INT DEFAULT NULL,
    group_id BIGINT NOT NULL,
    id BIGINT DEFAULT NEXT VALUE FOR members_seq PRIMARY KEY,
    name VARCHAR(255) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL, -- Adicionado campo 'email' se existir na sua entidade Member
    KEY fk_members_group (group_id),
    CONSTRAINT fk_members_group FOREIGN KEY (group_id) REFERENCES groups (id)
);