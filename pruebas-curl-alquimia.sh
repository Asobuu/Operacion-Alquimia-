#!/bin/bash
# =====================================================================
# Operacion Alquimia - Protocolo Savitar
# Pruebas con curl de los 6 requerimientos (2.1 a 2.6)
# Corre el servidor primero con: ./mvnw spring-boot:run
# =====================================================================

BASE=http://localhost:8080

# ===== SECCION 2.1 - Procesar los 3 reportes al mismo tiempo =====
curl -s -X POST "$BASE/api/devices/process" \
  -H "Content-Type: application/json" \
  -d '{
  "reports": [
    {"deviceId": "STAR_LABS", "distance": 640.3124, "messageFragment": "La piedra  esta en  laboratorio secreto"},
    {"deviceId": "CCPD", "distance": 63.2456, "messageFragment": " piedra filosofal esta en el  secreto"},
    {"deviceId": "STAR_SATELLITE", "distance": 404.9691, "messageFragment": "La piedra filosofal  en el laboratorio "}
  ]
}'
echo ""
# Esperado: labLocation x=120.0 y=-40.0 (aprox), reconstructedMessage:
# "La piedra filosofal esta en el laboratorio secreto"


# ===== SECCION 2.3 (caso de error) - Resultado acumulado sin datos =====
curl -s -X GET "$BASE/api/devices/result"
echo ""
# Esperado: 409 CONFLICT, InsufficientDataException
# (nota: esto es antes de registrar nada; si corriste la seccion 2.1 antes
#  en la misma sesion del servidor, ya habra datos y este paso devolvera
#  el resultado en vez del error -- para probar el error, usa un servidor
#  recien arrancado)


# ===== SECCION 2.2 - Registrar cada dispositivo individualmente =====
curl -s -X POST "$BASE/api/devices/STAR_LABS" \
  -H "Content-Type: application/json" \
  -d '{"distance": 640.3124, "messageFragment": "La piedra  esta en  laboratorio secreto"}'
echo ""

curl -s -X POST "$BASE/api/devices/CCPD" \
  -H "Content-Type: application/json" \
  -d '{"distance": 63.2456, "messageFragment": " piedra filosofal esta en el  secreto"}'
echo ""

curl -s -X POST "$BASE/api/devices/STAR_SATELLITE" \
  -H "Content-Type: application/json" \
  -d '{"distance": 404.9691, "messageFragment": "La piedra filosofal  en el laboratorio "}'
echo ""
# Esperado: 201 CREATED en cada uno, con el dispositivo + distancia + fragmento


# ===== SECCION 2.3 - Resultado acumulado con los 3 ya registrados =====
curl -s -X GET "$BASE/api/devices/result"
echo ""
# Esperado: 200 OK, mismo resultado que en la seccion 2.1:
# labLocation x=120.0 y=-40.0 (aprox), reconstructedMessage completo


# ===== SECCION 2.4 - Reemplazar por completo un dispositivo =====
curl -s -X PUT "$BASE/api/devices/CCPD" \
  -H "Content-Type: application/json" \
  -d '{"distance": 70.5, "messageFragment": "nuevo fragmento completo de prueba"}'
echo ""
# Esperado: 200 OK, CCPD ahora con distance=70.5 y el nuevo fragmento


# ===== SECCION 2.4 (caso de error) - Reemplazar un dispositivo sin info =====
curl -s -X DELETE "$BASE/api/devices/STAR_SATELLITE"
curl -s -X PUT "$BASE/api/devices/STAR_SATELLITE" \
  -H "Content-Type: application/json" \
  -d '{"distance": 100, "messageFragment": "algo"}'
echo ""
# Esperado: 404 NOT_FOUND, DeviceInfoNotFoundException (porque se borro antes)


# ===== SECCION 2.5 - Actualizacion parcial =====
curl -s -X POST "$BASE/api/devices/STAR_SATELLITE" \
  -H "Content-Type: application/json" \
  -d '{"distance": 404.9691, "messageFragment": "La piedra filosofal  en el laboratorio "}'
echo ""
curl -s -X PATCH "$BASE/api/devices/STAR_SATELLITE" \
  -H "Content-Type: application/json" \
  -d '{"distance": 500}'
echo ""
# Esperado: 200 OK, STAR_SATELLITE con distance=500 y el mismo messageFragment de antes
# (solo cambio lo que se envio, el fragmento no se toco)


# ===== SECCION 2.5 (caso de error) - Actualizacion parcial vacia =====
curl -s -X PATCH "$BASE/api/devices/STAR_SATELLITE" \
  -H "Content-Type: application/json" \
  -d '{}'
echo ""
# Esperado: 400 BAD_REQUEST, InvalidDeviceDataException
# ("la actualizacion debe incluir al menos la distancia o el fragmento del mensaje")


# ===== SECCION 2.6 - Eliminar =====
curl -s -X DELETE "$BASE/api/devices/CCPD"
echo "CCPD eliminado (sin contenido en la respuesta, 204)"
# Esperado: 204 NO_CONTENT


# ===== SECCION 2.6 (caso de error) - Eliminar algo que ya no existe =====
curl -s -X DELETE "$BASE/api/devices/CCPD"
echo ""
# Esperado: 404 NOT_FOUND, DeviceInfoNotFoundException


# ===== CASOS DE ERROR ADICIONALES =====

# dispositivo desconocido
curl -s -X POST "$BASE/api/devices/DRON_FANTASMA" \
  -H "Content-Type: application/json" \
  -d '{"distance": 10, "messageFragment": "hola"}'
echo ""
# Esperado: 404 NOT_FOUND, UnknownDeviceException

# distancia negativa
curl -s -X POST "$BASE/api/devices/STAR_LABS" \
  -H "Content-Type: application/json" \
  -d '{"distance": -5, "messageFragment": "hola"}'
echo ""
# Esperado: 400 BAD_REQUEST (validacion de @PositiveOrZero)

# procesar con menos de 3 reportes
curl -s -X POST "$BASE/api/devices/process" \
  -H "Content-Type: application/json" \
  -d '{"reports": [{"deviceId":"STAR_LABS","distance":1,"messageFragment":"a"}]}'
echo ""
# Esperado: 400 BAD_REQUEST (validacion @Size min=3 max=3)
