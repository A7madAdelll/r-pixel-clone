CREATE TABLE
IF NOT EXISTS users
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW
()
);

CREATE TABLE
IF NOT EXISTS pixels
(
    x           INT NOT NULL,
    y           INT NOT NULL,
    color       VARCHAR
(7) NOT NULL DEFAULT '#FFFFFF',
    updated_at  TIMESTAMP DEFAULT NOW
(),
    updated_by  VARCHAR
(100),
    PRIMARY KEY
(x, y)
);

INSERT INTO pixels
    (x, y, color)
SELECT
    (n / 100),
    (n % 100),
    '#FFFFFF'
FROM generate_series(0, 9999) AS n
ON CONFLICT DO NOTHING;
