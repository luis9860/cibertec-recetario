<?php
header("Content-Type: application/json; charset=UTF-8");
error_reporting(E_ALL);
ini_set('display_errors', 1);

/* ⏱ Permitir subidas grandes */
set_time_limit(600);
ini_set('memory_limit', '512M');

require __DIR__ . "/db.php";
require __DIR__ . "/auth_helpers.php";

/* 🔐 VALIDAR TOKEN */
$auth = require_auth($pdo);
$userId = $auth["user_id"];

/* 📦 VERIFICAR ARCHIVO */
if (!isset($_FILES["file"])) {
    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>"Archivo no enviado"]);
    exit;
}

$file = $_FILES["file"];

/* ❌ MANEJO DETALLADO DE ERRORES */
if ($file["error"] !== UPLOAD_ERR_OK) {

    $uploadErrors = [
        UPLOAD_ERR_INI_SIZE   => "Excede upload_max_filesize del servidor",
        UPLOAD_ERR_FORM_SIZE  => "Excede MAX_FILE_SIZE del formulario",
        UPLOAD_ERR_PARTIAL    => "Archivo parcialmente subido",
        UPLOAD_ERR_NO_FILE    => "No se subió archivo",
        UPLOAD_ERR_NO_TMP_DIR => "Falta carpeta temporal",
        UPLOAD_ERR_CANT_WRITE => "Error al escribir en disco",
        UPLOAD_ERR_EXTENSION  => "Extensión bloqueó la subida"
    ];

    $errorMsg = $uploadErrors[$file["error"]] ?? "Error desconocido en subida";

    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>$errorMsg]);
    exit;
}

/* 📏 LIMITE 400MB */
$maxSize = 400 * 1024 * 1024; // 400MB

if ($file["size"] > $maxSize) {
    http_response_code(400);
    echo json_encode([
        "ok"=>false,
        "error"=>"Archivo muy grande (máx 400MB)"
    ]);
    exit;
}

/* 🔎 VALIDAR MIME REAL */
$finfo = finfo_open(FILEINFO_MIME_TYPE);
$mime = finfo_file($finfo, $file["tmp_name"]);
finfo_close($finfo);

$allowedMimes = [
    'image/jpeg',
    'image/png',
    'image/webp',
    'video/mp4'
];

if (!in_array($mime, $allowedMimes)) {
    http_response_code(400);
    echo json_encode(["ok"=>false,"error"=>"Tipo de archivo inválido"]);
    exit;
}

/* 📂 DEFINIR CARPETA */
$typeFolder = (strpos($mime, 'video') !== false) ? "videos" : "images";

$baseStoragePath = __DIR__ . "/../storage/$typeFolder/";
$publicBaseUrl   = "http://localhost/storage/$typeFolder/";

/* 📁 CREAR CARPETA SI NO EXISTE */
if (!is_dir($baseStoragePath)) {
    if (!mkdir($baseStoragePath, 0777, true)) {
        http_response_code(500);
        echo json_encode(["ok"=>false,"error"=>"No se pudo crear carpeta"]);
        exit;
    }
}

/* 📄 EXTENSION SEGURA */
$extension = match($mime) {
    'video/mp4' => 'mp4',
    'image/png' => 'png',
    'image/webp'=> 'webp',
    default     => 'jpg'
};

/* 🏷 NOMBRE UNICO */
$fileName = uniqid("file_", true) . "." . $extension;
$destination = $baseStoragePath . $fileName;

/* 💾 MOVER ARCHIVO */
if (!move_uploaded_file($file["tmp_name"], $destination)) {
    http_response_code(500);
    echo json_encode(["ok"=>false,"error"=>"No se pudo guardar archivo"]);
    exit;
}

/* 🌐 URL FINAL */
$fileUrl = $publicBaseUrl . $fileName;

/* ✅ RESPUESTA */
echo json_encode([
    "ok"   => true,
    "url"  => $fileUrl,
    "tipo" => $typeFolder,
    "size" => $file["size"]
]);
