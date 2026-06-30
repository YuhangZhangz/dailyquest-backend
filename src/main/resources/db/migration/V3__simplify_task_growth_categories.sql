UPDATE daily_task
SET growth_category = 'NONE'
WHERE growth_category IS NULL
   OR growth_category = ''
   OR growth_category NOT IN ('NONE', 'WORK', 'SCHOOL', 'HEALTH', 'PERSONAL');
