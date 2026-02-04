CREATE TABLE regional (
    pk BIGSERIAL PRIMARY KEY,
    id INTEGER NOT NULL, -- id do endpoint externo
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_regional_nome_not_blank CHECK (length(trim(nome)) > 0)
);

CREATE INDEX ix_regional_id ON regional (id);
CREATE INDEX ix_regional_ativo ON regional (ativo);
