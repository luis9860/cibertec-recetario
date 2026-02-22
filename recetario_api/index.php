<?php
header("Content-Type: application/json; charset=UTF-8");
echo json_encode([
  "ok" => true,
  "service" => "recetario_api",
  "endpoints" => [
    "POST /auth_register.php",
    "POST /auth_login.php",
    "POST /recipes_create.php (Bearer)",
    "GET  /recipes_list.php (Bearer)",
    "GET  /ping.php"
  ]
]);
