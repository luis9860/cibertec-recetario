<?php
function get_bearer_token(): ?string {
    $headers = function_exists('getallheaders') ? getallheaders() : [];
    $auth = $headers['Authorization'] ?? $headers['authorization'] ?? null;
    if (!$auth) return null;

    if (preg_match('/Bearer\s(\S+)/', $auth, $m)) return $m[1];
    return null;
}

function require_auth(PDO $pdo): array {
    $token = get_bearer_token();
    if (!$token) {
        http_response_code(401);
        echo json_encode(["ok"=>false,"error"=>"Missing token"]);
        exit;
    }

    $tokenHash = hash('sha256', $token);
    $now = time();

    $stmt = $pdo->prepare("
        SELECT t.user_id, u.email, t.expires_at
        FROM auth_tokens t
        JOIN users u ON u.id = t.user_id
        WHERE t.token_hash = ?
        LIMIT 1
    ");
    $stmt->execute([$tokenHash]);
    $row = $stmt->fetch();

    if (!$row) {
        http_response_code(401);
        echo json_encode(["ok"=>false,"error"=>"Invalid token"]);
        exit;
    }

    if ((int)$row["expires_at"] < $now) {
        http_response_code(401);
        echo json_encode(["ok"=>false,"error"=>"Token expired"]);
        exit;
    }

    return ["user_id" => (int)$row["user_id"], "email" => (string)$row["email"]];
}
