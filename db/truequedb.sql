-- Criar o banco de dados
-- CREATE DATABASE trueque_db;

-- Criar tabela de usuários
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password TEXT,
    phone VARCHAR(20) UNIQUE NOT NULL,
    google_id VARCHAR(255) UNIQUE,
    profile_picture TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Criar tabela de anúncios
CREATE TABLE listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    condition VARCHAR(50),
    city VARCHAR(100),
    state VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ativo' CHECK (status IN ('ativo', 'negociando', 'trocado')),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Criar tabela de imagens dos anúncios
CREATE TABLE listing_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id UUID REFERENCES listings(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL
);

-- Criar tabela de ofertas de troca
CREATE TABLE trade_offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status VARCHAR(20) DEFAULT 'pendente' CHECK (status IN ('pendente', 'aceita', 'recusada')),
    created_at TIMESTAMP DEFAULT NOW(),
    accepted_at TIMESTAMP,
    rejected_at TIMESTAMP,
    offered_listing_id UUID REFERENCES listings(id) ON DELETE CASCADE,
    requested_listing_id UUID REFERENCES listings(id) ON DELETE CASCADE
);

-- Criar função para finalizar os anúncios e recusar outras ofertas ao aceitar uma proposta
-- CREATE OR REPLACE FUNCTION finalize_listings()
-- RETURNS TRIGGER AS $$
-- DECLARE
--     affected_listing UUID;
-- BEGIN
--     -- Marcar todos os anúncios dessa oferta como 'trocado'
--     UPDATE listings
--     SET status = 'trocado'
--     WHERE id IN (
--         SELECT listing_id
--         FROM listing_trade_offer
--         WHERE trade_offer_id = NEW.id
--     );

    -- Recusar outras ofertas pendentes que envolvam os mesmos anúncios
--     UPDATE trade_offers
--     SET status = 'recusada',
--         rejected_at = NOW()
--     WHERE id <> NEW.id
--       AND status = 'pendente'
--       AND id IN (
--           SELECT trade_offer_id
--           FROM listing_trade_offer
--           WHERE listing_id IN (
--               SELECT listing_id
--               FROM listing_trade_offer
--               WHERE trade_offer_id = NEW.id
--           )
--       );

--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;

-- -- Criar trigger para acionar a função após aceitação da proposta
-- CREATE TRIGGER trigger_finalize_trade
-- AFTER UPDATE ON trade_offers
-- FOR EACH ROW
-- WHEN (NEW.status = 'aceita' AND OLD.status IS DISTINCT FROM 'aceita')
-- EXECUTE FUNCTION finalize_listings();

-- Inserir dados na tabela users
INSERT INTO users (id, name, email, phone, password, google_id, profile_picture, city, state) VALUES
    (gen_random_uuid(), 'Alice Silva', 'alice@example.com', '63990637299', '$2a$10$bbB/dah1pHheRah2XMjKXOWCqS24laiCaAJ7x0uRfDvmRJ8SvL6Jy', NULL, NULL, 'São Paulo', 'SP'), -- senha 123456
    (gen_random_uuid(), 'Bruno Souza', 'bruno@example.com', '21987654321', NULL, 'google456', 'https://example.com/bruno.jpg', 'Rio de Janeiro', 'RJ'),
    (gen_random_uuid(), 'Carla Mendes', 'carla@example.com', '31987654321', NULL, 'google789', 'https://example.com/carla.jpg', 'Belo Horizonte', 'MG'),
    (gen_random_uuid(), 'Diego Rocha', 'diego@example.com', '41987654321', NULL, 'google101', 'https://example.com/diego.jpg', 'Curitiba', 'PR');

-- Inserir dados na tabela listings
INSERT INTO listings (id, user_id, title, description, category, condition, city, state) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'alice@example.com'), 'Notebook Dell', 'Notebook em ótimo estado', 'Eletrônicos', 'Usado', 'São Paulo', 'SP'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'bruno@example.com'), 'Bicicleta Caloi', 'Bicicleta quase nova', 'Esportes', 'Seminovo', 'Rio de Janeiro', 'RJ'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'carla@example.com'), 'Sofá Retrátil', 'Sofá confortável e bem conservado', 'Móveis', 'Usado', 'Belo Horizonte', 'MG'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'diego@example.com'), 'Videogame PS4', 'PS4 com dois controles', 'Eletrônicos', 'Usado', 'Curitiba', 'PR');

-- Inserir dados na tabela listing_images
INSERT INTO listing_images (id, listing_id, image_url) VALUES
    (gen_random_uuid(), (SELECT id FROM listings WHERE title = 'Notebook Dell'), 'https://example.com/notebook1.jpg'),
    (gen_random_uuid(), (SELECT id FROM listings WHERE title = 'Bicicleta Caloi'), 'https://example.com/bike.jpg'),
    (gen_random_uuid(), (SELECT id FROM listings WHERE title = 'Sofá Retrátil'), 'https://example.com/sofa.jpg'),
    (gen_random_uuid(), (SELECT id FROM listings WHERE title = 'Videogame PS4'), 'https://example.com/ps4.jpg');

-- Inserir ofertas de troca na tabela trade_offers
INSERT INTO trade_offers (id, status, created_at, offered_listing_id, requested_listing_id)
VALUES
    (gen_random_uuid(), 'pendente', NOW(), 
     (SELECT id FROM listings WHERE title = 'Notebook Dell'), 
     (SELECT id FROM listings WHERE title = 'Bicicleta Caloi')),  -- Alice oferece o notebook e Bruno solicita a bicicleta

    (gen_random_uuid(), 'pendente', NOW(), 
     (SELECT id FROM listings WHERE title = 'Sofá Retrátil'), 
     (SELECT id FROM listings WHERE title = 'Videogame PS4')),  -- Carla oferece o sofá e Diego solicita o PS4

    (gen_random_uuid(), 'pendente', NOW(), 
     (SELECT id FROM listings WHERE title = 'Videogame PS4'), 
     (SELECT id FROM listings WHERE title = 'Bicicleta Caloi')),  -- Diego oferece o PS4 e Bruno solicita a bicicleta

    (gen_random_uuid(), 'pendente', NOW(), 
     (SELECT id FROM listings WHERE title = 'Notebook Dell'), 
     (SELECT id FROM listings WHERE title = 'Sofá Retrátil'));  -- Alice oferece o notebook e Carla solicita o sofá
