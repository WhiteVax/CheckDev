ALTER TABLE wisher
ADD CONSTRAINT unique_interview_user UNIQUE (interview_id, user_id);