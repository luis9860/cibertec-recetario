<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";
require __DIR__ . "/auth_helpers.php";

$auth = require_auth($pdo);
$userId = $auth["user_id"];

$stmt = $pdo->prepare("
  SELECT 
    id, 
    title, 
    description, 
    visibility, 
    created_at,
    url_imagen AS image,
    url_video  AS video
  FROM recipes
  WHERE user_id = ?
  ORDER BY id DESC
  LIMIT 100
");
$stmt->execute([$userId]);

echo json_encode(["ok"=>true, "items"=>$stmt->fetchAll()]);
