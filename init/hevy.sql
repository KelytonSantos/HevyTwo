CREATE TYPE "series" AS ENUM (
  'drop_set',
  'warm_up_set',
  'normal_set',
  'failure_set'
);

CREATE TABLE "users" (
  "id" uuid PRIMARY KEY NOT NULL,
  "username" varchar(150) UNIQUE NOT NULL,
  "email" varchar NOT NULL,
  "password" varchar NOT NULL,
  "profile_img" bytea,
  "followers" integer NOT NULL DEFAULT 0,
  "following" integer NOT NULL DEFAULT 0,
  "workouts" integer NOT NULL DEFAULT 0,
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz,
  "deleted_at" timestamptz
);

CREATE TABLE "routines" (
  "id" uuid PRIMARY KEY NOT NULL,
  "user_id" uuid NOT NULL,
  "routine_name" varchar NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz,
  "deleted_at" timestamptz
);

CREATE TABLE "workouts" (
  "id" uuid PRIMARY KEY NOT NULL,
  "routine_id" uuid,
  "workout_name" varchar NOT NULL,
  "workout_image" bytea,
  "rest_time" timestamptz
);

CREATE TABLE "workout_sets" (
  "id" uuid PRIMARY KEY NOT NULL,
  "workout_id" uuid,
  "set" series DEFAULT 'normal_set',
  "unit" varchar(10) NOT NULL,
  "measure" decimal(5,2),
  "repetitions" integer
);

COMMENT ON COLUMN "users"."profile_img" IS 'armazenamento direto no relacional em bytes';

ALTER TABLE "routines" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "workouts" ADD FOREIGN KEY ("routine_id") REFERENCES "routines" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "workout_sets" ADD FOREIGN KEY ("workout_id") REFERENCES "workouts" ("id") DEFERRABLE INITIALLY IMMEDIATE;
