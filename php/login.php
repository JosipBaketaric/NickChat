<?php
require 'Config.php';

    function checkEmailAndPassword($con, $email, $password){
        $statement = mysqli_prepare($con, "SELECT * FROM users WHERE email = ? AND password = ?");
        mysqli_stmt_bind_param($statement, "ss",$email, $password);
        mysqli_stmt_execute($statement);

        mysqli_stmt_store_result($statement);
        if(mysqli_stmt_num_rows($statement) == 1){
            return true;
        } else{
            return false;
        }
    }

    function amIOnlineAlready($con, $email){
        $statement = mysqli_prepare($con, "SELECT loged FROM users WHERE email = ?");
        mysqli_stmt_bind_param($statement, "s",$email);
        mysqli_stmt_execute($statement);

        mysqli_stmt_bind_result($statement ,$loged);

        while (mysqli_stmt_fetch($statement)) {
            return $loged ? true:false;
        }
        return false;
    }

    function setMeOnline($con, $email){
        $statement = "UPDATE users SET loged = '1' WHERE email = '".$email."'";
        if(mysqli_query($con, $statement))
        {
            return true;
        }
        return false;
    }

    function validateEmail($email){
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            return false;
        }else{
            return true;
        }
    }

    function getNick($con, $email){
        $statement = mysqli_prepare($con, "SELECT nick FROM users WHERE email = ?");
        mysqli_stmt_bind_param($statement, "s",$email);
        mysqli_stmt_execute($statement);

        mysqli_stmt_bind_result($statement ,$nick);

        while (mysqli_stmt_fetch($statement)) {
            return $nick;
        }
        return false;
    }


    $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE,DB_PORT);
    if(!$con){
        $response["error"] = true;
        $response["error_msg"] = "Database connection failed";
        echo json_encode($response);
        die;
    }

        $email = $_POST["email"];
        $password = $_POST["password"];

        $password = md5($password);

        $response = array();
        $response["error"] = false;

        if (!validateEmail($email)) {
            $response["error"] = true;
            $response["error_msg"] = "invalid email. Move along nothing to see here!";
            echo json_encode($response);
            die;
        }


        if (!checkEmailAndPassword($con, $email, $password)) {
            $response["error"] = true;
            $response["error_msg"] = "Invalid credentials. Move along nothing to see here!";
            echo json_encode($response);
            die;
        }

        if(!setMeOnline($con, $email)){
            $response["error"] = true;
            $response["error_msg"] = "Couldn't set user to be online";
            echo json_encode($response);
            die;
        }



    $nick = getNick($con, $email);

    $response["user_email"] = $email;
    $response["user_nick"] = $nick;
    $response["server_response"] = "Login Successful! You may enter";
    echo json_encode($response);



//Tel other that I am here

$message = array(
    'TYPE' => "NEW_USER",
    'nick' => $nick,
);

$token_black = array();
$sql = "SELECT token_black FROM users WHERE loged='1'";
$result = $con->query($sql);

while($row = $result->fetch_assoc())
{
    array_push($token_black, $row["token_black"]);
}

$message = array('message' => $message);
if($pushStatus = sendPushNotification($token_black, $message)){
//
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

    }
    // Close connection
    curl_close($ch);
    return $result;
}

?>
?>