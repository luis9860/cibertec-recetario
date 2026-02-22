<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

require __DIR__ . "/db.php";
require __DIR__ . "/auth_helpers.php";

$token = get_bearer_token();
if (!$token) {
    http_response_code(401);
    echo json_encode(["ok"=>false,"error"=>"Missing token"]);
    exit;
}

$tokenHash = hash('sha256', $token);
$pdo->prepare("DELETE FROM auth_tokens WHERE token_hash=?")->execute([$tokenHash]);

echo json_encode(["ok"=>true]);
