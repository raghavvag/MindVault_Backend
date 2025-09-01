-- Requires users table (V1)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE documents (
                           id BIGSERIAL PRIMARY KEY,
                           uuid UUID NOT NULL DEFAULT gen_random_uuid(),
                           user_id BIGINT NOT NULL,
                           title TEXT,
                           filename VARCHAR(1024) NOT NULL,
                           storage_path TEXT NOT NULL,
                           file_type VARCHAR(128),
                           size BIGINT,
                           status VARCHAR(32) NOT NULL DEFAULT 'UPLOADED',
                           metadata_json TEXT,
                           uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
                           CONSTRAINT uq_documents_uuid UNIQUE (uuid),
                           CONSTRAINT fk_documents_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_documents_userid ON documents(user_id);
CREATE INDEX idx_documents_status ON documents(status);
-- simple text index for metadata searches (for small scale)
CREATE INDEX idx_documents_metadata_gin ON documents USING GIN (to_tsvector('english', coalesce(metadata_json, '')));
