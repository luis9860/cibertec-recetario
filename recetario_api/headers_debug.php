<?php
header("Content-Type: application/json; charset=UTF-8");
$headers = function_exists('getallheaders') ? getallheaders() : [];
echo json_encode(["headers"=>$headers]);
