<?php
/**
 * User: Josip
 * Date: 04.05.2016.
 * Time: 20:09
 */
require 'Config.php';

    function removeMeOnline($con, $nick){
        $statement = "UPDATE users SET loged = '0' WHERE nick = '".$nick."'";
        if(mysqli_query($con, $statement)){
            return true;
        }
        return false;
    }


    $conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    if ($conn->connect_error) {
        die;
    }

    $nick = $_POST["nick"];

if(!removeMeOnline($conn, $nick)){
    $response["error"] = true;
    $response["error_msg"] = "Couldn't set user to be offline";
}


//Tell the others that I am gone

$message = array(
    'TYPE' => 'USER_DISCONNECTED',
    'nick' => $nick,
);

$token_black = array();
$sql = "SELECT token_black FROM users";
$result = $conn->query($sql);
while($row = $result->fetch_assoc())
{
    array_push($token_black, $row["token"]);
}

$message = array('message' => $message);

$pushStatus = sendPushNotification($token_black, $message);



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
    // Close connection
    curl_close($ch);

}