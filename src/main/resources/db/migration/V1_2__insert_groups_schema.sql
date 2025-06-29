-- sample.groups test data

-- Usa NEXT VALUE FOR para obter o próximo ID da sequência (compatível com H2)
INSERT INTO groups (id, name, organization_id) VALUES(NEXT VALUE FOR groups_seq, 'Grupo 1 - Teste', NULL); -- Adicione organization_id se for NOT NULL ou ajuste para NULL
INSERT INTO groups (id, name, organization_id) VALUES(NEXT VALUE FOR groups_seq, 'Grupo 2 - Teste', NULL);
INSERT INTO groups (id, name, organization_id) VALUES(NEXT VALUE FOR groups_seq, 'Grupo 3 - Teste', NULL);
