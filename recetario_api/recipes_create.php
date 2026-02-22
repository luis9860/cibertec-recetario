<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";
require __DIR__ . "/auth_helpers.php";

$auth = require_auth($pdo);
$userId = $auth["user_id"];

$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) {
    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>"JSON inválido"]);
    exit;
}

$title = trim($data["title"] ?? "");
$description = trim($data["description"] ?? "");
$visibility = trim($data["visibility"] ?? "private");

/* ✅ createdAt: si tú envías segundos, guárdalo así. Si envías ms, también sirve, solo sé consistente */
$createdAt = (int)($data["createdAt"] ?? time());

/* ✅ NUEVO: URLs del upload */
$urlImagen = trim($data["urlImagen"] ?? "");
$urlVideo  = trim($data["urlVideo"] ?? "");

if ($title === "") {
    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>"title requerido"]);
    exit;
}

/* ✅ IMPORTANTE: la tabla recipes debe tener columnas url_imagen y url_video */
$stmt = $pdo->prepare(
  "INSERT INTO recipes (user_id, title, description, visibility, created_at, url_imagen, url_video)
   VALUES (?, ?, ?, ?, ?, ?, ?)"
);

$stmt->execute([
    $userId,
    $title,
    $description,
    $visibility,
    $createdAt,
    $urlImagen,
    $urlVideo
]);

echo json_encode(["ok"=>true, "id" => (int)$pdo->lastInsertId()]);
