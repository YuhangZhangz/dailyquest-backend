DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'daily_task'
          AND column_name = 'description'
          AND data_type <> 'text'
    ) THEN
        ALTER TABLE daily_task
            ALTER COLUMN description TYPE TEXT;
    END IF;
END
$$;
