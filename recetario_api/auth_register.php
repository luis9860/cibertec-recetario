<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

// (Opcional) CORS si pruebas desde web
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Access-Control-Allow-Methods: POST, OPTIONS");

if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") {
  http_response_code(200);
  exit;
}

require __DIR__ . "/db.php";

// Leer body crudo
$raw = file_get_contents("php://input");
$data = json_decode($raw, true);

if (!is_array($data)) {
  http_response_code(400);
  echo json_encode([
    "ok" => false,
    "error" => "JSON inválido",
    "detail" => "Body recibido: " . substr($raw ?? "", 0, 200)
  ]);
  exit;
}

$email = trim($data["email"] ?? "");
$pass  = (string)($data["password"] ?? "");

if ($email === "" || $pass === "") {
  http_response_code(400);
  echo json_encode(["ok"=>false,"error"=>"email y password requeridos"]);
  exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
  http_response_code(400);
  echo json_encode(["ok"=>false,"error"=>"email inválido"]);
  exit;
}

if (strlen($pass) < 8) {
  http_response_code(400);
  echo json_encode(["ok"=>false,"error"=>"password mínimo 8 caracteres"]);
  exit;
}

$hash = password_hash($pass, PASSWORD_DEFAULT);
$now = time();

try {
  $stmt = $pdo->prepare("INSERT INTO users (email, password_hash, created_at) VALUES (?,?,?)");
  $stmt->execute([$email, $hash, $now]);

  echo json_encode(["ok"=>true]);

} catch (Throwable $e) {

  // Duplicado de email (MySQL: 1062)
  $msg = $e->getMessage();
  if (strpos($msg, "1062") !== false) {
    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>"Este email ya está registrado"]);
    exit;
  }

  http_response_code(400);
  echo json_encode([
    "ok"=>false,
    "error"=>"No se pudo registrar",
    "detail"=>$msg
  ]);
}
