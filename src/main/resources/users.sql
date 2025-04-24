CREATE TABLE users (
                       id SERIAL PRIMARY KEY,                   -- Уникальный идентификатор пользователя
                       username VARCHAR(50) NOT NULL UNIQUE,    -- Имя пользователя (должно быть уникальным)
                       password CHAR(64) NOT NULL,              -- Хэшированный пароль (SHA-256, 64 символа)
                       role VARCHAR(10) CHECK (role IN ('ADMIN', 'USER')) NOT NULL, -- Роль пользователя
                       is_blocked BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO users (username, password, role) VALUES
                                                 ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'), -- Пароль: "admin123"
                                                 ('lol', '07123e1f482356c415f684407a3b8723e10b2cbbc0b8fcd6282c49d37c9c1abc', 'USER');   -- Пароль: "user123"

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,                   -- Уникальный идентификатор фильма
                          name VARCHAR(100) NOT NULL,              -- Название фильма
                          category VARCHAR(50) NOT NULL,           -- Категория фильма
                          price NUMERIC(10, 2) NOT NULL,           -- Цена биилета
                          quantity INT NOT NULL                    -- Количество билетов
);

CREATE TABLE cart (
                      id SERIAL PRIMARY KEY,                   -- Уникальный идентификатор записи корзины
                      user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Ссылка на ID пользователя
                      product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE, -- Ссылка на ID фильма
                      quantity INT NOT NULL                    -- номер места
);

CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,                   -- Уникальный идентификатор заказа
                        user_id INT NOT NULL REFERENCES users(id),  -- Ссылка на ID пользователя
                        product_id INT NOT NULL REFERENCES products(id), -- Ссылка на ID фильма
                        quantity INT NOT NULL,                   -- номер места
                        total_price NUMERIC(10, 2) NOT NULL,     -- Итоговая стоимость
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата и время заказа
);


CREATE TABLE seats (
                       id SERIAL PRIMARY KEY,                  -- Уникальный идентификатор места
                       product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE, -- Ссылка на ID фильма
                       seat_number INT NOT NULL,              -- Номер места
                       is_reserved BOOLEAN NOT NULL DEFAULT FALSE, -- Флаг: занято ли место
                       UNIQUE (product_id, seat_number)       -- Уникальная пара "фильм + номер места"
);
