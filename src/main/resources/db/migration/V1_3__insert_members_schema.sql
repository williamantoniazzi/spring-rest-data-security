-- sample.members test data

-- Usa NEXT VALUE FOR para obter o próximo ID da sequência (compatível com H2)
-- Adicione o campo 'email' aos INSERTs se ele estiver na sua tabela
INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 1, 17, 'Member 1 GRP 1', 'member1.grp1@example.com');
INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 1, 21, 'Member 2 GRP 1', 'member2.grp1@example.com');
INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 1, 45, 'Member 3 GRP 1', 'member3.grp1@example.com');

INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 2, 19, 'Member 1 GRP 2', 'member1.grp2@example.com');
INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 2, 65, 'Member 2 GRP 2', 'member2.grp2@example.com');

INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 3, 23, 'Member 1 GRP 3', 'member1.grp3@example.com');
INSERT INTO members (id, group_id, age, name, email) VALUES(NEXT VALUE FOR members_seq, 3, 44, 'Member 2 GRP 3', 'member2.grp3@example.com');
