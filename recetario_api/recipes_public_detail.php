<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";
require __DIR__ . "/auth_helpers.php";

/* 🔐 VALIDAR TOKEN */
$auth = require_auth($pdo);
$currentUserId = $auth["user_id"];

/* ✅ id requerido */
$id = isset($_GET["id"]) ? (int)$_GET["id"] : 0;
if ($id <= 0) {
  http_response_code(400);
  echo json_encode(["ok"=>false,"error"=>"id requerido"]);
  exit;
}

$stmt = $pdo->prepare("
  SELECT
    r.id,
    r.user_id,
    r.title,
    r.description,
    r.visibility,
    r.created_at,
    r.url_imagen AS image,
    r.url_video  AS video,
    u.email      AS authorEmail,
    (r.user_id = ?) AS isMine
  FROM recipes r
  JOIN users u ON u.id = r.user_id
  WHERE r.id = ?
  LIMIT 1
");
$stmt->execute([$currentUserId, $id]);
$row = $stmt->fetch();

if (!$row) {
  http_response_code(404);
  echo json_encode(["ok"=>false,"error"=>"No existe"]);
  exit;
}

/* 🔒 Permisos:
   - Si es public => OK
   - Si es private => solo dueño
*/
if ($row["visibility"] !== "public" && (int)$row["user_id"] !== (int)$currentUserId) {
  http_response_code(403);
  echo json_encode(["ok"=>false,"error"=>"No autorizado"]);
  exit;
}

echo json_encode(["ok"=>true, "item"=>$row]);
