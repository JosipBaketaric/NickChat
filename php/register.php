<?php
require 'Config.php';

    function checkIfEmailExists($con, $email){
        $statement = mysqli_prepare($con, "SELECT * FROM users WHERE email = ?");
        mysqli_stmt_bind_param($statement, "s",$email);
        mysqli_stmt_execute($statement);

        mysqli_stmt_store_result($statement);
        if(mysqli_stmt_num_rows($statement) != 0){
            return true;
        } else{
            return false;
        }
    }

    function checkIfNickExists($con, $nick){
        $statement = mysqli_prepare($con, "SELECT * FROM users WHERE nick = ?");
        mysqli_stmt_bind_param($statement, "s",$nick);
        mysqli_stmt_execute($statement);

        mysqli_stmt_store_result($statement);
        if(mysqli_stmt_num_rows($statement) > 0){
            return true;
        } else{
            return false;
        }
    }

    function validateEmail($email){
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            return false;
        }else{
            return true;
        }
    }

    function validateNick($nick){
        if (!preg_match("/^[a-zA-Z0-9]*$/",$nick)) {
            return false;
        }else{
            return true;
        }
    }

    $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE,DB_PORT);
    if(!$con){
        $response["error"] = true;
        $response["error_msg"] = "Database connection failed";
        echo json_encode($response);
        die;
    }

        if(!isset($_POST["email"]) || !isset($_POST["password"]) || !isset($_POST["nick"]) ){
            $response["error"] = true;
            $response["error_msg"] = "Didn't receive all info.";
            echo json_encode($response);
            die;
        }

        $email = $_POST["email"];
        $password = $_POST["password"];
        $nick = $_POST["nick"];

        $password = md5($password);

        $response = array();
        $response["error"] = false;

        if (!validateEmail($email)) {
            $response["error"] = true;
            $response["error_msg"] = "Invalid email.";
            echo json_encode($response);
            die;
        }

        if (!validateNick($nick)) {
            $response["error"] = true;
            $response["error_msg"] = "invalid nick. Only letters a-z and numbers allowed!";
            echo json_encode($response);
            die;

        }

        if (checkIfEmailExists($con, $email)) {
            $response["error"] = true;
            $response["error_msg"] = "Email already exists in database!";
            echo json_encode($response);
            die;
        }

        if(checkIfNickExists($con, $nick)) {
            $response["error"] = true;
            $response["error_msg"] = "Nick already exists in database! Chose another one jung apprentice";
            echo json_encode($response);
            die;
        }

        $statement = mysqli_prepare($con, "INSERT INTO users (nick, email, password, loged) VALUES (?,?,?,?)");
        $tempLoged = 0;
        mysqli_stmt_bind_param($statement, "sssi", $nick, $email, $password, $tempLoged);
        mysqli_stmt_execute($statement);

    $response["user_nick"] = $nick;
    $response["user_email"] = $email;
    $response["server_response"] = "You have successfully joined the dark side!";
    echo json_encode($response);

    mysqli_close($con);

?>