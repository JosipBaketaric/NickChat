<?php
/**
 * User: Josip
 * Date: 04.05.2016.
 * Time: 20:09
 */
require 'Config.php';

$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
$nick = $_POST["nick"];
$gotMsg = $_POST["msg"];

if (isset($_POST["type"])){
    $message = array(
        'TYPE' => $_POST["type"],
        'nick' => $nick,
        'msg' => $gotMsg,
    );
}

else {
    $message = array(
        'TYPE' => 'NEW_MESSAGE',
        'nick' => $nick,
        'msg' => $gotMsg,
    );
}

$token_black = array();
$sql = "SELECT token_black FROM users WHERE loged='1'";
$result = $conn->query($sql);

while($row = $result->fetch_assoc())
{
    array_push($token_black, $row["token_black"]);
}

$message = array('message' => $message);
if($pushStatus = sendPushNotification($token_black, $message)){
    $response["error"] = false;
    $response["msg"] = "Message Sent";
    echo json_encode($response);
}


function sendPushNotification($token_black, $message) {
    // Set POST variables
    $url = 'https://android.googleapis.com/gcm/send';
    $fields = array(
        'registration_ids' => $token_black,
        'data' => $message,
    );
    $headers = array(
        'Authorization: key=' . SECRET_KEY,
        'Content-Type: application/json'
    );


    // Open connection
    $ch = curl_init();

    // Set the url, number of POST vars, POST data
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    // Disabling SSL Certificate support temporarly
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

    // Execute post
    $result = curl_exec($ch);
    if ($result === FALSE) {
        $response["error"] = true;
        $response["error_msg"] = "failed sending message";
        echo json_encode($response);
        die;
    }
    // Close connection
    curl_close($ch);
    return $result;
}