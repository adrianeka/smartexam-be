-- ===================================================================================
-- Migration   : V14
-- Title       : Init Question-Media junction table
-- Author      : JiilanTj
-- Date        : 2026-04-16
-- Description : Many-to-many relationship between questions and media_files
-- ===================================================================================

CREATE TABLE question_media (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id     UUID            NOT NULL REFERENCES questions (id) ON DELETE CASCADE,
    media_file_id   UUID            NOT NULL REFERENCES media_files (id) ON DELETE CASCADE,
    position        INT             NOT NULL DEFAULT 0,
    UNIQUE (question_id, media_file_id)
);

CREATE INDEX idx_question_media_question_id   ON question_media (question_id);
CREATE INDEX idx_question_media_media_file_id ON question_media (media_file_id);
