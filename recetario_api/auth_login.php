<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";

$data = json_decode(file_get_contents("php://input"), true);
$email = trim($data["email"] ?? "");
$pass  = (string)($data["password"] ?? "");

if ($email === "" || $pass === "") {
    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>"email y password requeridos"]);
    exit;
}

$stmt = $pdo->prepare("SELECT id, password_hash FROM users WHERE email=? LIMIT 1");
$stmt->execute([$email]);
$user = $stmt->fetch();

if (!$user || !password_verify($pass, $user["password_hash"])) {
    http_response_code(401);
    echo json_encode(["ok"=>false,"error"=>"Credenciales inválidas"]);
    exit;
}

$userId = (int)$user["id"];

// limpia tokens viejos (opcional)
$pdo->prepare("DELETE FROM auth_tokens WHERE user_id=?")->execute([$userId]);

$token = bin2hex(random_bytes(32));
$tokenHash = hash('sha256', $token);

$now = time();
$expires = $now + (60 * 60 * 24 * 7); // 7 días

$stmt = $pdo->prepare("INSERT INTO auth_tokens (user_id, token_hash, created_at, expires_at) VALUES (?,?,?,?)");
$stmt->execute([$userId, $tokenHash, $now, $expires]);

echo json_encode(["ok"=>true, "token"=>$token, "expiresAt"=>$expires]);
