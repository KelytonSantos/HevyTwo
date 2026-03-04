CREATE TYPE "series_type" AS ENUM (
  'drop_set',
  'warm_up_set',
  'normal_set',
  'failure_set'
);

CREATE TYPE "status_type" AS ENUM (
  'pending',
  'canceled',
  'completed'
);

CREATE TABLE "users" (
  "id" uuid PRIMARY KEY,
  "username" varchar(150) UNIQUE NOT NULL,
  "email" varchar UNIQUE NOT NULL,
  "password" varchar NOT NULL,
  "profile_img" bytea,
  "followers" integer DEFAULT 0,
  "following" integer DEFAULT 0,
  "workouts_count" integer DEFAULT 0,
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz,
  "deleted_at" timestamptz
);

CREATE TABLE "routines" (
  "id" uuid PRIMARY KEY,
  "user_id" uuid,
  "routine_name" varchar NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz,
  "deleted_at" timestamptz
);

CREATE TABLE "routine_workouts" (
  "id" uuid PRIMARY KEY,
  "routine_id" uuid,
  "exercise_api_id" integer,
  "workout_name" varchar,
  "rest_time_seconds" integer,
  "order_index" integer
);

CREATE TABLE "routines_executions" (
  "id" uuid PRIMARY KEY,
  "routine_id" uuid,
  "user_id" uuid,
  "status" status_type DEFAULT 'pending',
  "total_time_seconds" integer,
  "total_weight_volume" decimal,
  "started_at" timestamptz NOT NULL,
  "ended_at" timestamptz
);

CREATE TABLE "workout_logs" (
  "id" uuid PRIMARY KEY,
  "execution_id" uuid,
  "exercise_api_id" integer,
  "workout_name" varchar,
  "workout_image" varchar
);

CREATE TABLE "workout_sets" (
  "id" uuid PRIMARY KEY,
  "workout_log_id" uuid,
  "set_type" series_type DEFAULT 'normal_set',
  "unit" varchar(10) NOT NULL,
  "measure" decimal(5,2),
  "repetitions" integer,
  "rest_time_actual" integer,
  "start_at" timestamptz,
  "end_at" timestamptz,
  "status" status_type DEFAULT 'pending',
  "order_index" integer
);

COMMENT ON COLUMN "users"."workouts_count" IS 'Contador total de treinos feitos';

COMMENT ON COLUMN "routine_workouts"."exercise_api_id" IS 'ID vindo da Wger API';

COMMENT ON COLUMN "routine_workouts"."rest_time_seconds" IS 'Tempo de descanso planejado';

COMMENT ON COLUMN "routine_workouts"."order_index" IS 'Ordem dos exercícios no treino';

COMMENT ON COLUMN "routines_executions"."routine_id" IS 'Referência ao modelo original';

COMMENT ON COLUMN "routines_executions"."total_time_seconds" IS 'Calculado no final: end_at - start_at';

COMMENT ON COLUMN "routines_executions"."total_weight_volume" IS 'Soma de peso * reps de todos os sets';

COMMENT ON COLUMN "workout_sets"."unit" IS 'kg, lbs, etc';

COMMENT ON COLUMN "workout_sets"."measure" IS 'Peso utilizado';

COMMENT ON COLUMN "workout_sets"."rest_time_actual" IS 'Quanto tempo de descanso ele realmente fez';

ALTER TABLE "routines" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "routine_workouts" ADD FOREIGN KEY ("routine_id") REFERENCES "routines" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "routines_executions" ADD FOREIGN KEY ("routine_id") REFERENCES "routines" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "routines_executions" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "workout_logs" ADD FOREIGN KEY ("execution_id") REFERENCES "routines_executions" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "workout_sets" ADD FOREIGN KEY ("workout_log_id") REFERENCES "workout_logs" ("id") DEFERRABLE INITIALLY IMMEDIATE;
