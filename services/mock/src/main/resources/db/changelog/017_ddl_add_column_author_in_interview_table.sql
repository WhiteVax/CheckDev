ALTER TABLE IF EXISTS interview
    ADD COLUMN IF NOT EXISTS author varchar(765);

UPDATE interview
SET author = 'Author'
WHERE author IS NULL;