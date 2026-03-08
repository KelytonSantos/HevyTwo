#!/bin/bash
# =============================================================
#  Seed script — popula o banco para testar o dashboard
#  Requer: curl, jq, psql
#  Uso: ./seed.sh
# =============================================================

set -e

BASE="http://localhost:8080"
DB_URL="postgresql://user:1234@localhost:5432/hevy"
PSQL="docker exec -i hevy-db psql -U user -d hevy"

EXERCISES=("VPPtusI" "8d8qJQI" "JGKowMS")

# cores
GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

log() { echo -e "${CYAN}[seed]${NC} $1"; }
ok()  { echo -e "${GREEN}[ok]${NC}   $1"; }

# =============================================================
# 1. Registrar usuários (1 principal + 2 extras pro percentil)
# =============================================================
log "Registrando usuários..."

register() {
  local user=$1 email=$2 pass=$3
  curl -s -X POST "$BASE/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$user\",\"email\":\"$email\",\"password\":\"$pass\"}" > /dev/null
}

register "seed_main"  "seed_main@hevy.com"  "Test1234!"
register "seed_user2" "seed_user2@hevy.com" "Test1234!"
register "seed_user3" "seed_user3@hevy.com" "Test1234!"
ok "Usuários criados"

# =============================================================
# 2. Login do usuário principal
# =============================================================
log "Fazendo login..."
LOGIN=$(curl -s -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"seed_main@hevy.com","password":"Test1234!"}')

JWT=$(echo "$LOGIN" | jq -r '.jwt')
AUTH="Authorization: Bearer $JWT"
ok "JWT obtido"

# =============================================================
# 3. Criar rotina
# =============================================================
log "Criando rotina..."
ROUTINE=$(curl -s -X POST "$BASE/routines" \
  -H "Content-Type: application/json" \
  -H "$AUTH" \
  -d '{"routineName":"Treino Seed"}')

ROUTINE_ID=$(echo "$ROUTINE" | jq -r '.id')
ok "Rotina criada: $ROUTINE_ID"

# =============================================================
# 4. Adicionar exercícios à rotina
# =============================================================
log "Adicionando exercícios..."
for EX_ID in "${EXERCISES[@]}"; do
  curl -s -X POST "$BASE/workouts/$EX_ID/$ROUTINE_ID" \
    -H "$AUTH" > /dev/null
  ok "Exercício $EX_ID adicionado"
done

# =============================================================
# Função: executa 1 sessão completa e retorna o executionId
# =============================================================
run_session() {
  local weight=$1   # peso em kg para os sets

  # inicia execução
  local EXEC_RESP
  EXEC_RESP=$(curl -s -X POST "$BASE/routines/init/$ROUTINE_ID" -H "$AUTH")
  local EXEC_ID
  EXEC_ID=$(echo "$EXEC_RESP" | jq -r '.id')

  # busca workoutLogs gerados
  local LOGS
  LOGS=$(curl -s "$BASE/workouts/my/$EXEC_ID" -H "$AUTH")
  local LOG_IDS
  mapfile -t LOG_IDS < <(echo "$LOGS" | jq -r '.[].id')

  # para cada log, cria e finaliza 1 set
  local order=1
  for LOG_ID in "${LOG_IDS[@]}"; do
    local SET_RESP
    SET_RESP=$(curl -s -X POST "$BASE/workouts/init/$LOG_ID" \
      -H "Content-Type: application/json" \
      -H "$AUTH" \
      -d "{\"unit\":\"kg\",\"rep\":10,\"orderIndex\":$order,\"measure\":$weight,\"type\":\"NORMAL_SET\",\"restTime\":60}")
    local SET_ID
    SET_ID=$(echo "$SET_RESP" | jq -r '.id')

    curl -s -X POST "$BASE/workouts/set/$SET_ID/finish" -H "$AUTH" > /dev/null
    ((order++))
  done

  # finaliza execução
  curl -s -X PUT "$BASE/routines/$EXEC_ID" -H "$AUTH" > /dev/null

  echo "$EXEC_ID"
}

# =============================================================
# 5. Criar sessões no mês atual (março/2026)
# =============================================================
log "Criando sessões do mês atual..."
EXEC_MAR_1=$(run_session 80)  ; ok "Sessão março-1: $EXEC_MAR_1"
EXEC_MAR_2=$(run_session 90)  ; ok "Sessão março-2: $EXEC_MAR_2"
EXEC_MAR_3=$(run_session 100) ; ok "Sessão março-3: $EXEC_MAR_3"

# =============================================================
# 6. Criar sessões retroativas (fevereiro e janeiro)
#    API só cria com Instant.now(), então criamos e backdatamos
# =============================================================
log "Criando sessões retroativas..."
EXEC_FEV_1=$(run_session 75) ; ok "Sessão fev-1: $EXEC_FEV_1"
EXEC_FEV_2=$(run_session 85) ; ok "Sessão fev-2: $EXEC_FEV_2"
EXEC_JAN_1=$(run_session 70) ; ok "Sessão jan-1: $EXEC_JAN_1"

# =============================================================
# 7. Atualizar datas no banco para simular meses passados
# =============================================================
log "Backdatando execuções no banco..."

$PSQL <<SQL
-- Fevereiro 2026
UPDATE routines_executions
SET started_at = '2026-02-10 10:00:00+00',
    ended_at   = '2026-02-10 11:00:00+00'
WHERE id IN ('$EXEC_FEV_1', '$EXEC_FEV_2');

UPDATE workout_logs
SET created_at = '2026-02-10 10:00:00+00'
WHERE execution_id IN ('$EXEC_FEV_1', '$EXEC_FEV_2');

UPDATE workout_sets
SET start_at = '2026-02-10 10:00:00+00',
    end_at   = '2026-02-10 10:30:00+00'
WHERE workout_log_id IN (
  SELECT id FROM workout_logs WHERE execution_id IN ('$EXEC_FEV_1', '$EXEC_FEV_2')
);

-- Janeiro 2026
UPDATE routines_executions
SET started_at = '2026-01-15 09:00:00+00',
    ended_at   = '2026-01-15 10:00:00+00'
WHERE id = '$EXEC_JAN_1';

UPDATE workout_logs
SET created_at = '2026-01-15 09:00:00+00'
WHERE execution_id = '$EXEC_JAN_1';

UPDATE workout_sets
SET start_at = '2026-01-15 09:00:00+00',
    end_at   = '2026-01-15 09:30:00+00'
WHERE workout_log_id IN (
  SELECT id FROM workout_logs WHERE execution_id = '$EXEC_JAN_1'
);
SQL
ok "Datas atualizadas"

# =============================================================
# 8. Criar volume para os usuários extras (pro percentil ter base)
# =============================================================
log "Populando usuários extras para percentil..."

seed_extra_user() {
  local email=$1 pass=$2 weight=$3

  local J
  J=$(curl -s -X POST "$BASE/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$email\",\"password\":\"$pass\"}" | jq -r '.jwt')

  local AUTH2="Authorization: Bearer $J"

  local R
  R=$(curl -s -X POST "$BASE/routines" \
    -H "Content-Type: application/json" \
    -H "$AUTH2" \
    -d '{"routineName":"Treino Extra"}' | jq -r '.id')

  for EX_ID in "${EXERCISES[@]}"; do
    curl -s -X POST "$BASE/workouts/$EX_ID/$R" -H "$AUTH2" > /dev/null
  done

  local E
  E=$(curl -s -X POST "$BASE/routines/init/$R" -H "$AUTH2" | jq -r '.id')

  local LOGS2
  LOGS2=$(curl -s "$BASE/workouts/my/$E" -H "$AUTH2")
  local IDS2
  mapfile -t IDS2 < <(echo "$LOGS2" | jq -r '.[].id')

  local o=1
  for LID in "${IDS2[@]}"; do
    local SID
    SID=$(curl -s -X POST "$BASE/workouts/init/$LID" \
      -H "Content-Type: application/json" \
      -H "$AUTH2" \
      -d "{\"unit\":\"kg\",\"rep\":10,\"orderIndex\":$o,\"measure\":$weight,\"type\":\"NORMAL_SET\",\"restTime\":60}" \
      | jq -r '.id')
    curl -s -X POST "$BASE/workouts/set/$SID/finish" -H "$AUTH2" > /dev/null
    ((o++))
  done

  curl -s -X PUT "$BASE/routines/$E" -H "$AUTH2" > /dev/null
}

seed_extra_user "seed_user2@hevy.com" "Test1234!" 40
seed_extra_user "seed_user3@hevy.com" "Test1234!" 60
ok "Usuários extras populados"

# =============================================================
# 9. Testar dashboard
# =============================================================
log "Testando dashboard do usuário principal..."
DASH=$(curl -s "$BASE/dashboard" -H "$AUTH")
echo ""
echo "$DASH" | jq .
echo ""
ok "Seed concluído!"
