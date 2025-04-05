-- CREATE DATABASE trueque_db

-- Criar tabela de usuários
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password TEXT, -- Apenas para login tradicional
    phone VARCHAR(20),
    google_id VARCHAR(255) UNIQUE, -- Apenas para login via Google
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
    category VARCHAR(100), -- Ex: Eletrônicos, Roupas, Móveis, etc.
    condition VARCHAR(50), -- [Novo, Usado, etc.]
    images TEXT[], -- Lista de URLs de imagens do item
    city VARCHAR(100), -- Localização do anúncio
    state VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ativo', -- [ativo, negociando, finalizado]
    created_at TIMESTAMP DEFAULT NOW()
);

-- Criar tabela de ofertas de troca
CREATE TABLE trade_offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    offered_by UUID REFERENCES users(id) ON DELETE CASCADE,
    received_by UUID REFERENCES users(id) ON DELETE CASCADE,
    offered_item_id UUID REFERENCES listings(id) ON DELETE CASCADE,
    requested_item_id UUID REFERENCES listings(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'pendente',
    created_at TIMESTAMP DEFAULT NOW(),
    accepted_at TIMESTAMP
);

-- Criar função para finalizar anúncios e recusar outras ofertas ao aceitar uma proposta
CREATE OR REPLACE FUNCTION finalize_listings()
RETURNS TRIGGER AS $$
BEGIN
    -- Finaliza os anúncios envolvidos na troca
    UPDATE listings 
    SET status = 'finalizado'
    WHERE id IN (NEW.offered_item_id, NEW.requested_item_id);
    
    -- Recusa todas as outras propostas para esses anúncios
    UPDATE trade_offers
    SET status = 'recusada'
    WHERE (offered_item_id = NEW.offered_item_id OR requested_item_id = NEW.requested_item_id)
    AND id <> NEW.id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criar gatilho para executar a função ao aceitar uma proposta
CREATE TRIGGER trigger_finalize_trade
AFTER UPDATE ON trade_offers
FOR EACH ROW
WHEN (NEW.status = 'aceita')
EXECUTE FUNCTION finalize_listings();

-- Inserir dados na tabela users
INSERT INTO users (id, name, email, phone, password, google_id, profile_picture, city, state) VALUES
    (gen_random_uuid(), 'Alice Silva', 'alice@example.com', '$2a$10$bbB/dah1pHheRah2XMjKXOWCqS24laiCaAJ7x0uRfDvmRJ8SvL6Jy', NULL, NULL, 'São Paulo', 'SP'), -- senha 123456
    (gen_random_uuid(), 'Bruno Souza', 'bruno@example.com', '21987654321', NULL, 'google456', 'https://example.com/bruno.jpg', 'Rio de Janeiro', 'RJ'),
    (gen_random_uuid(), 'Carla Mendes', 'carla@example.com', '31987654321', NULL, 'google789', 'https://example.com/carla.jpg', 'Belo Horizonte', 'MG'),
    (gen_random_uuid(), 'Diego Rocha', 'diego@example.com', '41987654321', NULL, 'google101', 'https://example.com/diego.jpg', 'Curitiba', 'PR');

-- Inserir dados na tabela listings
INSERT INTO listings (id, user_id, title, description, category, condition, images, city, state) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'alice@example.com'), 'Notebook Dell', 'Notebook em ótimo estado', 'Eletrônicos', 'Usado', ARRAY['https://example.com/notebook1.jpg'], 'São Paulo', 'SP'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'bruno@example.com'), 'Bicicleta Caloi', 'Bicicleta quase nova', 'Esportes', 'Seminovo', ARRAY['https://example.com/bike.jpg'], 'Rio de Janeiro', 'RJ'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'carla@example.com'), 'Sofá Retrátil', 'Sofá confortável e bem conservado', 'Móveis', 'Usado', ARRAY['https://example.com/sofa.jpg'], 'Belo Horizonte', 'MG'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'diego@example.com'), 'Videogame PS4', 'PS4 com dois controles', 'Eletrônicos', 'Usado', ARRAY['https://example.com/ps4.jpg'], 'Curitiba', 'PR');

-- Inserir dados na tabela trade_offers
INSERT INTO trade_offers (id, offered_by, received_by, offered_item_id, requested_item_id) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'alice@example.com'), (SELECT id FROM users WHERE email = 'bruno@example.com'), (SELECT id FROM listings WHERE title = 'Notebook Dell'), (SELECT id FROM listings WHERE title = 'Bicicleta Caloi')),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'bruno@example.com'), (SELECT id FROM users WHERE email = 'carla@example.com'), (SELECT id FROM listings WHERE title = 'Bicicleta Caloi'), (SELECT id FROM listings WHERE title = 'Sofá Retrátil')),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'carla@example.com'), (SELECT id FROM users WHERE email = 'diego@example.com'), (SELECT id FROM listings WHERE title = 'Sofá Retrátil'), (SELECT id FROM listings WHERE title = 'Videogame PS4')),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'diego@example.com'), (SELECT id FROM users WHERE email = 'alice@example.com'), (SELECT id FROM listings WHERE title = 'Videogame PS4'), (SELECT id FROM listings WHERE title = 'Notebook Dell'));