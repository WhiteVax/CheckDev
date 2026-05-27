CREATE TABLE IF NOT EXISTS filter_profile(
id SERIAL PRIMARY KEY,
name VARCHAR UNIQUE
);

INSERT INTO filter_profile(name) VALUES('Я автор');
INSERT INTO filter_profile(name) VALUES('Я участник');
INSERT INTO filter_profile(name) VALUES('Я не автор');
INSERT INTO filter_profile(name) VALUES('Я не участник');