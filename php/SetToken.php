<?php
/**
 * User: Josip
 * Date: 04.05.2016.
 * Time: 20:09
 */
require 'Config.php';

function addMeOnline($con, $nick, $token)
{
    $query = "UPDATE users SET token_black='".$token."' WHERE nick='".$nick."'";
    if(mysqli_query($con, $query)){
        return true;
    }
    return false;
   /* $statement = mysqli_prepare($con, "UPDATE users SET token_black=? WHERE nick=?");
    mysqli_stmt_bind_param($statement, "ss", $token, $nick);
    mysqli_stmt_execute($statement);

    mysqli_stmt_store_result($statement);
    if(mysqli_stmt_num_rows($statement) == 1){
        return true;
    } else{
        return false;
    }*/
}

$con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE,DB_PORT);
if(!$con){
    $response["error"] = true;
    $response["error_msg"] = "Database connection failed";
    echo json_encode($response);
    die;
}

$nick = $_POST["nick"];
$token = $_POST["token"];

if(!addMeOnline($con, $nick, $token)){
    $response["error"] = true;
    $response["error_msg"] = "Couldn't add token";
    echo json_encode($response);
    die;
}

$response["error"] = false;
$response["msg"] = "Token added";
echo json_encode($response);
die;
























