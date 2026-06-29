ALTER TABLE daily_task
    ADD COLUMN IF NOT EXISTS growth_category VARCHAR(30);

UPDATE daily_task
SET growth_category = 'NONE'
WHERE growth_category IS NULL;

ALTER TABLE daily_task
    ALTER COLUMN growth_category SET DEFAULT 'NONE',
    ALTER COLUMN growth_category SET NOT NULL;
