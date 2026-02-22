<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";

$limit = isset($_GET["limit"]) ? (int)$_GET["limit"] : 50;
if ($limit <= 0) $limit = 50;
if ($limit > 200) $limit = 200;

$stmt = $pdo->prepare("
  SELECT
    r.id,
    r.title,
    r.visibility,
    r.created_at,
    r.url_imagen AS image,
    r.url_video  AS video,
    u.email      AS authorEmail,
    (r.description IS NOT NULL AND r.description <> '') AS hasDescription
  FROM recipes r
  JOIN users u ON u.id = r.user_id
  WHERE r.visibility = 'public'
  ORDER BY r.id DESC
  LIMIT ?
");
$stmt->bindValue(1, $limit, PDO::PARAM_INT);
$stmt->execute();

echo json_encode([
  "ok" => true,
  "items" => $stmt->fetchAll()
]);
