<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";
require __DIR__ . "/auth_helpers.php";

/* 🔐 VALIDACIÓN OPCIONAL DE TOKEN */
$currentUserId = null;
$headers = getallheaders();
// Intentamos obtener el usuario solo si se envía un token
if (isset($headers['Authorization'])) {
    try {
        // Asumiendo que require_auth devuelve el auth o podemos extraer el ID
        $auth = require_auth($pdo); 
        $currentUserId = $auth["user_id"];
    } catch (Exception $e) {
        $currentUserId = null;
    }
}

$isLoggedIn = ($currentUserId !== null);

$limit = isset($_GET["limit"]) ? (int)$_GET["limit"] : 100;

// Si NO está logueado, cortamos la descripción a 80 caracteres en el SQL
$descriptionField = $isLoggedIn 
    ? "r.description" 
    : "IF(LENGTH(r.description) > 80, CONCAT(LEFT(r.description, 80), '... [Inicia sesión para leer más]'), r.description)";

$stmt = $pdo->prepare("
  SELECT
    r.id,
    r.title,
    $descriptionField AS description,
    r.visibility,
    r.created_at,
    r.url_imagen AS image,
    r.url_video  AS video,
    u.email      AS authorEmail,
    (r.user_id = ?) AS isMine
  FROM recipes r
  JOIN users u ON u.id = r.user_id
  WHERE r.visibility = 'public'
  ORDER BY r.id DESC
  LIMIT ?
");

$stmt->bindValue(1, $currentUserId, PDO::PARAM_INT);
$stmt->bindValue(2, $limit, PDO::PARAM_INT);
$stmt->execute();

echo json_encode(["ok"=>true, "items"=>$stmt->fetchAll()]);