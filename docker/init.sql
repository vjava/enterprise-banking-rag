CREATE EXTENSION IF NOT EXISTS vector;

-- Drop old table if it was created with 1536 dimensions
DROP TABLE IF EXISTS vector_store;

CREATE TABLE IF NOT EXISTS vector_store (
                                            id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(768) -- Matches nomic-embed-text dimensions
    );