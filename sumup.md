# Executando uma Routine

> Pré-requisito: autenticado via JWT (`Authorization: Bearer <token>`)

---

## 1 — Iniciar a execução da rotina

```
POST /routines/init/{routineId}
```

Sem body. Retorna um `RoutineExecution` com seu `id` e cria automaticamente um `WorkoutLog` para cada exercício da rotina.

**Guarda o `routineExecution.id` retornado.**

---

## 2 — Buscar os WorkoutLogs (um por exercício)

```
GET /workouts/my/{routineExecutionId}
```

Sem body. Retorna a lista de `WorkoutLog`. Cada item tem seu próprio `id` (o `workoutLogId` usado no próximo passo), e está associado a um exercício.

---

## 3 — Para cada exercício: criar (iniciar) cada set

Repita para cada set de cada exercício:

```
POST /workouts/init/{workoutLogId}
```

**Body:**

```json
{
  "unit": "kg",
  "rep": 10,
  "orderIndex": 1,
  "measure": 80.0,
  "type": "NORMAL_SET",
  "restTime": 60
}
```

| Campo        | Tipo      | Descrição                                              |
| ------------ | --------- | ------------------------------------------------------ |
| `unit`       | `string`  | Unidade de medida (`"kg"`, `"lb"`, etc.)               |
| `rep`        | `integer` | Número de repetições                                   |
| `orderIndex` | `integer` | Posição do set na sequência (1, 2, 3…)                 |
| `measure`    | `decimal` | Peso / distância / tempo                               |
| `type`       | `enum`    | `NORMAL_SET`, `WARM_UP_SET`, `DROP_SET`, `FAILURE_SET` |
| `restTime`   | `integer` | Descanso em segundos                                   |

Retorna o `WorkoutSet` com seu `id`. **Guarda o `workoutSet.id`.**

---

## 4 — Finalizar cada set

```
POST /workouts/set/{workoutSetId}/finish
```

Sem body. Marca o set como `COMPLETED`.

> Para cancelar um set no lugar de finalizar: `POST /workouts/set/{workoutSetId}/cancel`

---

## 5 — Finalizar a execução da rotina

Depois de todos os sets de todos os exercícios concluídos:

```
PUT /routines/{routineExecutionId}
```

Sem body. Calcula o volume total, duração e marca a execução como `COMPLETED`.

---

## Resumo da ordem

```
POST /routines/init/{routineId}
  └─> GET /workouts/my/{routineExecutionId}
        └─> para cada workoutLog (exercício):
              └─> para cada set:
                    POST /workouts/init/{workoutLogId}       ← inicia set
                    POST /workouts/set/{workoutSetId}/finish  ← finaliza set
PUT /routines/{routineExecutionId}  ← finaliza a sessão
```
