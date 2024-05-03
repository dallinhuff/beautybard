-- a user profile for the application
CREATE TABLE IF NOT EXISTS app_user (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username  VARCHAR(32) UNIQUE NOT NULL,
    email     VARCHAR(128) UNIQUE NOT NULL,
    password  TEXT NOT NULL,
    image_url TEXT,
    bio       TEXT
);

-- a follow relationship between 2 users
CREATE TABLE IF NOT EXISTS follow (
    follower  UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    followee  UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (follower, followee)
);

-- the number of followers a user has
CREATE VIEW IF NOT EXISTS num_followers AS (
    SELECT followee AS id, COUNT(*)
    FROM follow
    GROUP BY followee
);

-- the number of users a user follows
CREATE VIEW IF NOT EXISTS num_following AS (
    SELECT follower AS id, COUNT(*)
    FROM follow
    GROUP BY follower
);

-- a company/brand that makes products
CREATE TABLE IF NOT EXISTS brand (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(64) NOT NULL,
    quality     VARCHAR(32) NOT NULL,
    image_url   TEXT,
    description TEXT,

    CONSTRAINT quality_kind
        CHECK (quality IN ('luxury', 'mid_range', 'drug_store'))
);

-- a product products by a company
-- that users purchase/use/like/review
CREATE TABLE IF NOT EXISTS product (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand_id    UUID NOT NULL REFERENCES brand(id) ON DELETE RESTRICT,
    name        VARCHAR(64) NOT NULL,
    cost        INT4 NOT NULL,
    image_url   TEXT,
    description TEXT
);

CREATE INDEX IF NOT EXISTS product_by_brand ON product(brand_id);

-- a relationship between a user and a product they use
CREATE TABLE IF NOT EXISTS product_user (
    user_id    UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    timestamp  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, product_id)
);

-- a relationship between a user and a product they enjoy
CREATE TABLE IF NOT EXISTS favorite_product (
    user_id    UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    timestamp  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, product_id)
);

-- a review/rating a user gave a product
CREATE TABLE IF NOT EXISTS product_review (
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    product_id      UUID NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    rating          INT4 NOT NULL,
    would_buy_again BOOL NOT NULL DEFAULT FALSE,
    review          TEXT,
    image_urls      TEXT[] NOT NULL DEFAULT [],
    created         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edited          TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS hair_product (
    id    UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    kind  VARCHAR(32) NOT NULL,
    scent VARCHAR(64),

    CONSTRAINT hair_product_kind
        CHECK (kind in ('shampoo', 'conditioner', 'styling_product'))
);

CREATE TABLE IF NOT EXISTS face_product (
    id          UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    kind        VARCHAR(32) NOT NULL,
    formula     VARCHAR(32) NOT NULL,
    color       VARCHAR(64) NOT NULL,
    shade_name  VARCHAR(64) NOT NULL,
    sheen       VARCHAR(32) NOT NULL,

    CONSTRAINT face_product_kind
        CHECK (kind IN ('foundation', 'concealer', 'blush', 'bronzer', 'highlighter')),
    CONSTRAINT face_product_formula
        CHECK (formula IN ('liquid', 'cream', 'powder')),
    CONSTRAINT face_product_sheen
        CHECK (sheen IN ('matte', 'dewy', 'satin'))
);

CREATE TABLE IF NOT EXISTS skincare_product (
    id                UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    kind              VARCHAR(32) NOT NULL,
    active_ingredient VARCHAR(64),

    CONSTRAINT skincare_product_kind
        CHECK (kind IN ('cleanser', 'moisturizer', 'serum', 'toner', 'mask'))
);

CREATE TABLE IF NOT EXISTS lip_product (
    id          UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    kind        VARCHAR(32) NOT NULL,
    color       VARCHAR(64) NOT NULL,
    shade_name  VARCHAR(64) NOT NULL,
    sheen       VARCHAR(32) NOT NULL,
    consistency VARCHAR(32) NOT NULL,
    hydrating   BOOLEAN NOT NULL DEFAULT FALSE,
    scent       VARCHAR(64),

    CONSTRAINT lip_product_kind
        CHECK (kind in ('lipstick', 'lip_gloss', 'lip_balm', 'lip_oil', 'lip_stain', 'lip_liner')),
    CONSTRAINT lip_product_sheen
        CHECK (sheen IN ('matte', 'glossy', 'satin')),
    CONSTRAINT lip_product_consistency
        CHECK (consistency in ('solid', 'liquid'))
);

CREATE TABLE IF NOT EXISTS eye_product (
    id         UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    kind       VARCHAR(32) NOT NULL,
    color      VARCHAR(64) NOT NULL,
    shade_name VARCHAR(64) NOT NULL,
    glitter    BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT eye_product_kind
        CHECK (kind IN ('eye_shadow', 'eye_liner', 'mascara'))
);

CREATE TABLE IF NOT EXISTS brow_product (
    id         UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    kind       VARCHAR(32) NOT NULL,
    color      VARCHAR(64) NOT NULL,
    shade_name VARCHAR(64) NOT NULL,

    CONSTRAINT brow_product_kind
        CHECK (kind IN ('brow_gel', 'brow_pencil', 'brow_powder'))
);

CREATE TABLE IF NOT EXISTS fragrance_product (
    id    UUID PRIMARY KEY REFERENCES product(id) ON DELETE CASCADE,
    scent VARCHAR(64) NOT NULL
);
